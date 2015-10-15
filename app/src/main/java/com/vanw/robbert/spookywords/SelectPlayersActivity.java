package com.vanw.robbert.spookywords;

import android.annotation.TargetApi;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Build;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.GridView;
import android.widget.Spinner;
import android.widget.Toast;


import java.util.ArrayList;
import java.util.Locale;
import java.util.Objects;

public class SelectPlayersActivity extends AppCompatActivity implements AdapterView.OnItemSelectedListener {

    public ArrayList<Player> playerList;
    ArrayAdapter<Player> adp;
    ArrayList<Integer> avatarIds;
    ArrayAdapter<Integer> avatarAdapter;
    GridView avatarGrid;

    boolean englishLex;

    Player player1;
    Player player2;
    Spinner spinner1;
    Spinner spinner2;
    DBHelper myDB;

    @TargetApi(Build.VERSION_CODES.KITKAT)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_select_players);
        setTitle(getResources().getString(R.string.title_activity_select_players));
        // set system locale as default language
        englishLex = !Objects.equals(Locale.getDefault().getLanguage(), "nl");
        Button langButton = (Button) findViewById(R.id.language);
        langButton.setText((englishLex ? "English" : "Nederlands"));

        // Database
        myDB = new DBHelper(this);
        if(myDB.numberOfRows(DBHelper.PLAYERS_TABLE_NAME) > 0)
            playerList = myDB.getAllPlayers();
        else playerList = new ArrayList<>();
        // loadPlayers();

        // setup the adapter and spinners
        adp = new PlayersAdapter(this,R.layout.item_player,R.id.tvName,playerList);
        spinner1 = (Spinner) findViewById(R.id.spinner);
        spinner2 = (Spinner) findViewById(R.id.spinner2);
        spinner1.setEmptyView(findViewById(R.id.empty_spinner_view));
        spinner2.setEmptyView(findViewById(R.id.empty_spinner2_view));
        spinner1.setAdapter(adp);
        spinner2.setAdapter(adp);
        spinner1.setOnItemSelectedListener(this);
        spinner2.setOnItemSelectedListener(this);

        // get list of avatar ids, looping over possible avatar's (called avatar0 - avatar24)
        // adding more avatars is as simple as adding images with correct name (avatar# + 1) now;
        avatarIds = new ArrayList<>();
        int i = 1;
        while (true) {
            i++;
            int drawableId = this.getResources().getIdentifier("avatar"+i,"drawable",this.getPackageName());
            if (drawableId == 0) break;
            else avatarIds.add(drawableId);
        }
        avatarAdapter = new AvatarsGridViewAdapter(this,R.layout.avatar_grid_item,avatarIds);
    }



    @Override
    public void onItemSelected(AdapterView<?> parent, View view, int position, long id)
    {
        Spinner spinner = (Spinner) parent;
        Player player = playerList.get(position);
        if(spinner.getId()== R.id.spinner) {
            player1 = player;
        }
        else if(spinner.getId() == R.id.spinner2) {
            player2 = player;
        }
        adp.notifyDataSetChanged();
    }

    @Override
    public void onNothingSelected(AdapterView<?> parent) {
        // do nothing!
    }



    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_select_players, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    public void addPlayer(View view) {
        boolean p2;
        p2 = view.getId() != R.id.p1plus;
        final boolean finalp2 = p2;

        LayoutInflater inflater = getLayoutInflater();
        View dialogLayout = inflater.inflate(R.layout.dialog_layout, null);
        AlertDialog.Builder alert = new AlertDialog.Builder(SelectPlayersActivity.this);
        alert.setTitle(getString(R.string.new_player));
        alert.setView(dialogLayout);
        avatarGrid = (GridView) dialogLayout.findViewById(R.id.gridView);
        avatarGrid.setChoiceMode(GridView.CHOICE_MODE_SINGLE);
        avatarGrid.setAdapter(avatarAdapter);
        avatarGrid.setItemChecked(0, true);
        final int[] selectedAvatar = new int[1];
        selectedAvatar[0] = avatarAdapter.getItem(0);
        avatarGrid.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                selectedAvatar[0] = avatarAdapter.getItem(position);
            }
        });

        final EditText newName = (EditText) dialogLayout.findViewById(R.id.newName);

        alert.setPositiveButton("Okay" ,new DialogInterface.OnClickListener() {
            @TargetApi(Build.VERSION_CODES.KITKAT)
            public void onClick(DialogInterface dialog, int whichButton) {
                String name = newName.getText().toString();
                if (!Objects.equals(name, "") && selectedAvatar[0] != 0) { // validate if name not empty
                    Player newPlayer = new Player(name, selectedAvatar[0]);
                    playerList.add(newPlayer);
                    myDB.insertPlayer(newPlayer);

                    adp.notifyDataSetChanged();
                    if(finalp2) { // the addplayer click came from p2
                        player2 = newPlayer;
                        spinner2.setSelection(playerList.size());
                    }
                    else {
                        player1 = newPlayer;
                        spinner1.setSelection(playerList.size());
                    }

                }
            }
        }).setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int whichButton) {
                // what ever you want to do with No option.
            }
        });

        alert.show();
        newName.requestFocus();
        InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
        imm.toggleSoftInput(InputMethodManager.SHOW_FORCED, 0);
    }

    public void switchLanguage(View view) {
        englishLex = !englishLex;
        Button langButton = (Button) findViewById(R.id.language);
        langButton.setText((englishLex ? "English" : "Nederlands"));
    }
    public void goButton (View view) {
        if (player1 == null && player2 == null) {
            Toast.makeText(this, R.string.play_select_two,Toast.LENGTH_SHORT).show();
        }
        else if (player1 == player2) {
            Toast.makeText(this, R.string.play_self,Toast.LENGTH_SHORT).show();
        }
        else {
            startGame(player1,player2);
        }
    }

    private void startGame(Player player1, Player player2) {
        Intent i = new Intent(this, GameActivity.class);
        i.putExtra("p1", player1);
        i.putExtra("p2", player2);
        i.putExtra("flag_EN", englishLex);
        startActivity(i);
    }


    @Override
    public void onBackPressed() {

        // Otherwise defer to system default behavior.
        super.onBackPressed();
    }
}
