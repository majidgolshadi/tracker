package ir.carpino.tracker.entity.kafka;

import lombok.Data;
import org.springframework.util.MultiValueMap;

import java.util.Map;

@Data
public class CreateRideLog {
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
}
