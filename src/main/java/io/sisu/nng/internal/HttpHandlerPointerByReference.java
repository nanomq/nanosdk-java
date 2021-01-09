package io.sisu.nng.internal;

public class HttpHandlerPointerByReference extends NngPointerByReference {
    public HttpHandlerPointer getHandlerPointer() {
        HttpHandlerPointer handler = new HttpHandlerPointer();
        handler.setPointer(getValue());
        return handler;
    }
}
