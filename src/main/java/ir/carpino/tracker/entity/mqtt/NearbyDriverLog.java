package ir.carpino.tracker.entity.mqtt;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import ir.carpino.tracker.entity.rest.Driver;
import lombok.Getter;
import org.eclipse.paho.client.mqttv3.MqttMessage;

import java.io.Serializable;

@Getter
public class NearbyDriverLog implements Serializable {
    private String rideId;
    private String driverId;
    private double lat;
    private double lon;
    private long timestamp;

    public NearbyDriverLog(String rideId, Driver driver) {
        this.rideId = rideId;
        driverId = driver.getId();
        lat = driver.getLat();
        lon = driver.getLon();
        timestamp = System.currentTimeMillis();
    }

    public MqttMessage toMqttMessage() throws JsonProcessingException {
        ObjectMapper mapper = new ObjectMapper();

        MqttMessage mqttMessage = new MqttMessage(mapper.writeValueAsBytes(this));
        mqttMessage.setRetained(true);
        mqttMessage.setQos(1);

        return mqttMessage;
    }
}
