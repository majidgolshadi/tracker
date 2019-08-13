package ir.carpino.tracker.entity.mysql;


import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.persistence.*;
import java.util.Date;

@Getter @Setter
@Entity
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "driver_geo_location")
public class BiDriverLocation {
    @Id
    @GeneratedValue(strategy=GenerationType.IDENTITY)
    private int id;

    @Column(length = 32, nullable = false)
    private String driverId;

    @Column(length = 20, nullable = false)
    private String status;

    @Column(length = 24, nullable = false)
    private String controller;

    @Column(length = 10, nullable = false)
    private String carCategory;

    @Column(nullable = false)
    private double lat;

    @Column(nullable = false)
    private double lon;

    @Column(nullable = false, updatable = false)
    private Date timestamp;
}
