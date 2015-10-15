package com.vanw.robbert.spookywords;

import android.app.ProgressDialog;
import android.content.Context;
import android.os.AsyncTask;
import android.util.Log;

import java.util.ArrayList;
import java.util.Collections;
import java.util.logging.Handler;
import java.util.logging.LogRecord;

/**
 * Created by Robbert van Waardhuizen on 10-10-2015.
 * Student number: 10543147
 */
public class AsyncGetGames extends AsyncTask<Void, Integer, ArrayList<Game>>
{
    DBHelper dbHelper;
    private Context context;
    ProgressDialog progressDialog;

    private OnTaskCompleted listener;
    public AsyncGetGames(Context context, OnTaskCompleted listener) {
        this.context = context;
        this.listener = listener;
        dbHelper = new DBHelper(context);

        // open up progress dialog
        progressDialog = new ProgressDialog(context);
        progressDialog.setTitle("Loading games..");
        progressDialog.setProgressStyle(ProgressDialog.STYLE_HORIZONTAL);
        progressDialog.setProgress(0);
        progressDialog.setMax(dbHelper.numberOfRows(DBHelper.GAMES_TABLE_NAME));
        progressDialog.show();
    }

    @Override
    protected final ArrayList<Game> doInBackground(Void... params) {
        if(dbHelper == null) dbHelper = new DBHelper(context);
        ArrayList<String> id_list = dbHelper.getAllGameIds();
        ArrayList<Game> gameList = new ArrayList<>();
        for(String id : id_list) {
            gameList.add(dbHelper.getGame(id));
            Log.d("execute"," getting game in background..");
            publishProgress(1);
        }

        return gameList;
    }

    protected void onPreExecute (){
        Log.d("PreExceute", "On pre Exceute......");
    }

    protected void onProgressUpdate(Integer...a){
        Log.d("progress update ... ", a[0]+"");
    }

    protected void onPostExecute(ArrayList<Game> result) {
        Log.d("onPostExecute: ", "" + result);
        Collections.sort(result, Game.COMPARE_BY_DATE);
        listener.onTaskCompleted(result);
        
        if (progressDialog.isShowing()){
            progressDialog.dismiss();
        }
    }


}
