package group.chatroom.chatroomserver.scheduler;

import group.chatroom.chatroomserver.container.RoomContainer;
import group.chatroom.chatroomserver.entity.Room;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;

/**
 * 定时清理没有人的房间
 */
@Component
public class RoomCloseScheduler {

    @Scheduled(cron = "0 */10 * * * *")
    public void closedEmptyRoomSchedule() {
        removeRoomByList(findEmptyRoom());
        LoggerFactory.getLogger(RoomCloseScheduler.class).info("关闭了一些空的房间");
    }

    private List<Room> findEmptyRoom() {
        List<Room> emptyRoomList = new ArrayList<>();
        for (Room room : roomContainer.getRoomList()) {
            if (room.getUserContainer().getUserList().isEmpty()) {
                emptyRoomList.add(room);
            }
        }
        return emptyRoomList;
    }

    private void removeRoomByList(List<Room> emptyRoomList) {
        for (Room room : emptyRoomList) {
            roomContainer.removeRoom(room.getRoomId());
        }
    }

    @Autowired
    private RoomContainer roomContainer;
}
