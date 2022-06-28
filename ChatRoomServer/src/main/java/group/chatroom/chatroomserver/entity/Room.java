package group.chatroom.chatroomserver.entity;

import group.chatroom.chatroomserver.container.UserContainer;

public class Room {

    public String getRoomId() {
        return roomId;
    }

    public void setRoomId(String roomId) {
        this.roomId = roomId;
    }

    public String getRoomPassword() {
        return roomPassword;
    }

    public void setRoomPassword(String roomPassword) {
        this.roomPassword = roomPassword;
    }

    public String getRoomName() {
        return roomName;
    }

    public void setRoomName(String roomName) {
        this.roomName = roomName;
    }

    public UserContainer getUserContainer() {
        return userContainer;
    }

    private String roomId = "null";
    private String roomPassword = "null";
    private String roomName = "聊天室";
    private final UserContainer userContainer = new UserContainer();
}
