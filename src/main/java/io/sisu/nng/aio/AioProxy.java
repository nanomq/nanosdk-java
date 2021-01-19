package io.sisu.nng.aio;

import io.sisu.nng.Message;
import io.sisu.nng.NngException;

public interface AioProxy {
    void setMessage(Message msg);
    Message getMessage();

    void assertSuccessful() throws NngException;

    void recvAsync();
    void sendAsync();

    void sleep(int millis);

    boolean begin();
    void finish(int error);
}
