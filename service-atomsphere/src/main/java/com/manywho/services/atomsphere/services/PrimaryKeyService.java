package com.manywho.services.atomsphere.services;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.apache.commons.codec.binary.Base64;
import java.util.HashMap;

public class PrimaryKeyService {

    private ObjectMapper objectMapper;

    public PrimaryKeyService() {
        objectMapper = new ObjectMapper();
    }

    public HashMap<String, String> deserializePrimaryKey(String serializedPrimaryKey) {
        try {
            byte[] decoded = Base64.decodeBase64(serializedPrimaryKey.getBytes());
            return objectMapper.readValue(new String(decoded, "UTF-8"), new TypeReference<HashMap<String,Object>>() {});

        } catch (Exception e) {
            throw new RuntimeException("The primary key is not valid. It should be a JSON object, then Base64 encoded, e.g. {\"id\": 1} would become eyJpZCI6MX0=", e);
        }
    }

    public String serializePrimaryKey(HashMap<String, String> primaryKeys) {
        try {
            return new String(Base64.encodeBase64(objectMapper.writeValueAsString(primaryKeys).getBytes()), "UTF-8");
        } catch (Exception e) {
            throw new RuntimeException("error when serialize primary key", e);
        }
    }
}
