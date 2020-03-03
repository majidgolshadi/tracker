package ir.carpino.tracker.utils;

import lombok.extern.slf4j.Slf4j;
import org.springframework.http.*;
import org.springframework.http.client.HttpComponentsClientHttpRequestFactory;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.HttpStatusCodeException;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponentsBuilder;

import javax.servlet.http.HttpServletRequest;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.Enumeration;

@Slf4j
public final class HttpEntityFactory {

    public HttpEntity<String> createInstance(String body, HttpServletRequest request) {
        return createInstance(body, request, new LinkedMultiValueMap<>());
    }

    public HttpEntity<String> createInstance(String body, HttpServletRequest request, MultiValueMap<String, String> extraHeaders) {

        HttpHeaders headers = new HttpHeaders();
        Enumeration<String> headerNames = request.getHeaderNames();
        while (headerNames.hasMoreElements()) {
            String headerName = headerNames.nextElement();
            headers.set(headerName, request.getHeader(headerName));
        }

        headers.addAll(extraHeaders);
        headers.remove(HttpHeaders.CONTENT_LENGTH);

        return new HttpEntity<>(body, headers);
    }

    public ResponseEntity<String> reverseProxy(HttpEntity<String> httpEntity, HttpMethod method, HttpServletRequest request, String upstreamUrl) throws URISyntaxException {
        // To skip not support types
        if (method == null) {
            log.warn("request method type is invalid");
            return ResponseEntity
                    .status(HttpStatus.BAD_REQUEST)
                    .body(null);
        }

        String requestUrl = request.getRequestURI();
        URI uri = UriComponentsBuilder.fromUri(new URI(upstreamUrl))
                .path(requestUrl)
                .query(request.getQueryString())
                .build(true).toUri();


        RestTemplate restTemplate = new RestTemplate();
        restTemplate.setRequestFactory(new HttpComponentsClientHttpRequestFactory());

        ResponseEntity<String> responseEntity;

        try {
            responseEntity = restTemplate.exchange(uri, method, httpEntity, String.class);
        } catch (HttpStatusCodeException e) {
            log.error("reverse proxy exception ", e.getCause());
            responseEntity = ResponseEntity.status(e.getRawStatusCode())
                    .headers(e.getResponseHeaders())
                    .body(e.getResponseBodyAsString());
        }

        log.info("status_code: {}, url: {}", responseEntity.getStatusCodeValue(), requestUrl);
        return responseEntity;
    }
}