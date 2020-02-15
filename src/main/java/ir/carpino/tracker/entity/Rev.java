package ir.carpino.tracker.entity;

import lombok.extern.slf4j.Slf4j;

import java.io.Serializable;
import java.util.UUID;

@Slf4j
public class Rev implements Serializable {
    private long seq;
    private String uuid;

    public final static String INIT_REV = "0-0";

    public Rev() {
        seq = 0;
        uuid = "0";
    }

    public Rev(long seq, String uuid) {
        this.seq = seq;
        this.uuid = uuid;
    }

    public static Rev generateRev(Rev currentRev) {
        return new Rev(currentRev.seq + 1, UUID.randomUUID().toString());
    }

    public static Rev generateRev() {
        return generateRev(new Rev(0, ""));
    }

    @Override
    public String toString() {
        return String.format("%d-%s", seq, uuid);
    }
}
