package com.vanw.robbert.spookywords;

import android.content.Context;
import android.database.Cursor;
import android.os.AsyncTask;
import android.util.Log;

/**
 * Created by Robbert van Waardhuizen on 10-10-2015.
 * Student number: 10543147
 */
public class AsyncSaveGame extends AsyncTask<Object, Integer, String>
{
    DBHelper dbHelper;
    private Context context;
    public AsyncSaveGame(Context context) {
        this.context = context;
        dbHelper = new DBHelper(context);
    }


    @Override
    protected String doInBackground(Object... params) {
        if(dbHelper == null) dbHelper = new DBHelper(context);
        if(params[0].getClass().equals(Game.class)) {
            dbHelper.updateGame((Game) params[0]);
        }
        else if(params[0].getClass().equals(Player.class)) {
            dbHelper.updatePlayer((Player) params[0]);
        }
        return "You are at PostExecute";
    }

    protected void onPreExecute (){
        Log.d("PreExceute", "On pre Exceute......");
    }

    protected String doInBackground(Void...arg0) {

        Log.d("DoINBackGround","On doInBackground...");

        for(int i=0; i<10; i++){
            Integer in = i;
            publishProgress(i);
        }
        return "You are at PostExecute";
    }

    protected void onProgressUpdate(Integer...a){
        Log.d("progress update ... ", a[0]+"");
    }

    protected void onPostExecute(String result) {
        Log.d("onPostExecute: ", ""+result);
    }
}
