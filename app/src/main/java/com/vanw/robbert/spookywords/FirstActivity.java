package com.vanw.robbert.spookywords;

import android.content.Context;
import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;

public class FirstActivity extends AppCompatActivity {

    DBHelper myDB;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_first);
        // Database
        myDB = new DBHelper(this);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_first, menu);
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

    public void newGame(View view) {
        Intent i = new Intent(this, SelectPlayersActivity.class);
        startActivity(i);
    }
    public void highScores(View view) {
        //TODO get list of highscores from player objects
        Intent i = new Intent(this, HighscoresActivity.class);
        startActivity(i);
    }

    public void openSettings(View view) {
        //TODO open settings
    }

    public void continueGame(View view) {
        Game lastGame = myDB.getLastGame();
        if(lastGame != null) {
            Intent i = new Intent(this, GameActivity.class);
            Bundle extras = new Bundle();
            extras.putSerializable("game",lastGame);
            i.putExtras(extras);
            startActivity(i);

        }
    }
}
