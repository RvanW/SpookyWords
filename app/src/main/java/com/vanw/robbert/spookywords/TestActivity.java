package com.vanw.robbert.spookywords;

import android.annotation.TargetApi;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.Gravity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.Objects;

public class TestActivity extends AppCompatActivity implements SettingsFragment.OnFragmentInteractionListener {

    Lexicon lexicon;
    Lexicon alt_lexicon;
    Game game;

    Boolean defaultEnglish = true; // set english as default

    @Override
    public void onSaveInstanceState(Bundle savedInstanceState) {
        // Always call the superclass so it can save the view hierarchy state
        super.onSaveInstanceState(savedInstanceState);
        // Save the user's current game state
        savedInstanceState.putSerializable("game", game);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_test);
        if (savedInstanceState != null) {
            // Restore game state
            game = (Game) savedInstanceState.getSerializable("game");
            Log.v("GAME","Saved instance found");
        }
        else if (game != null) {
            Log.v("GAME", "Running game found..");
        }
        else {
            Log.v("GAME","Creating new game..");
            // create new lexicon only once here
            lexicon = new Lexicon(this, defaultEnglish ? "english.txt" : "dutch.txt");
            game = new Game(lexicon);
        }
        createSettingsFragment();
        updateView();
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

    public void onSubmit(View view) {
        EditText textInput = (EditText) findViewById(R.id.editText1);

        String letter = textInput.getText().toString().toLowerCase();
        // validate if input is filled in and not a number
        if(letter.length() != 1 || !Character.isLetter(letter.charAt(0))) {
            Toast.makeText(getApplicationContext(), "Please fill in a valid character",
                    Toast.LENGTH_SHORT).show();
        }
        else if(!game.ended()) {
            game.guess(letter); // counts as a guess if game is not ended already.
        }
        updateView();

    }

    public void updateView() {
        EditText textInput = (EditText) findViewById(R.id.editText1);
        TextView textView = (TextView) findViewById(R.id.textview1);
        TextView turnIndicator = (TextView) findViewById(R.id.turn);
        Button submitButton = (Button) findViewById(R.id.submit);

        if(game.ended()) { // check if the game has ended and update the display accordingly
            textView.setText("game ended! winner = " + game.getPlayerName(game.winner()));
            hide_keyboard(this);
            submitButton.setVisibility(View.GONE);
            textInput.setVisibility(View.GONE);
        }
        else {
            submitButton.setVisibility(View.VISIBLE);
            textInput.setVisibility(View.VISIBLE);
            textView.setText(game.guessedLetters);
            turnIndicator.setText(game.getPlayerName(game.turn()));
            turnIndicator.setGravity(game.turn() ? Gravity.NO_GRAVITY : Gravity.END);
            textInput.setText("");
        }

    }

    public void onReset(View view) {
        lexicon.reset();
        game = new Game(lexicon);
        updateView();
        toggleSettings();
    }

    @TargetApi(Build.VERSION_CODES.KITKAT)
    public void onChangeLanguage(View view) {

        AlertDialog.Builder adb =  new AlertDialog.Builder(this);
        adb
                .setTitle("Warning")
                .setMessage("Changing language will reset your current game.. Continue?")
                .setPositiveButton(android.R.string.yes, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        defaultEnglish = !defaultEnglish;
                        if(alt_lexicon == null) {
                            alt_lexicon = new Lexicon(getApplicationContext(), defaultEnglish ? "english" : "dutch" + ".txt");
                            game = new Game(alt_lexicon);
                        }
                        else if(defaultEnglish) {
                            lexicon.reset();
                            game = new Game(lexicon);
                        }
                        else {
                            alt_lexicon.reset();
                            game = new Game(alt_lexicon);
                        }
                        updateView();
                        toggleSettings();
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

    @Override
    public void onFragmentInteraction(Uri uri) {

    }

    @Override
    public void onBackPressed() {

        int count = getFragmentManager().getBackStackEntryCount();


        if (count == 0) {
            super.onBackPressed();
            //additional code
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
