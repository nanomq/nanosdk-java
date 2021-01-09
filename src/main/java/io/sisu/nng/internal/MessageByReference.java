package io.sisu.nng.internal;

public class MessageByReference extends NngPointerByReference {
    public MessagePointer getMessage() {
        final MessagePointer msg = new MessagePointer();
        msg.setPointer(getPointer().getPointer(0));
        return msg;
    }
}
