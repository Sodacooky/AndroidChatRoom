package group.chatroom.chatroomclient.collect;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

public class CollectDBOpenHelper extends SQLiteOpenHelper {


    public CollectDBOpenHelper(Context context) {
        super(context, "ChatRoomCollect.db", null, 1);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        /*
        create table collect(
            collect_time integer primary key ,
            from_username TEXT,
            content TEXT
        )
         */
        db.execSQL("create table collect(\n" +
                "            collect_time integer primary key ,\n" +
                "            from_username TEXT,\n" +
                "            content TEXT\n" +
                "        )");
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("drop table if exists collect");
        db.execSQL("create table collect(\n" +
                "            collect_time integer primary key ,\n" +
                "            from_username TEXT,\n" +
                "            content TEXT\n" +
                "        )");
    }
}
