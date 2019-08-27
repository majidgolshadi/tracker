package ir.carpino.tracker.entity.rest;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Builder;
import lombok.Getter;

import java.io.Serializable;

@Getter
@Builder
public class Driver implements Serializable {

    private String id;

    @JsonInclude(JsonInclude.Include.NON_DEFAULT)
    private double lat;

    @JsonInclude(JsonInclude.Include.NON_DEFAULT)
    private double lon;

    private String category;
}
