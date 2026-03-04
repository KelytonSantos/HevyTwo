package com.hevy.demo.models.enums;

import java.sql.SQLException;

import org.postgresql.util.PGobject;

import jakarta.persistence.AttributeConverter;
import jakarta.persistence.Converter;

@Converter
public class SeriesConverter implements AttributeConverter<Series, Object> {

    @Override
    public Object convertToDatabaseColumn(Series attribute) {
        if (attribute == null) {
            return null;
        }
        PGobject pgObject = new PGobject();
        try {
            pgObject.setType("series_type");
            pgObject.setValue(attribute.getDbValue());
        } catch (SQLException ex) {
            throw new IllegalArgumentException("Failed to convert Series", ex);
        }
        return pgObject;
    }

    @Override
    public Series convertToEntityAttribute(Object dbData) {
        if (dbData == null) {
            return null;
        }
        if (dbData instanceof PGobject pgObject) {
            return Series.fromDbValue(pgObject.getValue());
        }
        return Series.fromDbValue(dbData.toString());
    }
}
