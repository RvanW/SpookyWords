package com.vanw.robbert.spookywords;

import android.annotation.TargetApi;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.Configuration;
import android.net.Uri;
import android.os.Build;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.Locale;
import java.util.Objects;

public class FirstActivity extends AppCompatActivity implements AdapterView.OnItemClickListener, AdapterView.OnItemLongClickListener ,OnTaskCompleted {
    DBHelper myDB;
    public RecentGamesListViewAdapter recentGamesListViewAdapter;
    ListView listView;
    public ArrayList<Game> gameList;

    @Override
    public void onTaskCompleted(ArrayList<Game> gameList) {
        // this task returns a list of most recent played games from database (max 20)
        this.gameList.clear();
        this.gameList.addAll(gameList);
        recentGamesListViewAdapter.notifyDataSetChanged();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_first);
        // create and hide the menu
        createSettingsFragment();

        // get last played language from sharedprefs
        SharedPreferences prefs = this.getSharedPreferences(
                getResources().getString(R.string.preference_file_key), Context.MODE_PRIVATE);
        String lang = prefs.getString("system_lang", null);
        if(lang != null) Log.d("system lang:", lang);
        // if a language is set and it doesn't equal the current language.. switch
        if(lang != null && !lang.equals(Locale.getDefault().getLanguage())) switchLocaleLanguage_mainMenu(null);

        // initialize database
        myDB = new DBHelper(this);
        gameList = new ArrayList<>();
        // AsyncGetGames will fetch a list of recent games, displays a fancy loading bar if this is slow
        // It updates the ArrayList<Game> gameList and sorts it by date when done through interface callback
        new AsyncGetGames(this, this).execute();

        // setup the adapter and listview click listeners
        recentGamesListViewAdapter = new RecentGamesListViewAdapter(this,R.layout.game_list_row,gameList);
        listView = (ListView) findViewById(R.id.listView2);
        listView.setEmptyView(findViewById(R.id.empty_list_view));
        listView.setAdapter(recentGamesListViewAdapter);
        listView.setOnItemClickListener(this);
        listView.setOnItemLongClickListener(this);

    }

    @Override
    protected void onRestart() {
        super.onRestart();
        // Update the recent games on restart
        new AsyncGetGames(this, this).execute();
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
            toggleSettings();
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    public void newGame(View view) {
        Intent i = new Intent(this, SelectPlayersActivity.class);
        startActivity(i);
    }
    public void highScores(View view) {
        Intent i = new Intent(this, HighscoresActivity.class);
        startActivity(i);
    }

    // function might be used for 'continue last game' button
    public void continueGame(View view) {
        Game lastGame = myDB.getLastGame();
        startSavedGame(lastGame);
    }

    public void startSavedGame(Game game) {
        if(game != null) {
            Intent i = new Intent(this, GameActivity.class);
            Bundle extras = new Bundle();
            extras.putSerializable("game", game);
            i.putExtras(extras);
            startActivity(i);
        }
    }

    // first we create and hide the fragment
    public void createSettingsFragment() {

        // Build and replace the settings menu fragment in settings_container
        SettingsFragment settingsFragment = SettingsFragment.newInstance("outgame");
        FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
        transaction
                .replace(R.id.settings_container, settingsFragment, "settings")
                .hide(settingsFragment)
                .commit();
    }
    // check visibility of fragment and toggle
    public void toggleSettings() {
        FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
        Fragment fragment = getSupportFragmentManager().findFragmentByTag("settings");
        if (fragment.isVisible()) {
            transaction
                    .setCustomAnimations(android.R.anim.fade_in, android.R.anim.fade_out)
                    .hide(fragment)
                    .commit();
        } else {
            transaction
                    .setCustomAnimations(android.R.anim.fade_in, android.R.anim.fade_out)
                    .show(fragment)
                    // add to back stack so user may press back to close
                    .addToBackStack("settings")
                    .commit();
        }
    }

    @TargetApi(Build.VERSION_CODES.KITKAT)
    public void switchLocaleLanguage_mainMenu(View view) {
        String lang;
        if(Objects.equals(Locale.getDefault().getLanguage(), "en")) {
            lang = "nl";
        }
        else {
            lang =  "en";
        }
        Locale locale = new Locale(lang);
        Locale.setDefault(locale);
        Configuration config = new Configuration();
        config.locale = locale;
        getBaseContext().getResources().updateConfiguration(config, getBaseContext().getResources().getDisplayMetrics());

        // store the language in sharedpreferences
        SharedPreferences prefs = this.getSharedPreferences(
                getResources().getString(R.string.preference_file_key), Context.MODE_PRIVATE);
        prefs.edit().putString("system_lang",lang).apply();

        // refresh activity for locale changes to take effect
        Intent i = new Intent(this, this.getClass());
        startActivity(i);
        this.finish();
    }


    // if a game is clicked, rebuild it and play
    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        Game game = gameList.get(position);
        startSavedGame(game);
    }


    // if a game is long clicked, ask if user wants to delete the game
    @Override
    public boolean onItemLongClick(AdapterView<?> parent, View view, final int position, long id) {
        final Game game = gameList.get(position);

        AlertDialog.Builder adb =  new AlertDialog.Builder(this);
        adb
                .setTitle(R.string.warning)
                .setMessage(R.string.delete_game_dialog)
                .setPositiveButton(android.R.string.yes, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        myDB.deleteGame(game.getId());
                        gameList.remove(position);
                        recentGamesListViewAdapter.notifyDataSetChanged();
                    }
                })
                .setNegativeButton(android.R.string.no, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        // do nothing
                    }
                })
                .setIcon(android.R.drawable.ic_dialog_alert)
                .show();
        return true;
    }
}
