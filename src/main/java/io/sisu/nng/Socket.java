package io.sisu.nng;

import com.sun.jna.Pointer;
import io.sisu.nng.internal.MessageByReference;
import io.sisu.nng.internal.NngFlags;
import io.sisu.nng.internal.NngOptions;
import io.sisu.nng.internal.SocketStruct;
import io.sisu.nng.internal.jna.Size;
import io.sisu.nng.internal.jna.SizeByReference;

import java.nio.ByteBuffer;
import java.util.function.BiFunction;
import java.util.function.Function;

/**
 * Abstract base class of an nng Socket, implementing the common nng api for Socket communication.
 *
 *
 */
public abstract class Socket implements AutoCloseable {

    // Underlying nng_socket struct. Primarily an opaque data type with a public id
    protected final SocketStruct.ByValue socket;
    private String url;

    protected Socket(Function<SocketStruct, Integer> socketOpener) throws NngException {
        final SocketStruct ref = new SocketStruct();
        int rv = socketOpener.apply(ref);
        if (rv != 0) {
            String err = Nng.lib().nng_strerror(rv);
            throw new NngException(err);
        }
        this.socket = new SocketStruct.ByValue(ref);
    }

    protected Socket(BiFunction<SocketStruct, String, Integer> socketOpener, String url) throws NngException {
        final SocketStruct ref = new SocketStruct();
        int rv = socketOpener.apply(ref, url);

        if (rv != 0) {
            String err = Nng.lib().nng_strerror(rv);
            throw new NngException(err);
        }
        this.socket = new SocketStruct.ByValue(ref);
        this.url = url;
    }

    /**
     * Close the Socket. Outstanding messages or data may or may not be flushed, depending on the
     * protocol.
     * <p>
     * Note: Data loss may occur if the Socket is closed if there are outstanding messages in the
     * underlying send queue.
     *
     * @throws NngException if an nng error occurs
     */
    public void close() throws NngException {
        int rv = Nng.lib().nng_close(this.socket);
        if (rv != 0) {
            String err = Nng.lib().nng_strerror(rv);
            throw new NngException(err);
        }
    }

    /**
     * Set the timeout for receive requests on the Socket
     *
     * @param timeoutMillis timeout duration in milliseconds
     * @throws NngException if an nng error occurs setting the timeout
     */
    public void setReceiveTimeout(int timeoutMillis) throws NngException {
        int rv = Nng.lib().nng_socket_set_ms(this.socket, NngOptions.RECV_TIMEOUT, timeoutMillis);
        if (rv != 0) {
            String err = Nng.lib().nng_strerror(rv);
            throw new NngException(err);
        }
    }

    /**
     * Set the timeout for send requests on the Socket
     *
     * @param timeoutMillis timeout duration in milliseconds
     * @throws NngException if an nng error occurs setting the timeout
     */
    public void setSendTimeout(int timeoutMillis) throws NngException {
        int rv = Nng.lib().nng_socket_set_ms(this.socket, NngOptions.SEND_TIMEOUT, timeoutMillis);
        if (rv != 0) {
            String err = Nng.lib().nng_strerror(rv);
            throw new NngException(err);
        }
    }

    /**
     * Send a new Message using the provided ByteBuffer for the message body
     *
     * @param buffer a ByteBuffer containing the message body
     * @throws NngException if an nng error occurs during sending
     */
    public void sendMessage(ByteBuffer buffer) throws NngException {
        sendMessage(buffer, null);
    }

    public void sendMessage(ByteBuffer body, ByteBuffer header) throws NngException {
        final Message msg = new Message();
        msg.append(body);

        if (header != null && header.limit() > 0) {
            msg.appendToHeader(header);
        }

        sendMessage(msg);
    }

    /**
     * Send the provide Message on the Socket. If the underlying nng call reports it's successfully
     * taken and enqueued the Message, invalidate it.
     *
     * @param msg the Message to send
     * @throws NngException if the socket raised an error when accepting the messaging for sending
     * @throws IllegalStateException if the Message state is invalid
     */
    public void sendMessage(Message msg) throws NngException {
        if (msg.valid.compareAndSet(true, false)) {
            int rv = Nng.lib().nng_sendmsg(this.socket, msg.getMessagePointer(), 0);
            if (rv != 0) {
                // failed to send, need to flag that it's still valid memory
                msg.valid.set(true);
                throw new NngException(Nng.lib().nng_strerror(rv));
            }
        } else {
            throw new IllegalStateException("Message state is invalid");
        }
    }

    /**
     * Attempt to receive a Message on the Socket.
     *
     * @return the received Message, owned now by the JVM
     * @throws NngException if the Socket raises an error on receiving
     */
    public Message receiveMessage() throws NngException {
        return receiveMessage(0);
    }

