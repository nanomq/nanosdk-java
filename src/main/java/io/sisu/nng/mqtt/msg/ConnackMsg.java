package io.sisu.nng.mqtt.msg;

import com.sun.jna.Pointer;
import io.sisu.nng.Message;
import io.sisu.nng.Nng;
import io.sisu.nng.NngException;
import io.sisu.nng.internal.mqtt.PropertyPointer;
import io.sisu.nng.internal.mqtt.constants.MqttPacketType;

public class ConnackMsg extends MqttMessage{

    public ConnackMsg() throws NngException {
        super(MqttPacketType.NNG_MQTT_CONNACK);
    }

    public ConnackMsg(Message message) throws NngException {
        super(message.getMessagePointer());
    }

    public byte getReturnCode() {
        return Nng.lib().nng_mqtt_msg_get_connack_return_code(super.msg);
    }

    public byte getFlags() {
        return Nng.lib().nng_mqtt_msg_get_connack_flags(super.msg);
    }

    public PropertyPointer getProperty() {
        return Nng.lib().nng_mqtt_msg_get_connect_property(super.msg);
    }

}
