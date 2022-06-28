package group.chatroom.chatroomserver.scheduler;

import group.chatroom.chatroomserver.container.RoomContainer;
import group.chatroom.chatroomserver.entity.Room;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

/**
 * 定时检查每个房间的每个用户的心跳时间，
 * 如果太久没有心跳，那么把他remove并且广播离开房间消息
 */
@Component
public class UserCleanScheduler {

    @Scheduled(cron = "0 */2 * * * *")
    public void cleanUserSchedule() {
        for (Room room : roomContainer.getRoomList()) {
            room.getUserContainer().kickDisconnected();
        }
    }


    @Autowired
    private RoomContainer roomContainer;

}
