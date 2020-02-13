package ir.carpino.tracker.service;

import ir.carpino.tracker.entity.Rev;
import ir.carpino.tracker.entity.hazelcast.DriverData;
import ir.carpino.tracker.entity.mqtt.MqttDriverLocation;
import ir.carpino.tracker.entity.mysql.DriverLocation;
import ir.carpino.tracker.repository.tracker.DriverLocationRepository;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;
import org.springframework.beans.factory.annotation.Autowired;

import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.Optional;

import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

public class MysqlPersisterTests {

    private final String ID = "1";
    private final String TIME_ZONE_ID = "Asia/Tehran";

    @Test
    public void testResolveConflictDriverNotFound() {
        DriverLocationRepository driverLocRepoMock = mock(DriverLocationRepository.class);
        when(driverLocRepoMock.findById(ID)).thenReturn(Optional.empty());

        MqttDriverLocation mqttDriverLocation = new MqttDriverLocation();
        mqttDriverLocation.setId(ID);
        DriverData driverData = new DriverData(mqttDriverLocation);

        MysqlPersister mysqlPersister = new MysqlPersister(null, driverLocRepoMock);

        Assert.assertFalse(mysqlPersister.resolveConflict(driverData));
    }

    @Test
    public void testResolveConflictDataWithNewestInDB() {
        final long timestampLong = 2063727427443L;
        final LocalDateTime timestamp = LocalDateTime.ofInstant(Instant.ofEpochMilli(timestampLong), ZoneId.of(TIME_ZONE_ID));

        DriverLocation driverLocation = new DriverLocation();
        driverLocation.setId(ID);
        driverLocation.setLat(1L);
        driverLocation.setLon(2L);
        driverLocation.setCarCategory("vip");
        driverLocation.setTimestamp(timestamp);
        driverLocation.setRev(new Rev());

        Optional<DriverLocation> opt = Optional.of(driverLocation);

        DriverLocationRepository driverLocRepoMock = Mockito.mock(DriverLocationRepository.class);
        when(driverLocRepoMock.findById(ID)).thenReturn(opt);

        MqttDriverLocation mqttDriverLocation = new MqttDriverLocation();
        mqttDriverLocation.setId(ID);
        DriverData driverData = new DriverData(mqttDriverLocation);

        MysqlPersister mysqlPersister = new MysqlPersister(null, driverLocRepoMock);

        Assert.assertFalse(mysqlPersister.resolveConflict(driverData));
    }

    @Test
    public void testResolveConflictDataWithNewestInMemory() {
        final long timestampLong = 0L;
        final double newLat = 2;
        final double newLon = 3;
        final String newCarCategory = "ss";
        final LocalDateTime timestamp = LocalDateTime.ofInstant(Instant.ofEpochMilli(timestampLong), ZoneId.of(TIME_ZONE_ID));

        DriverLocation driverLocation = new DriverLocation();
        driverLocation.setId(ID);
        driverLocation.setLat(1);
        driverLocation.setLon(1);
        driverLocation.setCarCategory("vip");
        driverLocation.setTimestamp(timestamp);
        driverLocation.setRev(new Rev());

        Optional<DriverLocation> opt = Optional.of(driverLocation);

        DriverLocationRepository driverLocRepoMock = Mockito.mock(DriverLocationRepository.class);
        when(driverLocRepoMock.findById(ID)).thenReturn(opt);

        MqttDriverLocation mqttDriverLocation = new MqttDriverLocation();
        mqttDriverLocation.setId(ID);
        mqttDriverLocation.setLat(newLat);
        mqttDriverLocation.setLon(newLon);
        mqttDriverLocation.setCarCategory(newCarCategory);
        DriverData driverData = new DriverData(mqttDriverLocation);

        MysqlPersister mysqlPersister = new MysqlPersister(null, driverLocRepoMock);


        Assert.assertTrue(mysqlPersister.resolveConflict(driverData));

        Assert.assertEquals(ID, driverLocation.getId());
        Assert.assertEquals(String.valueOf(newLat), String.valueOf(driverLocation.getLat()));
        Assert.assertEquals(String.valueOf(newLon), String.valueOf(driverLocation.getLon()));
        Assert.assertEquals(newCarCategory, driverLocation.getCarCategory());

        Assert.assertNotEquals(timestampLong, driverData.getDriverLocation().getTimeStamp());
        Assert.assertNotEquals(newCarCategory, Rev.INIT_REV);
    }
}
