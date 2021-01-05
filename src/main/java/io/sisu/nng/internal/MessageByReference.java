package io.sisu.nng.internal;

import com.sun.jna.Native;
import com.sun.jna.Pointer;
import com.sun.jna.ptr.ByReference;

public class MessageByReference extends ByReference {
    public MessageByReference() {
        this(null);
    }

    public MessageByReference(Pointer value) {
        super(Native.POINTER_SIZE);
        setValue(value);
    }

    public void setValue(Pointer value) {
        getPointer().setPointer(0, value);
    }

    public Pointer getValue() {
        return getPointer().getPointer(0);
    }

    public MessagePointer getMessage() {
        final MessagePointer msg = new MessagePointer();
        msg.setPointer(getPointer().getPointer(0));
        return msg;
    }
}
