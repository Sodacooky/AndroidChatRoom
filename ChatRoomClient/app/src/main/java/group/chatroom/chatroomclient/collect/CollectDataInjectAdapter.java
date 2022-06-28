package group.chatroom.chatroomclient.collect;

import android.content.Context;
import android.database.Cursor;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CursorAdapter;
import android.widget.TextView;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Locale;

import group.chatroom.chatroomclient.R;

public class CollectDataInjectAdapter extends CursorAdapter {

    public CollectDataInjectAdapter(Context context, Cursor c) {
        super(context, c, false);
        this.layoutInflater = LayoutInflater.from(context);
    }

    @Override
    public View newView(Context context, Cursor cursor, ViewGroup parent) {
        View inflate = this.layoutInflater.inflate(R.layout.activity_main_collect_text, null);
        setData(inflate, cursor);
        return inflate;
    }

    @Override
    public void bindView(View view, Context context, Cursor cursor) {
        setData(view, cursor);
    }

    private void setData(View view, Cursor cursor) {
        //get item widgets
        TextView text = view.findViewById(R.id.collect_text);
        TextView user = view.findViewById(R.id.collect_user);
        TextView time = view.findViewById(R.id.collect_time);
        //set
        text.setText(cursor.getString(cursor.getColumnIndex("content")));
        user.setText(cursor.getString(cursor.getColumnIndex("from_username")));
        Calendar date = Calendar.getInstance();
        date.setTimeInMillis(cursor.getLong(0));
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault());
        time.setText(simpleDateFormat.format(date.getTime()));
    }

    private LayoutInflater layoutInflater;
}
