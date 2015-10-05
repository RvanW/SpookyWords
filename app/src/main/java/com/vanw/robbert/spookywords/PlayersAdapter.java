package com.vanw.robbert.spookywords;

import android.app.Activity;
import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.RatingBar;
import android.widget.TextView;

import java.util.ArrayList;

/**
 * Created by Robbert on 2-10-2015.
 */
public class PlayersAdapter extends ArrayAdapter<String> {
    Context context;
    int layoutResourceId;
    int textViewId;
    ArrayList<String> players = new ArrayList<>();

    public PlayersAdapter(Context context, int layoutResourceId, int textViewId, ArrayList<String> players) {
        super(context, layoutResourceId, textViewId, players);
        this.textViewId = textViewId;
        this.layoutResourceId = layoutResourceId;
        this.context = context;
        this.players = players;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        return getCustomView(position,convertView,parent, false);
    }
    @Override
    public View getDropDownView(int position, View convertView, ViewGroup parent) {
        return getCustomView(position,convertView,parent, true);
    }

    public View getCustomView(int position, View convertView, ViewGroup parent, boolean hideFirstElement) {
        // Check if an existing view is being reused, otherwise inflate the view
        View row = convertView;
        if (row == null) {
            LayoutInflater inflater = ((Activity) context).getLayoutInflater();
            row = inflater.inflate(layoutResourceId, parent, false);
        }
        TextView text1 = (TextView) row.findViewById(textViewId);
        //
        if (position == 0 && hideFirstElement) { // first element in the list is used as placeholder.. so don't display it
            text1.setVisibility(View.GONE);
        }
        else if(players.get(position) != null) {
            String player = players.get(position);
            text1.setVisibility(View.VISIBLE);
            text1.setText(player);
        }
        // Return the completed view to render on screen
        return row;
    }

}