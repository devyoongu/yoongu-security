package com.yoongu.security.apiserver.common.util.object;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.io.IOException;

public class ObjectMapperUtils {

    private static final ObjectMapper objectMapper = ObjectMapperComponent.getInstance;

    public static <T> T parse(String jsonString) {
        try {
            return objectMapper.readValue(jsonString, new TypeReference<T>() {});
        } catch (JsonProcessingException e) {
            throw new RuntimeException(e);
        }
    }

    public static <T> T parse(String jsonString, TypeReference<T> typeReference) {
        try {
            return objectMapper.readValue(jsonString, typeReference);
        } catch (JsonProcessingException e) {
            throw new RuntimeException(e);
        }
    }

    public static <T> T parse(JsonNode json) {
        try {
            return objectMapper.readValue(json.toString(), new TypeReference<T>() {});
        } catch (JsonProcessingException e) {
            throw new RuntimeException(e);
        }
    }

    public static <T> T parse(JsonNode json, TypeReference<T> typeReference) {
        try {
            return objectMapper.readValue(json.toString(), typeReference);
        } catch (JsonProcessingException e) {
            throw new RuntimeException(e);
        }
    }

    public static <T> String toString(T src) {
        try {
            return objectMapper.writeValueAsString(src);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
}
