package ir.carpino.tracker.repository.bi;

import ir.carpino.tracker.entity.mysql.BiDriverLocation;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface BiDriverLocationRepository extends CrudRepository<BiDriverLocation, String> {

}
