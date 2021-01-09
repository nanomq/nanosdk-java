package io.sisu.nng.internal;

import com.sun.jna.Pointer;
import com.sun.jna.PointerType;

public class NativeStringPointer extends PointerType {
    @Override
    public String toString() {
        if (getPointer() != Pointer.NULL) {
            return getPointer().getString(0);
        }
        return null;
    }

    public NativeStringPointer() {
        super();
    }

    public NativeStringPointer(Pointer p) {
        this.setPointer(p);
    }
}
