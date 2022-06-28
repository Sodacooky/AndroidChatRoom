package group.chatroom.chatroomclient;

import android.app.AlertDialog;
import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.os.StrictMode;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import group.chatroom.chatroomclient.collect.CollectDBService;
import group.chatroom.chatroomclient.collect.CollectDataInjectAdapter;
import group.chatroom.chatroomclient.core.ChatClient;
import group.chatroom.chatroomclient.core.ChatClientHolder;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        //allow socket/http on main thread
        StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();
        StrictMode.setThreadPolicy(policy);

        //startup data
        injectCollectListData();

        //listView remove callback
        ListView listView = this.findViewById(R.id.collect_list);
        listView.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
            @Override
            public boolean onItemLongClick(AdapterView<?> parent, View view, int position, long id) {
                ListView self = (ListView) parent;
                //菜单
                View operationDialogView = LayoutInflater.from(MainActivity.this).inflate(R.layout.dialog_collect_operation, null);
                AlertDialog collectOperationMenuDialog = new AlertDialog.Builder(MainActivity.this)
                        .setTitle("选择操作")
                        .setView(operationDialogView)
                        .create();
                collectOperationMenuDialog.show();
                Button copyCollectBtn = (Button) operationDialogView.findViewById(R.id.btn_copy_collect);
                Button deleteCollectBtn = (Button) operationDialogView.findViewById(R.id.btn_delete_collect);
                //copy
                copyCollectBtn.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        try {
                            //获取要复制的文本
                            TextView textView = (TextView) view.findViewById(R.id.collect_text);
                            String toCopy = textView.getText().toString();
                            //剪贴板操作
                            ClipboardManager cm = (ClipboardManager) MainActivity.this.getSystemService(CLIPBOARD_SERVICE);
                            ClipData mClipData = ClipData.newPlainText("收藏内容", toCopy);
                            cm.setPrimaryClip(mClipData);
                            //提示
                            Toast.makeText(MainActivity.this, "复制成功", Toast.LENGTH_LONG).show();
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    }
                });
                //delete
                deleteCollectBtn.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        //dialog confirm
                        new AlertDialog.Builder(MainActivity.this)
                                .setTitle("确认？")
                                .setMessage("真的要删除这条聊天记录收藏吗？")
                                .setPositiveButton("确定", new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialog, int which) {
                                        collectDBService.remove(self.getItemIdAtPosition(position));
                                        injectCollectListData();
                                        collectOperationMenuDialog.dismiss();
                                        Toast.makeText(MainActivity.this, "删除成功", Toast.LENGTH_SHORT).show();
                                    }
                                })
                                .setNegativeButton("取消", new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialog, int which) {
                                        Toast.makeText(MainActivity.this, "放弃了删除", Toast.LENGTH_SHORT).show();
                                    }
                                })
                                .create()
                                .show();
                    }
                });
                return true;
            }
        });

        //join room button
        Button joinRoomBtn = this.findViewById(R.id.btn_join_room);
        joinRoomBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                View dialogView = LayoutInflater.from(MainActivity.this).inflate(R.layout.dialog_join_room, null);
                //dialog
                new AlertDialog.Builder(MainActivity.this)
                        .setTitle("加入房间")
                        .setView(dialogView)
                        .setPositiveButton("连接", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                EditText roomIdInput = dialogView.findViewById(R.id.join_room_id);
                                EditText roomPwdInput = dialogView.findViewById(R.id.join_room_password);
                                tryJoinRoom(roomIdInput.getText().toString(), roomPwdInput.getText().toString());
                            }
                        })
                        .setNegativeButton("取消", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                Toast.makeText(MainActivity.this, "放弃加入房间", Toast.LENGTH_SHORT).show();
                            }
                        })
                        .create()
                        .show();
            }
        });

        //create room button
        Button createRoomBtn = this.findViewById(R.id.btn_create_room);
        createRoomBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                View dialogView = LayoutInflater.from(MainActivity.this).inflate(R.layout.dialog_create_room, null);
                //dialog
                new AlertDialog.Builder(MainActivity.this)
                        .setTitle("创建房间")
                        .setView(dialogView)
                        .setPositiveButton("连接", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                EditText roomPwdInput = dialogView.findViewById(R.id.create_room_password);
                                tryCreateRoom(roomPwdInput.getText().toString());
                            }
                        })
                        .setNegativeButton("取消", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                Toast.makeText(MainActivity.this, "放弃创建房间", Toast.LENGTH_SHORT).show();
                            }
                        })
                        .create()
                        .show();
            }
        });


    }

    private void tryJoinRoom(String roomId, String roomPassword) {
        ChatClient instance = ChatClientHolder.getInstance();
        if (instance.tryJoinRoom(roomId, roomPassword)) {
            Toast.makeText(MainActivity.this, "加入房间成功", Toast.LENGTH_SHORT).show();
            startActivity(new Intent(MainActivity.this, ChatActivity.class));
        } else {
            Toast.makeText(MainActivity.this, instance.getLastError(), Toast.LENGTH_LONG).show();
        }
    }

    private void tryCreateRoom(String roomPassword) {
        ChatClient instance = ChatClientHolder.getInstance();
        if (instance.tryCreateRoom(roomPassword)) {
            Toast.makeText(MainActivity.this, "创建房间成功", Toast.LENGTH_SHORT).show();
            startActivity(new Intent(MainActivity.this, ChatActivity.class));
        } else {
            Toast.makeText(MainActivity.this, instance.getLastError(), Toast.LENGTH_LONG).show();
        }
    }

    private void injectCollectListData() {
        ListView listView = this.findViewById(R.id.collect_list);
        Cursor query = collectDBService.query();
        if (query.getCount() > 0) {
            //hide "there is no content"
            this.findViewById(R.id.non_collect_hint).setVisibility(View.INVISIBLE);
            //try remove old content
            if (listView.getAdapter() != null) {
                listView.setAdapter(null);
            }
            //get new data
            CollectDataInjectAdapter collectDataInjectAdapter = new CollectDataInjectAdapter(this, query);
            //apply to listView
            listView.setAdapter(collectDataInjectAdapter);
            //try update?
            collectDataInjectAdapter.notifyDataSetChanged();
        } else {
            if (listView.getAdapter() != null) {
                listView.setAdapter(null);
            }
        }
    }


    private final CollectDBService collectDBService = new CollectDBService(this);
}
