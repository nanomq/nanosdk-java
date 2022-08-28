package io.sisu.nng.mqtt.data;

import lombok.*;

public class TopicQos {
    private String topic;
    private byte qos;


    public String getTopic() {
        return topic;
    }

    public void setTopic(String topic) {
        this.topic = topic;
    }

    public byte getQos() {
        return qos;
    }

    public void setQos(byte qos) {
        this.qos = qos;
    }

    public TopicQos(String topic, byte qos) {
        this.topic = topic;
        this.qos = qos;
    }

    public TopicQos() {
    }
}
