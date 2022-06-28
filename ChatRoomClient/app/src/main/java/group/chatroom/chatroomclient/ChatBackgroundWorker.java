package group.chatroom.chatroomclient;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Looper;
import android.view.View;
import android.widget.ListView;
import android.widget.Toast;

import com.fasterxml.jackson.databind.JsonNode;

import java.util.Calendar;

import group.chatroom.chatroomclient.chat.MessageAdapter;
import group.chatroom.chatroomclient.chat.MyMessage;
import group.chatroom.chatroomclient.core.ChatClient;
import group.chatroom.chatroomclient.core.ChatClientHolder;

public class ChatBackgroundWorker implements Runnable {

    public ChatBackgroundWorker(Context context, View contentListView, Activity activity) {
        this.context = context;
        this.contentListView = contentListView;
        this.activity = activity;
    }

    public void start() {
        //check status
        if (isKeepContinue) return;
        //start, flag set to true
        isKeepContinue = true;
        //create thread
        thread = new Thread(this);
        thread.start();
    }

    public void stop() {
        //check status
        if (!isKeepContinue) return;
        //set flag to false
        isKeepContinue = false;
        //wait
        try {
            thread.join();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void run() {
        Looper.prepare();//？？？？？？？？？？
        //get chat client
        ChatClient instance = ChatClientHolder.getInstance();
        //不是每次失败都是真失败
        int failureCount = 0;
        //每秒发送一个fetchMessage
        while (this.isKeepContinue) {
            JsonNode jsonNode = instance.fetchMessage();
            //是否以外断连
            if (jsonNode == null) {
                //不是每次失败都是真失败
                failureCount++;
                continue;
            }
            //但看看是不是失败太多次了
            if (failureCount > 0) {
                unexpectedQuit();
                break;
            }
            //如果有消息，交给“分发器”处理
            for (JsonNode oneNode : jsonNode) {
                updateDispatcher(oneNode);
            }
            //sleep
            try {
                Thread.sleep(1000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            //reset
            failureCount = 0;
        }
    }

    private void updateDispatcher(JsonNode oneNode) {
        activity.runOnUiThread(new Runnable() {
            @Override
            public void run() {
                //两大消息类型，系统消息（通知）与用户消息（聊天）
                if (oneNode.get("type").asText().equals("updateInfo")) {
                    //更新用户名消息
                    ChatClientHolder.getInstance().updateUserList();
                    //更新用户列表
                } else {
                    //系统广播，房间出入等，与聊天消息
                    addNewMessage(oneNode);
                }
            }
        });
    }

    private void unexpectedQuit() {
        activity.runOnUiThread(new Runnable() {
            @Override
            public void run() {
                Toast.makeText(activity.getApplicationContext(), "意外断开了连接", Toast.LENGTH_LONG).show();
                //set flag
                isKeepContinue = false;
                //move to main act
                Intent intent = new Intent(activity.getApplicationContext(), MainActivity.class)
                        .setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
                activity.startActivity(intent);
            }
        });
    }

    private void addNewMessage(JsonNode jsonNode) {
        //Toast.makeText(this.context, "收到 " + message, Toast.LENGTH_SHORT).show();
        ListView listView = (ListView) contentListView;
        //生成MyMessage
        MyMessage message = new MyMessage();
        //type, {"type":"system"/"message"}
        message.setType(jsonNode.get("type").asText());
        //content
        message.setContent(jsonNode.get("content").asText());
        //如果是系统消息，则没有来源用户
        if (jsonNode.get("type").asText().equals("message")) {
            message.setFromUserId(jsonNode.get("fromUserId").asText());
        }
        //便于时间顺序展示
        message.setTimeStamp(Calendar.getInstance().getTimeInMillis());
        //Item添加到listView
        MessageAdapter adapter = (MessageAdapter) listView.getAdapter();
        adapter.addMessage(message, contentListView);
        adapter.notifyDataSetChanged();
        //滚动！
        listView.setSelection(listView.getCount() - 1);
    }

    //chat activity context
    private Context context;
    //root view of chat activity
    private View contentListView;
    //use to execute work on ui thread
    private Activity activity;
    //thread control
    private Boolean isKeepContinue = Boolean.FALSE;
    //thread
    private Thread thread;

}
