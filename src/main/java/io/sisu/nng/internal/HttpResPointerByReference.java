package io.sisu.nng.internal;

public class HttpResPointerByReference extends PointerByReference {
    public HttpResPointer getHttpReqPointer() {
        HttpResPointer res = new HttpResPointer();
        res.setPointer(getPointer().getPointer(0));
        return res;
    }
}
