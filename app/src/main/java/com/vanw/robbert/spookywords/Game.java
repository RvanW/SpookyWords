package com.vanw.robbert.spookywords;

import android.text.Html;
import android.util.Log;

import java.io.Serializable;
import java.util.ArrayList;

/**
 * Created by Robbert on 28-9-2015.
 */
public class Game implements Serializable {
    String id;
    public String getId() { return this.id; }
    Lexicon lexicon;
    boolean flagEN;
    boolean turn = true;
    boolean winner;
    public String guessedLetters = "";
    public String message;
    Player p1;
    Player p2;

    Game(Lexicon lexicon) {
        this.lexicon = lexicon;
        this.id = UUIDGenerator.nextUUID();
    }

    // constructor for replicating saved game state
    Game(String id, Player player1, Player player2, boolean turn, boolean flag, String guessedLetters, String message ) {
        this.id = id;
        this.p1 = player1;
        this.p2 = player2;
        this.turn = turn;
        this.flagEN = flag;
        this.guessedLetters = guessedLetters;

        this.message = message;

    }
    public void resetGame() {
        this.lexicon.reset();
        turn = true;
        guessedLetters = "";
        message = null;
    }


    public void guess(String letter) {

        guessedLetters += letter;
        lexicon.filter(guessedLetters);

        if(!ended()) {
            turn = !turn; // I only want to switch turns if the game didn't end with this guess.
        }
        else{ // when it does end this turn..
            // convert the last letter to a red color
            letter = "<font color=#ff446b>"+letter+"</font>";
            guessedLetters = guessedLetters.substring(0,guessedLetters.length() - 1) + letter;
        }

    }
    public boolean turn(){
        return turn;
    }
    public boolean ended() {
        if(lexicon != null) {
            if (lexicon.lastWord != null && lexicon.lastWord.equals(guessedLetters.replaceAll("\\<.*?>", ""))) { // Player spelled the full word..
                winner = !turn(); // Winner will be the opposite player
                message = setMessage(true, guessedLetters, getPlayerObject(winner()).getName());
                return true;
            } else if (lexicon.count() == 0) { // Or the player spelled something not in our lexicon
                winner = !turn(); // Winner will be the opposite player
                message = setMessage(false, guessedLetters, getPlayerObject(winner()).getName());
                return true;
            }
        }
        message = null;
        return false;
    }

    public String setMessage(boolean isAWord, String guessedLetters, String winnerName) {
        String fullMessage = "";
        fullMessage += (isAWord ? "is_a_word" : "is_no_word") + "||";
        fullMessage += (guessedLetters.replaceAll("\\<.*?>", "") + "||");
        fullMessage += (winnerName);
        if (!isAWord && lexicon.lastWord != null) fullMessage += ("||"+lexicon.lastWord);
        return fullMessage;
    }
    public boolean winner() {
        return winner;
    }

    public Player awardAndSaveWinner() {
        Player winner = getPlayerObject(winner());
        winner.addScore(1);
        Log.v("WINNER: ", "winner awarded!");
        return winner;
    }

    public Player getPlayerObject(boolean turn) {
        if(turn) {
            return p1;
        }
        return p2;
    }

    public void setPlayers(Player p1, Player p2) {
        this.p1 = p1;
        this.p2 = p2;
    }
}
