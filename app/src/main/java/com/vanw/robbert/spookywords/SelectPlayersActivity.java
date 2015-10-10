package com.vanw.robbert.spookywords;

import android.annotation.TargetApi;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
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

//database
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteException;
import android.database.sqlite.SQLiteOpenHelper;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.Objects;

public class SelectPlayersActivity extends AppCompatActivity implements AdapterView.OnItemSelectedListener {

    public ArrayList<Player> playerList;
    ArrayList<String> playerNames;
    ArrayAdapter<Player> adp;

    boolean englishLex; // true by default for now
    static boolean p2; // indicator if p2 is up to choose a name

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
        spinner1.setAdapter(adp);
        spinner2.setAdapter(adp);
        spinner1.setOnItemSelectedListener(this);
        spinner2.setOnItemSelectedListener(this);

    }



    @Override
    public void onItemSelected(AdapterView<?> parent, View view, int position, long id)
    {
    Spinner spinner = (Spinner) parent;
    Player player = playerList.get(position);
    if(spinner.getId()== R.id.spinner) player1 = player;
    else if(spinner.getId() == R.id.spinner2) player2 = player;
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

        AlertDialog.Builder alert = new AlertDialog.Builder(SelectPlayersActivity.this);
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
                    Player newPlayer = new Player(name);
                    playerList.add(newPlayer);
                    myDB.insertPlayer(newPlayer);

                    adp.notifyDataSetChanged();
                    if(finalp2) {
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
        Log.v("SelectPlayersActivity flag_EN: ", String.valueOf(englishLex));
        startActivity(i);
    }


    @Override
    public void onBackPressed() {

        // Otherwise defer to system default behavior.
        super.onBackPressed();
    }
}
