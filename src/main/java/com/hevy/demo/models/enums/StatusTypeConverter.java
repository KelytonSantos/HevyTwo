package com.hevy.demo.models.enums;

import java.sql.SQLException;

import org.postgresql.util.PGobject;

import jakarta.persistence.AttributeConverter;
import jakarta.persistence.Converter;

@Converter
public class StatusTypeConverter implements AttributeConverter<StatusType, Object> {

    @Override
    public Object convertToDatabaseColumn(StatusType attribute) {
        if (attribute == null) {
            return null;
        }
        PGobject pgObject = new PGobject();
        try {
            pgObject.setType("status_type");
            pgObject.setValue(attribute.getDbValue());
        } catch (SQLException ex) {
            throw new IllegalArgumentException("Failed to convert StatusType", ex);
        }
        return pgObject;
    }

    @Override
    public StatusType convertToEntityAttribute(Object dbData) {
        if (dbData == null) {
            return null;
        }
        if (dbData instanceof PGobject pgObject) {
            return StatusType.fromDbValue(pgObject.getValue());
        }
        return StatusType.fromDbValue(dbData.toString());
    }
}
