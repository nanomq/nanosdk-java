package io.sisu.nng.mqtt.msg;

import io.sisu.nng.Nng;
import io.sisu.nng.NngException;
import io.sisu.nng.internal.jna.UInt32;
import io.sisu.nng.internal.mqtt.PropertyPointer;
import io.sisu.nng.internal.mqtt.TopicQosPointer;
import io.sisu.nng.internal.mqtt.constants.MqttPacketType;

public class SubscribeMsg extends MqttMessage {
    public SubscribeMsg() throws NngException {
        super(MqttPacketType.NNG_MQTT_SUBSCRIBE);
    }

    public void setTopics(TopicQosPointer topicList, int count) {
        Nng.lib().nng_mqtt_msg_set_subscribe_topics(super.msg, topicList, new UInt32(count));
    }

    public void setProperty(PropertyPointer property) {
        Nng.lib().nng_mqtt_msg_set_subscribe_property(super.msg, property);
    }
}
