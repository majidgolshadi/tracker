package ir.carpino.tracker.entity.kafka;

import ir.carpino.tracker.entity.rest.Driver;
import lombok.Data;
import lombok.Getter;

@Data
public class NearbyDriverLog {
    private String rideId;
    private String driverId;
    private double srcLat;
    private double srcLon;
    private double driverLat;
    private double driverLon;
    private long timestamp;

    public NearbyDriverLog(String rideId, double srcLat, double srcLon, Driver driver) {
        this.rideId = rideId;
        driverId = driver.getId();
        this.srcLat = srcLat;
        this.srcLon = srcLon;
        driverLat = driver.getLat();
        driverLon = driver.getLon();
        timestamp = System.currentTimeMillis();
    }
}
