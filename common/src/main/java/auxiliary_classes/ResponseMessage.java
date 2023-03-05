package auxiliary_classes;

import java.io.Serializable;

public class ResponseMessage<T> implements Serializable {
    private String typeName;
    private T responseData;


    public ResponseMessage(String typeName, T commandData) {
        this.typeName = typeName;
        this.responseData = commandData;
    }

    public String getTypeName() {
        return typeName;
    }

    public T getResponseData() {
        return responseData;
    }

    public void setTypeName(String typeName) {
        this.typeName = typeName;
    }

    public void setResponseData(T responseData) {
        this.responseData = responseData;
    }
}
