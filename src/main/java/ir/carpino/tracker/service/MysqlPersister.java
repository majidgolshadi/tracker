package ir.carpino.tracker.service;

import ir.carpino.tracker.entity.mysql.DriverLocation;
import ir.carpino.tracker.repository.DriverLocationRepository;
import ir.carpino.tracker.repository.OnlineUserRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.geo.Point;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import java.util.Date;

@Slf4j
@Service
public class MysqlPersister {

    private final DriverLocationRepository driverLocationRepository;
    private final OnlineUserRepository onlineUserRepository;

    public MysqlPersister(OnlineUserRepository onlineUserRepository, DriverLocationRepository driverLocationRepository) {
        this.driverLocationRepository = driverLocationRepository;
        this.onlineUserRepository = onlineUserRepository;
    }

    @Scheduled(fixedRateString = "${tracker.db.update-mysql-milliseconds-rate}")
    public void dbUpdate() {
        log.info("update mysql db");
        onlineUserRepository.getOnlineUsers().forEach((userId, device) -> {

            DriverLocation driver = new DriverLocation(
                    device.getId(),
                    new Date(Long.valueOf(device.getTimeStamp())),
                    device.getStatus(),
                    device.getController(),
                    device.getCarCategory(),
                    new Point(device.getLat(),device.getLon()),
                    device.getLat(),
                    device.getLon(),
                    device.getPayload()
            );

            driverLocationRepository.save(driver);

        });
    }
}
