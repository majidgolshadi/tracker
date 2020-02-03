package ir.carpino.tracker.controller;

import ir.carpino.tracker.controller.exception.CarCategoryNotFoundException;
import ir.carpino.tracker.entity.mqtt.Device;
import ir.carpino.tracker.entity.rest.Driver;
import ir.carpino.tracker.repository.OnlineUserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@RestController
public class DriverController {

    @Autowired
    OnlineUserRepository repository;

    @Value("#{'${tracker.driver.car-category-type}'.split(',')}")
    private List<String> categoryType;

    /**
     *
     * @param userLat
     * @param userLog
     * @param distance
     * @param category optional return all types if it's null
     * @return
     */
    @GetMapping("/v1/driver/near")
    public List<Driver> nearDrivers(@RequestParam(value = "lat") double userLat, @RequestParam(value = "lon") double userLog, @RequestParam(value = "distance") double distance, @RequestParam(value = "category", required = false) String category) {

        if (category != null && !categoryType.contains(category)) {
            throw new CarCategoryNotFoundException(String.format("category type %s not found", category));
        }

        return repository.getOnlineUsers()
                .entrySet()
                .stream()
                .filter(entry -> {
                    if (category == null)
                        return true;

                    if (entry.getValue().getCarCategory().equals(category))
                        return true;

                    return false;
                })
                .filter(entry -> distance > entry.getValue().getGeoDistance(userLat, userLog))
                .map(entry -> Driver.builder()
                        .id(entry.getKey())
                        .lat(entry.getValue().getLat())
                        .lon(entry.getValue().getLon())
                        .category(entry.getValue().getCarCategory())
                        .build()
                ).collect(Collectors.toList());
    }

    @GetMapping("/v1/driver/location")
    public List<Driver> driversLocation(@RequestParam(value = "driverId", required = false) String driverId) {
        List<Driver> drivers = new ArrayList<>();

        if (driverId != null) {
            Device device = repository.getOnlineUsers().get(driverId);
            drivers.add(
                    Driver.builder()
                    .id(device.getId())
                    .lat(device.getLat())
                    .lon(device.getLon())
                    .category(device.getCarCategory())
                    .build()
            );

            return drivers;
        }

        return repository.getOnlineUsers()
                .values().stream()
                .map(device -> Driver.builder().id(
                        device.getId())
                        .lat(device.getLat())
                        .lon(device.getLon())
                        .category(device.getCarCategory())
                        .build()
                ).collect(Collectors.toList());
    }
}
