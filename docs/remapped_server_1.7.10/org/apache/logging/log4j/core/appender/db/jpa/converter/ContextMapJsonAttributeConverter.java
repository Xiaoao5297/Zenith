package org.apache.logging.log4j.core.appender.db.jpa.converter;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.io.IOException;
import java.util.Map;
import javax.persistence.AttributeConverter;
import javax.persistence.Converter;
import javax.persistence.PersistenceException;
import org.apache.logging.log4j.core.helpers.Strings;

@Converter(autoApply = false)
public class ContextMapJsonAttributeConverter implements AttributeConverter<Map<String, String>, String> {

    static final ObjectMapper OBJECT_MAPPER = new ObjectMapper();

    public ContextMapJsonAttributeConverter() {}

    public String convertToDatabaseColumn(Map<String, String> map) {
        if (map == null) {
            return null;
        } else {
            try {
                return ContextMapJsonAttributeConverter.OBJECT_MAPPER.writeValueAsString(map);
            } catch (IOException ioexception) {
                throw new PersistenceException("Failed to convert map to JSON string.", ioexception);
            }
        }
    }

    public Map<String, String> convertToEntityAttribute(String s) {
        if (Strings.isEmpty(s)) {
            return null;
        } else {
            try {
                return (Map) ContextMapJsonAttributeConverter.OBJECT_MAPPER.readValue(s, new TypeReference<Map<String, String>>() {
                });
            } catch (IOException ioexception) {
                throw new PersistenceException("Failed to convert JSON string to map.", ioexception);
            }
        }
    }
}
