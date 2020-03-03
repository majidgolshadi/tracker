package ir.carpino.tracker.configuraiton;

import lombok.Getter;
import lombok.Setter;
import org.eclipse.paho.client.mqttv3.*;
import org.eclipse.paho.client.mqttv3.persist.MemoryPersistence;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Getter
@Setter
@Configuration
@ConfigurationProperties(prefix = "tracker.mqtt")
public class MqttConfiguration {
    private String clientId;
    private String url;
    private String username;
    private String password;
    private int connectionTimeout;
    private int maxInflight = 1000;

    private MqttConnectOptions getMqttOption() {
        MqttConnectOptions options = new MqttConnectOptions();

        options.setAutomaticReconnect(true);
        options.setCleanSession(false);

        options.setConnectionTimeout(connectionTimeout);
        options.setMaxInflight(maxInflight);

        options.setUserName(username);
        options.setPassword(password.toCharArray());

        return options;
    }

    @Bean
    public MqttClient getMqttClient() throws MqttException {
        MqttClient publisher = new MqttClient(url, clientId, new MemoryPersistence());
        publisher.connect(getMqttOption());

        return publisher;
    }

    @Bean
    public MqttAsyncClient getAsyncMqttClient() throws MqttException {
        MqttAsyncClient  publisher = new MqttAsyncClient(url, clientId, new MemoryPersistence());
        publisher.connect(getMqttOption());

        DisconnectedBufferOptions bufferOpts = new DisconnectedBufferOptions();
        bufferOpts.setBufferEnabled(true);
        bufferOpts.setBufferSize(1000);
        bufferOpts.setPersistBuffer(false);
        bufferOpts.setDeleteOldestMessages(true);

        publisher.setBufferOpts(bufferOpts);

        return publisher;
    }

}
