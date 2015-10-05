package com.vanw.robbert.spookywords;

import java.io.Serializable;
import java.util.ArrayList;

/**
 * Created by Robbert on 28-9-2015.
 */
public class Game implements Serializable {
    Lexicon lexicon;
    boolean turn = true;
    boolean winner;
    public String guessedLetters = "";
    String p1;
    String p2;

    Game() {
        // empty constructor..
    }
    Game(Lexicon lexicon) {
        this.lexicon = lexicon;
    }
    public void guess(String letter) {
        guessedLetters += letter;
        lexicon.filter(guessedLetters);

        if(!ended()) turn = !turn; // I don't want to switch turns if the game ends with this guess.
    }
    public boolean turn(){
        return turn;
    }
    public boolean ended() {
        if (lexicon.lastWord != null && lexicon.lastWord.equals(guessedLetters) // Player spelled the full word..
                || lexicon.count() == 0) { // Or the player spelled something not in our lexicon
            winner = !turn(); // Winner will be the opposite player
            return true;
        }
        return false;
    }
    public boolean winner() {
        return winner;
    }

    public String getPlayerName(boolean turn) { // some fake player name retrieval..
        if(turn) {
            return p1;
        }
        return p2;
    }
    public void setPlayerNames(String p1, String p2) {
        this.p1 = p1;
        this.p2 = p2;
    }
}
