package io.sisu.nng;

import io.sisu.nng.aio.Aio;
import io.sisu.nng.aio.AioCallback;
import io.sisu.nng.internal.ContextStruct;
import io.sisu.nng.internal.NngOptions;
import io.sisu.nng.internal.SocketStruct;

/**
 * Wrapper of an NNG context, allowing for multi-threaded use of Sockets.
 *
 * This wrapper is pretty basic at the moment and effectively works synchronously.
 */
public class Context {
    private final Socket socket;
    private final ContextStruct.ByValue context;
    private final Aio aio;

    public Context(Socket socket) throws NngException {
        this(socket, null);
    }

    public Context(Socket socket, AioCallback callback) throws NngException {
        ContextStruct contextStruct = new ContextStruct();
        SocketStruct.ByValue socketStruct = (SocketStruct.ByValue) socket.getSocketStruct();

        int rv = Nng.lib().nng_ctx_open(contextStruct, socketStruct);
        if (rv != 0) {
            throw new NngException(Nng.lib().nng_strerror(rv));
        }

        this.socket = socket;
        this.context = new ContextStruct.ByValue(contextStruct);
        this.aio = new Aio(this, callback);
    }

    public Message receiveMessage() throws NngException {
        Nng.lib().nng_ctx_recv(context, aio.getAioPointer());
        aio.waitForFinish();

        int rv = aio.getResult();
        if (rv != 0) {
            throw new NngException(Nng.lib().nng_strerror(rv));
        }

        return aio.getMessage();
    }

    public void sendMessage(Message msg) throws NngException {
        aio.setMessage(msg);
        Nng.lib().nng_ctx_send(context, aio.getAioPointer());
        aio.waitForFinish();

        int rv = aio.getResult();
        if (rv != 0) {
            throw new NngException(Nng.lib().nng_strerror(rv));
        }
        msg.valid = false;
    }

    public void setReceiveTimeout(int timeoutMillis) throws NngException {
        int rv = Nng.lib().nng_ctx_set_ms(this.context, NngOptions.RECV_TIMEOUT, timeoutMillis);
        if (rv != 0) {
            String err = Nng.lib().nng_strerror(rv);
            throw new NngException(err);
        }
    }

    public void setSendTimeout(int timeoutMillis) throws NngException {
        int rv = Nng.lib().nng_ctx_set_ms(this.context, NngOptions.SEND_TIMEOUT, timeoutMillis);
        if (rv != 0) {
            String err = Nng.lib().nng_strerror(rv);
            throw new NngException(err);
        }
    }

    public void trigger() {
        aio.sleep(100);
    }

    public ContextStruct.ByValue getContextStruct() {
        return context;
    }
}
