package ir.carpino.tracker.service;

import com.vividsolutions.jts.geom.Coordinate;
import com.vividsolutions.jts.geom.GeometryFactory;
import ir.carpino.tracker.entity.mysql.DriverLocation;
import ir.carpino.tracker.repository.DriverLocationRepository;
import ir.carpino.tracker.repository.OnlineUserRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import java.util.Date;

@Slf4j
@Service
public class MysqlPersister {

    private final DriverLocationRepository driverLocationRepository;
    private final OnlineUserRepository onlineUserRepository;
    private final GeometryFactory factory;

    public MysqlPersister(OnlineUserRepository onlineUserRepository, DriverLocationRepository driverLocationRepository) {
        this.driverLocationRepository = driverLocationRepository;
        this.onlineUserRepository = onlineUserRepository;

        factory = new GeometryFactory();
    }

    @Scheduled(fixedRateString = "${tracker.db.update-mysql-milliseconds-rate}")
    public void dbUpdate() {
        log.trace("update mysql db");
        onlineUserRepository.getOnlineUsers().forEach((userId, device) -> {

            DriverLocation driver = new DriverLocation(
                    device.getId(),
                    new Date(Long.valueOf(device.getTimeStamp())),
                    device.getStatus(),
                    device.getController(),
                    device.getCarCategory(),
                    factory.createPoint(new Coordinate(device.getLat(),device.getLon())),
                    device.getLat(),
                    device.getLon(),
                    device.getPayload()
            );

            driverLocationRepository.save(driver);

        });
    }
}
