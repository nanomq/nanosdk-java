package io.sisu.nng.internal.mqtt;

import com.sun.jna.Pointer;
import io.sisu.nng.internal.NngPointerByReference;

public class BytesPointerByReference extends NngPointerByReference {
    public BytesPointerByReference() {
    }

    public BytesPointerByReference(Pointer p) {
        super(p);
    }

    public BytesPointer getBytesPointer() {
        BytesPointer bytePtr = new BytesPointer();
        bytePtr.setPointer(getValue());
        return bytePtr;
    }
}
