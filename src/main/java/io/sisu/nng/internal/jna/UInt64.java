package io.sisu.nng.internal.jna;

import com.sun.jna.IntegerType;

import java.nio.ByteOrder;

public class UInt64 extends IntegerType {
    public UInt64() {
        this(0);
    }
    public UInt64(long value) {
        this(value, true);
    }
    public UInt64(long value, boolean unsigned) {
        super(8, value, unsigned);
    }

    public long convert() {
        if (ByteOrder.nativeOrder() == ByteOrder.LITTLE_ENDIAN) {
            return Long.reverseBytes(longValue());
        }
        return longValue();
    }
}
