package io.sisu.nng.internal;

public class AioPointerByReference extends NngPointerByReference {

    public AioPointer getAioPointer() {
        final AioPointer aio = new AioPointer();
        aio.setPointer(getPointer().getPointer(0));
        return aio;
    }
}
