package io.sisu.nng.aio;

import io.sisu.nng.Message;

/**
 * A ContextProxy provides an API for event handler functions or closures to (relatively) safely
 * manipulate the underlying {@link Context} in response to {@link Aio} operations (e.g. send,
 * receive, sleep).
 *
 * Each of the methods provided returns immediately and will not block.
 */
public interface ContextProxy {
    /**
     * Send the given {@link Message} asynchronously via the Context. Will not block.
     *
     * @param message the {@link Message} to send
     */
    void send(Message message);

    /**
     * Attempt a receive operation on the Context. Returns immediately after adding the operation
     * to the underlying queue.
     */
    void receive();

    /**
     * Suspend the Context for the given duration, triggering the wake handler upon timeout. Does
     * not block.
     *
     * @param millis the time in milliseconds to sleep for
     */
    void sleep(int millis);

    /**
     * Put the given object value into the backing state of the underlying Context using the given
     * key as a reference. The object is then available for retrieval by {@link #get(String)} in
     * other event handlers.
     *
     * If an object for the key already exists, it is replaced.
     *
     * @param key a String value to use as the key
     * @param value the object to store in the Context
     */
    void put(String key, Object value);

    /**
     * Get an object from the Context's state using the given key, if it exists. Otherwise, returns
     * null.
     *
     * @param key a String value used as the key
     * @return the Object if found, or null if not present
     */
    Object get(String key);

    /**
     * Get an object from the Context's state using the given key, returning the value if found or
     * the provided defaultValue if not.
     *
     * @param key a String value used as the key
     * @param defaultValue an object to return if the key is not found
     * @return the found Object or the provided default value
     */
    Object getOrDefault(Object key, Object defaultValue);
}
