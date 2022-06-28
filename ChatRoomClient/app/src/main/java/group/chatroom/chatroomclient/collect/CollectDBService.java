package group.chatroom.chatroomclient.collect;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;

public class CollectDBService {

    private CollectDBOpenHelper collectDBOpenHelper;

    public CollectDBService(Context context) {
        this.collectDBOpenHelper = new CollectDBOpenHelper(context);
    }

    public boolean add(Collect collect) {
        ContentValues cv = new ContentValues();
        cv.put("collect_time", collect.getCollectTimestamp());
        cv.put("from_username", collect.getFromUser());
        cv.put("content", collect.getContent());
        return this.collectDBOpenHelper.getWritableDatabase()
                .insert("collect", "collect_time", cv)
                != -1;
    }

    public boolean remove(Long collectTime) {
        return this.collectDBOpenHelper.getWritableDatabase()
                .delete("collect", "collect_time=?", new String[]{collectTime.toString()})
                != 0;
    }

    public Cursor query() {
        return this.collectDBOpenHelper.getReadableDatabase()
                .rawQuery("select collect_time as _id, collect_time,from_username,content from collect order by collect_time desc", null);
    }

}
