package ir.carpino.tracker.repository;

import com.hazelcast.core.IMap;
import ir.carpino.tracker.entity.hazelcast.DriverData;
import ir.carpino.tracker.entity.mqtt.MqttDriverLocation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Repository;

import java.util.Map;
import java.util.concurrent.TimeUnit;

@Repository
public class OnlineUserRepository {

    @Value("${tracker.cache.expire-time-milliseconds}")
    private long ttl;

    private IMap<String, DriverData> map;

    @Autowired
    public OnlineUserRepository(IMap<String, DriverData> replicatedMap) {
        map = replicatedMap;
    }

    public void aliveUser(String userId, MqttDriverLocation mqttDriverLocation) {
        DriverData driverData = map.getOrDefault(userId, new DriverData(mqttDriverLocation));
        driverData.setDriverLocation(mqttDriverLocation);

        try {
            map.lock(userId);
            map.put(userId, driverData, ttl, TimeUnit.MILLISECONDS);
        } finally {
            map.unlock(userId);
        }
    }

    public Map<String, DriverData> getOnlineUsers() {
        return map;
    }
}
