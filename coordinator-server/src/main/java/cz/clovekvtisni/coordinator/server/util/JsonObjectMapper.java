package cz.clovekvtisni.coordinator.server.util;

import org.codehaus.jackson.map.DeserializationConfig;
import org.codehaus.jackson.map.ObjectMapper;
import org.codehaus.jackson.map.SerializationConfig;

public class JsonObjectMapper extends ObjectMapper {
    public JsonObjectMapper() {
        configure(SerializationConfig.Feature.WRITE_DATES_AS_TIMESTAMPS, false);
        configure(SerializationConfig.Feature.REQUIRE_SETTERS_FOR_GETTERS, true);
        configure(DeserializationConfig.Feature.FAIL_ON_UNKNOWN_PROPERTIES, false);
    }
}
