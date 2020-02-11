package ir.carpino.tracker.service;

import ir.carpino.tracker.entity.mysql.DriverLocation;
import ir.carpino.tracker.entity.Rev;
import ir.carpino.tracker.repository.OnlineUserRepository;
import ir.carpino.tracker.repository.tracker.DriverLocationRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import java.sql.Date;
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
        onlineUserRepository.getOnlineUsers().forEach( (id, driverData) -> {

            log.info("driver id {}", id);
            trackerCounter++;

            Date timestamp = new Date(driverData.getDriverLocation().getTimeStamp());
            boolean upsertFailed = false;

            driverLocationRepository.upsert_driver_location(
                    driverData.getRev().toString(), Rev.generateRev(driverData.getRev()).toString(),
                    id, driverData.getDriverLocation().getCarCategory(),
                    driverData.getDriverLocation().getLat(), driverData.getDriverLocation().getLon(),
                    timestamp, upsertFailed);

            if (upsertFailed) {
                Optional driverLocationOpt = driverLocationRepository.findById(id);

                if (driverLocationOpt.isPresent()) {
                    DriverLocation driver = (DriverLocation) driverLocationOpt.get();

                    if (driverData.getDriverLocation().getTimeStamp() > driver.getTimestamp().getTime()) {
                        DriverLocation driverlocation = new DriverLocation(
                                id,
                                timestamp,
                                driverData.getDriverLocation().getLat(),
                                driverData.getDriverLocation().getLon(),
                                Rev.generateRev(),
                                driverData.getDriverLocation().getCarCategory()
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
