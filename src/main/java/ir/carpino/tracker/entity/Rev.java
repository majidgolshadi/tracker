package ir.carpino.tracker.entity;

import java.io.Serializable;
import java.util.UUID;

public class Rev implements Serializable {
    private long seq;
    private String uuid;

    public Rev(String currentRev) {
        String[] seqUuid = currentRev.split("-", 1);

        if (seqUuid.length < 1) {
            throw new RuntimeException("Invalid rev string structure");
        }

        this.seq = Integer.parseInt(seqUuid[0]);
        this.uuid = seqUuid[1];
    }

    private Rev(long seq, String uuid) {
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
