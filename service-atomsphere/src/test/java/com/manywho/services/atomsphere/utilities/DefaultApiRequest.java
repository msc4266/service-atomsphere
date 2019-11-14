package com.manywho.services.sql.utilities;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.common.io.Resources;
import com.manywho.sdk.api.run.elements.type.ObjectDataRequest;
import com.manywho.sdk.services.jaxrs.resolvers.ObjectMapperContextResolver;
import org.jboss.resteasy.core.Dispatcher;
import org.jboss.resteasy.mock.MockHttpRequest;
import org.jboss.resteasy.mock.MockHttpResponse;
import org.json.JSONException;
import org.skyscreamer.jsonassert.JSONAssert;

import javax.ws.rs.core.MediaType;
import java.io.IOException;
import java.net.URISyntaxException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.HashMap;

public class DefaultApiRequest {

    public static void describeServiceRequestAndAssertion(String url, String requestPathFile, HashMap<String, String> requestReplacements, String expectedResponsePathFile, Dispatcher dispatcher) throws IOException, URISyntaxException, JSONException {

        ObjectMapper objectMapper = new ObjectMapperContextResolver().getContext(null);
        MockHttpResponse response = new MockHttpResponse();

        MockHttpRequest request = MockHttpRequest.post(url)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsBytes(getObjectDataRequestFromFile(requestPathFile,requestReplacements)));

        dispatcher.invoke(request, response);

        JSONAssert.assertEquals(
                JsonFormatUtil.getFileContentAsJson(expectedResponsePathFile),
                response.getContentAsString(),
                false
        );
    }

    public static void loadDataRequestAndAssertion(String url, String requestPathFile, HashMap<String, String> requestReplacements, String expectedResponsePathFile, Dispatcher dispatcher) throws IOException, URISyntaxException, JSONException {

        MockHttpResponse response = loadDataRequestResponse(url, requestPathFile, requestReplacements, dispatcher);

        JSONAssert.assertEquals(
                JsonFormatUtil.getFileContentAsJson(expectedResponsePathFile),
                response.getContentAsString(),
                false
        );
    }

    public static MockHttpResponse loadDataRequestResponse(String url, String requestPathFile, HashMap<String, String> requestReplacements, Dispatcher dispatcher) throws IOException, URISyntaxException, JSONException {
        ObjectMapper objectMapper = new ObjectMapperContextResolver().getContext(null);
        MockHttpResponse response = new MockHttpResponse();

        MockHttpRequest request = MockHttpRequest.post(url)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsBytes(getObjectDataRequestFromFile(requestPathFile,requestReplacements)));

        dispatcher.invoke(request, response);

        return response;
    }

    public static void saveDataRequestAndAssertion(String url, String requestPathFile, HashMap<String, String> requestReplacements, String expectedResponsePathFile, Dispatcher dispatcher) throws IOException, URISyntaxException, JSONException {

        ObjectMapper objectMapper = new ObjectMapperContextResolver().getContext(null);
        MockHttpResponse response = new MockHttpResponse();

        MockHttpRequest request = MockHttpRequest.put(url)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsBytes(getObjectDataRequestFromFile(requestPathFile,requestReplacements)));

        dispatcher.invoke(request, response);

        JSONAssert.assertEquals(
                JsonFormatUtil.getFileContentAsJson(expectedResponsePathFile),
                response.getContentAsString(),
                false
        );
    }

    private static ObjectDataRequest getObjectDataRequestFromFile(String requestPathFile, HashMap<String, String> requestReplacements) throws IOException, URISyntaxException {
        String fileContent = new String(Files.readAllBytes(Paths.get(Resources.getResource(requestPathFile).toURI())));

        for(String key: requestReplacements.keySet()) {
            fileContent = fileContent.replace(key, requestReplacements.get(key));
        }

        return JsonFormatUtil.getObjectDataRequestFromString(fileContent);
    }
}
