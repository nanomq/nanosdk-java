package io.sisu.nng.aio;

import com.sun.jna.Pointer;
import io.sisu.nng.internal.NngCallback;

import java.util.function.BiConsumer;

/**
 * Convenience wrapper around NNG's aio callback functions.
 *
 * The general design is to allow for leaning into Java's design more than NNG's C design, keeping
 * state in the JVM and allowing for the consumer to have limited access to the aio api via a
 * reference to an AioProxy. (This provides a way to perform aio operations like setting or getting
 * a Message, calling send, sleep, etc.)
 */
public class AioCallback<T> implements NngCallback {
    protected AioProxy proxy = null;

    public T args = null;
    public BiConsumer<AioProxy, T> consumer = (a, b) -> {};

    public AioCallback() { }

    /**
     * Configure a new AioCallback with the given BiConsumer that will be called with both a proxy
     * implementation to the underlying Aio instance and the given args object T.
     *
     * @param consumer the callback logic in the form of a BiConsumer
     * @param args reference to a Java object to pass with each call of the callback
     */
    public AioCallback(BiConsumer<AioProxy, T> consumer, T args) {
        this.consumer = consumer;
        this.args = args;
    }

    protected void setAioProxy(AioProxy proxy) {
        this.proxy = proxy;
    }

    /**
     * This is the actual entrypoint for the underlying NNG callback function and will be triggered
     * from native code. We don't use the given Pointer p as we keep the callback arg on the JVM in
     * the form of <pre>args</pre>. This method simply wraps calling our provided BiConsumer.
     *
     * @param p unused JNA Pointer to the callback arguments, in practice should be a null pointer
     */
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
