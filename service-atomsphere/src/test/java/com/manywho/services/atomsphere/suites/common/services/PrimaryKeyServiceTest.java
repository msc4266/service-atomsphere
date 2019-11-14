package com.manywho.services.sql.suites.common.services;

import com.manywho.services.sql.services.PrimaryKeyService;
import org.junit.Test;

import java.util.HashMap;

import static org.junit.Assert.assertEquals;

public class PrimaryKeyServiceTest {
    @Test
    public void serializeIdTest() {
        PrimaryKeyService primaryKeyService = new PrimaryKeyService();
        HashMap<String, String> primaryKey = new HashMap<>();
        primaryKey.put("id", "1");
        String serialized = primaryKeyService.serializePrimaryKey(primaryKey);

        assertEquals("eyJpZCI6IjEifQ==", serialized);
    }

    @Test
    public void serializeMultipleIdTest() {
        PrimaryKeyService primaryKeyService = new PrimaryKeyService();
        HashMap<String, String> primaryKey = new HashMap<>();
        primaryKey.put("countryname", "Uruguay");
        primaryKey.put("cityname", "Montevideo");
        String serialized = primaryKeyService.serializePrimaryKey(primaryKey);

        assertEquals("eyJjaXR5bmFtZSI6Ik1vbnRldmlkZW8iLCJjb3VudHJ5bmFtZSI6IlVydWd1YXkifQ==", serialized);
    }

    @Test
    public void serializeMultipleIdExampleAliasesTest() {
        PrimaryKeyService primaryKeyService = new PrimaryKeyService();
        HashMap<String, String> primaryKey = new HashMap<>();
        primaryKey.put("Country Name", "Uruguay");
        primaryKey.put("City Name", "Montevideo");
        String serialized = primaryKeyService.serializePrimaryKey(primaryKey);

        assertEquals("eyJDb3VudHJ5IE5hbWUiOiJVcnVndWF5IiwiQ2l0eSBOYW1lIjoiTW9udGV2aWRlbyJ9", serialized);
    }

    @Test
    public void deserializeTest() {
        PrimaryKeyService primaryKeyService = new PrimaryKeyService();
        HashMap<String, String> primaryKey = primaryKeyService.deserializePrimaryKey("eyJjaXR5bmFtZSI6Ik1vbnRldmlkZW8iLCJjb3VudHJ5bmFtZSI6IlVydWd1YXkifQ==");

        assertEquals(2, primaryKey.size());
        assertEquals("Uruguay", primaryKey.get("countryname"));
        assertEquals("Montevideo", primaryKey.get("cityname"));
    }

    @Test
    public void consistentSerializationAndDeserialization() {
        PrimaryKeyService primaryKeyService = new PrimaryKeyService();
        HashMap<String, String> toSerialize = new HashMap<>();
        toSerialize.put("cityname", "London");

        String serialized = primaryKeyService.serializePrimaryKey(toSerialize);
        HashMap<String, String> deserialized = primaryKeyService.deserializePrimaryKey(serialized);

        assertEquals(toSerialize.get("cityname"), deserialized.get("cityname"));
        assertEquals("London", toSerialize.get("cityname"));
    }
}
