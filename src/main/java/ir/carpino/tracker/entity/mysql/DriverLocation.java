package ir.carpino.tracker.entity.mysql;


import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.Index;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;
import java.util.Date;

@Getter
@Entity
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "driver_location")
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
    @Index(name = "rev_index")
    private Rev rev;
}
