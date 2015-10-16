package nl.mprog.ghost;

import android.content.Context;
import android.os.AsyncTask;
import android.util.Log;

/**
 * Created by Robbert van Waardhuizen on 10-10-2015.
 * Student number: 10543147
 * Saving game on every turn, doing this async will help smooth gameplay
 */
class AsyncSaveGame extends AsyncTask<Object, Integer, String>
{
    private DBHelper dbHelper;
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
}
