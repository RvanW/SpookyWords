package nl.mprog.ghost;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.DatabaseUtils;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

/**
 * Created by Robbert van Waardhuizen on 8-10-2015.
 * Student number: 10543147
 */
class DBHelper extends SQLiteOpenHelper {
    private static final String DATABASE_NAME = "MyDBName.db";
    // Table players
    public static final String PLAYERS_TABLE_NAME = "players";
    private static final String PLAYERS_COLUMN_ID = "id";
    private static final String PLAYERS_COLUMN_PLAYER = "player";
    // Table games
    public static final String GAMES_TABLE_NAME = "games";
    private static final String GAMES_COLUMN_ID = "id";
    private static final String GAMES_COLUMN_DATE = "date";
    private static final String GAMES_COLUMN_LANG_FLAG = "langflag";
    private static final String GAMES_COLUMN_P1 = "p1";
    private static final String GAMES_COLUMN_P2 = "p2";
    private static final String GAMES_COLUMN_TURN = "turn";
    private static final String GAMES_COLUMN_GUESSED_LETTERS = "guessedletters";
    private static final String GAMES_COLUMN_MESSAGE = "message";
    // this is the maximum amount of games retrieved from database by getAllGames() sorted by date
    private static final String GAMES_MAX_RECENT = "20";

    private Gson gson;

    public DBHelper(Context context)
    {
        super(context, DATABASE_NAME, null, 1);

    }


    @Override
    public void onCreate(SQLiteDatabase db) {
        // Table players
        db.execSQL(
                "CREATE TABLE " + PLAYERS_TABLE_NAME +
                        "(" + PLAYERS_COLUMN_ID + " string primary key, " +
                        PLAYERS_COLUMN_PLAYER + " BLOB)"
        );
        // Table games
        db.execSQL(
                "CREATE TABLE " + GAMES_TABLE_NAME +
                        "(" + GAMES_COLUMN_ID + " string primary key, " +
                        GAMES_COLUMN_LANG_FLAG + " int, " +
                        GAMES_COLUMN_TURN + " int, " +
                        GAMES_COLUMN_GUESSED_LETTERS + " string, " +
                        GAMES_COLUMN_MESSAGE + " string, " +
                        GAMES_COLUMN_P1 + " BLOB, " +
                        GAMES_COLUMN_P2 + " BLOB, " +
                        GAMES_COLUMN_DATE + " datetime)"
        );
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS " + PLAYERS_TABLE_NAME);
        db.execSQL("DROP TABLE IF EXISTS " + GAMES_TABLE_NAME);
        onCreate(db);
    }

    public void insertPlayer(Player player)
    {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues contentValues = new ContentValues();
        gson = new Gson();
        contentValues.put(PLAYERS_COLUMN_ID, player.getId());
        contentValues.put(PLAYERS_COLUMN_PLAYER, gson.toJson(player).getBytes());
        db.insert(PLAYERS_TABLE_NAME, null, contentValues);
    }

    public void insertGame(Game game)
    {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues contentValues = new ContentValues();
        gson = new Gson();
        contentValues.put(GAMES_COLUMN_ID, game.getId()); // string
        contentValues.put(GAMES_COLUMN_LANG_FLAG, (game.flagEN ? 1 : 0)); // boolean to int
        contentValues.put(GAMES_COLUMN_P1, gson.toJson(game.p1).getBytes()); // BLOB
        contentValues.put(GAMES_COLUMN_P2, gson.toJson(game.p2).getBytes());// BLOB
        contentValues.put(GAMES_COLUMN_TURN, (game.turn() ? 1 : 0)); // bool to int
        contentValues.put(GAMES_COLUMN_GUESSED_LETTERS, game.guessedLetters); // string
        contentValues.put(GAMES_COLUMN_MESSAGE, game.message); // String
        contentValues.put(GAMES_COLUMN_DATE, getDateTime());
        db.insert(GAMES_TABLE_NAME, null, contentValues);
    }

