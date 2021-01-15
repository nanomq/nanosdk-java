package io.sisu.nng.internal.jna;

import com.sun.jna.IntegerType;
import com.sun.jna.Native;

import java.nio.ByteOrder;

public class Size extends IntegerType {
    protected boolean converted = false;

    public Size() {
        this(0);
    }
    public Size(long value) {
        this(value, true);
    }
    public Size(long value, boolean unsigned) {
        super(Native.SIZE_T_SIZE, value, unsigned);
    }

    public long convert() {
        if (converted) {
            return longValue();
        }

        if (ByteOrder.nativeOrder() == ByteOrder.LITTLE_ENDIAN) {
            return Long.reverseBytes(longValue());
        }
        return longValue();
    }
}
