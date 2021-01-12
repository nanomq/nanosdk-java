package io.sisu.nng;

import com.sun.jna.Pointer;
import io.sisu.nng.internal.MessageByReference;
import io.sisu.nng.internal.NngFlags;
import io.sisu.nng.internal.NngOptions;
import io.sisu.nng.internal.SocketStruct;
import io.sisu.nng.jna.Size;
import io.sisu.nng.jna.SizeByReference;

import java.nio.ByteBuffer;
import java.util.function.Function;

public abstract class Socket {
    protected final SocketStruct.ByValue socket;

    protected Socket(Function<SocketStruct, Integer> socketOpener) throws NngException {
        final SocketStruct ref = new SocketStruct();
        int rv = socketOpener.apply(ref);
        if (rv != 0) {
            String err = Nng.lib().nng_strerror(rv);
            throw new NngException(err);
        }
        this.socket = new SocketStruct.ByValue(ref);
    }

    public void close() throws NngException {
        int rv = Nng.lib().nng_close(this.socket);
        if (rv != 0) {
            String err = Nng.lib().nng_strerror(rv);
            throw new NngException(err);
        }
    }

    public void setReceiveTimeout(int timeoutMillis) throws NngException {
        int rv = Nng.lib().nng_socket_set_ms(this.socket, NngOptions.RECV_TIMEOUT, timeoutMillis);
        if (rv != 0) {
            String err = Nng.lib().nng_strerror(rv);
            throw new NngException(err);
        }
    }

    public void setSendTimeout(int timeoutMillis) throws NngException {
        int rv = Nng.lib().nng_socket_set_ms(this.socket, NngOptions.SEND_TIMEOUT, timeoutMillis);
        if (rv != 0) {
            String err = Nng.lib().nng_strerror(rv);
            throw new NngException(err);
        }
    }

    public void sendMessage(ByteBuffer body) throws NngException {
        sendMessage(body, null);
    }

    public void sendMessage(ByteBuffer body, ByteBuffer header) throws NngException {
        final Message msg = new Message();
        msg.append(body);

        // TODO: this seems silly
        if (header != null && header.limit() > 0) {
            msg.appendToHeader(header);
        }

        int rv = Nng.lib().nng_sendmsg(this.socket, msg.getMessagePointer(), 0);
        if (rv != 0) {
            // failed to send, so free our message before we toss the exception
            int rvFree = Nng.lib().nng_msg_free(msg.getMessagePointer());
            if (rvFree != 0) {
                throw new NngException(Nng.lib().nng_strerror(rvFree));
            }
            throw new NngException(Nng.lib().nng_strerror(rv));
        }
    }

    public void sendMessage(Message msg) throws NngException {
        if (msg.isValid()) {
            int rv = Nng.lib().nng_sendmsg(this.socket, msg.getMessagePointer(), 0);
            if (rv != 0) {
                throw new NngException(Nng.lib().nng_strerror(rv));
            }
            msg.valid = false;
        } else {
            throw new NngException("Message state is invalid");
        }
    }

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

    public void dial(String url) throws NngException {
        // TODO: simple api for now
        int rv = Nng.lib().nng_dial(this.socket, url, Pointer.NULL, 0);
        if (rv != 0) {
            throw new NngException(Nng.lib().nng_strerror(rv));
        }
    }
    public void listen(String url) throws NngException {
        // TODO: simple api for now
        int rv = Nng.lib().nng_listen(this.socket, url, Pointer.NULL, 0);
        if (rv != 0) {
            throw new NngException(Nng.lib().nng_strerror(rv));
        }
    }

    /**
     * Sends raw data over the Socket.
     *
     * Note: explicitly does NOT support NNG_FLAG_ALLOC option as its assumed the ByteBuffer is
     * allocated and managed by the JVM.
     * @param data a directly allocated ByteBuffer of data to send
     * @param blockUntilSent boolean flag determining if the call waits until the message is sent,
     *                       or returns immediately after queuing the data, or until a timer expires
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

    public void send(ByteBuffer data) throws NngException {
        send(data, true);
    }

    /**
     * Receives raw data on the Socket.
     *
     * Note: explicitly does NOT support NNG_FLAG_ALLOC, forcing the caller to own and manage the
     * lifetime of the provided ByteBuffer
     * @param buffer A directly allocated ByteBuffer to receive data into
     * @param blockUntilReceived boolean flag determining if the call waits until the message is
     *                           sent, or returns immediately after queuing the data, or until a
     *                           timer expires
     * @return long number of bytes received
     * @throws NngException
     */
    public long receive(ByteBuffer buffer, boolean blockUntilReceived)
            throws NngException, IllegalArgumentException {
        if (!buffer.isDirect()) {
            throw new IllegalArgumentException("ByteBuffer provided is not directly allocated");
        }

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

    public long receive(ByteBuffer buffer) throws NngException {
        return receive(buffer, true);
    }

    public int getId() {
        return socket.id;
    }

    public SocketStruct getSocketStruct() {
        return socket;
    }
}
