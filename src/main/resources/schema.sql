use carpino;;

create procedure upsert_driver_location(IN in_current_rev varchar(255), IN in_next_rev varchar(255),
                                        IN in_driver_id varchar(24), IN in_car_category varchar(16),
                                        IN in_lat double, IN in_lon double, IN in_timestamp datetime,
                                        OUT done tinyint(1))
BEGIN
    SET done = TRUE;

    IF (select 1 from driver_location where rev = in_current_rev)
    THEN
        UPDATE driver_location SET lat=in_lat, lon=in_lon, rev=in_next_rev, car_category=in_car_category, `timestamp`=in_timestamp WHERE rev = in_current_rev;
    ELSE
        IF (select 1 from driver_location where id = in_driver_id)
        THEN
            SET done = FALSE;
        ELSE
            INSERT INTO driver_location (id, lat, lon, rev, car_category, `timestamp`) VALUES (in_driver_id, in_lat, in_lon, in_next_rev, in_car_category, in_timestamp);
        END IF;
    END IF;

END;

