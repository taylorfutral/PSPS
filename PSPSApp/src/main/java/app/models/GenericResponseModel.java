package app.models;

public class GenericResponseModel {
    int resultCode;
    String message;

    public GenericResponseModel(int resultCode, String message) {
        this.resultCode = resultCode;
        this.message = message;
    }

    public int getResultCode() {
        return resultCode;
    }

    public String getMessage() {
        return message;
    }
}
