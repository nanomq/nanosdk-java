package io.sisu.nng.aio;

import io.sisu.nng.Message;
import io.sisu.nng.Nng;
import io.sisu.nng.NngException;
import io.sisu.nng.Socket;
import io.sisu.nng.internal.ContextStruct;
import io.sisu.nng.internal.NngOptions;
import io.sisu.nng.internal.SocketStruct;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.*;
import java.util.function.BiConsumer;
import java.util.function.Consumer;

/**
 * Wrapper of an NNG context, allowing for multi-threaded use of individual Sockets.
 *
 * Unlike the native nng_context, the Java Context provides a built in event dispatcher for the
 * common event types (data received, data sent, wake from sleep). While I'm still designing the
 * high-level API around this, for now there are 3 potential approaches for using a Context:
 *
 * 1. Synchronously using sendMessageSync()/recvMessageSync
 * 2. Asynchronously with CompletableFutures using sendMessage()/recvMessage()
 * 3. Asynchronously with callbacks via (optionally) registering event handlers.
 *
 * In the 3rd case (event handlers), one must "set the wheels in motion" by performing an initial
 * asynchronous operation (like via a recvMessage() call).
 *
 * NOTE: Given Contexts are primarily for asynchronous usage and don't require their own dedicated
 * threads, it's important to keep the Context from being garbage collected.
 */
public class Context implements AutoCloseable {
    private final Socket socket;
    private final ContextStruct.ByValue context;
    private AioCallback<?> aioCallback;
    private Aio aio;

    private final BlockingQueue<Work> queue = new LinkedBlockingQueue<>();
    private final HashMap<String, Object> stateMap = new HashMap<>();

    private final Map<Event, BiConsumer<ContextProxy, Message>> eventHandlers = new HashMap<>();
    private static final BiConsumer<ContextProxy, Message> noop = (a, m) -> {};

    /**
     * The supported asynchronous event types, corresponding to the core asynchronous operations
     */
    public enum Event {
        RECV,
        SEND,
        WAKE,
    }

    /**
     * A unit of asynchronous work awaiting completion. Effectively a container for state while
     * the Context waits for the AioCallback to fire.
     */
    public static class Work {
        // Type of Work we're expecting
        public final Event event;
        // The CompletableFuture to notify upon completion or exception
        public final CompletableFuture<Object> future;

        public Message msg = null;

        public Work(Event event, CompletableFuture<Object> future) {
            this.event = event;
            this.future = future;
        }
    }

    /**
     * A {@link ContextProxy} instance bound to this Context.
     */
    private final ContextProxy proxy = new ContextProxy() {
        @Override
        public void send(Message message) {
            sendMessage(message);
        }

        @Override
        public void receive() {
            receiveMessage();
        }

        @Override
        public void sleep(int millis) {
            Context.this.sleep(millis);
        }

        @Override
        public void put(String key, Object value) {
            stateMap.put(key, value);
        }

        @Override
        public Object get(String key) {
            return stateMap.get(key);
        }

        @Override
        public Object getOrDefault(Object key, Object defaultValue) {
            return stateMap.getOrDefault(key, defaultValue);
        }
    };

    /**
     * Asynchronous handler for dispatching queued Work to appropriate event handlers based on the
     * Work's Event type.
     *
     * @param aioProxy reference to the AioProxy for the underlying nng aio instance
     * @param ctx reference to the Context this method is dispatching for
     */
    private static void dispatch(AioProxy aioProxy, Context ctx) {
        try {
            // XXX: to guard against race conditions, we using a poll-based approach in case the
            // callback fires before the work is added to the queue. (Should only happen if empty.)
            Work work = ctx.queue.poll(5, TimeUnit.SECONDS);
            if (work == null) {
                // XXX: no known work or queue is cleared because we're closing the Context
                return;
            }

            try {
                // Raises an exception on error, but for successful sends, receives, or wakes will
                // pass normally.
                aioProxy.assertSuccessful();

                // Look up a potential event handler
                BiConsumer<ContextProxy, Message> consumer = ctx.eventHandlers.getOrDefault(work.event, noop);
                Object result = null;

                // Todo: Excessive use of switch at the moment
                switch (work.event) {
                    case RECV:
                        result = aioProxy.getMessage();
                        break;
                    case SEND:
                        // fallthrough
                    case WAKE:
                        break;
                    default:
                        System.err.println("Unexpected event type: " + work.event);
                }

                // Apply the event handler with the optional Message
                consumer.accept(ctx.proxy, (Message) result);

                if (work.future != null) {
                    work.future.complete(result);
                }
            } catch (NngException e) {
                // if we were sending and failed, we still own the message
                if (work.msg != null) {
                    work.msg.setValid();
                }

                if (work.future != null) {
                    work.future.completeExceptionally(e);
                }
            }

        } catch (Exception e) {
            // We don't get specific yet on parsing failure types
            e.printStackTrace();
        }
    }

