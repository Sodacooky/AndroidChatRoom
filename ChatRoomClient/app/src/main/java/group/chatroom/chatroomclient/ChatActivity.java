package group.chatroom.chatroomclient;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

import group.chatroom.chatroomclient.chat.MessageAdapter;
import group.chatroom.chatroomclient.collect.Collect;
import group.chatroom.chatroomclient.collect.CollectDBService;
import group.chatroom.chatroomclient.core.ChatClient;
import group.chatroom.chatroomclient.core.ChatClientHolder;
import group.chatroom.chatroomclient.core.User;

public class ChatActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chat);

        injectRoomStartupData();

        //create background worker
        ListView listView = (ListView) findViewById(R.id.chat_content_list);
        listView.setAdapter(new MessageAdapter(ChatActivity.this));
        this.chatBackgroundWorker = new ChatBackgroundWorker(ChatActivity.this, listView, this);
        this.chatBackgroundWorker.start();

        //set item collect
        listView.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
            @Override
            public boolean onItemLongClick(AdapterView<?> parent, View view, int position, long id) {
                //忽略系统消息
                if (view.findViewById(R.id.system_msg_text) != null) {
                    return true;
                }

                //confirm dialog
                new AlertDialog.Builder(ChatActivity.this)
                        .setTitle("确认")
                        .setMessage("确定要收藏这条内容吗？")
                        .setPositiveButton("确定", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                Collect collect = new Collect();
                                //收藏瞬间时间作为时间戳
                                collect.setCollectTimestamp(Calendar.getInstance().getTimeInMillis());
                                //transfer message from view to collect
                                //content
                                TextView content_text = (TextView) view.findViewById(R.id.content_text);
                                if (content_text == null) {
                                    content_text = (TextView) view.findViewById(R.id.content_text2);
                                }
                                collect.setContent(content_text.getText().toString());
                                //username
                                TextView content_user = (TextView) view.findViewById(R.id.user_name);
                                if (content_user == null) {
                                    content_user = (TextView) view.findViewById(R.id.user_name2);
                                }
                                collect.setFromUser(content_user.getText().toString());
                                //save to db
                                collectDBService.add(collect);
                                //
                                Toast.makeText(ChatActivity.this, "收藏成功", Toast.LENGTH_SHORT).show();
                            }
                        })
                        .setNegativeButton("取消", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                Toast.makeText(ChatActivity.this, "放弃成功", Toast.LENGTH_SHORT).show();
                            }
                        })
                        .create()
                        .show();
                return true;
            }
        });


        //set menu button
        Button menuBtn = this.findViewById(R.id.btn_chat_menu);
        menuBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                View dialogView = LayoutInflater.from(ChatActivity.this).inflate(R.layout.dialog_chat_menu, null);
                //action list dialog
                AlertDialog menuDialog = new AlertDialog.Builder(ChatActivity.this)
                        .setTitle("菜单")
                        .setView(dialogView)
                        .create();
                menuDialog.show();
                //set child button
                setDialogChangeNameBtnListener((Button) dialogView.findViewById(R.id.menu_change_name), menuDialog);
                setDialogShowUserListBtnListener((Button) dialogView.findViewById(R.id.menu_user_list), menuDialog);
                setDialogQuitBtnListener((Button) dialogView.findViewById(R.id.menu_quit), menuDialog);
            }
        });

        //set send button
        Button sendBtn = this.findViewById(R.id.btn_text_send);
        sendBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //get edit text
                EditText textInput = (EditText) ChatActivity.this.findViewById(R.id.text_input);
                //work
                trySendMessage(textInput.getText().toString());
                //flush
                textInput.setText("");
            }
        });
    }

    private void injectRoomStartupData() {
        ChatClient instance = ChatClientHolder.getInstance();
        //roomName
        androidx.appcompat.widget.Toolbar toolbar = (androidx.appcompat.widget.Toolbar) this.findViewById(R.id.toolbar2);
        toolbar.setTitle("房间ID：" + instance.getRoom().getRoomId());
        //userList
        instance.updateUserList();
    }


    private void setDialogShowUserListBtnListener(Button showUserListBtn, AlertDialog parent) {
        showUserListBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //get view
                View userListDialogView = LayoutInflater.from(ChatActivity.this).inflate(R.layout.dialog_userlist, null);
                ListView userListView = (ListView) userListDialogView.findViewById(R.id.userlist_list);
                //transfer to view
                List<User> userList = ChatClientHolder.getInstance().getUserList();
                List<String> usernameList = new ArrayList<>();
                for (User user : userList) usernameList.add(user.getUserName());
                ArrayAdapter<String> newAdapter = new ArrayAdapter<>(ChatActivity.this, android.R.layout.simple_list_item_1, usernameList);
                userListView.setAdapter(null);
                userListView.setAdapter(newAdapter);
                newAdapter.notifyDataSetChanged();
                //make dialog
                new AlertDialog.Builder(ChatActivity.this)
                        .setTitle("用户列表")
                        .setView(userListDialogView)
                        .create()
                        .show();
                //
                parent.dismiss();
            }
        });
    }

    private void setDialogChangeNameBtnListener(Button changeNameBtn, AlertDialog parent) {
        changeNameBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                View dialogView = LayoutInflater.from(ChatActivity.this).inflate(R.layout.dialog_change_name, null);
                //the change name dialog
                new AlertDialog.Builder(ChatActivity.this)
                        .setTitle("更改昵称？")
                        .setView(dialogView)
                        .setPositiveButton("确定", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                EditText editText = (EditText) dialogView.findViewById(R.id.change_name_input);
                                tryUpdateUsername(editText.getText().toString());
                            }
                        })
                        .setNegativeButton("取消", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                            }
                        })
                        .create()
                        .show();
                parent.dismiss();
            }
        });
    }

    private void setDialogQuitBtnListener(Button quitBtn, AlertDialog parent) {
        quitBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                new AlertDialog.Builder(ChatActivity.this)
                        .setTitle("注意")
                        .setMessage("你要退出房间吗？")
                        .setPositiveButton("确定，断开连接", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                //quit room
                                ChatClient instance = ChatClientHolder.getInstance();
                                chatBackgroundWorker.stop();
                                instance.quitRoom();
                                //move to main act
                                Intent intent = new Intent(ChatActivity.this, MainActivity.class)
                                        .setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
                                startActivity(intent);
                            }
                        })
                        .setNegativeButton("取消", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                            }
                        })
                        .create()
                        .show();
                parent.dismiss();
            }
        });
    }


    private void tryUpdateUsername(String newUsername) {
        if (newUsername == null || newUsername.length() < 1) {
            Toast.makeText(ChatActivity.this, "请检查输入", Toast.LENGTH_LONG).show();
        }
        ChatClient instance = ChatClientHolder.getInstance();
        if (instance.updateUserName(newUsername)) {
            Toast.makeText(ChatActivity.this, "成功", Toast.LENGTH_SHORT).show();
        } else {
            Toast.makeText(ChatActivity.this, "失败" + instance.getLastError(), Toast.LENGTH_LONG).show();
        }
    }

    private void trySendMessage(String message) {
        //check
        if (message == null || message.length() < 1) {
            Toast.makeText(ChatActivity.this, "没有输入", Toast.LENGTH_SHORT).show();
            return;
        }
        //execute
        if (ChatClientHolder.getInstance().sendText(message)) {
            Log.i("", "send success");
        } else {
            Toast.makeText(ChatActivity.this, "发送失败", Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK) {
            new AlertDialog.Builder(ChatActivity.this)
                    .setTitle("注意")
                    .setMessage("你要退出房间吗？")
                    .setPositiveButton("确定，断开连接", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            //quit room
                            ChatClient instance = ChatClientHolder.getInstance();
                            chatBackgroundWorker.stop();
                            instance.quitRoom();
                            //move to main act
                            Intent intent = new Intent(ChatActivity.this, MainActivity.class)
                                    .setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
                            startActivity(intent);
                        }
                    })
                    .setNegativeButton("取消", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                        }
                    })
                    .create()
                    .show();
            return true;//block
        }
        return super.onKeyDown(keyCode, event);
    }


    private ChatBackgroundWorker chatBackgroundWorker;

    private final CollectDBService collectDBService = new CollectDBService(ChatActivity.this);
}