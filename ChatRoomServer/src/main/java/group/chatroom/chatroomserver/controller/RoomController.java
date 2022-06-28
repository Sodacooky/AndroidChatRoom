package group.chatroom.chatroomserver.controller;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import group.chatroom.chatroomserver.container.RoomContainer;
import group.chatroom.chatroomserver.container.UserContainer;
import group.chatroom.chatroomserver.entity.Room;
import group.chatroom.chatroomserver.entity.User;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Calendar;
import java.util.List;
import java.util.Map;


/**
 * 处理来自用户的消息，并广播
 * 同时广播和处理用户的进入和退出消息
 */
@RestController
@RequestMapping("/room")
public class RoomController {

    @PostMapping("/send_message")
    public Response sendMessage(@RequestBody JsonNode body) {
        //check valid
        if (!body.has("roomId") || !body.has("userId") || !body.has("data")) {
            return new Response()
                    .setSuccess(false)
                    .setMessage("缺少参数");
        }
        //check room exist
        if (!roomContainer.isRoomExist(body.get("roomId").asText())) {
            return new Response()
                    .setSuccess(false)
                    .setMessage("房间不存在");
        }
        Room room = roomContainer.getRoom(body.get("roomId").asText());
        //check is user belong(exist)
        if (!room.getUserContainer().isUserExist(body.get("userId").asText())) {
            return new Response()
                    .setSuccess(false)
                    .setMessage("你不属于此房间或已断开连接");
        }
        //broadcast
        room.getUserContainer().broadcastMessage(body.get("data"));
        //response
        return new Response()
                .setSuccess(true)
                .setMessage("发送成功");
    }

    //其实也是心跳
    @PostMapping("/fetch_message")
    public Response fetchMessage(@RequestBody JsonNode json) {
        //check valid
        if (!json.has("roomId") || !json.has("userId")) {
            return new Response()
                    .setSuccess(false)
                    .setMessage("缺少参数");
        }
        //check room exist
        if (!roomContainer.isRoomExist(json.get("roomId").asText())) {
            return new Response()
                    .setSuccess(false)
                    .setMessage("房间不存在");
        }
        Room room = roomContainer.getRoom(json.get("roomId").asText());
        //check is user belong(exist)
        if (!room.getUserContainer().isUserExist(json.get("userId").asText())) {
            return new Response()
                    .setSuccess(false)
                    .setMessage("你不属于此房间或已断开连接");
        }
        //get caching message, update last heartbeat time
        User user = room.getUserContainer().getUser(json.get("userId").asText());
        List<JsonNode> cachingMessage = user.moveCachingMessage();
        user.setLastHeartbeatTime(Calendar.getInstance().getTime());
        //build response
        return new Response()
                .setSuccess(true)
                .setMessage("你成功了！")
                .setData(cachingMessage);

    }

    @PostMapping("/get_user_list")
    public Response getUserList(@RequestBody JsonNode json) {
        //check valid
        if (!json.has("roomId")) {
            return new Response()
                    .setSuccess(false)
                    .setMessage("缺少参数");
        }
        //check room exist
        if (!roomContainer.isRoomExist(json.get("roomId").asText())) {
            return new Response()
                    .setSuccess(false)
                    .setMessage("房间不存在");
        }
        //build response
        return new Response()
                .setSuccess(true)
                .setMessage("你成功了！（大声）")
                .setData(roomContainer.getRoom(json.get("roomId").asText()).getUserContainer().getUserList());
    }

    @PostMapping("/update_username")
    public Response updateUsername(@RequestBody JsonNode json) {
        //check valid
        if (!json.has("roomId") || !json.has("userId") || !json.has("userName")) {
            return new Response()
                    .setSuccess(false)
                    .setMessage("缺少参数");
        }
        //check room exist
        if (!roomContainer.isRoomExist(json.get("roomId").asText())) {
            return new Response()
                    .setSuccess(false)
                    .setMessage("房间不存在");
        }
        //check is user belong(exist)
        Room room = roomContainer.getRoom(json.get("roomId").asText());
        if (!room.getUserContainer().isUserExist(json.get("userId").asText())) {
            return new Response()
                    .setSuccess(false)
                    .setMessage("你不属于此房间或已断开连接");
        }
        //get user container and check existence
        UserContainer userContainer = room.getUserContainer();
        if (userContainer.isUserNameExist(json.get("userName").asText())) {
            return new Response()
                    .setSuccess(false)
                    .setMessage("该名称已被使用");
        }
        //apply
        User user = userContainer.getUser(json.get("userId").asText());
        String oldName = user.getUserName();
        user.setUserName(json.get("userName").asText());
        //broadcast message
        userContainer.broadcastMessage(objectMapper.convertValue(Map.of("type", "updateInfo"), JsonNode.class));
        userContainer.broadcastMessage(objectMapper.convertValue(
                Map.of("type", "system", "content", oldName + " 更名为 " + user.getUserName()),
                JsonNode.class));
        //
        return new Response()
                .setSuccess(true)
                .setMessage("成功");
    }

    @PostMapping("/update_avatar")
    public Response updateAvatar(@RequestBody JsonNode json) {
        //check valid
        if (!json.has("roomId") || !json.has("userId") || !json.has("avatarIndex")) {
            return new Response()
                    .setSuccess(false)
                    .setMessage("缺少参数");
        }
        //check room exist
        if (!roomContainer.isRoomExist(json.get("roomId").asText())) {
            return new Response()
                    .setSuccess(false)
                    .setMessage("房间不存在");
        }
        Room room = roomContainer.getRoom(json.get("roomId").asText());
        if (!room.getUserContainer().isUserExist(json.get("userId").asText())) {
            return new Response()
                    .setSuccess(false)
                    .setMessage("你不属于此房间或已断开连接");
        }
        //get user container and check existence
        UserContainer userContainer = room.getUserContainer();
        if (userContainer.isAvatarExist(json.get("avatarIndex").asInt())) {
            return new Response()
                    .setSuccess(false)
                    .setMessage("该头像已被使用，非法操作");
        }
        //apply
        User user = userContainer.getUser(json.get("userId").asText());
        user.setAvatarIndex(json.get("avatarIndex").asInt());
        //broadcast message
        userContainer.broadcastMessage(objectMapper.convertValue(Map.of("type", "updateInfo"), JsonNode.class));
        //
        return new Response()
                .setSuccess(true)
                .setMessage("成功");
    }

    @PostMapping("/exit_room")
    public Response exitRoom(@RequestBody JsonNode json) {
        //check valid
        if (!json.has("roomId") || !json.has("userId")) {
            return new Response()
                    .setSuccess(false)
                    .setMessage("缺少参数");
        }
        //check room exist
        if (!roomContainer.isRoomExist(json.get("roomId").asText())) {
            return new Response()
                    .setSuccess(false)
                    .setMessage("房间不存在");
        }
        //check user belong
        Room room = roomContainer.getRoom(json.get("roomId").asText());
        if (!room.getUserContainer().isUserExist(json.get("userId").asText())) {
            return new Response()
                    .setSuccess(false)
                    .setMessage("你不属于此房间或已断开连接");
        }
        //remove user and broadcast message
        room.getUserContainer().removeUser(json.get("userId").asText());
        room.getUserContainer().broadcastMessage(objectMapper.convertValue(Map.of("type", "updateInfo"), JsonNode.class));
        //
        return new Response()
                .setSuccess(true)
                .setMessage("成功");
    }

    private final ObjectMapper objectMapper = new ObjectMapper();
    @Autowired
    private RoomContainer roomContainer;
}
