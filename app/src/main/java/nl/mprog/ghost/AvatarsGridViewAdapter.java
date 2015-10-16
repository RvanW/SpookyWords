package nl.mprog.ghost;

/**
 * Created by Robbert van Waardhuizen on 12-10-2015.
 * Student number: 10543147
 * Used to create a gridView of possible avatars when creating a new player
 */

import android.app.Activity;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;

import java.util.ArrayList;

class AvatarsGridViewAdapter extends ArrayAdapter<Integer> {
    private Context context;
    private int layoutResourceId;
    private ArrayList<Integer> data = new ArrayList<>();

    public AvatarsGridViewAdapter(Context context,
                                  ArrayList<Integer> data) {
        super(context, R.layout.avatar_grid_item, data);
        this.layoutResourceId = R.layout.avatar_grid_item;
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
    }
}
