package ir.carpino.tracker.entity.mqtt;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;


@Getter
@Setter
@JsonIgnoreProperties(ignoreUnknown = true)
public class Device {
    private String id;

    @JsonProperty("loc")
    private String location;

    @JsonProperty("ts")
    private String timeStamp;

    private String payload;

    private double lat;
    private double lon;

    private float f_lat;
    private float f_lon;

    private String carCategory;
    private String status;
    private String controller;

    public void setLocation(String location) {
        this.location = location;

        String[] geoLoc = this.location.split(",");

        lat = Double.valueOf(geoLoc[0]);
        f_lat = Float.valueOf(geoLoc[0]);

        lon = Double.valueOf(geoLoc[1]);
        f_lon = Float.valueOf(geoLoc[1]);
    }

    public long getLongTimestamp() {
        return Long.valueOf(timeStamp);
    }

    public void setNamespaceMetaData(String namespaceMetaData) {
        String[] metadata = namespaceMetaData.split("/", 7);
        status = metadata[3];
        controller = metadata[4];
        carCategory = metadata[5];
    }
}
