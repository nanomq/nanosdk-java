package io.sisu.nng.mqtt.msg;

import com.sun.jna.Pointer;
import io.sisu.nng.Message;
import io.sisu.nng.Nng;
import io.sisu.nng.NngException;
import io.sisu.nng.internal.jna.Size;
import io.sisu.nng.internal.jna.UInt32;
import io.sisu.nng.internal.mqtt.PropertyPointer;
import io.sisu.nng.internal.mqtt.TopicQosPointer;
import io.sisu.nng.internal.mqtt.constants.MqttPacketType;
import io.sisu.nng.mqtt.data.TopicQos;

import java.util.List;

public class SubscribeMsg extends MqttMessage {

    private List<TopicQos> topicQosList;

    public SubscribeMsg() throws NngException {
        super(MqttPacketType.NNG_MQTT_SUBSCRIBE);
    }

    public SubscribeMsg(Message msg) throws NngException {
        super(msg.getMessagePointer());
    }

    public SubscribeMsg(List<TopicQos> topicQosList) throws NngException {
        super(MqttPacketType.NNG_MQTT_SUBSCRIBE);
        this.topicQosList = topicQosList;
        setTopicQosList(topicQosList);
    }

    private TopicQosPointer createTopicQosPointer(List<TopicQos> topicQosList) {
        TopicQosPointer topicQosPointer = Nng.lib().nng_mqtt_topic_qos_array_create(new Size(topicQosList.size()));
        for (int i = 0; i < topicQosList.size(); i++) {
            Nng.lib().nng_mqtt_topic_qos_array_set(topicQosPointer, new Size(i), topicQosList.get(i).getTopic(), topicQosList.get(i).getQos());
        }
        return topicQosPointer;
    }

    public void setTopicQosList(List<TopicQos> topicQosList) {
        this.topicQosList = topicQosList;
        TopicQosPointer topicQosPointer = createTopicQosPointer(topicQosList);
        Nng.lib().nng_mqtt_msg_set_subscribe_topics(super.msg, topicQosPointer, new UInt32(topicQosList.size()));
        Nng.lib().nng_mqtt_topic_qos_array_free(topicQosPointer, new Size(topicQosList.size()));
    }

    public List<TopicQos> getTopicQosList() {
        return this.topicQosList;
    }

}