    private Cursor getPlayerData(String id){
        SQLiteDatabase db = this.getReadableDatabase();
        return db.rawQuery("select * from " + PLAYERS_TABLE_NAME + " where " + PLAYERS_COLUMN_ID + " = ?", new String[]{id});
    }

    // get player object as gson blob and convert it to Player object
    public Player getPlayer(String id){
        Cursor res = getPlayerData(id);
        res.moveToFirst();
        byte[] blob = res.getBlob(res.getColumnIndex(PLAYERS_COLUMN_PLAYER));
        String json = new String(blob);
        gson = new Gson();
        res.close();
        return gson.fromJson(json, new TypeToken<Player>() {
        }.getType());
    }

    private Cursor getGameData(String id){
        SQLiteDatabase db = this.getReadableDatabase();
        return db.rawQuery("select * from " + GAMES_TABLE_NAME + " where " + GAMES_COLUMN_ID + " = ? ", new String[]{id});
    }

    // construct the game from a save (reload)
    // could not store the entire game object as gson, because it holds a Lexicon and two players (memory issues)
    // therefore I pulled apart the game object saving the vital variables separately and reconstruct the lexicon based on 'guessedLetters'
    public Game getGame(String id){
        Cursor res = getGameData(id);
        if(res == null) return null;
        res.moveToFirst();
        gson = new Gson();

        // player 1
        byte[] blob_player1 = res.getBlob(res.getColumnIndex(GAMES_COLUMN_P1));
        String json_player1 = new String(blob_player1);
        Player player1 = gson.fromJson(json_player1, new TypeToken<Player>() {
        }.getType());
        // player 2
        byte[] blob_player2 = res.getBlob(res.getColumnIndex(GAMES_COLUMN_P2));
        String json_player2 = new String(blob_player2);
        Player player2 = gson.fromJson(json_player2, new TypeToken<Player>() {
        }.getType());
        // turn
        boolean turn = (res.getInt(res.getColumnIndex(GAMES_COLUMN_TURN)) == 1);
        // language flag
        boolean flag = (res.getInt(res.getColumnIndex(GAMES_COLUMN_LANG_FLAG)) == 1);
        // guessed letters
        String guessedLetters = res.getString(res.getColumnIndex(GAMES_COLUMN_GUESSED_LETTERS));
        // message
        String message = res.getString(res.getColumnIndex(GAMES_COLUMN_MESSAGE));
        // date since last change;
        Date lastPlayed = null;
        DateFormat formatter = new SimpleDateFormat("dd-MM-yyyy HH:mm:ss");
        try {
            lastPlayed = formatter.parse(res.getString(res.getColumnIndex(GAMES_COLUMN_DATE)));
        } catch (ParseException e) {
            e.printStackTrace();
        }
        res.close();

        return new Game(id, player1, player2, turn, flag, guessedLetters, message, lastPlayed);
    }

    public Game getLastGame() {
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor res = db.rawQuery("select id from " + GAMES_TABLE_NAME + " ORDER BY datetime(" + GAMES_COLUMN_DATE + ") DESC LIMIT 1", null);
        if(res != null) {
            if (res.moveToFirst()) {
                String lastGameID = res.getString(res.getColumnIndex(GAMES_COLUMN_ID));
                res.close();
                return getGame(lastGameID);
            } else {
                return null;
            }
        }
        else {
            return null;
        }
    }

    public int numberOfRows(String table_name){
        SQLiteDatabase db = this.getReadableDatabase();
        return (int) DatabaseUtils.queryNumEntries(db, table_name);
    }

    public void updatePlayer(Player player)
    {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues contentValues = new ContentValues();
        gson = new Gson();
        contentValues.put(PLAYERS_COLUMN_PLAYER, gson.toJson(player).getBytes());
        contentValues.put(PLAYERS_COLUMN_ID, player.getId());
        db.update(PLAYERS_TABLE_NAME, contentValues, PLAYERS_COLUMN_ID + " = ? ", new String[] { player.getId() } );
    }

