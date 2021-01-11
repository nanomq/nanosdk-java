package io.sisu.nng.jna;

public class UInt64ByReference extends NumberByReference {
    public UInt64 getUInt64() {
        UInt64 value = new UInt64();
        value.setValue(getPointer().getLong(0));
        return value;
    }
}
