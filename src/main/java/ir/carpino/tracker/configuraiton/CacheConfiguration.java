package ir.carpino.tracker.configuraiton;

import com.github.benmanes.caffeine.cache.Cache;
import com.github.benmanes.caffeine.cache.Caffeine;
import ir.carpino.tracker.entity.mqtt.Device;
import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.concurrent.TimeUnit;


@Setter
@Getter
@Configuration
@ConfigurationProperties(prefix = "tracker.cache")
public class CacheConfiguration {
    private long expireTimeMilliseconds;

    @Bean
    public Cache<String, Device> initCache() {
        return Caffeine.newBuilder()
                .expireAfterWrite(expireTimeMilliseconds, TimeUnit.MILLISECONDS)
                .build();
    }
}
