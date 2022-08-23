package io.sisu.nng.mqtt.msg;

import com.sun.jna.Pointer;
import io.sisu.nng.Nng;
import io.sisu.nng.NngException;
import io.sisu.nng.internal.jna.UInt32;
import io.sisu.nng.internal.jna.UInt32ByReference;
import io.sisu.nng.internal.mqtt.BytesPointer;
import io.sisu.nng.internal.mqtt.PropertyPointer;
import io.sisu.nng.internal.mqtt.constants.MqttPacketType;

import java.nio.ByteBuffer;

public class PublishMsg extends MqttMessage {

    public PublishMsg() throws NngException {
        super(MqttPacketType.NNG_MQTT_PUBLISH);
    }

    public void setQos(byte qos) {
        Nng.lib().nng_mqtt_msg_set_publish_qos(super.msg, qos);
    }

    public byte getQos() {
        return Nng.lib().nng_mqtt_msg_get_publish_qos(super.msg);
    }

    public void setRetain(boolean retain) {
        Nng.lib().nng_mqtt_msg_set_publish_retain(super.msg, retain);
    }

    public boolean getRetain() {
        return Nng.lib().nng_mqtt_msg_get_publish_retain(super.msg);
    }

    public void setDup(boolean dup) {
        Nng.lib().nng_mqtt_msg_set_publish_dup(super.msg, dup);
    }

    boolean getDup() {
        return Nng.lib().nng_mqtt_msg_get_publish_dup(super.msg);
    }

    public int setTopic(String topic) throws NngException {
        int rv = Nng.lib().nng_mqtt_msg_set_publish_topic(super.msg, topic);
        if (rv != 0) {
            String err = String.format("set topic failed: %s", topic);
            throw new NngException(err);
        }
        return rv;
    }

    public String getTopic() {
        UInt32ByReference u32 = new UInt32ByReference();
        String topic = Nng.lib().nng_mqtt_msg_get_publish_topic(super.msg, u32);
        int len = u32.getUInt32().intValue();
        return topic.substring(0, len);
    }

    public void setPayload(byte[] payload, long len) {
        Nng.lib().nng_mqtt_msg_set_publish_payload(super.msg, payload, new UInt32(len));
    }

    public void setPayload(ByteBuffer payload, long len) {
        Nng.lib().nng_mqtt_msg_set_publish_payload(super.msg, payload, new UInt32(len));
    }

    public void setPayload(Pointer payload, long len) {
        Nng.lib().nng_mqtt_msg_set_publish_payload(super.msg, payload, new UInt32(len));
    }

    public ByteBuffer getPayload() {
        UInt32ByReference u32 = new UInt32ByReference();
        BytesPointer payload = Nng.lib().nng_mqtt_msg_get_publish_payload(super.msg, u32);
        int len = u32.getUInt32().intValue();
        if (len == 0) {
            return null;
        }
        return payload.getPointer().getByteBuffer(0, len);
    }

    public PropertyPointer getProperty() {
        return Nng.lib().nng_mqtt_msg_get_publish_property(super.msg);
    }

    public void setProperty(PropertyPointer property) {
        Nng.lib().nng_mqtt_msg_set_publish_property(super.msg, property);
    }

}
