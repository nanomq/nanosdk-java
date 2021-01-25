package io.sisu.nng.aio;

import com.sun.jna.Pointer;
import io.sisu.nng.internal.NngCallback;

import java.util.function.BiConsumer;

/**
 * Convenience wrapper around NNG's AIO callback functions.
 */
public class AioCallback<T> implements NngCallback {
    protected AioProxy proxy = null;

    public T args = null;
    public BiConsumer<AioProxy, T> consumer = (a, b) -> {};

    public AioCallback() { }

    public AioCallback(BiConsumer<AioProxy, T> consumer, T args) {
        this.consumer = consumer;
        this.args = args;
    }

    protected void setAioProxy(AioProxy proxy) {
        this.proxy = proxy;
    }

    @Override
    public void callback(Pointer p) {
        try {
            this.consumer.accept(proxy, this.args);
        } catch (Exception e) {
            // TODO: What, if anything? Think about this.
            System.err.println(e.getMessage());
        }
    }
}
