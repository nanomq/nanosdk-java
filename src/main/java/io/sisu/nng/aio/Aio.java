package io.sisu.nng.aio;

import com.sun.jna.CallbackThreadInitializer;
import com.sun.jna.Native;
import com.sun.jna.Pointer;
import io.sisu.nng.Message;
import io.sisu.nng.Nng;
import io.sisu.nng.NngException;
import io.sisu.nng.Socket;
import io.sisu.nng.internal.AioPointer;
import io.sisu.nng.internal.AioPointerByReference;

import java.nio.ByteBuffer;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * Asynchronous IO (AIO) handle used primarily by Contexts.
 *
 * This class can be used directly, but is designed to be most useful as part of a Context. Most
 * developers will interact with the Aio instance via its AioProxy interface and not the Aio
 * instance direactly.
 *
 * In general, most methods are simply wrappers around the native NNG aio functions for manipulating
 * the aio. However, the introduction of the AioCallback approach provides a Java-friendly way to
 * provide callback event handlers. @see io.sisu.nng.aio.Context
 */
public class Aio implements AioProxy {
    // Global counter for Aio instances created
    public static AtomicInteger created = new AtomicInteger(0);
    // Global counter for Aio instances freed
    public static AtomicInteger freed = new AtomicInteger(0);

    public static final int TIMEOUT_INFINITE = -1;
    public static final int TIMEOUT_DEFAULT_DURATION = -2;

    private final AioPointer aio;
    private final AioPointerByReference pointer;
    private AioCallback<?> cb;

    // Flag for if this Aio is still owned by the JVM (true) or has been freed (false)
    private AtomicBoolean valid = new AtomicBoolean(false);

    /**
     * Allocate a new Aio with a no-op callback.
     * @throws NngException if allocation fails
     */
    public Aio() throws NngException {
        this(null);
    }

    /**
     * Allocate a new Aio with the given AioCallback for processing events.
     *
     * @param cb an instance of AioCallback for handling events on this Aio
     * @throws NngException if allocation fails
     */
    public Aio(AioCallback cb) throws NngException {
        this.pointer = new AioPointerByReference();

        // XXX: JNA config for daemonizing any new callback thread giving it an identifiable name
        Native.setCallbackThreadInitializer(cb, new CallbackThreadInitializer(true, false, "AioCallback"));

        final int rv = Nng.lib().nng_aio_alloc(this.pointer, cb, Pointer.NULL);
        if (rv != 0) {
            throw new NngException(Nng.lib().nng_strerror(rv));
        }
        this.aio = this.pointer.getAioPointer();
        this.valid.set(true);
        this.cb = cb;
        if (cb != null) {
            this.cb.setAioProxy(this);
        }
        created.incrementAndGet();
    }

    public void setOutput(int index, ByteBuffer buffer) {
        if (index < 0 || index > 3) {
            throw new IndexOutOfBoundsException("index must be between 0 and 3");
        }

        if (buffer.isDirect()) {
            Nng.lib().nng_aio_set_output(aio, index, Native.getDirectBufferPointer(buffer));
        } else {
            ByteBuffer directBuffer = ByteBuffer.allocateDirect(buffer.limit() - buffer.position());
            directBuffer.put(buffer);
            Nng.lib().nng_aio_set_output(aio, index, Native.getDirectBufferPointer(directBuffer));
        }
    }

    public String getOutputAsString(int index) {
        if (index < 0 || index > 3) {
            throw new IndexOutOfBoundsException("index must be between 0 and 3");
        }

        Pointer p = Nng.lib().nng_aio_get_output(aio, index);
        return p.getString(0);
    }

    public boolean begin() {
        return Nng.lib().nng_aio_begin(aio);
    }

