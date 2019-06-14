package app.models;

import com.fasterxml.jackson.annotation.JsonProperty;

public class TopicRequestModel {
    private String topic;

    public TopicRequestModel(@JsonProperty(value = "topic", required = true) String topic) {
        this.topic = topic;
    }

    public String getTopic() {
        return topic;
    }
}
