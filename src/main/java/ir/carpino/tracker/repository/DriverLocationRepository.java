package ir.carpino.tracker.repository;

import ir.carpino.tracker.entity.mysql.tracker.DriverLocation;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface DriverLocationRepository extends CrudRepository<DriverLocation, String> {

}