    public void updateGame(Game game)
    {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues contentValues = new ContentValues();
        gson = new Gson();
        contentValues.put(GAMES_COLUMN_ID, game.getId()); // string
        contentValues.put(GAMES_COLUMN_LANG_FLAG, game.flagEN); // boolean
        contentValues.put(GAMES_COLUMN_P1, gson.toJson(game.p1).getBytes()); // BLOB
        contentValues.put(GAMES_COLUMN_P2, gson.toJson(game.p2).getBytes());// BLOB
        contentValues.put(GAMES_COLUMN_TURN, game.turn()); // bool
        contentValues.put(GAMES_COLUMN_GUESSED_LETTERS, game.guessedLetters); // string
        contentValues.put(GAMES_COLUMN_MESSAGE, game.message); // ArrayList<String> to String
        contentValues.put(GAMES_COLUMN_DATE, getDateTime());
        db.update(GAMES_TABLE_NAME, contentValues, GAMES_COLUMN_ID + " = ? ", new String[]{game.getId()});
    }

    public Integer deletePlayer (String id)
    {
        SQLiteDatabase db = this.getWritableDatabase();
        return db.delete(PLAYERS_TABLE_NAME,
                "id = ? ",
                new String[] { id });
    }

    public void deleteGame(String id)
    {
        SQLiteDatabase db = this.getWritableDatabase();
        db.delete(GAMES_TABLE_NAME,
                "id = ? ",
                new String[] { id });
    }

    public ArrayList<Player> getAllPlayers()
    {
        ArrayList<Player> array_list = new ArrayList<>();
        //hp = new HashMap();
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor res =  db.rawQuery( "select * from " + PLAYERS_TABLE_NAME, null );
        res.moveToFirst();
        gson = new Gson();
        while(!res.isAfterLast()){
            byte[] blob = res.getBlob(res.getColumnIndex(PLAYERS_COLUMN_PLAYER));
            String json = new String(blob);
            Player playerObject = gson.fromJson(json, new TypeToken<Player>() {
            }.getType());
            array_list.add(playerObject);
            res.moveToNext();
        }
        res.close();

        return array_list;
    }

    public ArrayList<Game> getAllGames() {
        ArrayList<Game> array_list = new ArrayList<>();
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor res =  db.rawQuery("select " + GAMES_COLUMN_ID + " from " + GAMES_TABLE_NAME + " ORDER BY datetime(" + GAMES_COLUMN_DATE + ") DESC LIMIT " + GAMES_MAX_RECENT, null);
        res.moveToFirst();
        gson = new Gson();
        while(!res.isAfterLast()){
            String id = res.getString(res.getColumnIndex(GAMES_COLUMN_ID));
            Game gameObject = getGame(id);
            array_list.add(gameObject);
            res.moveToNext();
        }
        res.close();

        return array_list;
    }
    public ArrayList<String> getAllGameIds() {
        ArrayList<String> array_list = new ArrayList<>();
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor res =  db.rawQuery("select " + GAMES_COLUMN_ID + " from " + GAMES_TABLE_NAME + " ORDER BY datetime(" + GAMES_COLUMN_DATE + ") DESC LIMIT " + GAMES_MAX_RECENT, null);
        res.moveToFirst();
        while(!res.isAfterLast()){
            String id = res.getString(res.getColumnIndex(GAMES_COLUMN_ID));
            array_list.add(id);
            res.moveToNext();
        }
        res.close();

        return array_list;
    }
    private String getDateTime() {
        SimpleDateFormat dateFormat = new SimpleDateFormat("dd-MM-yyyy HH:mm:ss");
        Date date = new Date();
        return dateFormat.format(date);
    }
}