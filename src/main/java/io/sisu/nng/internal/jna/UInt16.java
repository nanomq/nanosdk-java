package io.sisu.nng.internal.jna;

import com.sun.jna.IntegerType;

import java.nio.ByteOrder;

public class UInt16 extends IntegerType {
    public UInt16() {
        this(0);
    }
    public UInt16(long value) {
        this(value, true);
    }
    public UInt16(int size, long value) {
        this(value, true);
    }

    public UInt16(long value, boolean unsigned) {
        super(2, value, unsigned);
    }

    public short convert() {
        if (ByteOrder.nativeOrder() == ByteOrder.LITTLE_ENDIAN) {
            return Short.reverseBytes(shortValue());
        }
        return shortValue();
    }
}
