package ir.carpino.tracker.controller;

import ir.carpino.tracker.controller.exception.CarCategoryNotFoundException;
import ir.carpino.tracker.entity.FindNearbyDrivers;
import ir.carpino.tracker.entity.mqtt.MqttDriverLocation;
import ir.carpino.tracker.entity.rest.Driver;
import ir.carpino.tracker.repository.OnlineUserRepository;
import ir.carpino.tracker.utils.CsvLogger;
import ir.carpino.tracker.utils.GeoHelper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.PostConstruct;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Slf4j
@RestController
public class DriverController {

    private GeoHelper geoHelper;
    private OnlineUserRepository repository;
    private CsvLogger<FindNearbyDrivers> findNearbyDriversLogger;

    @Value("#{'${tracker.driver.car-category-type}'.split(',')}")
    private List<String> categoryType;

    @Value("${tracker.csv-file-path.find-nearby-drivers}")
    private String findNearbyDriversCsvFilePath;

    @Autowired
    public DriverController(OnlineUserRepository repository) {
        this.repository = repository;
    }

    @PostConstruct
    private void initLogger() throws IOException {
        File findNearbyDriversCsvFile = new File(findNearbyDriversCsvFilePath);
        findNearbyDriversLogger = new CsvLogger<>(findNearbyDriversCsvFile);
        geoHelper = new GeoHelper();
    }

    /**
     * @param userLat
     * @param userLog
     * @param distance
     * @param category optional return all types if it's null
     * @return
     */

    @GetMapping("/v1/driver/near")
    public List<Driver> nearDrivers(@RequestParam(value = "lat") double userLat, @RequestParam(value = "lon") double userLog,
                                    @RequestParam(value = "distance") double distance, @RequestParam(value = "category", required = false)
                                            String category, @RequestParam(value = "rideId", required = false) String rideId) {

        if (category != null && !categoryType.contains(category)) {
            throw new CarCategoryNotFoundException(String.format("category type %s not found", category));
        }

        return repository.getOnlineUsers()
                .entrySet()
                .stream()
                .filter(entry -> {
                    if (category == null || category.isEmpty())
                        return true;

                    if (entry.getValue().getDriverLocation().getCarCategory().equals(category))
                        return true;

                    return false;
                })
                .filter(entry -> distance > geoHelper.distanceFromKM(entry.getValue().getDriverLocation().getLat(), entry.getValue().getDriverLocation().getLon(), userLat, userLog))
                .map(entry -> {
                    try {
                        findNearbyDriversLogger.log(FindNearbyDrivers.builder()
                                .driverId(entry.getValue().getDriverLocation().getId())
                                .riderId(rideId)
                                .timestamp(entry.getValue().getDriverLocation().getTimeStamp())
                                .build());
                    } catch (IOException e) {
                        log.error("write into the csv error ", e.getCause());
                    }

                    return Driver.builder()
                            .id(entry.getKey())
                            .lat(entry.getValue().getDriverLocation().getLat())
                            .lon(entry.getValue().getDriverLocation().getLon())
                            .category(entry.getValue().getDriverLocation().getCarCategory())
                            .build();
                }).collect(Collectors.toList());
    }

    @GetMapping("/v1/driver/location")
    public List<Driver> driversLocation(@RequestParam(value = "driverId", required = false) String driverId) {
        List<Driver> drivers = new ArrayList<>();

        if (driverId != null) {
            MqttDriverLocation driver = repository.getOnlineUsers().get(driverId).getDriverLocation();
            drivers.add(
                    Driver.builder()
                            .id(driver.getId())
                            .lat(driver.getLat())
                            .lon(driver.getLon())
                            .category(driver.getCarCategory())
                            .build()
            );

            return drivers;
        }

        return repository.getOnlineUsers()
                .values().stream()
                .map(device -> Driver.builder().id(
                        device.getDriverLocation().getId())
                        .lat(device.getDriverLocation().getLat())
                        .lon(device.getDriverLocation().getLon())
                        .category(device.getDriverLocation().getCarCategory())
                        .build()
                ).collect(Collectors.toList());
    }

    @PostMapping("/v1/driver/logs/flush")
    public void flushLogs() throws IOException {
        findNearbyDriversLogger.flush();
        log.info("flush driver logs into csv");
    }
}

