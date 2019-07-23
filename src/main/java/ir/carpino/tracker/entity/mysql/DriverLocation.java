package ir.carpino.tracker.entity.mysql;

import lombok.*;
import org.hibernate.annotations.Type;
import org.springframework.data.geo.Point;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;
import java.util.Date;

@Getter
@AllArgsConstructor
@NoArgsConstructor
@Entity
@Table(name = "driver_location")
public class DriverLocation {
    @Id
    private String id;

    @Column(length = 20, nullable = false)
    private Date timestamp;

    @Column(length = 20, nullable = false)
    private String status;

    @Column(length = 24, nullable = false)
    private String controller;

    @Column(length = 10, nullable = false)
    private String carCategory;

    @Type(type = "org.hibernate.                                                                                                       spatial.GeometryType")
    @Column(columnDefinition = "geometry(Point,4326)", nullable = false)
    private Point location;

    @Column(nullable = false)
    private float lat;

    @Column(nullable = false)
    private float lon;

    @Column(length = 24)
    private String rid;
}
