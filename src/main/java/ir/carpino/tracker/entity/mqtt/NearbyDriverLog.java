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
    private double srcLat;
    private double srcLon;
    private double driverLat;
    private double driverLon;
    private long timestamp;

    public NearbyDriverLog(String rideId, double srcLat, double srcLon, Driver driver) {
        this.rideId = rideId;
        driverId = driver.getId();
        this.srcLat = srcLat;
        this.srcLon = srcLon;
        driverLat = driver.getLat();
        driverLon = driver.getLon();
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
