package com.vanw.robbert.spookywords;

import android.annotation.TargetApi;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.Configuration;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.app.NavUtils;
import android.support.v7.app.AppCompatActivity;
import android.text.Html;
import android.util.Log;
import android.view.Gravity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.google.gson.Gson;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.Objects;

public class GameActivity extends AppCompatActivity implements SettingsFragment.OnFragmentInteractionListener {

    Lexicon lexicon;
    Lexicon alt_lexicon;
    Game game;

    Boolean defaultEnglish; // get this from sharedprefs later

    DBHelper myDB;
    Player player1;
    Player player2;

    @Override
    public void onSaveInstanceState(Bundle savedInstanceState) {
        // Always call the superclass so it can save the view hierarchy state
        super.onSaveInstanceState(savedInstanceState);
        // Save the user's current game state
        savedInstanceState.putSerializable("game", game);
    }

    @TargetApi(Build.VERSION_CODES.KITKAT)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_game);
        myDB = new DBHelper(this);
        Bundle extras = getIntent().getExtras();
        if (savedInstanceState != null) {
            // Restore game state if there is any
            game = (Game) savedInstanceState.getSerializable("game");
            Log.v("GAME", "Saved instance found");
        }
        else if (game != null) {
            Log.v("GAME", "Running game found..");
        }
        else if (extras.getSerializable("game") != null) {
            Log.v("GAME", "Continue game found..");
            game = myDB.getLastGame();
            if (game != null) {
                player1 = game.p1;
                player2 = game.p2;
                lexicon = new Lexicon(this,(game.flagEN ? "english" : "dutch") + ".txt");
                lexicon.filter(game.guessedLetters);
                game.lexicon = lexicon;
            }
        }
        else {
            Log.v("GAME", "Creating new game and lexicon..");

            // load up the two players from intent
            if(player1 == null) player1 = (Player) extras.getSerializable("p1");
            if(player2 == null) player2 = (Player) extras.getSerializable("p2");
            // get language
            defaultEnglish = extras.getBoolean("flag_EN");

            // create new lexicon only once here for default language
            if(defaultEnglish) {
                if(Objects.equals(Locale.getDefault().getLanguage(), "nl")) switchLanguage("en");
                lexicon = new Lexicon(this, "english.txt");
                game = new Game(lexicon);
            }
            else {
                if(Objects.equals(Locale.getDefault().getLanguage(), "en")) switchLanguage("nl");
                alt_lexicon = new Lexicon(this, "dutch.txt");
                game = new Game(alt_lexicon);
            }
            game.setPlayers(player1,player2);
            myDB.insertGame(game);
        }
        createSettingsFragment();
        updateView();
    }

    @Override
    protected void onPause(){
        myDB.updatePlayer(game.p1);
        myDB.updatePlayer(game.p2);
        myDB.updateGame(game);
        super.onPause();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_test, menu);
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

    public void createSettingsFragment() {
        
        // Build and replace the settings menu fragment
        SettingsFragment settingsFragment = new SettingsFragment();
        FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
        transaction
                .replace(R.id.settings_container, settingsFragment, "settings")
                .hide(settingsFragment)
                .commit();
    }

    public void toggleSettings() {
        FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
        Fragment fragment = getSupportFragmentManager().findFragmentByTag("settings");
        if(fragment.isVisible()) {
            transaction
                    .setCustomAnimations(android.R.anim.fade_in, android.R.anim.fade_out)
                    .hide(fragment)
                    .commit();
            getSupportFragmentManager().popBackStack();
        }
        else {
            transaction
                    .setCustomAnimations(android.R.anim.fade_in, android.R.anim.fade_out)
                    .show(fragment)
                    .addToBackStack("settings")
                    .commit();

            hide_keyboard(this);
        }

    }

    @TargetApi(Build.VERSION_CODES.KITKAT)
    public void onSubmit(View view) {
        EditText textInput = (EditText) findViewById(R.id.editText1);

        String letter = textInput.getText().toString().toLowerCase();
        // validate if input is filled in and not a number
        if(letter.length() != 1 || !Character.isLetter(letter.charAt(0))) {
            Toast.makeText(getApplicationContext(), R.string.toast_valid_character,
                    Toast.LENGTH_SHORT).show();
        }
        else if(!game.ended()) {
            game.guess(letter); // counts as a guess if game is not ended already.
            myDB.updateGame(game);
            if (game.ended()) { // save the player that wins because score changed
                Log.v("Winner!", "score + 1");

                // updates DB on score change;
                myDB.updatePlayer(game.awardAndSaveWinner());

                // notify this activity of player change
                player1 = game.p1;
                player2 = game.p2;
            }
        }
        updateView();

    }

    @TargetApi(Build.VERSION_CODES.KITKAT)
    public void updateView() {
        game.setPlayers(player1, player2);
        // Update the current game letters
        TextView textView = (TextView) findViewById(R.id.textview1);
        textView.setText(Html.fromHtml(game.guessedLetters));
        // build up message (if any)
        String messageString = "";
        if(game.message != null && !Objects.equals(game.message, "")) {
            String[] splittedMessage = game.message.split("||");
            if(Objects.equals(splittedMessage[0], "is_a_word")) {
                messageString = splittedMessage[1] + getString(R.string.is_a_word)+"\r\n"
                        + splittedMessage[2] + getString(R.string.wins_this_time);
            }
            else if(Objects.equals(splittedMessage[0], "is_no_word")) {
                messageString = splittedMessage[1] + getString(R.string.is_no_word)+"\r\n"
                        + splittedMessage[2] + getString(R.string.wins_this_time);
                if (splittedMessage.length == 4) {
                    messageString += "\r\n" + getString(R.string.last_word) + "'" + splittedMessage[3] + "'";
                }
            }
        }
        TextView message = (TextView) findViewById(R.id.message);
        message.setText(messageString);
        // two textviews indicating player names..
        TextView p1tv = (TextView) findViewById(R.id.p1tv);
        TextView p2tv = (TextView) findViewById(R.id.p2tv);
        p1tv.setText(player1.getName() + " (" + player1.getScore() + ")");
        p1tv.setTextSize(game.turn() ? 30 : 20);
        p2tv.setText(player2.getName() + " (" + player2.getScore() + ")");
        p2tv.setTextSize(game.turn() ? 20 : 30);
        // text input and submit button
        EditText textInput = (EditText) findViewById(R.id.editText1);
        Button submitButton = (Button) findViewById(R.id.submit);
        // rematch button
        Button rematchButton = (Button) findViewById(R.id.rematch);

        if(game.ended()) { // check if the game has ended and update the display accordingly
            hide_keyboard(this);
            submitButton.setVisibility(View.GONE);
            textInput.setVisibility(View.GONE);
            rematchButton.setVisibility(View.VISIBLE);
        }
        else { // otherwise the game is running or restarted
            submitButton.setVisibility(View.VISIBLE);
            textInput.setVisibility(View.VISIBLE);
            rematchButton.setVisibility(View.GONE);
            textInput.setText("");
        }
    }

    private void storeArrayList(ArrayList<Player> playerList) {
        SharedPreferences prefs = this.getApplicationContext().getSharedPreferences(getString(R.string.preference_file_key), Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = prefs.edit();
        List<Player> connections = new ArrayList<>(playerList);
        // convert java object to JSON format,
        // and returned as JSON formatted string
        String connectionsJSONString = new Gson().toJson(connections);
        editor.putString("playerList", connectionsJSONString);
        editor.apply();
    }

    public void newGame(View v) {

        if(defaultEnglish) { // reset and load the english lexicon
            if(lexicon == null) { // only create an english lexicon if it doesn't exist already
                lexicon = new Lexicon(getApplicationContext(), "english.txt");
            }
            else {
                lexicon.reset();
            }
            game.lexicon = lexicon;
            game.resetGame();
        }
        else { // reload the alternative (dutch) lexicon
            if(alt_lexicon == null) { // only create an alternative lexicon if it doesn't exist already
                alt_lexicon = new Lexicon(getApplicationContext(), "dutch.txt");
            }
            else {
                alt_lexicon.reset();
            }
            game.lexicon = alt_lexicon;
            game.resetGame();
        }
        updateView();
        // if button was fired from settings menu, toggle settings fragment
        if(v == null || v.getId() == R.id.newGame_settings) toggleSettings();
    }

    @TargetApi(Build.VERSION_CODES.KITKAT)
    public void onChangeLanguage(View view) {

        AlertDialog.Builder adb =  new AlertDialog.Builder(this);
        adb
                .setTitle(R.string.warning)
                .setMessage(R.string.change_language_dialog)
                .setPositiveButton(android.R.string.yes, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        defaultEnglish = !defaultEnglish;
                        switchLanguage(defaultEnglish ? "en" : "nl" );
//                        newGame(null);
//                        updateView();
//                        toggleSettings();
                    }
                })
                .setNegativeButton(android.R.string.no, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        // do nothing
                    }
                })
                .setIcon(android.R.drawable.ic_dialog_alert)
                .show();
    }
    public void switchLanguage(String lang) {
        Locale locale = new Locale((lang));
        Locale.setDefault(locale);
        Configuration config = new Configuration();
        config.locale = locale;
        getBaseContext().getResources().updateConfiguration(config, getBaseContext().getResources().getDisplayMetrics());

        // refresh activity for language changes to take effect
        Bundle extras = getIntent().getExtras();
        extras.putBoolean("flag_EN", defaultEnglish);
        extras.putSerializable("p1", player1);
        extras.putSerializable("p2", player2);
        Intent i = new Intent(this, GameActivity.class).putExtras(extras);
        startActivity(i);
        this.finish();

    }
    @Override
    public void onFragmentInteraction(Uri uri) {

    }

    @Override
    public void onBackPressed() {
        int count = getFragmentManager().getBackStackEntryCount();
        Log.v("frag_count: ", count+ "");
        if (count == 0) {
            super.onBackPressed();
            //additional code
            NavUtils.navigateUpFromSameTask(this);
        } else {
            getFragmentManager().popBackStack();
        }
    }

    public static void hide_keyboard(Activity activity) {
        InputMethodManager inputMethodManager = (InputMethodManager) activity.getSystemService(Activity.INPUT_METHOD_SERVICE);
        //Find the currently focused view, so we can grab the correct window token from it.
        View view = activity.getCurrentFocus();
        //If no view currently has focus, create a new one, just so we can grab a window token from it
        if(view == null) {
            view = new View(activity);
        }
        inputMethodManager.hideSoftInputFromWindow(view.getWindowToken(), 0);
    }
}
