package io.sisu.nng.mqtt;

import io.sisu.nng.Nng;
import io.sisu.nng.NngException;
import io.sisu.nng.Socket;

public class MqttClientSocket extends Socket {
    public MqttClientSocket() throws NngException {
        super(Nng.lib()::nng_mqtt_client_open);
    }
}
