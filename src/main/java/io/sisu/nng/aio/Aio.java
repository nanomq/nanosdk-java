package io.sisu.nng.aio;

import com.sun.jna.CallbackThreadInitializer;
import com.sun.jna.Native;
import com.sun.jna.Pointer;
import io.sisu.nng.Context;
import io.sisu.nng.Message;
import io.sisu.nng.Nng;
import io.sisu.nng.NngException;
import io.sisu.nng.internal.AioPointer;
import io.sisu.nng.internal.AioPointerByReference;
import io.sisu.nng.internal.MessagePointer;

import java.nio.ByteBuffer;

public class Aio implements AioProxy {
    private final AioPointer aio;
    private AioCallback cb;
    private Context ctx;

    public Aio() throws NngException {
        this(null);
    }

    public Aio(Context ctx) throws NngException {
        this(ctx, null);
    }

    public Aio(Context ctx, AioCallback cb) throws NngException {
        this.ctx = ctx;

        AioPointerByReference ref = new AioPointerByReference();
        Native.setCallbackThreadInitializer(cb, new CallbackThreadInitializer(true, false, "AioCallback"));
        final int rv = Nng.lib().nng_aio_alloc(ref, cb, Pointer.NULL);
        if (rv != 0) {
            throw new NngException(Nng.lib().nng_strerror(rv));
        }
        this.aio = ref.getAioPointer();

        if (cb != null) {
            this.cb = cb;
            this.cb.setAioProxy(this);
        }
    }

    public void setOutput(int index, ByteBuffer buffer) {
        if (index < 0 || index > 3) {
            throw new IndexOutOfBoundsException("index must be between 0 and 3");
        }

        if (buffer.isDirect()) {
            Nng.lib().nng_aio_set_output(aio, index, Native.getDirectBufferPointer(buffer));
        } else {
            ByteBuffer directBuffer = ByteBuffer.allocateDirect(buffer.limit() - buffer.position());
            directBuffer.put(buffer);
            Nng.lib().nng_aio_set_output(aio, index, Native.getDirectBufferPointer(directBuffer));
        }
    }

    public String getOutputAsString(int index) {
        if (index < 0 || index > 3) {
            throw new IndexOutOfBoundsException("index must be between 0 and 3");
        }

        Pointer p = Nng.lib().nng_aio_get_output(aio, index);
        return p.getString(0);
    }

    public boolean begin() {
        return Nng.lib().nng_aio_begin(aio);
    }

    public void setTimeoutMillis(int timeoutMillis) {
        Nng.lib().nng_aio_set_timeout(aio, timeoutMillis);
    }

    public void finish(int err) {
        Nng.lib().nng_aio_finish(aio, err);
    }

    public void cancel() {
        Nng.lib().nng_aio_cancel(aio);
    }

    public int getResult() {
        return Nng.lib().nng_aio_result(aio);
    }

    public void waitForFinish() {
        Nng.lib().nng_aio_wait(aio);
    }

    public void assertSuccessful() throws NngException {
        int rv = Nng.lib().nng_aio_result(aio);
        if (rv != 0) {
            throw new NngException(Nng.lib().nng_strerror(rv));
        }
    }

    public AioPointer getAioPointer() {
        return this.aio;
    }

    @Override
    public void setMessage(Message msg) {
        Nng.lib().nng_aio_set_msg(aio, msg.getMessagePointer());
    }

    @Override
    public Message getMessage() {
        MessagePointer pointer = Nng.lib().nng_aio_get_msg(aio);
        if (pointer != null && pointer.getPointer() != Pointer.NULL) {
            return new Message(pointer);
        }
        return null;
    }

    @Override
    public void recvAsync() {
        Nng.lib().nng_ctx_recv(ctx.getContextStruct(), aio);
    }

    @Override
    public void sendAsync() {
        Nng.lib().nng_ctx_send(ctx.getContextStruct(), aio);
    }

    @Override
    public void sleep(int millis) {
        Nng.lib().nng_sleep_aio(millis, aio);
    }
}
