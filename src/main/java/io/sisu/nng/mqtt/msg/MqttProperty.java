package io.sisu.nng.mqtt.msg;

import io.sisu.nng.Nng;
import io.sisu.nng.internal.mqtt.PropertyPointer;

public class MqttProperty implements AutoCloseable {

    private PropertyPointer property;

    public MqttProperty() {
        this.property = Nng.lib().mqtt_property_alloc();
    }

    //TODO append property

    @Override
    public void close() throws Exception {
        Nng.lib().mqtt_property_free(this.property);
    }
}
