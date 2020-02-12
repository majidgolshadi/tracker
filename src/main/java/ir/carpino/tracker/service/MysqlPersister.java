package ir.carpino.tracker.service;

import ir.carpino.tracker.entity.Rev;
import ir.carpino.tracker.entity.mysql.DriverLocation;
import ir.carpino.tracker.repository.OnlineUserRepository;
import ir.carpino.tracker.repository.tracker.DriverLocationRepository;
import lombok.extern.slf4j.Slf4j;
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
    private boolean persistTrackerData;

    private final DriverLocationRepository driverLocationRepository;
    private final OnlineUserRepository onlineUserRepository;

    public MysqlPersister(OnlineUserRepository onlineUserRepository, DriverLocationRepository driverLocationRepository) {
        this.driverLocationRepository = driverLocationRepository;
        this.onlineUserRepository = onlineUserRepository;
    }

    @Scheduled(fixedRateString = "${tracker.db.update-tracker-mysql-milliseconds-rate}")
    public void trackerDbUpdate() {
        if (!persistTrackerData) {
            return;
        }

        log.trace("update tracker db {}", onlineUserRepository.getOnlineUsers().size());
        onlineUserRepository.getOnlineUsers().forEach((id, driverData) -> {

            String currentRev = driverData.getRev().toString();
            Rev nextRev = Rev.generateRev(driverData.getRev());
            if (driverData.getRev().toString().equals(Rev.INIT_REV)) {
                currentRev = "";
            }

            LocalDateTime timestamp = LocalDateTime.ofInstant(Instant.ofEpochMilli(driverData.getDriverLocation().getTimeStamp()), ZoneId.of(TIME_ZONE_ID));
            log.debug("driver id: {} timestamp: {}", id, timestamp.atZone(ZoneId.of(TIME_ZONE_ID)));

            boolean upsertFailed = driverLocationRepository.upsert_driver_location(
                    currentRev, nextRev.toString(),
                    id, driverData.getDriverLocation().getCarCategory(),
                    driverData.getDriverLocation().getLat(), driverData.getDriverLocation().getLon(),
                    timestamp);

            if (!upsertFailed) {
                driverData.setRev(nextRev);
                return;
            }

            Optional driverLocationOpt = driverLocationRepository.findById(id);
            if (driverLocationOpt.isPresent()) {

                DriverLocation driverLocation = (DriverLocation) driverLocationOpt.get();

                if (driverData.getDriverLocation().getTimeStamp() > driverLocation.getTimestamp().atZone(ZoneId.of(TIME_ZONE_ID)).toInstant().toEpochMilli()) {
                    driverLocation.setLat(driverData.getDriverLocation().getLat());
                    driverLocation.setLon(driverData.getDriverLocation().getLon());
                    driverLocation.setCarCategory(driverData.getDriverLocation().getCarCategory());
                    driverLocation.setRev(Rev.generateRev(driverLocation.getRev()));

                    try {
                        driverLocationRepository.save(driverLocation);
                    } catch (Exception ex) {
                        log.error("persist in tracker db error {}", ex.getCause());
                    }
                }
            }

        });
    }
}
