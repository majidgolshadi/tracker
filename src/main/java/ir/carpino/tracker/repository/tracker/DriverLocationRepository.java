package ir.carpino.tracker.repository.tracker;

import ir.carpino.tracker.entity.mysql.DriverLocation;
import org.springframework.data.jpa.repository.query.Procedure;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;

@Repository
public interface DriverLocationRepository extends CrudRepository<DriverLocation, String> {

    /**
     * update data if the rev is equal with what exist in DB
     * insert new data comes
     * in two cases above, the data will be set in DB and the store procedure return true
     *
     * if the app try to insert the data that it does not have the latest Rev info it will be failed and
     * return the conflict resolution task to the app
     *
     * the rev structure is the join of number and UUIDv4
     *
     * @param rev data current rev
     * @param nextRev data next rev
     * @param driverId driver id
     * @param carCategory car category type
     * @param lat latitude
     * @param lon longitude
     * @param timestamp timestamp
     * @return
     */
    @Procedure(name = "upsert_driver_location")
    public boolean upsert_driver_location(@Param("in_current_rev") String rev, @Param("in_next_rev") String nextRev,
                                       @Param("in_driver_id") String driverId, @Param("in_car_category") String carCategory,
                                       @Param("in_lat") double lat, @Param("in_lon") double lon,
                                       @Param("in_timestamp") LocalDateTime timestamp);
}
