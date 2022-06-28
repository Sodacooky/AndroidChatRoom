package group.chatroom.chatroomclient.core;

//静态单例类实现各处可调用
public class ChatClientHolder {

    public static ChatClient getInstance() {
        return chatClient;
    }

    private static final ChatClient chatClient = new ChatClient();
}
