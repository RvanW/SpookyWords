package nl.mprog.ghost;

import android.annotation.TargetApi;
import android.app.Activity;
import android.content.Context;
import android.os.Build;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.ArrayList;

/**
 * Created by Robbert van Waardhuizen on 2-10-2015.
 * Student number: 10543147
 */
class PlayersAdapter extends ArrayAdapter<Player> {
    private Context context;
    private int layoutResourceId;
    private int textViewId;
    private ArrayList<Player> players = new ArrayList<>();

    public PlayersAdapter(Context context, int layoutResourceId, ArrayList<Player> players) {
        super(context, R.layout.item_player, R.id.tvName, players);
        this.textViewId = R.id.tvName;
        this.layoutResourceId = layoutResourceId;
        this.context = context;
        this.players = players;

    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        return getCustomView(position,convertView,parent);
    }
    @TargetApi(Build.VERSION_CODES.KITKAT)
    @Override
    public View getDropDownView(int position, View convertView, ViewGroup parent) {
        return getCustomView(position,convertView,parent);
    }

    private View getCustomView(int position, View convertView, ViewGroup parent) {
        // Check if an existing view is being reused, otherwise inflate the view
        View row = convertView;
        if (row == null) {
            LayoutInflater inflater = ((Activity) context).getLayoutInflater();
            row = inflater.inflate(layoutResourceId, parent, false);
        }
        TextView text1 = (TextView) row.findViewById(textViewId);
        ImageView ivAvatar = (ImageView) row.findViewById(R.id.ivAvatar);
        // create the display
        if(players.get(position) != null) {
            Player player = players.get(position);
            text1.setText(player.getName());
            ivAvatar.setImageResource(player.avatarId);
        }
        // Return the completed view to render on screen
        return row;
    }

}