    /**
     * Set a timeout for asynchronous operations by the Aio.
     *
     * Two special timeouts are available:
     * <ul>
     *     <li>{@link #TIMEOUT_INFINITE} - no timeout will be used</li>
     *     <li>{@link #TIMEOUT_DEFAULT_DURATION} - the "default" socket timeout will be used</li>
     * </ul>
     *
     * The value is persistent and will be used for all subsequent asynchronous operations until
     * changed.
     *
     * @param timeoutMillis the timeout in milliseconds.
     */
    public void setTimeoutMillis(int timeoutMillis) {
        Nng.lib().nng_aio_set_timeout(aio, timeoutMillis);
    }

    public void finish(int err) {
        Nng.lib().nng_aio_finish(aio, err);
    }

    /**
     * Abort the current Aio operation, calling the {@link AioCallback} if present. (If so, the
     * callback will be notified the operation completed with a cancellation error.)
     */
    public void cancel() {
        Nng.lib().nng_aio_cancel(aio);
    }

    public int getResult() {
        return Nng.lib().nng_aio_result(aio);
    }

    /**
     * Wait for the current Aio operation to complete. If an {@link AioCallback} is in use by the
     * Aio, this call will return after the callback completes execution.
     *
     * If there's no pending operation, it will return immediately.
     */
    public void waitForFinish() {
        Nng.lib().nng_aio_wait(aio);
    }

    /**
     * Check that the latest Aio operation was successful. If not, throw an exception with the
     * error.
     *
     * @throws NngException if the latest Aio operation failed
     */
    public void assertSuccessful() throws NngException {
        int rv = Nng.lib().nng_aio_result(aio);
        if (rv != 0) {
            throw new NngException(Nng.lib().nng_strerror(rv));
        }
    }

    public AioPointer getAioPointer() {
        return this.aio;
    }

    /**
     * Set a {@link Message} on the Aio, usually used prior to a send operation.
     *
     * @param msg the {@link Message} to set
     */
    @Override
    public void setMessage(Message msg) {
        Nng.lib().nng_aio_set_msg(aio, msg.getMessagePointer());
    }

    /**
     * Get a {@link Message} set on the Aio, most likely as part of a receive operation.
     *
     * @return the {@link Message}, null if no {@link Message} is set
     */
    @Override
    public Message getMessage() {
        Pointer pointer = Nng.lib().nng_aio_get_msg(aio);

        // TODO: refactor into Optional<Message>? Throw directly?
        try {
            return new Message(pointer);
        } catch (NngException e) {
            return null;
        }
    }

    /**
     * Perform an asynchronous delay, causing the Aio's {@link AioCallback} to fire after the
     * provided timeout.
     *
     * @param millis time to sleep the Aio in milliseconds
     */
    @Override
    public void sleep(int millis) {
        Nng.lib().nng_sleep_aio(millis, aio);
    }

    /**
     * Perform an asynchronous sending operation on the given Socket. The Message to send must have
     * been previously set via {@link #setMessage(Message)}.
     *
     * Note: if you're looking for asynchronous Socket usage, use a Context instead.
     *
     * @param socket the Socket to attempt the send operation on
     */
    public void send(Socket socket) {
        Nng.lib().nng_send_aio(socket.getSocketStruct().byValue(), this.aio);
    }

    /**
     * Perform an asynchronous receiving operation on the given Socket.
     * @param socket the {@link Socket} to attempt the receive operation on
     */
    public void receive(Socket socket) {
        Nng.lib().nng_recv_aio(socket.getSocketStruct().byValue(), this.aio);
    }

    /**
     * Free this Aio and set it invalid. If an operation is in progress, the operation will be
     * cancelled and this method will block until it's completely canceled. The backing native
     * memory for the nng_aio will be freed. The Aio must not be reused at this point.
     */
    protected void free() {
        if (valid.compareAndSet(true, false)) {
            // System.out.println(String.format("JVM is freeing AIO %s", this));
            Nng.lib().nng_aio_free(this.aio);
            freed.incrementAndGet();
        }
    }
}
