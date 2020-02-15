package ir.carpino.tracker.service;

import ir.carpino.tracker.entity.Rev;
import ir.carpino.tracker.entity.hazelcast.DriverData;
import ir.carpino.tracker.entity.mysql.DriverLocation;
import ir.carpino.tracker.repository.OnlineUserRepository;
import ir.carpino.tracker.repository.tracker.DriverLocationRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.Optional;


@Slf4j
@Service
public class MysqlPersister {

    private final String TIME_ZONE_ID = "Asia/Tehran";

    @Value("${tracker.db.update-tracker-mysql.active}")
    public boolean persistTrackerData = false;

    private DriverLocationRepository driverLocationRepository;
    private OnlineUserRepository onlineUserRepository;

    @Autowired
    public MysqlPersister(OnlineUserRepository onlineUserRepository, DriverLocationRepository driverLocationRepository) {
        this.driverLocationRepository = driverLocationRepository;
        this.onlineUserRepository = onlineUserRepository;
    }

    @Scheduled(fixedRateString = "${tracker.db.update-tracker-mysql-milliseconds-rate}")
    public void trackerDbUpdate() {
        if (!persistTrackerData) {
            return;
        }

        log.info("update tracker db with {} drivers", onlineUserRepository.getOnlineUsers().size());
        onlineUserRepository.getOnlineUsers().forEach((id, driverData) -> {

            String currentRev = driverData.getRev().toString();
            Rev nextRev = Rev.generateRev(driverData.getRev());

            if (driverData.getRev().toString().equals(Rev.INIT_REV)) {
                currentRev = "";
            }

            LocalDateTime timestamp = LocalDateTime.ofInstant(Instant.ofEpochMilli(driverData.getDriverLocation().getTimeStamp()), ZoneId.of(TIME_ZONE_ID));
            log.debug("driver id: {} timestamp: {}", id, timestamp.atZone(ZoneId.of(TIME_ZONE_ID)));

            boolean done = driverLocationRepository.upsert_driver_location(
                    currentRev, nextRev.toString(),
                    id, driverData.getDriverLocation().getCarCategory(),
                    driverData.getDriverLocation().getLat(), driverData.getDriverLocation().getLon(),
                    timestamp);

            if (done) {
                // update driver data revision
                driverData.setRev(nextRev);
                return;
            }

            try {
                resolveConflict(driverData);
            } catch (Exception ex) {
                log.error("conflict resolution error ", ex.getCause());
            }

        });
    }

    public boolean resolveConflict(DriverData driverData) {
        String id = driverData.getDriverLocation().getId();
        Optional<DriverLocation> driverLocationOpt = driverLocationRepository.findById(id);

        if (!driverLocationOpt.isPresent()) {
            return false;
        }

        DriverLocation driverLocation = driverLocationOpt.get();

        // data in DB is newer than what data that exist in memory
        long inMemoryDataTimestamp = driverData.getDriverLocation().getTimeStamp();
        long dbDataTimestamp = driverLocation.getTimestamp().atZone(ZoneId.of(TIME_ZONE_ID)).toInstant().toEpochMilli();
        if (dbDataTimestamp > inMemoryDataTimestamp) {
            log.warn("db data timestamp {} is newer than in-memory one {}", dbDataTimestamp, inMemoryDataTimestamp);
            return false;
        }

        driverLocation.setLat(driverData.getDriverLocation().getLat());
        driverLocation.setLon(driverData.getDriverLocation().getLon());
        driverLocation.setCarCategory(driverData.getDriverLocation().getCarCategory());
        driverLocation.setRev(Rev.generateRev(driverLocation.getRev()));
        driverLocation.setTimestamp(LocalDateTime.ofInstant(Instant.ofEpochMilli(driverData.getDriverLocation().getTimeStamp()), ZoneId.of(TIME_ZONE_ID)));

        driverLocationRepository.save(driverLocation);

        // update in memory rev data
        driverData.setRev(driverLocation.getRev());
        return true;
    }
}