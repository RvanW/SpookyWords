package com.vanw.robbert.spookywords;

/**
 * Created by Robbert van Waardhuizen on 6-10-2015.
 * Student number: 10543147
 */

import android.app.Activity;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;
import java.util.ArrayList;

public class HighscoreListViewAdapter extends ArrayAdapter<Player> {
    Context context;
    int layoutResourceId;
    ArrayList<Player> data = new ArrayList<Player>();

    public HighscoreListViewAdapter(Context context, int layoutResourceId,
                                    ArrayList<Player> data) {
        super(context, layoutResourceId, data);
        this.layoutResourceId = layoutResourceId;
        this.context = context;
        this.data = data;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        View row = convertView;
        RecordHolder holder = null;

        if (row == null) {
            LayoutInflater inflater = ((Activity) context).getLayoutInflater();
            row = inflater.inflate(layoutResourceId, parent, false);
            holder = new RecordHolder();
            holder.avatar = (ImageView) row.findViewById(R.id.imageDisplay);
            holder.txtTitle = (TextView) row.findViewById(R.id.nameDisplay);
            holder.score = (TextView) row.findViewById(R.id.scoreDisplay);
            row.setTag(holder);
        } else {
            holder = (RecordHolder) row.getTag();
        }
        Player item = data.get(position);
        holder.txtTitle.setText(item.getName());
        holder.score.setText(item.getScore()+"");
        holder.avatar.setImageResource(item.avatarId);
        return row;

    }

    static class RecordHolder {
        TextView txtTitle;
        TextView score;
        ImageView avatar;
    }
}
