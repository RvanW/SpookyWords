package com.vanw.robbert.spookywords;

import android.annotation.TargetApi;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Build;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.Objects;

public class SelectPlayersActivity extends AppCompatActivity implements AdapterView.OnItemSelectedListener {

    public ArrayList<Player> playerList;
    ArrayList<String> playerNames;
    ArrayAdapter<String> adp;

    boolean englishLex = true; // true by default for now
    boolean p2;

    String player1;
    String player2;
    Spinner spinner1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_select_players);

        // dummy data for now
        playerList = new ArrayList<>();
        playerList.add(new Player("TestP1"));
        playerList.add(new Player("TestP2"));

        // build up the list of names to be displayed in spinner
        playerNames = new ArrayList<>();
        // add a 'placeholder' for the spinner
        playerNames.add(0,"I am..");
        for (Player player : playerList) {
            playerNames.add(player.getName());
        }

        // setup the adapter and spinner
        adp = new PlayersAdapter(this,R.layout.item_player,R.id.tvName,playerNames);
        spinner1 = (Spinner) findViewById(R.id.spinner);
        spinner1.setAdapter(adp);
        spinner1.setOnItemSelectedListener(this);

    }

    int spinnerCount = 0;
    @Override
    public void onItemSelected(AdapterView<?> parent, View view, int position, long id)
    {
        // little tweak so onItemSelected won't fire prematurely
        if (spinnerCount < 1)
        {
            spinnerCount++;
        }
        else
        {
            //only detect selection events that are not done whilst initializing
            Log.i("SpinnerCount ", " = " + spinnerCount);
            String player = playerNames.get(position);
            if(p2) {
                player2 = player;
            }
            else {
                player1 = player;
            }
        }
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
        AlertDialog.Builder alert = new AlertDialog.Builder(this);
        final EditText edittext = new EditText(this.getApplicationContext());
        edittext.setTextColor(getResources().getColor(R.color.grey_dark));
        alert.setTitle("New player..");
        alert.setMessage("Hi! What's your name?");

        alert.setView(edittext);

        alert.setPositiveButton("Okay", new DialogInterface.OnClickListener() {
            @TargetApi(Build.VERSION_CODES.KITKAT)
            public void onClick(DialogInterface dialog, int whichButton) {
                String name = edittext.getText().toString();
                if (!Objects.equals(name, "")) { // validate if not empty
                    // playerList.add(0, new Player(name));

                    playerNames.add(1, name);
                    spinner1.setSelection(1);
                    adp.notifyDataSetChanged();

                    if(p2) {
                        player2 = name;
                        //startGame(player1,player2);
                    }
                    else {
                        player1 = name;
                        //switchPlayer();
                    }
                }
            }
        }).setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int whichButton) {
                // what ever you want to do with No option.
            }
        });

        alert.show();
    }
    public void switchLanguage(View view) {
        englishLex = !englishLex;
        Button langButton = (Button) findViewById(R.id.language);
        langButton.setText("Language: "+(englishLex ? "English" : "Dutch"));
    }
    public void goButton (View view) {
        // if p2 has pressed go and a name was filled in..
        if(p2 && player2 != null) {
            startGame(player1, player2);
        }
        else if (player1 != null) {
            p2 = true;
            switchPlayer();
        }
    }

    private void startGame(String player1, String player2) {
        Intent i = new Intent(this, TestActivity.class);
        i.putExtra("p1", player1);
        i.putExtra("p2", player2);
        i.putExtra("flag_EN", englishLex);
        startActivity(i);
    }

    public void switchPlayer() {
        TextView playerIndicator = (TextView) findViewById(R.id.p1);
        TextView message = (TextView) findViewById(R.id.message);
        spinnerCount--;
        spinner1.setSelection(0);
        if(p2) {
            playerIndicator.setText("Player 2");
            message.setText("And who is this again?");
        }
        else {
            playerIndicator.setText("Player 1");
            message.setText("Hi! Who are you?");
        }
    }
    @Override
    public void onBackPressed() {
        // override the back button if p2 is on turn to choose
        if (p2) {
            p2 = false;
            switchPlayer();
            return;
        }

        // Otherwise defer to system default behavior.
        super.onBackPressed();
    }
}
