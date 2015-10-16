package nl.mprog.ghost;

/**
 * Created by Robbert van Waardhuizen on 12-10-2015.
 * Student number: 10543147
 */
import android.app.Activity;
import android.content.Context;
import android.text.Html;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Locale;

public class RecentGamesListViewAdapter extends ArrayAdapter<Game> {
    private Context context;
    private int layoutResourceId;
    private ArrayList<Game> data = new ArrayList<>();

    public RecentGamesListViewAdapter(Context context,
                                      ArrayList<Game> data) {
        super(context, R.layout.game_list_row, data);
        this.layoutResourceId = R.layout.game_list_row;
        this.context = context;
        this.data = data;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        View row = convertView;
        RecordHolder holder;

        if (row == null) {
            LayoutInflater inflater = ((Activity) context).getLayoutInflater();
            row = inflater.inflate(layoutResourceId, parent, false);

            holder = new RecordHolder();
            holder.p1tv = (TextView) row.findViewById(R.id.p1name);
            holder.p2tv = (TextView) row.findViewById(R.id.p2name);
            holder.p1avatar = (ImageView)row.findViewById(R.id.gameP1avatar);
            holder.p2avatar = (ImageView)row.findViewById(R.id.gameP2avatar);
            holder.guessedLetters = (TextView) row.findViewById(R.id.displayword);
            holder.date = (TextView) row.findViewById(R.id.dateDisplay);
            holder.flag = (ImageView) row.findViewById(R.id.flagen);
            row.setTag(holder);
        } else {
            holder = (RecordHolder) row.getTag();
        }
        // get Game object and set all the views
        Game item = data.get(position);
        SimpleDateFormat outputFormat = new SimpleDateFormat("dd-MM-yyyy HH:mm", Locale.getDefault());
        holder.date.setText(outputFormat.format(item.date));
        holder.p1tv.setText(item.p1.getName());
        holder.p2tv.setText(item.p2.getName());
        holder.p1avatar.setImageResource(item.p1.avatarId);
        holder.p2avatar.setImageResource(item.p2.avatarId);
        holder.flag.setImageResource(item.flagEN ? R.drawable.flag_en : R.drawable.flag_nl);
        holder.guessedLetters.setText(Html.fromHtml(item.guessedLetters));
        return row;

    }

    static class RecordHolder {
        TextView guessedLetters;
        TextView date;
        TextView p1tv;
        TextView p2tv;
        ImageView p1avatar;
        ImageView p2avatar;
        ImageView flag;
    }
}
