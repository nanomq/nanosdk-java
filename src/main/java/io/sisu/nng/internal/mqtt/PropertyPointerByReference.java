package io.sisu.nng.internal.mqtt;

import com.sun.jna.Pointer;
import com.sun.jna.PointerType;
import io.sisu.nng.internal.BodyPointer;
import io.sisu.nng.internal.NngPointerByReference;

public class PropertyPointerByReference extends NngPointerByReference {
    public PropertyPointerByReference() {
    }

    public PropertyPointerByReference(Pointer p) {
        super(p);
    }

    public PropertyPointer getPropertyPointer() {
        PropertyPointer prop = new PropertyPointer();
        prop.setPointer(getValue());
        return prop;
    }
}
