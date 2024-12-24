package com.pdd.redcurrant.domain.utils;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.databind.node.JsonNodeFactory;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;

@Slf4j
public final class MapperUtils {

    private static final ObjectMapper OBJECT_MAPPER = new ObjectMapper();

    static {
        OBJECT_MAPPER.findAndRegisterModules();
        OBJECT_MAPPER.configure(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS, false);
        OBJECT_MAPPER.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
        OBJECT_MAPPER.configure(DeserializationFeature.FAIL_ON_NUMBERS_FOR_ENUMS, true);
        OBJECT_MAPPER.configure(JsonGenerator.Feature.WRITE_BIGDECIMAL_AS_PLAIN, true);
        OBJECT_MAPPER.setNodeFactory(JsonNodeFactory.withExactBigDecimals(true));
    }

    private MapperUtils() {
    }

    /**
     * Method to convert an object to JSON string.
     *
     * @param obj object to be converted
     * @return json string. returns null, if caught some exceptions
     */
    public static String toString(Object obj) {
        return toString(obj, false);
    }

    /**
     * Method to convert an object to JSON string.
     *
     * @param obj      object to be converted
     * @param prettify passing true will format output
     * @return json string. returns null, if caught some exceptions
     */
    public static String toString(Object obj, boolean prettify) {
        try {
            if (obj == null || obj instanceof String) {
                return prettify ? OBJECT_MAPPER.writerWithDefaultPrettyPrinter()
                        .writeValueAsString(OBJECT_MAPPER.readTree((String) obj)) : (String) obj;
            }
            return prettify ? OBJECT_MAPPER.writerWithDefaultPrettyPrinter().writeValueAsString(obj)
                    : OBJECT_MAPPER.writeValueAsString(obj);
        } catch (JsonProcessingException ex) {
            log.info("Error converting toString. {}", ex.getLocalizedMessage());
            return null;
        }
    }

    /**
     * Method to convert an object to different class. Internally this Method performs
     * serialization and then deserializing to new class
     *
     * @param obj    object to be converted
     * @param tClass class reference of new object
     * @return converted object. null, if caught some exceptions
     */
    public static <T> T convert(Object obj, Class<T> tClass) {
        if (obj != null && obj.getClass().equals(tClass)) {
            // noinspection unchecked
            return (T) obj;
        }
        return convertObjects(obj, tClass);
    }

    /**
     * Method to convert an object to different class. Internally this Method performs
     * serialization and then deserializing to new class
     *
     * @param obj object to be converted
     * @param ref type reference of new object
     * @return converted object. null, if caught some exceptions
     */
    public static <T> T convert(Object obj, TypeReference<T> ref) {
        return convertObjects(obj, ref);
    }

    private static <T> T convertObjects(Object obj, Object ref) {
        try {
            if (obj == null) {
                return null;
            }
            if (ref instanceof Class cls && cls.isEnum() && obj instanceof String val) {
                return OBJECT_MAPPER.readValue(StringUtils.wrapIfMissing(val, "\""), (Class<T>) ref);
            }

            String val = (obj instanceof String ob) ? ob : MapperUtils.toString(obj);
            if (ref instanceof TypeReference) {
                return OBJECT_MAPPER.readValue(val, (TypeReference<T>) ref);
            } else {
                return OBJECT_MAPPER.readValue(val, (Class<T>) ref);
            }
        } catch (JsonProcessingException ex) {
            log.info("Error converting toObject. {}", ex.getLocalizedMessage());
            return null;
        }
    }

}
