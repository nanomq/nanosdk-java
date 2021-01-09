package io.sisu.nng.internal;

import com.sun.jna.Pointer;

public class BodyPointerByReference extends PointerByReference {
    public BodyPointerByReference() {

    }

    public BodyPointerByReference(Pointer p) {
        super(p);
    }

    public BodyPointer getBodyPointer() {
        BodyPointer body = new BodyPointer();
        body.setPointer(getValue());
        return body;
    }
}
