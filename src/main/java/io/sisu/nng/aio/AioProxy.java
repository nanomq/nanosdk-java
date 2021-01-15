package io.sisu.nng.aio;

import io.sisu.nng.Message;

public interface AioProxy {
    void setMessage(Message msg);
    Message getMessage();
    boolean begin();
    void finish(int error);
}
