package com.vanw.robbert.spookywords;

import android.app.Activity;
import android.content.Context;
import android.content.res.AssetManager;
import android.net.Uri;
import android.util.Log;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;

/**
 * Created by Robbert on 28-9-2015.
 */
public class Lexicon implements Serializable {
    HashSet<String> baseLexicon;
    HashSet<String> filterLexicon;
    String lastWord;

    Lexicon(Context context, String sourcePath)  {
        // open file and read into local data structure
        AssetManager am = context.getAssets();
        baseLexicon = new HashSet<>();
        BufferedReader reader = null;
        try {
            reader = new BufferedReader(
                    new InputStreamReader(am.open(sourcePath)));

            // do reading, usually loop until end of file reading
            String mLine = reader.readLine();
            while (mLine != null) {
                //process line
                baseLexicon.add(mLine);
                mLine = reader.readLine();
            }
        } catch (IOException e) {
            //log the exception
            Log.e("IOExeption_TAG", "Catched an error..", e);
        } finally {
            if (reader != null) {
                try {
                    reader.close();
                } catch (IOException e) {
                    //log the exception
                    Log.e("IOExeption_TAG", "Catched an error..", e);
                }
            }
        }
        filterLexicon = new HashSet<>(baseLexicon);

    }
    Lexicon(ArrayList<String> testWords) {
        baseLexicon = new HashSet<>(testWords);
        filterLexicon = new HashSet<>(testWords);

    }
    public void filter(String filterValue) {
        Iterator setIterator = filterLexicon.iterator();
        while(setIterator.hasNext()) {
            String word = (String) setIterator.next();
            if(!word.startsWith(filterValue)) {
                setIterator.remove();
            }
        }
        if(count() == 1) {
            lastWord = result();
        }
    }

    public String result() {
        return filterLexicon.iterator().next();
    }

    public int count(){
        return filterLexicon.size();
    }

    public void reset() {
        filterLexicon = new HashSet<>(baseLexicon);
    }
}

//Lexicon lexicon = new Lexicon("values/lexicon");