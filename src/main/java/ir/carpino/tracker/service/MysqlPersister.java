package ir.carpino.tracker.service;

import com.vividsolutions.jts.geom.Coordinate;
import com.vividsolutions.jts.geom.GeometryFactory;
import ir.carpino.tracker.entity.mysql.DriverLocation;
import ir.carpino.tracker.repository.bi.BiDriverLocationRepository;
import ir.carpino.tracker.repository.tracker.DriverLocationRepository;
import ir.carpino.tracker.repository.OnlineUserRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import java.util.Date;

@Slf4j
@Service
public class MysqlPersister {

    private final DriverLocationRepository driverLocationRepository;
    private final BiDriverLocationRepository biDriverLocationRepository;
    private final OnlineUserRepository onlineUserRepository;

    private final GeometryFactory factory;

    public MysqlPersister(OnlineUserRepository onlineUserRepository, DriverLocationRepository driverLocationRepository, BiDriverLocationRepository biDriverLocationRepository) {
        this.driverLocationRepository = driverLocationRepository;
        this.biDriverLocationRepository = biDriverLocationRepository;
        this.onlineUserRepository = onlineUserRepository;

        factory = new GeometryFactory();
    }
//
//    @Scheduled(fixedRateString = "${tracker.db.update-bi-mysql-milliseconds-rate}")
//    public void biDbUpdate() {
//        log.trace("update bi mysql db");
//        onlineUserRepository.getOnlineUsers().forEach((userId, device) -> {
//
//            biDriverLocationRepository.save(BiDriverLocation.builder()
//                    .driverId(device.getId())
//                    .carCategory(device.getCarCategory())
//                    .lat(device.getLat())
//                    .lon(device.getLon())
//                    .status(device.getStatus())
//                    .controller(device.getController())
//                    .timestamp(new Date(device.getLongTimestamp()))
//                    .build());
//        });
//    }


    @Scheduled(fixedRateString = "${tracker.db.update-tracker-mysql-milliseconds-rate}")
    public void trackerDbUpdate() {
        log.trace("update tracker mysql db");
        onlineUserRepository.getOnlineUsers().forEach((userId, device) -> {

            try {

                driverLocationRepository.save(
                        new DriverLocation(
                                device.getId(),
                                new Date(Long.valueOf(device.getTimeStamp())),
                                device.getStatus(),
                                device.getController(),
                                device.getCarCategory(),
                                factory.createPoint(new Coordinate(device.getLat(), device.getLon())),
                                device.getF_lat(),
                                device.getF_lon(),
                                device.getPayload()
                        ));
            } catch (Exception ex) {
                System.out.print("saalam");
            }
        });
    }
}
