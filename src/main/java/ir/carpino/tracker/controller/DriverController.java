package ir.carpino.tracker.controller;

import ir.carpino.tracker.entity.mqtt.Device;
import ir.carpino.tracker.repository.OnlineUserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.stream.Collectors;

@RestController
public class DriverController {

    @Autowired
    OnlineUserRepository repository;

    @GetMapping("/v1/driver/near")
    public List<String> nearDrivers(@RequestParam(value = "lat") double userLat, @RequestParam(value = "lon") double userLog, @RequestParam(value = "distance") double distance) {

        return repository.getOnlineUsers()
                .values()
                .stream()
                .filter(device -> distance >= device.getGeoDistance(userLat, userLog))
                .map(Device::getId)
                .collect(Collectors.toList());
    }
}
