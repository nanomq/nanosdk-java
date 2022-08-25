package io.sisu.nng.mqtt.data;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class TopicQos {
    private String topic;
    private byte qos;
}
