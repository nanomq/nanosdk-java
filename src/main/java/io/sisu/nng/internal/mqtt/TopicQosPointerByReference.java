package io.sisu.nng.internal.mqtt;

import com.sun.jna.Pointer;
import io.sisu.nng.internal.NngPointerByReference;

public class TopicQosPointerByReference extends NngPointerByReference {
    public TopicQosPointerByReference() {
    }

    public TopicQosPointerByReference(Pointer p) {
        super(p);
    }

    public TopicQosPointer getTopicQosPointer() {
        TopicQosPointer topicQosPointer = new TopicQosPointer();
        topicQosPointer.setPointer(getValue());
        return topicQosPointer;
    }
}
