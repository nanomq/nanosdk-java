package io.sisu.nng.internal.jna;

public class UInt32ByReference extends NumberByReference {
    public UInt32 getUInt32() {
        UInt32 value = new UInt32();
        value.setValue(getPointer().getInt(0));
        return value;
    }
}
