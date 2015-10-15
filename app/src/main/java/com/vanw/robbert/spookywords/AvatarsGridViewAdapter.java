package com.vanw.robbert.spookywords;

/**
 * Created by Robbert van Waardhuizen on 12-10-2015.
 * Student number: 10543147
 */

import android.app.Activity;
import android.content.Context;
import android.media.Image;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.ArrayList;

public class AvatarsGridViewAdapter extends ArrayAdapter<Integer> {
    Context context;
    int layoutResourceId;
    ArrayList<Integer> data = new ArrayList<Integer>();

    public AvatarsGridViewAdapter(Context context, int layoutResourceId,
                                  ArrayList<Integer> data) {
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
            holder.avatarImage = (ImageView) row.findViewById(R.id.imageView);
            row.setTag(holder);
        } else {
            holder = (RecordHolder) row.getTag();
        }
        Integer item = data.get(position);
        holder.avatarId = item;
        holder.avatarImage.setImageResource(item);
        return row;

    }
    @Override
    public Integer getItem(int position) {
        return data.get(position);
    }

    static class RecordHolder {
        Integer avatarId;
        ImageView avatarImage;
        TextView score;
    }
}
