package ir.carpino.tracker.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import ir.carpino.tracker.entity.kafka.CreateRideLog;
import ir.carpino.tracker.utils.HttpEntityFactory;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletRequest;
import java.net.URISyntaxException;
import java.util.Collections;
import java.util.Map;
import java.util.stream.Collectors;

@Slf4j
@RestController
public class DispatchController {

    private KafkaTemplate<String, String> kafkaClient;
    private HttpEntityFactory httpEntityFactory;

    @Value("${tracker.proxy-uri}")
    private String proxyUri;

    @Value("${tracker.kafka.topic.create-ride-log}")
    private String createRideTopic;

    private final ObjectMapper mapper;

    @Autowired
    public DispatchController(KafkaTemplate<String, String> kafkaClient) {
        this.kafkaClient = kafkaClient;
        httpEntityFactory = new HttpEntityFactory();
        mapper = new ObjectMapper();
    }

    @PostMapping("/v1/rides")
    public ResponseEntity<String> dispatching(@RequestBody String requestBody, HttpMethod method, HttpServletRequest request) throws URISyntaxException {
        MultiValueMap<String, String> additionalHeaders = new LinkedMultiValueMap<>();
        additionalHeaders.add("Ignore-Dispatch", "true");

        HttpEntity<String> httpEntity = httpEntityFactory.createInstance(requestBody, request, additionalHeaders);
        ResponseEntity<String> response = httpEntityFactory.reverseProxy(httpEntity, method, request, proxyUri);

        logCreateRide(request, requestBody, response);

        return response;
    }

    private void logCreateRide(HttpServletRequest request, String requestBody, ResponseEntity<String> response) {
        Map<String, String> reqHeaders = Collections
                .list(request.getHeaderNames())
                .stream()
                .collect(Collectors.toMap(h -> h, request::getHeader));

        try {
            kafkaClient.send(createRideTopic, mapper.writeValueAsString(new CreateRideLog(
                    response.getStatusCodeValue(),
                    reqHeaders,
                    requestBody,
                    response.getHeaders(),
                    response.getBody()
            )));

        } catch (Exception ex) {
            log.error("produce create ride log error: {}", ex.getMessage());
        }
    }
}
