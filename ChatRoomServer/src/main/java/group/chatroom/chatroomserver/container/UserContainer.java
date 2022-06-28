package group.chatroom.chatroomserver.container;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import group.chatroom.chatroomserver.entity.User;
import org.slf4j.LoggerFactory;

import java.util.*;

/**
 * 一个房间的用户
 */
public class UserContainer {

    /**
     * 应该放在线程或定时器中，定期把没有心跳的用户remove
     *
     * @return removed user
     */
    public List<User> kickDisconnected() {
        //get 2 mins ago
        Calendar calendar = Calendar.getInstance();
        calendar.add(Calendar.MINUTE, -2);
        Date twoMinAgo = calendar.getTime();
        //removed user
        List<User> removedUser = new ArrayList<>();
        //
        for (Iterator<User> iter = this.userList.iterator(); iter.hasNext(); ) {
            User user = iter.next();
            if (user.getLastHeartbeatTime().before(twoMinAgo)) {
                LoggerFactory.getLogger(UserContainer.class).info("失去心跳用户 {}", user.getUserId());
                broadcastMessage(new ObjectMapper().convertValue(Map.of("type", "updateInfo"), JsonNode.class));
                broadcastMessage(new ObjectMapper().
                        convertValue(Map.of("type", "system", "content", user.getUserName() + "离开了房间(超时)"),
                                JsonNode.class));
                iter.remove();
            }
        }
        return removedUser;
    }

    /**
     * 并无实际发送消息，只是把消息放到每个用户的cachingMessage中，等待下次心跳取走
     */
    public void broadcastMessage(JsonNode message) {
        for (User user : this.userList) {
//            //跳过发送人
//            if (message.get("type").asText().equals("message"))
//                if (message.get("userId").asText().equals(user.getUserId()))
//                    continue;
            //不跳过，因为客户端也从服务器端获取自己发送的消息更简单
            user.appendCachingMessage(message);
        }
    }

    public User addUser() {
        //build user
        User user = new User();
        user.setUserId(UUID.randomUUID().toString().replaceAll("-", ""));
        user.setUserName("用户" + user.getUserId().substring(0, 5));
        user.setAvatarIndex(this.getAvailableAvatarIndex());
        //insert into list
        this.userList.add(user);
        //broadcast message
        this.broadcastMessage(new ObjectMapper().
                convertValue(Map.of("type", "system", "content", user.getUserName() + "加入了房间"),
                        JsonNode.class));
        //
        LoggerFactory.getLogger(UserContainer.class).info("用户加入 {}", user.getUserId());
        return user;
    }

    public boolean removeUser(String userId) {
        //search for user
        boolean success = false;
        Iterator<User> iterator = this.userList.iterator();
        while (iterator.hasNext()) {
            User next = iterator.next();
            if (next.getUserId().equals(userId)) {
                this.broadcastMessage(new ObjectMapper().
                        convertValue(Map.of("type", "system", "content", next.getUserName() + "离开了房间"),
                                JsonNode.class));
                LoggerFactory.getLogger(UserContainer.class).info("用户离开 {}", next.getUserId());
                iterator.remove();
                success = true;
                break;
            }
        }
        return success;
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

    private int getAvailableAvatarIndex() {
        //fill 0-55
        Set<Integer> availableSet = new HashSet<>();
        for (int i = 0; i != 56; i++) availableSet.add(i);
        //fill used
        Set<Integer> existSet = new HashSet<>();
        for (User user : userList) existSet.add(user.getAvatarIndex());
        //sub
        availableSet.removeAll(existSet);
        //random
        return (Integer) availableSet.toArray()[new Random().nextInt(availableSet.size())];
    }

    private List<User> userList = new ArrayList<>();
}
