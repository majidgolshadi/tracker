package ir.carpino.tracker.entity.mysql.tracker;


import com.vividsolutions.jts.geom.Point;
import lombok.*;

import javax.persistence.*;
import java.util.Date;

@Getter
@Builder
@Entity
@Table(name = "driver_location")
public class DriverLocation {
    @Id
    @Column(length = 24, nullable = false)
    private String id;

    @Column(length = 20, nullable = false)
    private Date timestamp;

    @Column(length = 20, nullable = false)
    private String status;

    @Column(length = 24, nullable = false)
    private String controller;

    @Column(length = 10, nullable = false)
    private String carCategory;

    @Column(columnDefinition = "point", nullable = false)
    private Point location;

    @Column(nullable = false)
    private float lat;

    @Column(nullable = false)
    private float lon;

    @Column(length = 24)
    private String rid;
}
