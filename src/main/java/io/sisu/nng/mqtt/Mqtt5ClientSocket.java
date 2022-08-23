package io.sisu.nng.mqtt;

import io.sisu.nng.Nng;
import io.sisu.nng.NngException;
import io.sisu.nng.Socket;

public class Mqtt5ClientSocket extends Socket {
    public Mqtt5ClientSocket() throws NngException {
        super(Nng.lib()::nng_mqtt_client_open);
    }
}
