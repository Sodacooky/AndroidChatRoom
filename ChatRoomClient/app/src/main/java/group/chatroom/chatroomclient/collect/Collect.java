package group.chatroom.chatroomclient.collect;

public class Collect {

    public Long getCollectTimestamp() {
        return collectTimestamp;
    }

    public void setCollectTimestamp(Long collectTimestamp) {
        this.collectTimestamp = collectTimestamp;
    }

    public String getFromUser() {
        return fromUser;
    }

    public void setFromUser(String fromUser) {
        this.fromUser = fromUser;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public Collect() {
    }

    public Collect(Long collectTimestamp, String fromUser, String content) {
        this.collectTimestamp = collectTimestamp;
        this.fromUser = fromUser;
        this.content = content;
    }

    private Long collectTimestamp;
    private String fromUser;
    private String content;
}