    /**
     * Create a new Context for the given Socket.
     *
     * Note: Not all protocols support Contexts (e.g. Push0/Pull0)
     *
     * @param socket the given Socket to create a new Context for
     * @throws NngException on an nng error
     */
    public Context(Socket socket) throws NngException {
        ContextStruct contextStruct = new ContextStruct();
        SocketStruct.ByValue socketStruct = (SocketStruct.ByValue) socket.getSocketStruct();

        int rv = Nng.lib().nng_ctx_open(contextStruct, socketStruct);
        if (rv != 0) {
            throw new NngException(Nng.lib().nng_strerror(rv));
        }

        this.socket = socket;
        this.context = new ContextStruct.ByValue(contextStruct);
        this.aioCallback = new AioCallback<>(Context::dispatch, this);
        this.aio = new Aio(aioCallback);
    }

    /**
     * Set a receive event handler on the Context, replacing the existing if present. It will be
     * called upon completion of a Receive event regardless of outcome.
     *
     * The handler is of the form BiConsumer&lt;Contextproxy, Message&gt; and will have a
     * reference to this Context's {@link ContextProxy} set as well as a reference the received
     * {@link Message} instance.
     *
     * Note: For now, with the current API, it's advised that the handler free the Message itself
     * if it's not using it for a send operation or storing it for later use.
     *
     * @param handler the receive event handler
     */
    public void setRecvHandler(BiConsumer<ContextProxy, Message> handler) {
        this.eventHandlers.put(Event.RECV, handler);
    }

    /**
     * Set a send event handler on the Context, replacing the existing if present. It will be called
     * upon completion of a Send event regardless of outcome.
     *
     * The send handler will be provided a reference to a {@link ContextProxy}, corresponding to
     * this Context, when called. The handler should interact with the Context using the proxy and
     * not via a captured reference to the Context.
     *
     * @param handler the send handler to set
     */
    public void setSendHandler(Consumer<ContextProxy> handler) {
        this.eventHandlers.put(Event.SEND, (aioProxy, unused) -> handler.accept(aioProxy));
    }

    /**
     * Set a wake event handler on the Context, replacing the existing if present. It will be called
     * on completion of a sleep event, regardless of outcome.
     *
     * The wake handler will be provided a reference to a {@link ContextProxy}, corresponding to
     * this Context, when called. The handler should interact with the Context using the proxy and
     * not via a captured reference to the Context.
     *
     * @param handler the send handler to set
     */
    public void setWakeHandler(Consumer<ContextProxy> handler) {
        this.eventHandlers.put(Event.WAKE, (aioProxy, unused) -> handler.accept(aioProxy));
    }

    /**
     * Close the Context and try to safely release any resources (e.g. the Aio) in advance of
     * garbage collection.
     *
     * @throws NngException on error closing the Context
     */
    public void close() throws NngException {
        queue.clear();
        int rv = Nng.lib().nng_ctx_close(context);
        if (rv != 0) {
            throw new NngException(Nng.lib().nng_strerror(rv));
        }
        aio.free();
    }

    /**
     * Receive a {@link Message} asynchronously on this Context. The {@link Message} is owned by
     * the JVM and caller and should be either used for a subsequent send operation or freed when
     * no longer required.
     *
     * @return a CompletableFuture that is fulfilled with the {@link Message} upon success, or
     * completed exceptionally on failure or error.
     */
    public CompletableFuture<Message> receiveMessage() {
        CompletableFuture<Object> future = new CompletableFuture<>();

        this.queue.add(new Work(Event.RECV, future));
        Nng.lib().nng_ctx_recv(context, aio.getAioPointer());

        return future.thenApply(obj -> (Message) obj);
    }

