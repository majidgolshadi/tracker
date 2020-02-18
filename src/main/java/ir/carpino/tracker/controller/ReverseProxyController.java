package ir.carpino.tracker.controller;

import ir.carpino.tracker.utils.HttpEntityFactory;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletRequest;
import java.net.URISyntaxException;

@Slf4j
@RestController
public class ReverseProxyController {

    @Value("${tracker.proxy-uri}")
    private String proxyUri;

    private final HttpEntityFactory httpEntityFactory = new HttpEntityFactory();

    @RequestMapping("/**")
    public ResponseEntity<String> reverseProxy(@RequestBody(required = false) String body, HttpMethod method,
                                     HttpServletRequest request) throws URISyntaxException {

        HttpEntity<String> httpEntity = httpEntityFactory.createInstance(body, request);
        return httpEntityFactory.reverseProxy(httpEntity, method, request, proxyUri);
    }
}
