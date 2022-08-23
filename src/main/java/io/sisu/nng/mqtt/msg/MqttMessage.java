package io.sisu.nng.mqtt.msg;

import com.sun.jna.Pointer;
import io.sisu.nng.Message;
import io.sisu.nng.Nng;
import io.sisu.nng.NngException;
import io.sisu.nng.internal.MessageByReference;
import io.sisu.nng.internal.MessagePointer;
import io.sisu.nng.internal.NngCallback;
import io.sisu.nng.internal.SocketStruct;
import io.sisu.nng.internal.jna.Size;
import io.sisu.nng.internal.jna.UInt16;
import io.sisu.nng.internal.jna.UInt32;
import io.sisu.nng.internal.jna.UInt32ByReference;
import io.sisu.nng.internal.mqtt.*;
import io.sisu.nng.internal.mqtt.constants.MqttPacketType;

import java.nio.Buffer;
import java.nio.ByteBuffer;

public class MqttMessage extends Message {

    protected MessagePointer msg;

    public MqttMessage() throws NngException {
        this(0);
    }

    public MqttMessage(MqttPacketType packetType) throws NngException {
        this(0);
        Nng.lib().nng_mqtt_msg_set_packet_type(this.msg, packetType.getValue());
    }

    public MqttMessage(int size) throws NngException {
        final MessageByReference ref = new MessageByReference();
        int rv = Nng.lib().nng_mqtt_msg_alloc(ref, new Size(size));
        if (rv != 0) {
            final String err = Nng.lib().nng_strerror(rv);
            throw new NngException(err);
        }
        msg = ref.getMessage();
        valid.set(true);
        created.incrementAndGet();
    }

//    public int encode() {
//        return Nng.lib().nng_mqtt_msg_encode(this.msg);
//    }
//
//    public int decode() {
//        return Nng.lib().nng_mqtt_msg_decode(this.msg);
//    }

    public void setPacketType(MqttPacketType packetType) {
        Nng.lib().nng_mqtt_msg_set_packet_type(this.msg, packetType.getValue());
    }

    public MqttPacketType getPacketType() {
        byte pkType = Nng.lib().nng_mqtt_msg_get_packet_type(this.msg);
        return MqttPacketType.getFromValue(pkType);
    }

//
//    void nng_mqtt_msg_set_unsubscribe_topics(MessagePointer msg, TopicPointer topics, UInt32 count);
//
//    TopicPointer nng_mqtt_msg_get_unsubscribe_topics(MessagePointer msg, UInt32ByReference count);
//
//    PropertyPointer nng_mqtt_msg_get_unsubscribe_property(MessagePointer msg);
//
//    void nng_mqtt_msg_set_unsubscribe_property(MessagePointer msg, PropertyPointer property);
//
//    PropertyPointer nng_mqtt_msg_get_disconnect_property(MessagePointer msg);
//
//    void nng_mqtt_msg_set_disconnect_property(MessagePointer msg, PropertyPointer property);
//
//    TopicPointer nng_mqtt_topic_array_create(Size count);
//
//    void nng_mqtt_topic_array_set(TopicPointer topicList, Size index, String topic);
//
//    void nng_mqtt_topic_array_free(TopicPointer topicList, Size count);
//
//    TopicQosPointer nng_mqtt_topic_qos_array_create(Size count);
//
//    void nng_mqtt_topic_qos_array_set(
//            TopicQosPointer topicQosList, Size index, String topic, byte qos);
//
//    void nng_mqtt_topic_qos_array_free(TopicQosPointer topicQosList, Size count);
//
//    int nng_mqtt_set_connect_cb(SocketStruct socket, NngCallback cb, Pointer arg);
//
//    int nng_mqtt_set_disconnect_cb(SocketStruct socket, NngCallback cb, Pointer arg);
//
//    void nng_mqtt_msg_set_disconnect_reason_code(MessagePointer msgmsg, byte reason_code);
//
//    UInt32 get_mqtt_properties_len(PropertyPointer property);
//
//    int mqtt_property_free(PropertyPointer property);
//
//    void mqtt_property_foreach(PropertyPointer property, NngCallback cb);
//
//    int mqtt_property_dup(PropertyPointerByReference dup, PropertyPointer src);
//
//    PropertyPointer mqtt_property_pub_by_will(PropertyPointer willProperty);
//
//    PropertyPointer mqtt_property_alloc();
//
//    PropertyPointer mqtt_property_set_value_u8(byte prop_id, byte value);
//
//    PropertyPointer mqtt_property_set_value_u16(byte prop_id, UInt16 value);
//
//    PropertyPointer mqtt_property_set_value_u32(byte prop_id, UInt32 value);
//
//    PropertyPointer mqtt_property_set_value_varint(byte prop_id, UInt32 value);
//
//    PropertyPointer mqtt_property_set_value_binary(byte prop_id, byte[] value, UInt32 len, boolean copy_value);
//
//    PropertyPointer mqtt_property_set_value_binary(byte prop_id, ByteBuffer value, UInt32 len, boolean copy_value);
//
//    PropertyPointer mqtt_property_set_value_binary(byte prop_id, Pointer value, UInt32 len, boolean copy_value);
//
//    PropertyPointer mqtt_property_set_value_str(byte prop_id, String value, UInt32 len, boolean copy_value);
//
//    PropertyPointer mqtt_property_set_value_strpair(byte prop_id, String key, UInt32 key_len, String value, UInt32 value_len, boolean copy_value);
//
//    byte mqtt_property_get_value_type(byte prop_id);
//
//    void mqtt_property_append(PropertyPointer propertyList, PropertyPointer last);

}
