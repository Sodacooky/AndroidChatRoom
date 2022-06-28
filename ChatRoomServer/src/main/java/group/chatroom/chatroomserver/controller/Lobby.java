package group.chatroom.chatroomserver.controller;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import group.chatroom.chatroomserver.container.RoomContainer;
import group.chatroom.chatroomserver.entity.Room;
import group.chatroom.chatroomserver.entity.User;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

/**
 * 处理房间创建和连接
 */
@RestController
@RequestMapping("/lobby")
public class Lobby {

    @PostMapping("/create_room")
    public Response createRoom(@RequestBody JsonNode body) {
        //check valid
        if (!body.has("roomPassword")) {
            return new Response()
                    .setSuccess(false)
                    .setMessage("缺少参数");
        }
        //check password
        if (body.get("roomPassword") == null || body.get("roomPassword").asText().length() < 4) {
            return new Response()
                    .setSuccess(false)
                    .setMessage("没有设置密码或密码少于4位");
        }
        //create room
        Room room = this.roomContainer.createRoom(body.get("roomName").asText(), body.get("roomPassword").asText());
        //create user
        User user = room.getUserContainer().addUser();
        room.getUserContainer().broadcastMessage(objectMapper.convertValue(Map.of("type", "updateInfo"), JsonNode.class));
        //return response
        return new Response()
                .setSuccess(true)
                .setMessage("成功创建房间")
                .setData(Map.of("user", user, "room", room));
    }

    @PostMapping("/join_room")
    public Response joinRoom(@RequestBody JsonNode body) {
        //check valid
        if (!body.has("roomId") || !body.has("roomPassword")) {
            return new Response()
                    .setSuccess(false)
                    .setMessage("缺少参数");
        }
        //check room
        if (!roomContainer.isRoomExist(body.get("roomId").asText())) {
            return new Response()
                    .setSuccess(false)
                    .setMessage("房间不存在");
        }
        //check password
        Room room = roomContainer.getRoom(body.get("roomId").asText());
        if (!room.getRoomPassword().equals(body.get("roomPassword").asText())) {
            return new Response()
                    .setSuccess(false)
                    .setMessage("密码错误");
        }
        //create user
        User user = room.getUserContainer().addUser();
        room.getUserContainer().broadcastMessage(objectMapper.convertValue(Map.of("type", "updateInfo"), JsonNode.class));
        //return
        return new Response()
                .setSuccess(true)
                .setMessage("成功加入房间")
                .setData(Map.of("user", user, "room", room));
    }

    @GetMapping("/get_room_list")
    public List<Room> getRoomList() {
        //去除密码属性
        List<Room> roomList = roomContainer.getRoomList();
        roomList.forEach(room -> room.setRoomPassword("******"));
        //
        return roomList;
    }


    private ObjectMapper objectMapper = new ObjectMapper();
    @Autowired
    private RoomContainer roomContainer;
}
