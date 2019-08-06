package ir.carpino.tracker.repository;

import ir.carpino.tracker.entity.mysql.bi.BiDriverLocation;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface BiDriverLocationRepository extends CrudRepository<BiDriverLocation, String> {

}
