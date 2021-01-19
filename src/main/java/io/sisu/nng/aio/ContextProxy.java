package io.sisu.nng.aio;

import io.sisu.nng.Message;
import io.sisu.nng.NngException;

public interface ContextProxy {
    void send(Message message);
    void receive();
    void sleep(int millis);
    void assertSuccessful() throws NngException;

    void put(String key, Object value);
    Object get(String key);
    Object getOrDefault(Object key, Object defaultValue);
}
