package io.sisu.nng.aio;

import io.sisu.nng.Message;
import io.sisu.nng.NngException;
import io.sisu.nng.Socket;

/**
 * Interface for the NNG aio API primarily to be used by AioCallbacks.
 *
 * Each of these methods should correspond to those commonly used by aio callbacks in NNG programs.
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
