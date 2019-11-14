package com.manywho.services.sql.utilities;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import com.google.common.io.Resources;
import com.manywho.sdk.api.describe.DescribeServiceResponse;
import com.manywho.sdk.api.run.elements.type.ObjectDataRequest;
import com.manywho.sdk.api.run.elements.type.ObjectDataResponse;
import org.json.JSONException;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.net.URISyntaxException;
import java.util.Scanner;

public class JsonFormatUtil {

    public static ObjectDataRequest getObjectDataRequestFromJsonFile(String filePath) throws URISyntaxException, IOException {
        return getMapper().readValue(new File(Resources.getResource(filePath).toURI()), ObjectDataRequest.class);
    }

    public static ObjectDataRequest getObjectDataRequestFromString(String str) throws URISyntaxException, IOException {
        return getMapper().readValue(str, ObjectDataRequest.class);
    }

    public static String getFileContentAsJson(String filePath) throws FileNotFoundException, URISyntaxException, JSONException {

         return (new Scanner(getFile(filePath))).useDelimiter("\\Z").next();
    }

    public static String getDescribeServiceResponseAsJson(DescribeServiceResponse describeServiceResponse) throws JsonProcessingException {

        return getMapper().writeValueAsString(describeServiceResponse);
    }

    public static String getObjectDataResponseAsJson(ObjectDataResponse objectDataResponse) throws JsonProcessingException {

        return getMapper().writeValueAsString(objectDataResponse);
    }

    private static ObjectMapper getMapper () {

        return new ObjectMapper()
                .disable(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES)
                .disable(DeserializationFeature.FAIL_ON_IGNORED_PROPERTIES)
                .enable(DeserializationFeature.READ_ENUMS_USING_TO_STRING)
                .enable(SerializationFeature.WRITE_ENUMS_USING_TO_STRING)
                .enable(SerializationFeature.ORDER_MAP_ENTRIES_BY_KEYS)
                .registerModule(new JavaTimeModule());
    }

    protected static File getFile(String fileResourcePath) throws URISyntaxException {
        return new File(Resources.getResource(fileResourcePath).toURI());
    }
}
