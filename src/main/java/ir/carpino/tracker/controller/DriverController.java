package ir.carpino.tracker.controller;

import com.fasterxml.jackson.core.JsonProcessingException;
import ir.carpino.tracker.controller.exception.CarCategoryNotFoundException;
import ir.carpino.tracker.controller.exception.DriverNotFoundException;
import ir.carpino.tracker.entity.hazelcast.DriverData;
import ir.carpino.tracker.entity.mqtt.MqttDriverLocation;
import ir.carpino.tracker.entity.mqtt.NearbyDriverLog;
import ir.carpino.tracker.entity.rest.Driver;
import ir.carpino.tracker.repository.OnlineUserRepository;
import ir.carpino.tracker.utils.GeoHelper;
import lombok.extern.slf4j.Slf4j;
import org.eclipse.paho.client.mqttv3.IMqttClient;
import org.eclipse.paho.client.mqttv3.MqttException;
import org.eclipse.paho.client.mqttv3.MqttPersistenceException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Slf4j
@RestController
public class DriverController {

    private GeoHelper geoHelper;
    private OnlineUserRepository repository;
    private final IMqttClient mqttClient;

    @Value("#{'${tracker.driver.car-category-type}'.split(',')}")
    private List<String> categoryType;

    @Value("${tracker.mqtt.near-driver-log-topic}")
    private String nearbyDriversTopic;

    @Autowired
    public DriverController(IMqttClient mqttClient, OnlineUserRepository repository) {
        this.mqttClient = mqttClient;
        this.repository = repository;
        geoHelper = new GeoHelper();
    }

    /**
     * @param userLat
     * @param userLon
     * @param distance
     * @param category optional return all types if it's null
     * @return
     */
    @GetMapping("/v1/driver/near")
    public List<Driver> nearDrivers(@RequestParam(value = "lat") double userLat, @RequestParam(value = "lon") double userLon,
                                    @RequestParam(value = "distance") double distance, @RequestParam(value = "category", required = false)
                                            String category, @RequestParam(value = "rideId", required = false) String rideId) {

        final String lowerCaseCategory = category != null ? category.toLowerCase() : null;

        if (lowerCaseCategory != null && !categoryType.contains(lowerCaseCategory)) {
            throw new CarCategoryNotFoundException(String.format("category type %s not found", lowerCaseCategory));
        }

        return repository.getOnlineUsers()
                .entrySet()
                .stream()
                .filter(entry -> {
                    if (lowerCaseCategory == null || lowerCaseCategory.isEmpty())
                        return true;

                    if (entry.getValue().getDriverLocation().getCarCategory().toLowerCase().equals(lowerCaseCategory))
                        return true;

                    return false;
                })
                .filter(entry -> distance > geoHelper.distanceFromKM(entry.getValue().getDriverLocation().getLat(), entry.getValue().getDriverLocation().getLon(), userLat, userLon))
                .map(entry -> {
                    Driver driver = Driver.builder()
                            .id(entry.getKey())
                            .lat(entry.getValue().getDriverLocation().getLat())
                            .lon(entry.getValue().getDriverLocation().getLon())
                            .category(entry.getValue().getDriverLocation().getCarCategory())
                            .build();

                    try {
                        mqttClient.publish(nearbyDriversTopic, new NearbyDriverLog(rideId, userLat, userLon, driver).toMqttMessage());
                    } catch (JsonProcessingException e) {
                        log.error("nearby driver log to json error: {}", e.getMessage());
                    } catch (MqttPersistenceException e) {
                        log.error("mqtt persistency error: {}", e.getMessage());
                    } catch (MqttException e) {
                        log.error("publish to mqtt error: {}", e.getMessage());
                    }

                    return driver;
                }).collect(Collectors.toList());
    }

    @GetMapping("/v1/driver/location")
    public List<Driver> driversLocation(@RequestParam(value = "driverId", required = false) String driverId) {
        List<Driver> drivers = new ArrayList<>();

        if (driverId != null) {
            DriverData driverData = repository.getOnlineUsers().get(driverId);

            if (driverData == null) {
                throw new DriverNotFoundException(String.format("driver %s Not Found", driverId));
            }

            MqttDriverLocation driver = driverData.getDriverLocation();
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
}

