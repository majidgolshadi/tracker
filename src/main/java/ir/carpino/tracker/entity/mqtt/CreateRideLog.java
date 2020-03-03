package ir.carpino.tracker.entity.mqtt;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import ir.carpino.tracker.entity.rest.Driver;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.Getter;
import org.eclipse.paho.client.mqttv3.MqttMessage;
import org.springframework.util.MultiValueMap;

import java.io.Serializable;
import java.util.Map;

@Data
public class CreateRideLog implements Serializable {
    private int statusCode;
    private Map<String, String> reqHeader;
    private String reqBody;
    private MultiValueMap<String, String> resHeader;
    private String resBody;

    public CreateRideLog(int statusCode, Map<String, String> reqHeader, String reqBody, MultiValueMap<String, String> resHeader, String resBody) {
        this.statusCode = statusCode;
        this.reqHeader = reqHeader;
        this.reqBody = reqBody;
        this.resHeader = resHeader;
        this.resBody = resBody;
    }

    public MqttMessage toMqttMessage() throws JsonProcessingException {
        ObjectMapper mapper = new ObjectMapper();

        MqttMessage mqttMessage = new MqttMessage(mapper.writeValueAsBytes(this));
        mqttMessage.setRetained(true);
        mqttMessage.setQos(1);

        return mqttMessage;
    }
}
