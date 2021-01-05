package io.sisu.nng.internal;

import com.sun.jna.PointerType;

public class MessagePointer extends PointerType {
    // TODO: maybe remove this
    public static MessagePointer alloc(int size, NngLibrary lib) throws Exception {
        final MessageByReference ref = new MessageByReference();
        int rv = lib.nng_msg_alloc(ref, 0);
        if (rv != 0) {
            throw new Exception("Crap");
        }
        return ref.getMessage();
    }
}
