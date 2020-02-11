package ir.carpino.tracker.entity.mqtt;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.Test;

import java.io.IOException;

import static org.junit.Assert.*;

public class MqttDriverLocationTest {

    @Test
    public void setNameSpaceMetaData() throws IOException {
        ObjectMapper mapper = new ObjectMapper();
        String jsonStringData = "{\"@class\":\".ActorLocation\",\"id\":\"59732c42c9e77c00018fb558\",\"ts\":1563727427443,\"loc\":\"35.6440553,51.249601\"}";
        MqttDriverLocation device = mapper.readValue(jsonStringData, MqttDriverLocation.class);
        String topic = "loc/drv/5b94e6bda7b11b000168fdb9/available/58ac3f2bd6018000013f4464/vip/12300300/3/2/0/1/2/0/1/0/1/0/3/3/0/3/";
        device.setNamespaceMetaData(topic);

        assertEquals("59732c42c9e77c00018fb558", device.getId());
        assertEquals("vip", device.getCarCategory());
//        assertEquals("58ac3f2bd6018000013f4464", device.getController());
        assertEquals("available", device.getStatus());
        assertEquals("35.6440553,51.249601", device.getLocation());
        assertEquals("35.6440553", String.valueOf(device.getLat()));
        assertEquals("51.249601", String.valueOf(device.getLon()));

        assertNotEquals(Long.valueOf("1563727427443"), Long.valueOf(device.getTimeStamp()));

        System.out.print(device.getTimeStamp());
    }
}