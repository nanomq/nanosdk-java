package io.sisu.nng.internal;

public class HttpClientPointerByReference extends PointerByReference {

    public HttpClientPointer getHttpClientPointer() {
        HttpClientPointer client = new HttpClientPointer();
        client.setPointer(getValue());
        return client;
    }
}
