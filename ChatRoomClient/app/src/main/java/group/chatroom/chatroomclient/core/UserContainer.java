package group.chatroom.chatroomclient.core;

import com.fasterxml.jackson.databind.JsonNode;

import java.util.LinkedList;
import java.util.List;

/**
 * 内存中储存的用户列表，记得经常更新
 */
public class UserContainer {

    //用户数组JSON
    public void updateUser(JsonNode json) {
        if (!json.isArray()) {
            throw new RuntimeException("应为数组");
        }
        //移除本地的用户，使用远程的填充
        this.userList.clear();
        for (JsonNode oneUser : json) {
            //new
            User user = new User();
            user.setUserId(oneUser.get("userId").asText());
            user.setUserName(oneUser.get("userName").asText());
            user.setAvatarIndex(oneUser.get("avatarIndex").asInt());
            //add
            this.userList.add(user);
        }
    }

    public User getUser(String userId) {
        for (User user : this.userList) {
            if (user.getUserId().equals(userId)) return user;
        }
        return null;
    }

    public boolean isUserExist(String userId) {
        for (User user : this.userList) {
            if (user.getUserId().equals(userId)) return true;
        }
        return false;
    }

    public boolean isUserNameExist(String username) {
        for (User user : this.userList) {
            if (user.getUserName().equals(username)) return true;
        }
        return false;
    }

    public boolean isAvatarExist(Integer avatarIndex) {
        for (User user : this.userList) {
            if (user.getAvatarIndex().equals(avatarIndex)) return true;
        }
        return false;
    }

    public List<User> getUserList() {
        return userList;
    }

    public void setUserInfoList(List<User> userList) {
        this.userList = userList;
    }

    private List<User> userList = new LinkedList<>();
}
