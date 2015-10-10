package com.vanw.robbert.spookywords;

/**
 * Created by Robbert on 8-10-2015.
 */
import java.util.UUID;

public class UUIDGenerator {
    public static String nextUUID() {
        return UUID.randomUUID().toString();    }
}
