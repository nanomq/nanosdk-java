package io.sisu.nng.aio;

import com.sun.jna.Callback;

import java.util.function.BiConsumer;

/**
 * Convenience wrapper around NNG's AIO callback functions.
 */
public class AioCallback<T> implements Callback {

    private T args;
    private AioProxy proxy;
    private BiConsumer<AioProxy, T> callback;

    public AioCallback(BiConsumer<AioProxy, T> callback, T args) {
        this.callback = callback;
        this.args = args;
    }

    protected void setAioProxy(AioProxy proxy) {
        this.proxy = proxy;
    }

    public void callback() {
        try {
            callback.accept(proxy, this.args);
        } catch (Exception e) {
            // TODO: What, if anything? Think about this.
        }
    }
}
