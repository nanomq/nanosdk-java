package io.sisu.nng.internal.jna;

public class UInt64ByReference extends NumberByReference {
    public UInt64 getUInt64() {
        UInt64 value = new UInt64();
        value.setValue(getPointer().getNativeLong(0).longValue());
        return value;
    }
}
