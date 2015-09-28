package com.vanw.robbert.spookywords;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;

public class TestActivity extends AppCompatActivity {
    ArrayList<String> testWords = new ArrayList<>();

    Lexicon lexicon;
    Game game;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_test);
        testWords.add("spooky");
        testWords.add("pizza");
        testWords.add("hamburger");
        testWords.add("words");
        lexicon = new Lexicon(testWords);
        game = new Game(lexicon);
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
            return true;
        }

        return super.onOptionsItemSelected(item);
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
            submitButton.setVisibility(View.GONE);
            textInput.setVisibility(View.GONE);
        }
        else {
            submitButton.setVisibility(View.VISIBLE);
            textInput.setVisibility(View.VISIBLE);
            textView.setText(game.guessedLetters);
            turnIndicator.setText(game.getPlayerName(game.turn()));
            textInput.setText("");
        }

    }

    public void onReset(View view) {
        lexicon.reset();
        game = new Game(lexicon);
        updateView();
    }
}
