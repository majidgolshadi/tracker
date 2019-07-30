package ir.carpino.tracker.entity.mysql;


import lombok.*;

import org.springframework.data.annotation.CreatedDate;

import javax.persistence.*;
import java.util.Date;

@Getter
@Builder
@Entity
@Table(name = "driver_location")
public class DriverLocation {
    @Id
    @GeneratedValue(strategy=GenerationType.AUTO)
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
