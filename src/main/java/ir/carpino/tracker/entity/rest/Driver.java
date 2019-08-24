package ir.carpino.tracker.entity.rest;

import lombok.Builder;
import lombok.Getter;

import java.io.Serializable;

@Getter
@Builder
public class Driver implements Serializable {
    private String id;
    private double lat;
    private double lon;
}
