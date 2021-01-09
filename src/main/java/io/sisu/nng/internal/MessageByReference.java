package io.sisu.nng.internal;

public class MessageByReference extends PointerByReference {
    public MessagePointer getMessage() {
        final MessagePointer msg = new MessagePointer();
        msg.setPointer(getPointer().getPointer(0));
        return msg;
    }
}
