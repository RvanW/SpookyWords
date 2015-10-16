package nl.mprog.ghost;

import android.content.Context;
import android.content.res.AssetManager;
import android.util.Log;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.Serializable;
import java.util.HashSet;
import java.util.Iterator;

/**
 * Created by Robbert van Waardhuizen on 28-9-2015.
 * Student number: 10543147
 */
class Lexicon implements Serializable {
    private HashSet<String> baseLexicon;
    private HashSet<String> filterLexicon;
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
    }
    public void filter(String filterValue) {
        // I refactored this to add words to the filtered list initially, instead of removing words from the big base lexicon
        if (filterLexicon == null) {
            filterLexicon = new HashSet<>();
            for (String word : baseLexicon) {
                if (word.startsWith(filterValue)) { // add words that start with value
                    filterLexicon.add(word);
                }
            }
        }

        else {
            Iterator setIterator = filterLexicon.iterator();
            while (setIterator.hasNext()) {
                String word = (String) setIterator.next();
                if (!word.startsWith(filterValue)) { // delete words that do not start with value
                    setIterator.remove();
                } else if (word.length() > 3 && word.equals(filterValue)) { // if word longer than 3 chars match filtervalue.. game over
                    lastWord = word;
                    break;
                }
            }
            if (count() == 1) {
                lastWord = result();
            }
        }
    }

    private String result() {
        return filterLexicon.iterator().next();
    }

    public int count(){
        if(filterLexicon == null) return -1;
        return filterLexicon.size();
    }

    public void reset() {
        filterLexicon = null;
        lastWord = null;
    }
}