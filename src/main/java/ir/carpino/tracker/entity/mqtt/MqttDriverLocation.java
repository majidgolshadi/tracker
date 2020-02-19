package ir.carpino.tracker.entity.mqtt;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Getter;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;

import java.io.Serializable;


@Slf4j
@Getter
@Setter
@JsonIgnoreProperties(ignoreUnknown = true)
public class MqttDriverLocation implements Serializable {

    private String id;

    @JsonProperty("loc")
    private String location;

    @JsonProperty(value = "ts")
    private long timeStamp = System.currentTimeMillis();

    private double lat;
    private double lon;

    private String carCategory;
    private String status;

    public void setLocation(String location) {
        this.location = location;

        String[] geoLoc = this.location.split(",");

        lat = Double.valueOf(geoLoc[0]);
        lon = Double.valueOf(geoLoc[1]);
    }

    public void setNamespaceMetaData(String namespaceMetaData) {
        String[] metadata = namespaceMetaData.split("/", 7);
        status = metadata[3];
//        controller = metadata[4];
        carCategory = metadata[5];
    }
}
