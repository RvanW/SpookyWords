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
        this.layoutResourceId = R.layout.item_player;
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

    private View getCustomView(int position, View convertView, ViewGroup parent, boolean enabled) {
        // Check if an existing view is being reused, otherwise inflate the view
        View row = convertView;
        if (row == null) {
            LayoutInflater inflater = ((Activity) context).getLayoutInflater();
            row = inflater.inflate(layoutResourceId, parent, false);
        }
        TextView text1 = (TextView) row.findViewById(textViewId);
        ImageView ivAvatar = (ImageView) row.findViewById(R.id.ivAvatar);
        //
        if (!enabled) { // if element in the list is disabled.. don't display it
            text1.setVisibility(View.GONE);
            ivAvatar.setVisibility(View.GONE);
        }
        else if(players.get(position) != null) {
            Player player = players.get(position);
            text1.setVisibility(View.VISIBLE);
            text1.setText(player.getName());
            ivAvatar.setVisibility(View.VISIBLE);
            ivAvatar.setImageResource(player.avatarId);
        }
        // Return the completed view to render on screen
        return row;
    }

}