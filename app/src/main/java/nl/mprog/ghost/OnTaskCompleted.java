package nl.mprog.ghost;

import java.util.ArrayList;

/**
 * Created by Robbert van Waardhuizen on 14-10-2015.
 * Student number: 10543147
 * Simple interface to callback AsyncGetGames
 */
interface OnTaskCompleted{
    void onTaskCompleted(ArrayList<Game> result);
}
