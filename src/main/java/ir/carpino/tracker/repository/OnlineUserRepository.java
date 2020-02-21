package ir.carpino.tracker.repository;

import com.hazelcast.core.ReplicatedMap;
import ir.carpino.tracker.entity.hazelcast.DriverData;
import ir.carpino.tracker.entity.mqtt.MqttDriverLocation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Repository;

import java.util.concurrent.TimeUnit;

@Repository
public class OnlineUserRepository {

    @Value("${tracker.cache.expire-time-milliseconds}")
    private long ttl;

    private ReplicatedMap<String, DriverData> map;

    @Autowired
    public OnlineUserRepository(ReplicatedMap<String, DriverData> replicatedMap) {
        map = replicatedMap;
    }

    public void aliveUser(String userId, MqttDriverLocation mqttDriverLocation) {
        DriverData driverData = map.getOrDefault(userId, new DriverData(mqttDriverLocation));
        driverData.setDriverLocation(mqttDriverLocation);

        synchronized (map) {
            map.put(userId, driverData, ttl, TimeUnit.MILLISECONDS);
        }
    }

    public ReplicatedMap<String, DriverData> getOnlineUsers() {
        synchronized (map) {
            return map;
        }
    }
}
