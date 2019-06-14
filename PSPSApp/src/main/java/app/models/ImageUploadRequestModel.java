package app.models;

import com.fasterxml.jackson.annotation.JsonProperty;

public class ImageUploadRequestModel {
    private String[] topics;
    private String image;

    public ImageUploadRequestModel( @JsonProperty(value = "topics", required = true) String topics[],
                                    @JsonProperty(value = "image", required = true) String image) {
        this.topics = topics;
        this.image = image;
    }

    public String getImage() {
        return image;
    }

    public void setImage(String image) {
        this.image = image;
    }

    public String[] getTopics() {
        return topics;
    }

    public void setTopics(String[] topics) {
        this.topics = topics;
    }
}
