package ir.carpino.tracker.entity.mysql;


import ir.carpino.tracker.entity.Rev;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

import javax.persistence.*;
import java.util.Date;

@Getter
@Entity
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "driver_location", indexes = { @Index(columnList = "rev", name = "rev_index")})
public class DriverLocation {
    @Id
    @Column(length = 24, nullable = false)
    private String id;

    @Column(length = 20, nullable = false)
    private Date timestamp;

    @Column(nullable = false)
    private double lat;

    @Column(nullable = false)
    private double lon;

    @Column(columnDefinition = "varchar(255)", nullable = false)
    private Rev rev;

    @Column(name = "car_category", length = 16, nullable = false)
    private String carCategory;
}
