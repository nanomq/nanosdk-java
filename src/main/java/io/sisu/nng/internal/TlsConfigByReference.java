package io.sisu.nng.internal;

public class TlsConfigByReference extends NngPointerByReference {
    public TlsConfigPointer getTlsConfig() {
        return new TlsConfigPointer(getPointer().getPointer(0));
    }
}
