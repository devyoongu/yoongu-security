package com.yoongu.security.apiserver.common.converter;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.yoongu.security.apiserver.common.util.JsonUtil;
import com.yoongu.security.apiserver.common.util.object.ObjectMapperComponent;
import java.io.IOException;
import javax.persistence.AttributeConverter;
import org.apache.commons.lang3.StringUtils;

public class JsonNodeConverter implements AttributeConverter<JsonNode, String> {

    private final ObjectMapper objectMapper = ObjectMapperComponent.getInstance;

    @Override
    public String convertToDatabaseColumn(JsonNode myDoc) {
        try {
            return objectMapper.writeValueAsString(myDoc);
        } catch (JsonProcessingException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public JsonNode convertToEntityAttribute(String myDocJson) {
        try {
            if (StringUtils.isBlank(myDocJson)) {
                return objectMapper.nullNode();
            }
            String converted = JsonUtil.jsonStringToObject(myDocJson);
            return objectMapper.readValue(converted, JsonNode.class);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
}
