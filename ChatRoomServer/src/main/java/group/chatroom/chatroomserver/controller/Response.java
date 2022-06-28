package group.chatroom.chatroomserver.controller;

import org.springframework.stereotype.Component;

@Component
public class Response {

    public Boolean getSuccess() {
        return success;
    }

    public Response setSuccess(Boolean success) {
        this.success = success;
        return this;
    }

    public String getMessage() {
        return message;
    }

    public Response setMessage(String message) {
        this.message = message;
        return this;
    }

    public Object getData() {
        return data;
    }

    public Response setData(Object data) {
        this.data = data;
        return this;
    }

    public Response(Boolean success, String message, Object data) {
        this.success = success;
        this.message = message;
        this.data = data;
    }

    public Response() {
    }

    private Boolean success;
    private String message;
    private Object data;
}
