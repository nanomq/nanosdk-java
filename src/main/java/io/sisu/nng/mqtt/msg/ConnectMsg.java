package io.sisu.nng.mqtt.msg;

import com.sun.jna.Pointer;
import io.sisu.nng.Nng;
import io.sisu.nng.NngException;
import io.sisu.nng.internal.jna.UInt16;
import io.sisu.nng.internal.jna.UInt32;
import io.sisu.nng.internal.jna.UInt32ByReference;
import io.sisu.nng.internal.mqtt.BytesPointer;
import io.sisu.nng.internal.mqtt.PropertyPointer;
import io.sisu.nng.internal.mqtt.constants.MqttPacketType;

import java.nio.Buffer;
import java.nio.ByteBuffer;

public class ConnectMsg extends MqttMessage {

    public ConnectMsg() throws NngException {
        super(MqttPacketType.NNG_MQTT_CONNECT);
    }

    public void setProtoVersion(int version) {
        Nng.lib().nng_mqtt_msg_set_connect_proto_version(super.msg, (byte) version);
    }

    public void setKeepAlive(short keepAlive) {
        Nng.lib().nng_mqtt_msg_set_connect_keep_alive(super.msg, new UInt16(keepAlive));
    }

    public void setClientId(String clientId) {
        Nng.lib().nng_mqtt_msg_set_connect_client_id(super.msg, clientId);
    }

    public void setUserName(String username) {
        Nng.lib().nng_mqtt_msg_set_connect_user_name(super.msg, username);
    }

    public void setPassword(String password) {
        Nng.lib().nng_mqtt_msg_set_connect_password(super.msg, password);
    }

    public void setCleanSession(boolean cleanSession) {
        Nng.lib().nng_mqtt_msg_set_connect_clean_session(super.msg, cleanSession);
    }

    public void setWillTopic(String topic) {
        Nng.lib().nng_mqtt_msg_set_connect_will_topic(super.msg, topic);
    }

    public void setWillMsg(byte[] data, int len) {
        Nng.lib().nng_mqtt_msg_set_connect_will_msg(super.msg, data, new UInt32(len));
    }

    public void setWillMsg(Pointer data, int len) {
        Nng.lib().nng_mqtt_msg_set_connect_will_msg(super.msg, data, new UInt32(len));
    }

    public void setWillMsg(Buffer data, int len) {
        Nng.lib().nng_mqtt_msg_set_connect_will_msg(super.msg, data, new UInt32(len));
    }

    public void setWillRetain(boolean retain) {
        Nng.lib().nng_mqtt_msg_set_connect_will_retain(super.msg, retain);
    }

    public void setWillQos(byte qos) {
        Nng.lib().nng_mqtt_msg_set_connect_will_qos(super.msg, qos);
    }

    public void setProperty(PropertyPointer property) {
        Nng.lib().nng_mqtt_msg_set_connect_property(super.msg, property);
    }

    String getUserName() {
        return Nng.lib().nng_mqtt_msg_get_connect_user_name(super.msg);
    }

    String getPassword() {
        return Nng.lib().nng_mqtt_msg_get_connect_password(super.msg);
    }

    boolean getCleanSession() {
        return Nng.lib().nng_mqtt_msg_get_connect_clean_session(super.msg);
    }

    int getProtoVersion() {
        return Nng.lib().nng_mqtt_msg_get_connect_proto_version(super.msg);
    }

    short getKeepAlive() {
        UInt16 keepAlvin = Nng.lib().nng_mqtt_msg_get_connect_keep_alive(super.msg);
        return keepAlvin.convert();
    }

    String getClientId() {
        return Nng.lib().nng_mqtt_msg_get_connect_client_id(super.msg);
    }

    String getWillTopic() {
        return Nng.lib().nng_mqtt_msg_get_connect_will_topic(super.msg);
    }

    ByteBuffer getWillMsg() {
        UInt32ByReference u32 = new UInt32ByReference();
        BytesPointer willMsg = Nng.lib().nng_mqtt_msg_get_connect_will_msg(super.msg, u32);
        int len = u32.getUInt32().intValue();
        if (len == 0) {
            return ByteBuffer.allocate(0);
        }
        if (willMsg.getPointer() == Pointer.NULL) {
            return null;
        }
        return willMsg.getPointer().getByteBuffer(0, len);
    }

    boolean getWillRetain() {
        return Nng.lib().nng_mqtt_msg_get_connect_will_retain(super.msg);
    }

    byte getWillQos() {
        return Nng.lib().nng_mqtt_msg_get_connect_will_qos(super.msg);
    }

    PropertyPointer getProperty() {
        return Nng.lib().nng_mqtt_msg_get_connect_property(super.msg);
    }

}
