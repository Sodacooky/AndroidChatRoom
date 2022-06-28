package group.chatroom.chatroomclient.chat;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

import group.chatroom.chatroomclient.R;
import group.chatroom.chatroomclient.core.ChatClient;
import group.chatroom.chatroomclient.core.ChatClientHolder;
import group.chatroom.chatroomclient.core.User;

public class MessageAdapter extends BaseAdapter {

    private final List<MyMessage> data = new ArrayList<>();
    private Context context;

    public MessageAdapter(Context context) {
        this.context = context;
    }

    public void addMessage(MyMessage message, View root) {
        //add to list
        this.data.add(message);
//        //notice listview to update
//        ListView contentListView = (ListView) root.findViewById(R.id.chat_content_list);
//        contentListView.deferNotifyDataSetChanged();
    }

    @Override
    public int getCount() {
        return data.size();
    }

    @Override
    public Object getItem(int position) {
        return data.get(position);
    }

    @Override
    public long getItemId(int position) {
        return data.get(position).getTimeStamp();
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        //get data
        MyMessage now = (MyMessage) getItem(position);
        LayoutInflater layoutInflater = LayoutInflater.from(this.context);
        //
        if (now.getType().equals("system")) {
            //system notice
            View sysMsgLayoutView = layoutInflater.inflate(R.layout.activity_chat_system_comment, null);
            TextView textView = (TextView) sysMsgLayoutView.findViewById(R.id.system_msg_text);
            textView.setText(now.getContent());
            return sysMsgLayoutView;
        } else if (now.getType().equals("message")) {
            //message
            //item layout
            View toAddLayoutView = null;
            //child view
            TextView content = null;
            TextView username = null;
            //left of right
            if (now.getFromUserId().equals(ChatClientHolder.getInstance().getUser().getUserId())) {
                //self
                toAddLayoutView = layoutInflater.inflate(R.layout.activity_chat_right_comment, null);
                content = (TextView) toAddLayoutView.findViewById(R.id.content_text2);
                username = (TextView) toAddLayoutView.findViewById(R.id.user_name2);
            } else {
                //other
                toAddLayoutView = layoutInflater.inflate(R.layout.activity_chat_left_comment, null);
                content = (TextView) toAddLayoutView.findViewById(R.id.content_text);
                username = (TextView) toAddLayoutView.findViewById(R.id.user_name);
            }
            //set view content
            ChatClient instance = ChatClientHolder.getInstance();
            User user = instance.getRoom().getUserContainer().getUser(now.getFromUserId());
            if (user == null) {
                //避免user list还未更新
                String tempUsername = "用户" + now.getFromUserId().substring(0, 7) + " 加载中...";
                username.setText(tempUsername);
            } else {
                username.setText(user.getUserName());
            }
            content.setText(now.getContent());
            return toAddLayoutView;
        }
        return null;
    }
}
