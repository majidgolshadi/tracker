package ir.carpino.tracker.repository.tracker;

import ir.carpino.tracker.entity.mysql.DriverLocation;
import org.springframework.data.jpa.repository.query.Procedure;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;

@Repository
public interface DriverLocationRepository extends CrudRepository<DriverLocation, String> {

    @Procedure(name = "upsert_driver_location", outputParameterName = "failed")
    public boolean upsert_driver_location(@Param("in_current_rev") String rev, @Param("in_next_rev") String nextRev,
                                       @Param("in_driver_id") String driverId, @Param("in_car_category") String carCategory,
                                       @Param("in_lat") double lat, @Param("in_lon") double lon,
                                       @Param("in_timestamp") LocalDateTime timestamp);
}
