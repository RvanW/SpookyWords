package com.vanw.robbert.spookywords;

import android.annotation.TargetApi;
import android.app.Activity;
import android.content.Context;
import android.os.Build;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.RatingBar;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.Objects;

/**
 * Created by Robbert on 2-10-2015.
 */
public class PlayersAdapter extends ArrayAdapter<Player> {
    Context context;
    int layoutResourceId;
    int textViewId;
    ArrayList<Player> players = new ArrayList<>();

    public PlayersAdapter(Context context, int layoutResourceId, int textViewId, ArrayList<Player> players) {
        super(context, layoutResourceId, textViewId, players);
        this.textViewId = textViewId;
        this.layoutResourceId = layoutResourceId;
        this.context = context;
        this.players = players;

    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        if(!isEnabled(position)) {
            TextView text1 = (TextView) convertView.findViewById(textViewId);
            text1.setText("Select player 2's name..");
            return getCustomView(position,convertView,parent, true);
        }
        return getCustomView(position,convertView,parent, isEnabled(position));
    }
    @TargetApi(Build.VERSION_CODES.KITKAT)
    @Override
    public View getDropDownView(int position, View convertView, ViewGroup parent) {
        // split down player string to just the name (minus score)

        return getCustomView(position,convertView,parent, isEnabled(position));
    }

    @Override
    public boolean isEnabled(int position) {
        //TODO disable opponent's name
        return true;
    }

    public View getCustomView(int position, View convertView, ViewGroup parent, boolean enabled) {
        // Check if an existing view is being reused, otherwise inflate the view
        View row = convertView;
        if (row == null) {
            LayoutInflater inflater = ((Activity) context).getLayoutInflater();
            row = inflater.inflate(layoutResourceId, parent, false);
        }
        TextView text1 = (TextView) row.findViewById(textViewId);
        //
        if (!enabled) { // first element in the list is used as placeholder.. so don't display it
            text1.setVisibility(View.GONE);
        }
        else if(players.get(position) != null) {
            Player player = players.get(position);
            text1.setVisibility(View.VISIBLE);
            text1.setText(player.getName());
        }
        // Return the completed view to render on screen
        return row;
    }

}