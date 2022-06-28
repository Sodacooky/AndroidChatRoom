package group.chatroom.chatroomserver.container;

import group.chatroom.chatroomserver.entity.Room;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.UUID;

@Component
public class RoomContainer {

    public Room createRoom(String roomName, String roomPassword) {
        //build room
        Room room = new Room();
        room.setRoomId(UUID.randomUUID().toString().replaceAll("-", "").substring(0, 5));
        room.setRoomName(roomName == null ? "聊天室" + room.getRoomId() : roomName);
        room.setRoomPassword(roomPassword);
        //add to list
        this.roomList.add(room);
        //
        LoggerFactory.getLogger(RoomContainer.class).info("创建了房间 {}", room.getRoomId());
        return room;
    }

    public boolean removeRoom(String roomId) {
        //search for user
        boolean success = false;
        Iterator<Room> iterator = this.roomList.iterator();
        while (iterator.hasNext()) {
            Room next = iterator.next();
            if (next.getRoomId().equals(roomId)) {
                LoggerFactory.getLogger(RoomContainer.class).info("销毁了房间 {}", next.getRoomId());
                iterator.remove();
                success = true;
                break;
            }
        }
        return success;
    }

    public boolean isRoomExist(String roomId) {
        for (Room room : this.roomList) {
            if (room.getRoomId().equals(roomId)) return true;
        }
        return false;
    }

    public Room getRoom(String roomId) {
        for (Room room : this.roomList) {
            if (room.getRoomId().equals(roomId)) return room;
        }
        return null;
    }

    public List<Room> getRoomList() {
        return roomList;
    }

    public RoomContainer() {
    }

    private final List<Room> roomList = new ArrayList<>();
}
