package com.vanw.robbert.spookywords;

import android.annotation.TargetApi;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.Configuration;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.text.Html;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import java.util.Locale;
import java.util.Objects;
import java.util.regex.Pattern;

public class GameActivity extends AppCompatActivity {

    Lexicon lexicon; // english
    Lexicon alt_lexicon; // dutch
    Game game;

    Boolean dictionaryEnglish;
    int flagId;
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
            // make sure there is no game running, might be redundant
            Log.v("GAME", "Running game found..");
        }

        // here a game is recreated (from recent games)
        else if (extras.getSerializable("game") != null) {
            Log.v("GAME", "Continue game found..");
            game = (Game) extras.getSerializable("game");
            if(extras.containsKey("flag_EN")) {
                game.flagEN = extras.getBoolean("flag_EN");

            }
            if (game != null) {
                // update players  in this game from DB because score may have changed from other games
                player1 = myDB.getPlayer(game.p1.getId());
                player2 = myDB.getPlayer(game.p2.getId());
                game.p1 = player1;
                game.p2 = player2;
                if(game.flagEN) { // determine which language and rebuild lexicon
                    lexicon = new Lexicon(this, ("english.txt"));
                    lexicon.filter(game.guessedLetters);
                    game.lexicon = lexicon;
                }
                else {
                    alt_lexicon = new Lexicon(this, ("dutch.txt"));
                    alt_lexicon.filter(game.guessedLetters);
                    game.lexicon = alt_lexicon;
                }
                dictionaryEnglish = game.flagEN;
            }


        }
        else if (extras.containsKey("gameID")) { // this is only true if a user switches locale (system) languages
            // get language
            dictionaryEnglish = extras.getBoolean("flag_EN");
            game = myDB.getGame(extras.getString("gameID"));
            if(dictionaryEnglish) {
                lexicon = new Lexicon(this, "english.txt");
                // update and reset game lexicon
                game.lexicon = lexicon;
            }
            else {
                alt_lexicon = new Lexicon(this, "dutch.txt");
                // update and reset game lexicon
                game.lexicon = alt_lexicon;
            }
            game.resetGame();
            new AsyncSaveGame(this).execute(game);
        }

        // if all conditions are false we are creating a new game from scratch
        else {
            Log.v("GAME", "Creating new game and lexicon..");
            // load up the two players from intent
            if(player1 == null) player1 = (Player) extras.getSerializable("p1");
            if(player2 == null) player2 = (Player) extras.getSerializable("p2");
            // get language from intent
            dictionaryEnglish = extras.getBoolean("flag_EN");

            // create new lexicon only once here for selected language
            if(dictionaryEnglish) {
                lexicon = new Lexicon(this, "english.txt");
                game = new Game(lexicon);
            }
            else {
                alt_lexicon = new Lexicon(this, "dutch.txt");
                game = new Game(alt_lexicon);

            }
            game.setPlayers(player1,player2);
            game.flagEN = dictionaryEnglish;
            myDB.insertGame(game);
        }
        // set the two avatars
        if(player1 != null && player2 != null) {
            ImageView p1image = (ImageView) findViewById(R.id.p1avatar);
            ImageView p2image = (ImageView) findViewById(R.id.p2avatar);
            p1image.setImageResource(player1.avatarId);
            p2image.setImageResource(player2.avatarId);
        }

        createSettingsFragment();
        updateView();
    }

    @Override
    protected void onPause(){
        myDB.updatePlayer(game.p1);
        myDB.updatePlayer(game.p2);
        if(!game.ended()) new AsyncSaveGame(this).execute(game);
        super.onPause();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_game, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            // item.setIcon() // could set an icon instead of text
            toggleSettings();
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    // Build the settings menu fragment, hide it, and put it in settings_container
    public void createSettingsFragment() {
        SettingsFragment settingsFragment = SettingsFragment.newInstance("ingame");
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
            if (game.ended()) { // save the player that wins because score changed
                Log.v("Winner!", "score + 1");
                game.getPlayerObject(game.winner()).addScore(1);
                // updates DB on score change;
                myDB.updatePlayer(game.getPlayerObject(game.winner()));
                // notify this activity of player change
                player1 = game.p1;
                player2 = game.p2;
            }
            // async save on every turn..
            new AsyncSaveGame(this).execute(game);
        }
        updateView();

    }

    // Since the app uses different Locales, I need to build up my string in this activity to get access to string resources
    // Little bit dirty but does the trick nicely
    @TargetApi(Build.VERSION_CODES.KITKAT)
    private String convertMessage() {
        String messageString = "";
        if(game.message != null && !Objects.equals(game.message, "")) {
            String[] splittedMessage = game.message.split(Pattern.quote("||"));
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
        return messageString;
    }

    @TargetApi(Build.VERSION_CODES.KITKAT)
    public void updateView() {
        // Update the current game letters
        TextView textView = (TextView) findViewById(R.id.textview1);
        textView.setText(Html.fromHtml(game.guessedLetters));
        // build up message (if any)
        String messageString = convertMessage();
        TextView message = (TextView) findViewById(R.id.message);
        message.setText(messageString);
        // two textviews indicating player names..
        TextView p1tv = (TextView) findViewById(R.id.p1tv);
        TextView p2tv = (TextView) findViewById(R.id.p2tv);
        p1tv.setText(game.p1.getName() + " (" + game.p1.getScore() + ")");
        p1tv.setTextSize(game.turn() ? 30 : 20);
        p2tv.setText(game.p2.getName() + " (" + game.p2.getScore() + ")");
        p2tv.setTextSize(game.turn() ? 20 : 30);
        // text input and submit button
        EditText textInput = (EditText) findViewById(R.id.editText1);
        Button submitButton = (Button) findViewById(R.id.submit);
        // rematch button
        Button rematchButton = (Button) findViewById(R.id.rematch);
        // set the image indicating language
        int uri = dictionaryEnglish ? R.drawable.flag_en : R.drawable.flag_nl;
        if(flagId != uri) {
            flagId = uri;
            Drawable res = ContextCompat.getDrawable(this,uri);
            ImageView imageView = (ImageView) findViewById(R.id.flag);
            imageView.setImageDrawable(res);
        }

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

    public void newGame(View v) {
        game.flagEN = dictionaryEnglish;
        if(dictionaryEnglish) { // reset and load the english lexicon
            if(lexicon == null) { // only create an english lexicon if it doesn't exist already
                lexicon = new Lexicon(getApplicationContext(), "english.txt");
            }
            else {
                lexicon.reset();
            }
            game.lexicon = lexicon;
        }
        else { // reload the alternative (dutch) lexicon
            if(alt_lexicon == null) { // only create an alternative lexicon if it doesn't exist already
                alt_lexicon = new Lexicon(getApplicationContext(), "dutch.txt");
            }
            else {
                alt_lexicon.reset();
            }
            game.lexicon = alt_lexicon;
        }
        game.resetGame();
        updateView();
        // if button was fired from settings menu, toggle settings fragment
        if(v == null || v.getId() == R.id.newGame_settings) toggleSettings();
    }

    @TargetApi(Build.VERSION_CODES.KITKAT)
    public void onChangeDictionaryLanguage(View view) {

        AlertDialog.Builder adb =  new AlertDialog.Builder(this);
        adb
                .setTitle(R.string.warning)
                .setMessage(R.string.change_language_dialog)
                .setPositiveButton(android.R.string.yes, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        dictionaryEnglish = !dictionaryEnglish;
                        newGame(null);
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

    @TargetApi(Build.VERSION_CODES.KITKAT)
    public void switchLocaleLanguage(View view) {
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
        prefs.edit().putString("system_lang", lang).apply();

        // refresh activity for locale changes to take effect
        Bundle extras = getIntent().getExtras();
        extras.putString("gameID", game.getId());
        extras.putBoolean("flag_EN", dictionaryEnglish);
        extras.putSerializable("p1", player1);
        extras.putSerializable("p2", player2);
        Intent i = new Intent(this, GameActivity.class).putExtras(extras);
        startActivity(i);
        this.finish();

    }

    // override onbackpressed so the settings fragment can be closed by pressing up/back
    @Override
    public void onBackPressed() {
        int count = getFragmentManager().getBackStackEntryCount();
        Log.v("frag_count: ", count + "");
        if (count == 0) {
            super.onBackPressed();

        } else {
            getFragmentManager().popBackStack();
        }
    }


    // static function to hide keyboard
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


    public void selectNewPlayers(View view) {
        Intent i = new Intent(this, SelectPlayersActivity.class);
        startActivity(i);
    }
}
