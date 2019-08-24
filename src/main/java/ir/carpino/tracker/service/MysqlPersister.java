package ir.carpino.tracker.service;

import com.vividsolutions.jts.geom.Coordinate;
import com.vividsolutions.jts.geom.GeometryFactory;
import ir.carpino.tracker.entity.mysql.BiDriverLocation;
import ir.carpino.tracker.entity.mysql.DriverLocation;
import ir.carpino.tracker.repository.bi.BiDriverLocationRepository;
import ir.carpino.tracker.repository.tracker.DriverLocationRepository;
import ir.carpino.tracker.repository.OnlineUserRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import java.util.Date;

@Slf4j
@Service
public class MysqlPersister {

    private final DriverLocationRepository driverLocationRepository;
    private final BiDriverLocationRepository biDriverLocationRepository;
    private final OnlineUserRepository onlineUserRepository;

    @Value("${tracker.db.update-tracker-mysql.active}")
    private boolean persistTrackerData;

    private final GeometryFactory factory;

    public MysqlPersister(OnlineUserRepository onlineUserRepository, DriverLocationRepository driverLocationRepository, BiDriverLocationRepository biDriverLocationRepository) {
        this.driverLocationRepository = driverLocationRepository;
        this.biDriverLocationRepository = biDriverLocationRepository;
        this.onlineUserRepository = onlineUserRepository;

        factory = new GeometryFactory();
    }

    @Scheduled(fixedRateString = "${tracker.db.update-bi-mysql-milliseconds-rate}")
    public void biDbUpdate() {
        log.trace("update bi mysql db");

        try {
            onlineUserRepository.getOnlineUsers().forEach((userId, device) -> {
                BiDriverLocation driverLocation = new BiDriverLocation();
                driverLocation.setDriverId(device.getId());
                driverLocation.setCarCategory(device.getCarCategory());
                driverLocation.setLat(device.getLat());
                driverLocation.setLon(device.getLon());
                driverLocation.setStatus(device.getStatus());
                driverLocation.setController(device.getController());
                driverLocation.setTimestamp(new Date(device.getLongTimestamp()));

                biDriverLocationRepository.save(driverLocation);
            });
        } catch (Exception ex) {
            log.error("persist in bi db error {}", ex.getCause());
        }
    }


    @Scheduled(fixedRateString = "${tracker.db.update-tracker-mysql-milliseconds-rate}")
    public void trackerDbUpdate() {
        if (!persistTrackerData) {
            return;
        }

        log.trace("update tracker mysql db");
        onlineUserRepository.getOnlineUsers().forEach((userId, device) -> {

            DriverLocation driverlocation = new DriverLocation(
                    device.getId(),
                    new Date(Long.valueOf(device.getTimeStamp())),
                    device.getStatus(),
                    device.getController(),
                    device.getCarCategory(),
                    factory.createPoint(new Coordinate(device.getLat(), device.getLon())),
                    device.getF_lat(),
                    device.getF_lon(),
                    device.getPayload()
            );

            try {
                driverLocationRepository.save(driverlocation);
            } catch (Exception ex) {
                log.error("persist in tracker db error {}", ex.getCause());
            }
        });
    }
}
