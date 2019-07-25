package ir.carpino.tracker.repository;

import com.github.benmanes.caffeine.cache.Cache;
import ir.carpino.tracker.entity.mqtt.Device;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Repository;

import java.util.concurrent.ConcurrentMap;

@Slf4j
@Repository
public class OnlineUserRepository {

    @Autowired
    private Cache<String, Device> cache;

    public void aliveUser(Device device) {
        cache.put(device.getId(), device);
    }

    public ConcurrentMap<String, Device> getOnlineUsers() {
        return cache.asMap();
    }
}
