package io.sisu.nng.internal;

public class MessageByReference extends NngPointerByReference {
    public MessagePointer getMessage() {
        return new MessagePointer(getPointer().getPointer(0));
    }
}
