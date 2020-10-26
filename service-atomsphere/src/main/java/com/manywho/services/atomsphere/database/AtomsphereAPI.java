package com.manywho.services.atomsphere.database;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.ProtocolException;
import java.net.URL;
import java.util.Base64;
import java.util.logging.Logger;
import org.json.JSONObject;
import com.manywho.services.atomsphere.ServiceConfiguration;

public class AtomsphereAPI {
    private static Logger logger = Logger.getLogger(AtomsphereAPI.class.getName());

//	private static JSONObject executeAPI(String accountId, String token, String entityName, String method, String resource, JSONObject payload) 
//	{
//		String payloadString=null;
//		if (payload!=null) payloadString=payload.toString();
//		return executeAPI(accountId, token, entityName, method, resource, payloadString, false);
//	}
	
	//Pagination of Query Results
	public static JSONObject executeAPIQueryMore(ServiceConfiguration configuration, String token, String entityName, String queryToken, boolean isAPIMEntity) 
	{
		return executeAPI(configuration, token, entityName, "POST", "queryMore", queryToken, isAPIMEntity);
	}
	
    //queryMore queryToken uses just a text blob, not jsonobject so we pass in a string payload
    //APIM https://api.boomi.com/apim/api/rest/v1/{accountID}
    public static JSONObject executeAPI(ServiceConfiguration configuration, String token, String entityName, String method, String resource, String payload, boolean isAPIMEntity) 
	{
		if (resource!=null)
			resource="/"+resource;
		else 
			resource="";
		String urlString = "https://api.boomi.com/api/rest/v1/";
		if (isAPIMEntity)
			urlString = "https://api.boomi.com/apim/api/rest/v1/";
        StringBuffer response= new StringBuffer();
        HttpURLConnection conn=null;
        String responseCode = null;
		
        try {
Thread.sleep(300); //Boomi Default Rate Limit is 1 call per 200ms   	
    		URL url = new URL(urlString+String.format("%s/%s%s", configuration.getAccount(), entityName, resource));
    		logger.info("Account: " + configuration.getAccount());
    		logger.info(method + " " + url.toString());
            conn = (HttpURLConnection) url.openConnection();
			conn.setRequestMethod(method);
	        conn.setDoOutput(true);
	        conn.setRequestProperty("Content-Type", "application/json");
	        conn.setRequestProperty("Accept", "application/json");        

	    	if (!configuration.useIDPCredentials())
	    	{
	    		token = buildAuthToken(configuration.getUsername(), configuration.getPassword());
	    		logger.info("API using set credentials");
	    	}
	    	//restore base64 padding due to identity provider authorization puking on =
	    	while ((token.length()%4)!=0)
	    		token+="=";
	    	token = "Basic " + token;
	    	conn.setRequestProperty ("Authorization", token);
	    	if (payload!=null)
	    	{
		        conn.setDoInput(true);
	    		String body = payload;
//		        logger.info(body);
		        byte[] input = body.getBytes();
		 		conn.setRequestProperty("Content-Length", ""+input.length);
		        OutputStream os = conn.getOutputStream();
		        os.write(input, 0, input.length);  
	    	}  
	    		 
			responseCode = conn.getResponseCode() +"";
	        
			BufferedReader br = new BufferedReader(new InputStreamReader(conn.getInputStream())) ;
			String responseLine = null;
			while ((responseLine = br.readLine()) != null) {
			    response.append(responseLine.trim());
			}
		} catch (ProtocolException e1) {
			throw new RuntimeException(e1);
		} catch (InterruptedException e1) {
			throw new RuntimeException(e1);
		} catch (IOException e) {
			try {
				if (conn!=null)
				{
					responseCode = conn.getResponseCode() +"";
					response.append("Error code: " + conn.getResponseCode());
					response.append(" " + conn.getResponseMessage() + " ");
					InputStream errorStream = conn.getErrorStream();
					if (errorStream!=null)
					{
						BufferedReader br = new BufferedReader(new InputStreamReader(errorStream)) ;
						String responseLine = null;
						while ((responseLine = br.readLine()) != null) {
						    response.append(responseLine.trim());
						}
					}
					logger.severe("API Error:" + response.toString());
					throw new RuntimeException(response.toString());
				} else throw new RuntimeException(e);
			} catch (IOException e1) {
				logger.severe("API Error:" + e1.toString());
				throw new RuntimeException(e1);
			}
		}
		logger.info(response.toString());
        String responseString = response.toString();
        JSONObject responseObj = null;
        if (responseString.length()==0)
        {	
        	responseObj = new JSONObject();
        	responseObj.put("statusCode", responseCode);
        } else {
        	responseObj = new JSONObject(response.toString());
        }
        return responseObj;
	}
    
    public static String buildAuthToken(String username, String password)
    {
    	//remove the base64 padding char because the injection to authorization chokes on it with:
    	// Unable to provision, see the following errors:\n\n1) Error in custom provider, java.lang.IllegalArgumentException: Chunk .........is not a valid entry\n while locating com.manywho.sdk.services.providers.AuthenticatedWhoProvider\n at
    	String token = new String(Base64.getEncoder().encode((username + ":" + password).getBytes()));
    	return token.replaceAll("=","");
    }
}
