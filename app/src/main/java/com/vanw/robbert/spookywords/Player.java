package com.vanw.robbert.spookywords;

import java.io.Serializable;
import java.util.concurrent.atomic.AtomicLong;

/**
 * Created by Robbert on 1-10-2015.
 */
public class Player implements Serializable {
    String name;

    static final AtomicLong NEXT_ID = new AtomicLong(0);
    final long id = NEXT_ID.getAndIncrement();
    public long getId() { return id;}

    public Player(String name) {
        this.name = name;
    }
    public String getName() {
        return this.name;
    }
    public void setName(String name) {
        this.name = name;
    }
}