    public Message receiveMessage(int flags) throws NngException {
        final MessageByReference ref = new MessageByReference();
        int rv = Nng.lib().nng_recvmsg(this.socket, ref, flags);
        if (rv != 0) {
            throw new NngException(Nng.lib().nng_strerror(rv));
        }
        return new Message(ref.getMessage());
    }

    /**
     * Dial the given url
     *
     * @param url a valid nng url as a String
     * @throws NngException on an invalid url or failure to connect
     */
    public void dial(String url) throws NngException {
        // TODO: simple api for now
        int rv = Nng.lib().nng_dial(this.socket, url, Pointer.NULL, 0);
        if (rv != 0) {
            throw new NngException(Nng.lib().nng_strerror(rv));
        }
    }

    /**
     * Listen for connections on the given url
     *
     * @param url a valid nng url as a String
     * @throws NngException on an invalid url or failure to listen
     */
    public void listen(String url) throws NngException {
        int rv = Nng.lib().nng_listen(this.socket, url, Pointer.NULL, 0);
        if (rv != 0) {
            throw new NngException(Nng.lib().nng_strerror(rv));
        }
    }

    /**
     * Sends raw data over the Socket.
     *
     * Note: explicitly does NOT support NNG_FLAG_ALLOC option as it's assumed the ByteBuffer is
     * allocated and managed by the JVM and will not be freed by the call to nng_send.
     *
     * @param data a directly allocated ByteBuffer of data to send
     * @param blockUntilSent boolean flag determining if the caller waits until the message is
     *                       accepted for sending by the Socket
     * @throws NngException if the underlying nng_send call returns non-zero
     */
    public void send(ByteBuffer data, boolean blockUntilSent) throws NngException {
        // TODO: should we convert ByteBuffer's that aren't directly allocated?

        Size size = new Size(data.limit() - data.position());
        int flags = blockUntilSent ? 0 : NngFlags.NONBLOCK;

        int rv = Nng.lib().nng_send(this.socket, data, size, flags);
        if (rv != 0) {
            throw new NngException(Nng.lib().nng_strerror(rv));
        }
    }

    /**
     * Send raw data over a {@link Socket}
     *
     * @param data a {@link ByteBuffer} containing the data to send
     * @throws NngException on error sending the data
     */
    public void send(ByteBuffer data) throws NngException {
        send(data, true);
    }

    /**
     * Receives raw data on the Socket into a {@link ByteBuffer}
     *
     * Note: Currently does not support NNG_FLAG_ALLOC, forcing the caller to own and manage the
     * lifetime of the provided ByteBuffer
     *
     * @param buffer the ByteBuffer to receive data into
     * @param blockUntilReceived boolean flag determining if the call waits until the message is
     *                           received or returns immediately
     * @return long number of bytes received
     * @throws NngException if an underlying nng error occurs
     * @throws IllegalArgumentException if the provided ByteBuffer is not a direct ByteBuffer
     */
    public long receive(ByteBuffer buffer, boolean blockUntilReceived)
            throws NngException, IllegalArgumentException {
        int flags = blockUntilReceived ? 0 : NngFlags.NONBLOCK;

        // We're not using ALLOC mode for now, so we need to provide the address of our Size
        // for the nng_recv() to read
        SizeByReference sizeRef = new SizeByReference();
        Size size = new Size(buffer.limit() - buffer.position());
        sizeRef.setSize(size);

        int rv = Nng.lib().nng_recv(this.socket, buffer, sizeRef, flags);
        if (rv != 0) {
            throw new NngException(Nng.lib().nng_strerror(rv));
        }

        // On success, our size should be updated to reflect the actual data received, which may
        // be less than the total buffer limit.
        // TODO: what should we do with this case?
        return sizeRef.getSize().convert();
    }

    /**
     * Receive a raw message into the provided {@link ByteBuffer}, blocking until data is received
     * or a timeout occurs.
     *
     * @param buffer a direct ByteBuffer
     * @return long number of bytes received
     * @throws NngException if an nng error occurs
     */
    public long receive(ByteBuffer buffer) throws NngException {
        return receive(buffer, true);
    }

//    public void setTlsConfig(TlsConfig config) throws NngException {
//        int rv;
//
//        rv = Nng.lib().nng_socket_set_ptr(this.socket, NngOptions.TLS_CONFIG, config.getPointer());
//        if (rv != 0) {
//            throw new NngException(Nng.lib().nng_strerror(rv));
//        }
//    }

    public SocketStruct getSocketStruct() {
        return socket;
    }
}