    /**
     * Try to receive a Message on the Context, blocking until received.
     *
     * @return the received Message
     * @throws NngException on error or timeout
     */
    public Message receiveMessageSync() throws NngException {
        try {
            return receiveMessage().get();
        } catch (InterruptedException e) {
            throw  new NngException("interrupted");
        } catch (ExecutionException e) {
            if (e.getCause() instanceof NngException) {
                throw (NngException) e.getCause();
            } else {
                throw new NngException("unknown execution exception");
            }
        }
    }

    /**
     * Send a Message on the Context. If the Message is accepted for sending, the Message will be
     * invalidated.
     *
     * @param msg the Message to send
     * @return a {@link CompletableFuture} that will either complete on success or complete
     * exceptionally on error or timeout. It's the caller's responsibility to then either retry or
     * free the Message.
     */
    public CompletableFuture<Void> sendMessage(Message msg) {
        // XXX: potential for TOCTOU, but we set the Message optimistically invalid before we know
        // if it's successfully been accepted for sending
        if (!msg.isValid()) {
            CompletableFuture<Void> future = new CompletableFuture<>();
            future.completeExceptionally(new IllegalStateException("Message is invalid"));
            return future;
        }

        CompletableFuture<Object> future = new CompletableFuture<>();
        Work work = new Work(Event.SEND, future);
        // Set the a reference to the Message on the Work instance to keep it from being garbage
        // collected in case the caller doesn't keep a reference as well
        work.msg = msg;
        this.queue.add(work);

        // XXX: we set the Message invalid for now, assuming success. If the Message fails to send
        // then the event handler will set it back to valid
        msg.setInvalid();
        aio.setMessage(msg);
        Nng.lib().nng_ctx_send(context, aio.getAioPointer());

        return future.thenApply((unused) -> null);
    }

    /**
     * Attempt to send the given Message synchronously on the Context, blocking until it's either
     * accepted for sending, an error occurs, or a timeout.
     *
     * If the Message is accepted for sending, it will be marked invalid for future use.
     *
     * @param msg the Message to send
     * @throws NngException on error or timeout
     */
    public void sendMessageSync(Message msg) throws NngException {
        try {
            sendMessage(msg).join();
        } catch (Exception e) {
            if (e.getCause() instanceof NngException) {
                throw (NngException) e.getCause();
            } else {
                throw new NngException("unknown execution exception");
            }
        }
    }

    /**
     * Sleep the Context for the given duration, triggering the Wake handler upon timeout.
     *
     * @param millis number of milliseconds to sleep
     * @return a CompletableFuture that completes upon the wake event concluding or an error
     */
    public CompletableFuture<Void> sleep(int millis) {
        CompletableFuture<Object> future = new CompletableFuture<>();
        this.queue.add(new Work(Event.WAKE, future));
        aio.sleep(millis);
        return future.thenApply((unused) -> null);
    }

    /**
     * Set a receive timeout on the Context.
     *
     * @param timeoutMillis timeout in milliseconds
     * @throws NngException on error setting the timeout
     */
    public void setReceiveTimeout(int timeoutMillis) throws NngException {
        int rv = Nng.lib().nng_ctx_set_ms(this.context, NngOptions.RECV_TIMEOUT, timeoutMillis);
        if (rv != 0) {
            String err = Nng.lib().nng_strerror(rv);
            throw new NngException(err);
        }
    }

    /**
     * Set a send timeout on the Context
     *
     * @param timeoutMillis timeout in milliseconds
     * @throws NngException on error setting the timeout
     */
    public void setSendTimeout(int timeoutMillis) throws NngException {
        int rv = Nng.lib().nng_ctx_set_ms(this.context, NngOptions.SEND_TIMEOUT, timeoutMillis);
        if (rv != 0) {
            String err = Nng.lib().nng_strerror(rv);
            throw new NngException(err);
        }
    }
}
