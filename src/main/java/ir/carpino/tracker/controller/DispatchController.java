package ir.carpino.tracker.controller;

import ir.carpino.tracker.utils.HttpEntityFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletRequest;
import java.net.URISyntaxException;

@RestController
public class DispatchController {

    @Value("${tracker.proxy-uri}")
    private String proxyUri;

    @Autowired
    ReverseProxyController reverseProxyController;

    private final HttpEntityFactory httpEntityFactory = new HttpEntityFactory();

    @PostMapping("/v1/rides")
    public ResponseEntity<String> dispatching(String body, HttpMethod method, HttpServletRequest request) throws URISyntaxException {
        MultiValueMap<String, String> additionalHeaders = new LinkedMultiValueMap<>();
        additionalHeaders.add("Ignore-Dispatch", "true");

        HttpEntity<String> httpEntity = httpEntityFactory.createInstance(body, request, additionalHeaders);
        return httpEntityFactory.reverseProxy(httpEntity, method, request, proxyUri);
    }
}
