package io.sisu.nng.mqtt.msg;

import com.sun.jna.Pointer;
import io.sisu.nng.Nng;
import io.sisu.nng.NngException;
import io.sisu.nng.internal.jna.UInt32ByReference;
import io.sisu.nng.internal.mqtt.BytesPointer;
import io.sisu.nng.internal.mqtt.PropertyPointer;
import io.sisu.nng.internal.mqtt.constants.MqttPacketType;

import java.nio.ByteBuffer;

public class SubackMsg extends MqttMessage {
    public SubackMsg() throws NngException {
        super(MqttPacketType.NNG_MQTT_SUBACK);
    }

    public SubackMsg(Pointer p) throws NngException {
        super(p);
    }

    public ByteBuffer getReturnCodes() {
        UInt32ByReference u32 = new UInt32ByReference();
        BytesPointer bytes = Nng.lib().nng_mqtt_msg_get_suback_return_codes(super.msg, u32);
        int count = u32.getUInt32().intValue();
        if (count == 0) {
            return null;
        }
        return bytes.getPointer().getByteBuffer(0, count);
    }

    public PropertyPointer getProperty() {
        return Nng.lib().nng_mqtt_msg_get_suback_property(super.msg);
    }
}
