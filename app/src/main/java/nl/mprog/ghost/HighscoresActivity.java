package nl.mprog.ghost;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;

import java.util.ArrayList;
import java.util.Collections;

public class HighscoresActivity extends AppCompatActivity implements AdapterView.OnItemClickListener {
    private HighscoreListViewAdapter highscoreListViewAdapter;
    private ListView listView;
    private ArrayList<Player> playerList;
    private DBHelper myDB;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_highscores);
        setTitle(getResources().getString(R.string.title_activity_highscores));
        myDB = new DBHelper(this);
        playerList = myDB.getAllPlayers();

        Collections.sort(playerList, Player.COMPARE_BY_SCORE);

        highscoreListViewAdapter = new HighscoreListViewAdapter(this, playerList);
        listView = (ListView) findViewById(R.id.listView);
        listView.setAdapter(highscoreListViewAdapter);
        listView.setOnItemClickListener(this);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_highscores, menu);
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

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        // nothing yet
    }
}
