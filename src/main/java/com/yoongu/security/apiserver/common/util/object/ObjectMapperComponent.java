package com.yoongu.security.apiserver.common.util.object;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.JsonDeserializer;
import com.fasterxml.jackson.databind.JsonSerializer;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.databind.SerializerProvider;
import com.fasterxml.jackson.datatype.jdk8.Jdk8Module;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import java.io.IOException;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;

public class ObjectMapperComponent {

    public static final ObjectMapper getInstance = Holder.objectMapper;

    private static class Holder {

        private static final ObjectMapper objectMapper = new ObjectMapper();

        static {
            objectMapper.disable(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS);
            JavaTimeModule javaTimeModule = new JavaTimeModule();
            javaTimeModule.addSerializer(LocalDate.class, new LocalDateSerializer());
            javaTimeModule.addSerializer(LocalDateTime.class, new LocalDateTimeSerializer());
            javaTimeModule.addSerializer(LocalTime.class, new LocalTimeSerializer());
            javaTimeModule.addDeserializer(LocalDate.class, new LocalDateDeserializer());
            javaTimeModule.addDeserializer(LocalDateTime.class, new LocalDateTimeDeserializer());
            javaTimeModule.addDeserializer(LocalTime.class, new LocalTimeDeserializer());
            objectMapper.registerModules(javaTimeModule, new Jdk8Module());
        }
    }

    private static final DateTimeFormatter DATE_FORMATTER = DateTimeFormatter.ISO_LOCAL_DATE;

    private static final DateTimeFormatter DATE_TIME_FORMATTER = DateTimeFormatter.ISO_LOCAL_DATE_TIME;

    private static final DateTimeFormatter TIME_FORMATTER = DateTimeFormatter.ISO_LOCAL_TIME;

    private static class LocalDateSerializer extends JsonSerializer<LocalDate> {

        @Override
        public void serialize(LocalDate value, JsonGenerator gen, SerializerProvider serializers) throws IOException {
            gen.writeString(value.format(DATE_FORMATTER));
        }
    }

    private static class LocalDateDeserializer extends JsonDeserializer<LocalDate> {

        @Override
        public LocalDate deserialize(JsonParser jsonParser, DeserializationContext context) throws IOException {
            return LocalDate.parse(jsonParser.getValueAsString(), DATE_FORMATTER);
        }
    }

    private static class LocalDateTimeSerializer extends JsonSerializer<LocalDateTime> {

        @Override
        public void serialize(LocalDateTime value, JsonGenerator gen, SerializerProvider serializers) throws IOException {
            gen.writeString(value.format(DATE_TIME_FORMATTER));
        }
    }

    private static class LocalDateTimeDeserializer extends JsonDeserializer<LocalDateTime> {

        @Override
        public LocalDateTime deserialize(JsonParser jsonParser, DeserializationContext context) throws IOException {
            return LocalDateTime.parse(jsonParser.getValueAsString(), DATE_TIME_FORMATTER);
        }
    }

    private static class LocalTimeSerializer extends JsonSerializer<LocalTime> {

        @Override
        public void serialize(LocalTime value, JsonGenerator gen, SerializerProvider serializers) throws IOException {
            gen.writeString(value.format(TIME_FORMATTER));
        }
    }

    private static class LocalTimeDeserializer extends JsonDeserializer<LocalTime> {

        @Override
        public LocalTime deserialize(JsonParser jsonParser, DeserializationContext context) throws IOException {
            return LocalTime.parse(jsonParser.getValueAsString(), TIME_FORMATTER);
        }
    }
}
