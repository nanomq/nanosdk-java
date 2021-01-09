package io.sisu.nng.internal;

import com.sun.jna.Native;
import com.sun.jna.Pointer;
import com.sun.jna.ptr.ByReference;

public class MessageByReference extends PointerByReference {
    public MessagePointer getMessage() {
        final MessagePointer msg = new MessagePointer();
        msg.setPointer(getPointer().getPointer(0));
        return msg;
    }
}
