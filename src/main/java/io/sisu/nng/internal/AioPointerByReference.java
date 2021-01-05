package io.sisu.nng.internal;

import com.sun.jna.Native;
import com.sun.jna.Pointer;
import com.sun.jna.ptr.ByReference;

public class AioPointerByReference extends ByReference {
    public AioPointerByReference() {
        this(null);
    }

    public AioPointerByReference(Pointer value) {
        super(Native.POINTER_SIZE);
        setValue(value);
    }

    public void setValue(Pointer value) {
        getPointer().setPointer(0, value);
    }

    public Pointer getValue() {
        return getPointer().getPointer(0);
    }

    public AioPointer getAioPointer() {
        final AioPointer aio = new AioPointer();
        aio.setPointer(getPointer().getPointer(0));
        return aio;
    }
}
