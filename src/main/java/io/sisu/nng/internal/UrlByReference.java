package io.sisu.nng.internal;

import com.sun.jna.Native;
import com.sun.jna.Pointer;
import com.sun.jna.Structure;
import com.sun.jna.ptr.ByReference;

public class UrlByReference extends ByReference {
    public UrlByReference() {
        this(null);
    }

    public UrlByReference(Pointer value) {
        super(Native.POINTER_SIZE);
        setValue(value);
    }

    public void setValue(Pointer value) {
        getPointer().setPointer(0, value);
    }

    public Pointer getValue() {
        return getPointer().getPointer(0);
    }

    public UrlStruct getUrl() {
        return Structure.newInstance(UrlStruct.class, getValue());
    }
}
