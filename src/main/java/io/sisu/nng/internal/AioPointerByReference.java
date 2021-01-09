package io.sisu.nng.internal;

import com.sun.jna.Native;
import com.sun.jna.Pointer;
import com.sun.jna.ptr.ByReference;

public class AioPointerByReference extends PointerByReference {

    public AioPointer getAioPointer() {
        final AioPointer aio = new AioPointer();
        aio.setPointer(getPointer().getPointer(0));
        return aio;
    }
}
