package io.sisu.nng.aio;

import io.sisu.nng.Message;
import io.sisu.nng.NngException;
import io.sisu.nng.Socket;

/**
 * Interface for the {@link Aio} API primarily to be used by an {@link AioCallback}.
 *
 * Each of these methods should correspond to those commonly used by {@link Aio} callbacks in NNG
 * programs. In practice, those using {@link Context} will use a {@link ContextProxy} when writing
 * event handlers and not call methods on the {@link AioProxy} directly.
 */
public interface AioProxy {
    void setMessage(Message msg);
    Message getMessage();

    void assertSuccessful() throws NngException;

    void sleep(int millis);

    boolean begin();
    void finish(int error);

    void send(Socket socket);
    void receive(Socket socket);
}
