package group.chatroom.chatroomserver.entity;

import com.fasterxml.jackson.databind.JsonNode;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

public class User {

    public Integer getAvatarIndex() {
        return avatarIndex;
    }

    public void setAvatarIndex(Integer avatarIndex) {
        this.avatarIndex = avatarIndex;
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public String getUserName() {
        return userName;
    }

    public Date getLastHeartbeatTime() {
        return lastHeartbeatTime;
    }

    public void setLastHeartbeatTime(Date lastHeartbeatTime) {
        this.lastHeartbeatTime = lastHeartbeatTime;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public List<JsonNode> moveCachingMessage() {
        //copy
        List<JsonNode> copy = new ArrayList<>(this.cachingMessage);
        //clean the old
        this.cachingMessage.clear();
        //
        return copy;
    }

    public void clearCachingMessage() {
        this.cachingMessage.clear();
    }

    public void appendCachingMessage(JsonNode newMessage) {
        this.cachingMessage.add(newMessage);
    }

    private Integer avatarIndex = -1;
    private String userId = "null";
    private String userName = "null";
    private final List<JsonNode> cachingMessage = new ArrayList<>();
    private Date lastHeartbeatTime = Calendar.getInstance().getTime();
}
