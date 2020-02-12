package ir.carpino.tracker.entity.mysql;

import ir.carpino.tracker.entity.Rev;
import lombok.extern.slf4j.Slf4j;

import javax.persistence.AttributeConverter;

@Slf4j
public class RevConverter implements AttributeConverter<Rev, String> {
    @Override
    public String convertToDatabaseColumn(Rev rev) {
        return rev.toString();
    }

    @Override
    public Rev convertToEntityAttribute(String s) {
        String[] seqUuid = s.split("-", 2);

        if (seqUuid.length < 1) {
            throw new RuntimeException("Invalid rev string structure");
        }

        return new Rev(Integer.parseInt(seqUuid[0]), seqUuid[1]);
    }
}