package ir.carpino.tracker.entity.mqtt;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Getter;
import lombok.Setter;
import org.locationtech.spatial4j.context.SpatialContext;
import org.locationtech.spatial4j.distance.CartesianDistCalc;
import org.locationtech.spatial4j.distance.DistanceUtils;

import javax.annotation.PostConstruct;


@Getter
@Setter
@JsonIgnoreProperties(ignoreUnknown = true)
public class Device {

    CartesianDistCalc geoCalculator = new CartesianDistCalc();

    private String id;

    @JsonProperty("loc")
    private String location;

    @JsonProperty(value = "ts")
    private long timeStamp = System.currentTimeMillis();

    private String payload;

    private double lat;
    private double lon;

    private String carCategory;
    private String status;
    private String controller;

    public void setLocation(String location) {
        this.location = location;

        String[] geoLoc = this.location.split(",");

        lat = Double.valueOf(geoLoc[0]);
        lon = Double.valueOf(geoLoc[1]);
    }

    public void setNamespaceMetaData(String namespaceMetaData) {
        String[] metadata = namespaceMetaData.split("/", 7);
        status = metadata[3];
        controller = metadata[4];
        carCategory = metadata[5];
    }

    private SpatialContext ctx = SpatialContext.GEO;

    public Double getGeoDistance(Double lat, Double lon) {
        return DistanceUtils.DEG_TO_KM * geoCalculator.distance(ctx.getShapeFactory().pointXY(this.lat, this.lon), lat, lon);
    }
}
