package app.models;


public class AllTopicsResponseModel {
    private int resultCode;
    private String[] topics;

    public AllTopicsResponseModel(int resultCod, String[] topics) {
        this.resultCode = resultCode;
        this.topics = topics;
    }

    public int getResultCode() {
        return resultCode;
    }

    public String[] getTopics() {
        return topics;
    }
}
