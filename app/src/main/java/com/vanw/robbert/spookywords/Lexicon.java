package com.vanw.robbert.spookywords;

import android.net.Uri;

import java.util.ArrayList;

/**
 * Created by Robbert on 28-9-2015.
 */
public class Lexicon {
    ArrayList<String> baseLexicon;
    ArrayList<String> filterLexicon;
    String lastWord;

    Lexicon(String sourcePath) {
        // open file and read into local data structure
    }
    Lexicon(ArrayList<String> testWords) {
        baseLexicon = new ArrayList<>(testWords);
        filterLexicon = new ArrayList<>(testWords);

    }
    public void filter(String filterValue) {
        ArrayList<String> tempList = new ArrayList<>();
        for(String word : filterLexicon) {
            if(word.startsWith(filterValue)) {
                tempList.add(word);
            }
        }
        filterLexicon = new ArrayList<>(tempList);
        if(count() == 1) {
            lastWord = result();
        }
    }

    public String result() {
        return filterLexicon.get(0);
    }

    public int count(){
        return filterLexicon.size();
    }

    public void reset() {
        filterLexicon = new ArrayList<>(baseLexicon);
    }
}

//Lexicon lexicon = new Lexicon("values/lexicon");