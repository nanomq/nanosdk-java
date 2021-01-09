package io.sisu.nng.internal;

public class AioPointerByReference extends PointerByReference {

    public AioPointer getAioPointer() {
        final AioPointer aio = new AioPointer();
        aio.setPointer(getPointer().getPointer(0));
        return aio;
    }
}
