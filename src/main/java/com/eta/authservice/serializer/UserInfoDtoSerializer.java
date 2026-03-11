package com.eta.authservice.serializer;

import com.eta.authservice.model.UserInfoDto;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.apache.kafka.common.header.Headers;
import org.apache.kafka.common.serialization.Serializer;

import java.util.Map;

public class UserInfoDtoSerializer implements Serializer<UserInfoDto> {

    // Called when serializer is initialized with configuration
    @Override
    public void configure(Map<String, ?> configs, boolean isKey) {
        Serializer.super.configure(configs, isKey);
    }

    // Converts UserInfoDto object into byte[] before sending to Kafka
    @Override
    public byte[] serialize(String key, UserInfoDto value) {

        byte[] returnByteValue = null;
        ObjectMapper objectMapper = new ObjectMapper();// ObjectMapper used to convert Java object to JSON

        try{
            returnByteValue = objectMapper.writeValueAsString(value).getBytes(); // Convert object to JSON string then to byte array
        }catch (Exception exception){
            exception.printStackTrace();
        }

        return returnByteValue;
    }

    // Optional method supporting Kafka headers (default implementation used)
    @Override
    public byte[] serialize(String topic, Headers headers, UserInfoDto data) {
        return Serializer.super.serialize(topic, headers, data);
    }

    // Called when serializer is closed
    @Override
    public void close() {
        Serializer.super.close();
    }
}