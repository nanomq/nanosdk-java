package io.sisu.nng.jna;

import com.sun.jna.IntegerType;

import java.nio.ByteOrder;

public class UInt32 extends IntegerType {
    public UInt32() {
        this(0);
    }
    public UInt32(long value) {
        this(value, true);
    }
    public UInt32(long value, boolean unsigned) {
        super(4, value, unsigned);
    }

    public int convert() {
        if (ByteOrder.nativeOrder() == ByteOrder.LITTLE_ENDIAN) {
            return Integer.reverseBytes(intValue());
        }
        return intValue();
    }
}
