package nl.mprog.ghost;

import java.io.Serializable;
import java.util.Comparator;

/**
 * Created by Robbert van Waardhuizen on 1-10-2015.
 * Student number: 10543147
 */
public class Player implements Serializable {
    private String name;
    private int score;
    private String id;
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
    public void addScore() {
        score += 1;
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

