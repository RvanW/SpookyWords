package nl.mprog.ghost;

/**
 * Created by Robbert van Waardhuizen on 8-10-2015.
 * Student number: 10543147
 */
import java.util.UUID;

class UUIDGenerator {
    public static String nextUUID() {
        return UUID.randomUUID().toString();    }
}
