package io.sisu.nng;

import com.sun.jna.Callback;
import com.sun.jna.CallbackProxy;
import com.sun.jna.Native;
import com.sun.jna.Pointer;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.charset.StandardCharsets;

public class AioTest {

    @Test
    public void simpleAioTest() throws NngException {
        Callback cb = new CallbackProxy() {
            @Override
            public Object callback(Object[] args) {
                System.out.println("Hello from the callback!");
                for (Object arg : args) {
                    if (arg instanceof Pointer) {
                        Pointer p = (Pointer) arg;
                        System.out.println("p[0]: " + p.getInt(0));
                        System.out.println("p[1]: " + p.getString(4, "UTF-8"));
                    }
                }
                return 69;
            }

            @Override
            public Class<?>[] getParameterTypes() {
                return new Class[] { Pointer.class };
            }

            @Override
            public Class<?> getReturnType() {
                return Integer.class;
            }
        };

        ByteBuffer b1 = ByteBuffer.allocateDirect(128);
        b1.order(ByteOrder.nativeOrder());
        b1.putInt(-568).put(Native.toByteArray("sup!"));
        AioContext aio = new AioContext(cb, Native.getDirectBufferPointer(b1));
        aio.setTimeoutMillis(500);
        Assertions.assertTrue(aio.begin());
        aio.setOutput(0, ByteBuffer.allocateDirect(10).put("Hey man".getBytes(StandardCharsets.UTF_8)));
        aio.finish(0);

        aio.assertSuccessful();
        Assertions.assertEquals("Hey man", aio.getOutputAsString(0));
    }
}
