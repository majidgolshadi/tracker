package ir.carpino.tracker.entity.hazelcast;

import ir.carpino.tracker.entity.Rev;
import ir.carpino.tracker.entity.mqtt.MqttDriverLocation;
import lombok.Getter;
import lombok.Setter;

import java.io.Serializable;

@Setter
@Getter
public class DriverData implements Serializable {
    private MqttDriverLocation driverLocation;
    private Rev rev;

    public DriverData(MqttDriverLocation driverLocation) {
        this.driverLocation = driverLocation;
        this.rev = new Rev();
    }
}
