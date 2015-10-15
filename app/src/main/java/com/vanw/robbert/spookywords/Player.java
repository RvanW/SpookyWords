package com.vanw.robbert.spookywords;

import android.content.Context;
import android.content.SharedPreferences;
import android.util.Log;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.io.Serializable;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.concurrent.atomic.AtomicLong;
import java.util.concurrent.atomic.AtomicReference;

/**
 * Created by Robbert van Waardhuizen on 1-10-2015.
 * Student number: 10543147
 */
public class Player implements Serializable {
    String name;
    int score;
    String id;
    Integer avatarId;

    public String getId() {
        return this.id;
    }

    public Player(String name, Integer avatar) {
        this.name = name;
        this.score = 0;
        this.id = UUIDGenerator.nextUUID();
        this.avatarId = avatar;
    }
    public String getName() {
        return this.name;
    }
    public void setName(String name) {
        this.name = name;
    }
    public int getScore() {
        return this.score;
    }
    public void addScore(int points) {
        score += points;
    }

    
    // for sorting
    public static Comparator<Player> COMPARE_BY_NAME = new Comparator<Player>() {
        public int compare(Player one, Player other) {
            return one.name.compareTo(other.name);
        }
    };

    public static Comparator<Player> COMPARE_BY_SCORE = new Comparator<Player>() {
        public int compare(Player one, Player other) {
            return one.getScore() < other.getScore() ? +1 : one.getScore() > other.getScore() ? -1 : 0;
        }
    };
}

