create procedure upsert_driver_location(IN in_current_rev varchar(255), IN in_next_rev varchar(255),
                                        IN in_driver_id   varchar(24), IN in_lat double, IN in_lon double,
                                        IN in_timestamp   datetime, OUT failed tinyint(1))
  BEGIN
    SET failed = FALSE;

    IF (select 1 from driver_location where rev = in_current_rev)
    THEN
      UPDATE driver_location SET lat=in_lat, lon=in_log, rev=in_current_rev, `timestamp`=in_timestamp;
    ELSE
      IF (select 1 from driver_location where id = in_driver_id)
      THEN
        SET failed = TRUE;
      ELSE
        INSERT INTO driver_location (id, lat, lon, rev, `timestamp`) VALUES (in_driver_id, in_lat, in_lon, in_next_rev, in_timestamp);
      END IF;
    END IF;

  END;

