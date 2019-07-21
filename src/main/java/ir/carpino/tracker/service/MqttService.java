package ir.carpino.tracker.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import ir.carpino.tracker.entity.mqtt.Device;
import ir.carpino.tracker.repository.OnlineUserRepository;
import lombok.extern.slf4j.Slf4j;
import org.eclipse.paho.client.mqttv3.IMqttClient;
import org.eclipse.paho.client.mqttv3.IMqttMessageListener;
import org.eclipse.paho.client.mqttv3.MqttException;
import org.eclipse.paho.client.mqttv3.MqttMessage;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import javax.annotation.PostConstruct;

@Slf4j
@Service
public class MqttService implements IMqttMessageListener {

    @Value("${tracker.mqtt.location-topic}")
    private String locationTopic;

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
        client.subscribe(locationTopic, 2, this);
    }

    @Override
    public void messageArrived(String topic, MqttMessage message) throws Exception {
        String content = message.toString();
        Device device = mapper.readValue(content, Device.class);
        device.setNamespaceMetaData(topic);
        device.setPayload(message.getPayload().toString());

        onlineUserRepository.aliveUser(device);
    }
}
