package ir.carpino.tracker.configuraiton;

import com.hazelcast.config.Config;
import com.hazelcast.core.Hazelcast;
import com.hazelcast.core.HazelcastInstance;
import com.hazelcast.core.IMap;
import com.hazelcast.core.ReplicatedMap;
import ir.carpino.tracker.entity.hazelcast.DriverData;
import lombok.Getter;
import lombok.Setter;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;


@Setter
@Getter
@Configuration
public class CacheConfiguration {

    private final static String REPLICATED_MAP_NAME = "points";

    @Bean
    @Primary
    public  HazelcastInstance initCache() {
        Config cfg = new Config();
        return Hazelcast.newHazelcastInstance(cfg);
    }

    @Bean
    public ReplicatedMap<String, DriverData> getReplicatedMap(HazelcastInstance instance) {
        return instance.getReplicatedMap(REPLICATED_MAP_NAME);
    }

    @Bean
    public IMap<String, DriverData> getMap(HazelcastInstance instance) {
        return instance.getMap(REPLICATED_MAP_NAME);
    }
}
