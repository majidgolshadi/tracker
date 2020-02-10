package ir.carpino.tracker.service;

import ir.carpino.tracker.entity.mysql.DriverLocation;
import ir.carpino.tracker.entity.mysql.Rev;
import ir.carpino.tracker.repository.OnlineUserRepository;
import ir.carpino.tracker.repository.tracker.DriverLocationRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import java.sql.Date;
import java.sql.Driver;
import java.util.Optional;


@Slf4j
@Service
public class MysqlPersister {

    private final DriverLocationRepository driverLocationRepository;
    private final OnlineUserRepository onlineUserRepository;

    @Value("${tracker.db.update-tracker-mysql.active}")
    private boolean persistTrackerData;

    private static int trackerCounter = 0;

    public MysqlPersister(OnlineUserRepository onlineUserRepository, DriverLocationRepository driverLocationRepository) {
        this.driverLocationRepository = driverLocationRepository;
        this.onlineUserRepository = onlineUserRepository;
    }

    @Scheduled(fixedRateString = "${tracker.db.update-tracker-mysql-milliseconds-rate}")
    public void trackerDbUpdate() {
        if (!persistTrackerData) {
            return;
        }

        trackerCounter = 0;

        log.trace("update tracker mysql db");
        onlineUserRepository.getOnlineUsers().forEach( (id, device) -> {

            trackerCounter++;

            Date timestamp = new Date(device.getTimeStamp());
            boolean upsertFailed = false;
            driverLocationRepository.upsert("", "", id, device.getLat(), device.getLon(),
                    timestamp, upsertFailed);

            if (upsertFailed) {
                Optional driverLocationOpt = driverLocationRepository.findById(id);

                if (driverLocationOpt.isPresent()) {
                    DriverLocation driver = (DriverLocation) driverLocationOpt.get();

                    if (device.getTimeStamp() > driver.getTimestamp().getTime()) {
                        DriverLocation driverlocation = new DriverLocation(
                                id,
                                timestamp,
                                device.getLat(),
                                device.getLon(),
                                Rev.generateRev()
                        );


                        try {
                            driverLocationRepository.save(driverlocation);
                        } catch (Exception ex) {
                            log.error("persist in tracker db error {}", ex.getCause());
                        }
                    }
                }
            }

        });

        log.info("update {} driver tracker data", trackerCounter);
    }
}
