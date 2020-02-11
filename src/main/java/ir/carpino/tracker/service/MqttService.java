package ir.carpino.tracker.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import ir.carpino.tracker.entity.mqtt.MqttDriverLocation;
import ir.carpino.tracker.repository.OnlineUserRepository;
import lombok.extern.slf4j.Slf4j;
import org.eclipse.paho.client.mqttv3.IMqttClient;
import org.eclipse.paho.client.mqttv3.IMqttMessageListener;
import org.eclipse.paho.client.mqttv3.MqttException;
import org.eclipse.paho.client.mqttv3.MqttMessage;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import javax.annotation.PostConstruct;

@Slf4j
@Service
public class MqttService implements IMqttMessageListener {

    @Value("${tracker.mqtt.location-topic}")
    private String locationTopic;

    @Value("${tracker.mqtt.data-duplication-threshold}")
    private int threshold;

    private int duplicationTime;
    private int lastOnlineUsers;

    private final IMqttClient client;
    private final OnlineUserRepository onlineUserRepository;
    private final ObjectMapper mapper;

    @Autowired
    public MqttService(IMqttClient client, OnlineUserRepository onlineUserRepository) {
        this.client = client;
        this.onlineUserRepository = onlineUserRepository;

        mapper = new ObjectMapper();
    }

    @PostConstruct
    private void subscribe() throws MqttException {
        client.subscribe(locationTopic, this);
    }

    private void resubscribe() {
        log.warn("unsubscribe/subscribe to topic {}", locationTopic);

        try {
            client.unsubscribe(locationTopic);
            this.subscribe();

        } catch (MqttException ex) {
            log.error(ex.getMessage());
        }
    }

    @Override
    public void messageArrived(String topic, MqttMessage message) throws MqttException {
        try {
            String content = message.toString();
            MqttDriverLocation driverLocation = mapper.readValue(content, MqttDriverLocation.class);
            driverLocation.setNamespaceMetaData(topic);

            onlineUserRepository.aliveUser(driverLocation.getId(), driverLocation);
        } catch (Exception ex) {
            log.error("pars MQTT income data error {}", ex.getCause());
            resubscribe();
        }
    }

    /**
     * Based on EMQX issue https://github.com/emqx/emqx/issues/1216 recover connection with monitor data set size
     * @throws MqttException
     */
    @Scheduled(fixedRateString = "${tracker.mqtt.connection-validation-milliseconds-rate}")
    public void checkIncomingOnlineDataValidation() {
        int onlineUsers = onlineUserRepository.getOnlineUsers().size();

        if (lastOnlineUsers == onlineUsers) {
            duplicationTime++;
        } else {
            duplicationTime = 0;
        }

        if (duplicationTime > threshold) {
            resubscribe();
            duplicationTime = 0;
        }

        lastOnlineUsers = onlineUsers;
    }
}
