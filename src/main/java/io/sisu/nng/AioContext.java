package io.sisu.nng;

import com.sun.jna.Callback;
import com.sun.jna.Native;
import com.sun.jna.Pointer;
import io.sisu.nng.internal.AioPointer;
import io.sisu.nng.internal.AioPointerByReference;

import java.nio.ByteBuffer;

public class AioContext {
    private final AioPointer aio;

    public AioContext(Callback cb, Pointer arg) throws NngException {
        AioPointerByReference ref = new AioPointerByReference();
        final int rv = Nng.lib().nng_aio_alloc(ref, cb, arg);
        if (rv != 0) {
            throw new NngException(Nng.lib().nng_strerror(rv));
        }
        aio = ref.getAioPointer();
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

    public void waitForFinish() {
        Nng.lib().nng_aio_wait(aio);
    }

    public void assertSuccessful() throws NngException {
        int rv = Nng.lib().nng_aio_result(aio);
        if (rv != 0) {
            throw new NngException(Nng.lib().nng_strerror(rv));
        }
    }
}
