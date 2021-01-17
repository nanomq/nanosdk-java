package io.sisu.nng.internal.jna;

public class UInt16ByReference extends NumberByReference {
    public UInt16 getUInt16() {
        UInt16 value = new UInt16();
        value.setValue(getPointer().getShort(0));
        return value;
    }
}
