package group.chatroom.chatroomclient.core;

import com.fasterxml.jackson.databind.JsonNode;

import java.util.List;
import java.util.Map;

public class ChatClient {

    /**
     * 尝试加入房间，成功则会更新储存的Room与User
     *
     * @param roomId       房间ID
     * @param roomPassword 房间密码
     * @return 是否成功
     */
    public boolean tryJoinRoom(String roomId, String roomPassword) {
        JsonNode resp = HttpHelper.post("/lobby/join_room",
                Map.of("roomId", roomId, "roomPassword", roomPassword));
        if (resp == null) {
            this.lastError = "连接失败";
            return false;
        }
        //check status
        if (!resp.has("success") || !resp.get("success").asBoolean()) {
            this.lastError = resp.get("message").asText();
            return false;
        }
        //fill roomInfo and userInfo
        this.user = new User();
        this.room = new Room();
        this.user.setUserId(resp.get("data").get("user").get("userId").asText());
        this.user.setUserName(resp.get("data").get("user").get("userName").asText());
        this.user.setAvatarIndex(resp.get("data").get("user").get("avatarIndex").asInt());
        this.room.setRoomId(resp.get("data").get("room").get("roomId").asText());
        this.room.setRoomName(resp.get("data").get("room").get("roomName").asText());
        //
        return true;
    }

    /**
     * 尝试创建房间，成功则会更新储存的Room与User
     *
     * @param roomPassword 预期的房间密码
     * @return 是否成功
     */
    public boolean tryCreateRoom(String roomPassword) {
        JsonNode resp = HttpHelper.post("/lobby/create_room",
                Map.of("roomName", "roomName", "roomPassword", roomPassword));
        if (resp == null) {
            this.lastError = "连接失败";
            return false;
        }
        //check status
        if (!resp.has("success") || !resp.get("success").asBoolean()) {
            this.lastError = resp.get("message").asText();
            return false;
        }
        //fill roomInfo and userInfo
        this.user = new User();
        this.room = new Room();
        this.user.setUserId(resp.get("data").get("user").get("userId").asText());
        this.user.setUserName(resp.get("data").get("user").get("userName").asText());
        this.user.setAvatarIndex(resp.get("data").get("user").get("avatarIndex").asInt());
        this.room.setRoomId(resp.get("data").get("room").get("roomId").asText());
        this.room.setRoomName(resp.get("data").get("room").get("roomName").asText());
        //return
        return true;
    }

    /**
     * 获取当前所在房间的用户列表，获取成功后会更新内部储存的Room的UserContainer
     *
     * @return 是否成功
     */
    public boolean updateUserList() {
        JsonNode resp = HttpHelper.post("/room/get_user_list", Map.of("roomId", this.room.getRoomId()));
        if (resp == null) {
            this.lastError = "连接失败";
            return false;
        }
        //check status
        if (!resp.has("success") || !resp.get("success").asBoolean()) {
            this.lastError = resp.get("message").asText();
            return false;
        }
        //fill userInfo
        UserContainer userContainer = this.room.getUserContainer();
        userContainer.updateUser(resp.get("data"));
        //return
        return true;
    }

    public List<User> getUserList() {
        return this.room.getUserContainer().getUserList();
    }

    public boolean sendText(String text) {
        JsonNode resp = HttpHelper.post("/room/send_message",
                Map.of("roomId", this.room.getRoomId(),
                        "userId", this.user.getUserId(),
                        "data", Map.of("type", "message", "content", text, "fromUserId", this.user.getUserId())));
        if (resp == null) {
            this.lastError = "连接失败";
            return false;
        }
        //check status
        if (!resp.has("success") || !resp.get("success").asBoolean()) {
            this.lastError = resp.get("message").asText();
            return false;
        } else {
            return true;
        }
    }

    //返回的是一个message array json
    public JsonNode fetchMessage() {
        JsonNode resp = HttpHelper.post("/room/fetch_message",
                Map.of("roomId", this.room.getRoomId(), "userId", this.user.getUserId()));
        if (resp == null) {
            this.lastError = "连接失败";
            return null;
        }
        //check status
        if (!resp.has("success") || !resp.get("success").asBoolean()) {
            this.lastError = resp.get("message").asText();
            return null;
        } else {
            return resp.get("data");
        }
    }

    public boolean updateAvatar(Integer newAvatarIndex) {
        JsonNode resp = HttpHelper.post("/room/update_avatar",
                Map.of("roomId", this.room.getRoomId(),
                        "userId", this.user.getUserId(),
                        "avatarIndex", newAvatarIndex));
        if (resp == null) {
            this.lastError = "连接失败";
            return false;
        }
        //check status
        if (!resp.has("success") || !resp.get("success").asBoolean()) {
            this.lastError = resp.get("message").asText();
            return false;
        } else {
            return true;
        }
    }

    public boolean updateUserName(String newUserName) {
        JsonNode resp = HttpHelper.post("/room/update_username",
                Map.of("roomId", this.room.getRoomId(),
                        "userId", this.user.getUserId(),
                        "userName", newUserName));
        if (resp == null) {
            this.lastError = "连接失败";
            return false;
        }
        //check status
        if (!resp.has("success") || !resp.get("success").asBoolean()) {
            this.lastError = resp.get("message").asText();
            return false;
        } else {
            return true;
        }
    }

    public void quitRoom() {
        //send quit request
        JsonNode resp = HttpHelper.post("/room/exit_room",
                Map.of("roomId", this.room.getRoomId(),
                        "userId", this.user.getUserId()));
        if (resp == null) {
            this.lastError = "连接失败";
            return;
        }
        if (!resp.has("success") || !resp.get("success").asBoolean()) {
            this.lastError = resp.get("message").asText();
            return;
        }
        //reset room and user
        this.room = null;
        this.user = null;
    }

    public String getLastError() {
        return this.lastError;
    }

    public Room getRoom() {
        return this.room;
    }

    public User getUser() {
        return this.user;
    }


    //client side info
    private Room room = null;
    private User user = null;
    //last error message
    private String lastError = "";
}
