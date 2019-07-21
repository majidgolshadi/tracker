package ir.carpino.tracker.repository;

import com.github.benmanes.caffeine.cache.Cache;
import com.github.benmanes.caffeine.cache.Caffeine;
import ir.carpino.tracker.entity.mqtt.Device;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Repository;

import javax.annotation.PostConstruct;
import java.util.concurrent.ConcurrentMap;
import java.util.concurrent.TimeUnit;

@Slf4j
@Repository
public class OnlineUserRepository {

    @Value("${fake-ride.online-user.millisecond-inactive-time}")
    private int expireTime;

    private Cache<String, Device> cache;
    private static Device shareDevice;

    @PostConstruct
    public void initCache() {
        cache = Caffeine.newBuilder()
                .expireAfterAccess(expireTime, TimeUnit.MILLISECONDS)
                .build((key) -> null);
    }

    public void aliveUser(Device device) {
        cache.put(device.getId(), device);
    }

    public ConcurrentMap<String, Device> getOnlineUsers() {
        return cache.asMap();
    }
}
