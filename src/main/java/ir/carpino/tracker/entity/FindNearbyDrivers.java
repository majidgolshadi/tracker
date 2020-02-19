package ir.carpino.tracker.entity;

import lombok.Builder;

import java.util.ArrayList;
import java.util.List;

@Builder
public class FindNearbyDrivers implements CsvLogData {
    final String action = "find_nearby_drivers";
    String riderId;
    String driverId;
    long timestamp;

    @Override
    public List<String> getValues() {
        return new ArrayList<String>() {{
            add(action);
            add(riderId);
            add(driverId);
            add(String.valueOf(timestamp));
        }};
    }
}
