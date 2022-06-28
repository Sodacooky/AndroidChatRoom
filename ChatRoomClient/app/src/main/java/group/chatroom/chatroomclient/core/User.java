package group.chatroom.chatroomclient.core;

/**
 * 本地需要使用的用户信息
 */
public class User {

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public Integer getAvatarIndex() {
        return avatarIndex;
    }

    public void setAvatarIndex(Integer avatarIndex) {
        this.avatarIndex = avatarIndex;
    }

    public User() {
    }

    public User(String userId, String userName, Integer avatarIndex) {
        this.userId = userId;
        this.userName = userName;
        this.avatarIndex = avatarIndex;
    }

    private String userId = "null";
    private String userName = "null";
    private Integer avatarIndex = -1;
}
