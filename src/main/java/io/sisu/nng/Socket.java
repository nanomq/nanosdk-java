package io.sisu.nng;

import com.sun.jna.Pointer;
import io.sisu.nng.internal.MessageByReference;
import io.sisu.nng.internal.SocketStruct;

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

    public int getId() {
        return socket.id;
    }

    public SocketStruct getSocketStruct() {
        return socket;
    }
}
