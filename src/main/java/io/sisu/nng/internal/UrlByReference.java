package io.sisu.nng.internal;

import com.sun.jna.Structure;

public class UrlByReference extends PointerByReference {

    public UrlStruct getUrl() {
        return Structure.newInstance(UrlStruct.class, getValue());
    }
}
