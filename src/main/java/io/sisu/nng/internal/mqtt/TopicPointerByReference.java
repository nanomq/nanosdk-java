package io.sisu.nng.internal.mqtt;

import com.sun.jna.Pointer;
import io.sisu.nng.internal.NngPointerByReference;

public class TopicPointerByReference extends NngPointerByReference {

    public TopicPointerByReference() {
    }

    public TopicPointerByReference(Pointer p) {
        super(p);
    }

    public TopicPointer getTopicPointer() {
        TopicPointer topicPointer = new TopicPointer();
        topicPointer.setPointer(getValue());
        return topicPointer;
    }
}
