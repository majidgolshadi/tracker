package ir.carpino.tracker.repository;

import com.hazelcast.core.ReplicatedMap;
import ir.carpino.tracker.entity.hazelcast.DriverData;
import ir.carpino.tracker.entity.mqtt.MqttDriverLocation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import java.util.Map;

@Repository
public class OnlineUserRepository {

    private ReplicatedMap<String, DriverData> map;

    @Autowired
    public OnlineUserRepository(ReplicatedMap<String, DriverData> replicatedMap) {
        map = replicatedMap;
    }

    public void aliveUser(String userId, MqttDriverLocation mqttDriverLocation) {
        DriverData driverData = map.getOrDefault(userId, new DriverData(mqttDriverLocation));
        driverData.setDriverLocation(mqttDriverLocation);

        map.put(userId, driverData);
    }

    public Map<String, DriverData> getOnlineUsers() {
        return map;
    }
}
