package ir.carpino.tracker.repository;

import com.hazelcast.core.ReplicatedMap;
import ir.carpino.tracker.entity.mqtt.Device;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import java.util.Map;

@Repository
public class OnlineUserRepository {

    private ReplicatedMap<String, Device> map;

    @Autowired
    public OnlineUserRepository(ReplicatedMap<String, Device> replicatedMap) {
        map = replicatedMap;
    }

    public void aliveUser(String userId, Device device) {
        map.put(userId, device);
    }

    public Map<String, Device> getOnlineUsers() {
        return map;
    }
}
