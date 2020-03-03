package ir.carpino.tracker.entity.mysql;


import ir.carpino.tracker.entity.Rev;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.persistence.*;
import java.io.Serializable;
import java.time.LocalDateTime;

@Getter
@Setter
@Entity
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "driver_location", indexes = { @Index(columnList = "rev", name = "rev_index")})
public class DriverLocation implements Serializable {
    @Id
    @Column(length = 24, nullable = false)
    private String id;

    @Column(nullable = false)
    private LocalDateTime timestamp;

    @Column(nullable = false)
    private double lat;

    @Column(nullable = false)
    private double lon;

    @Column(columnDefinition = "varchar(255)", nullable = false)
    @Convert(converter = RevConverter.class)
    private Rev rev;

    @Column(name = "car_category", length = 16, nullable = false)
    private String carCategory;

    public void setCarCategory(String carCategory) {
        this.carCategory = carCategory.toLowerCase();
    }
}
