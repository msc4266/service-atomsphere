package com.manywho.services.atomsphere.actions.utility_getFlowIPAddress;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.ProtocolException;
import java.net.URL;
import java.util.Base64;

import org.json.JSONObject;

import com.manywho.sdk.api.run.elements.config.ServiceRequest;
import com.manywho.sdk.services.actions.ActionCommand;
import com.manywho.sdk.services.actions.ActionResponse;
import com.manywho.services.atomsphere.database.Database;
import com.manywho.services.atomsphere.ServiceConfiguration;

public class GetIPAddressCommand implements ActionCommand<ServiceConfiguration, GetIPAddress, GetIPAddress.Inputs, GetIPAddress.Outputs>{

	@Override
	public ActionResponse<GetIPAddress.Outputs> execute(ServiceConfiguration configuration, ServiceRequest request,
			GetIPAddress.Inputs input) {
		String ipAddress = "";
		
        StringBuffer response= new StringBuffer();
        HttpURLConnection conn=null;
        String responseCode = null;
		
        try {
//    		URL url = new URL("https://api.myip.com");
    		URL url = new URL("https://api.my-ip.io/ip.json");
            conn = (HttpURLConnection) url.openConnection();
			conn.setRequestMethod("GET");
	        conn.setDoOutput(true);
	        conn.setRequestProperty("Content-Type", "application/json");
//	        conn.setRequestProperty("Accept", "application/json");	    		
			responseCode = conn.getResponseCode() +"";
	        
			BufferedReader br = new BufferedReader(new InputStreamReader(conn.getInputStream())) ;
			String responseLine = null;
			while ((responseLine = br.readLine()) != null) {
			    response.append(responseLine.trim());
			}
		} catch (ProtocolException e1) {
			throw new RuntimeException(e1);
		} catch (IOException e) {
			try {
				if (conn!=null)
				{
					responseCode = conn.getResponseCode() +"";
					response.append("Error code: " + conn.getResponseCode());
					response.append(" " + conn.getResponseMessage() + " ");
					BufferedReader br = new BufferedReader(new InputStreamReader(conn.getErrorStream())) ;
					String responseLine = null;
					while ((responseLine = br.readLine()) != null) {
					    response.append(responseLine.trim());
					}
					throw new RuntimeException(response.toString());
				} else throw new RuntimeException(e);
			} catch (IOException e1) {
				throw new RuntimeException(e1);
			}
		}
        String responseString = response.toString();
        JSONObject responseObj = null;
        if (responseString.length()==0)
        {	
        	ipAddress = responseCode;
        } else {
        	responseObj = new JSONObject(response.toString());
        	ipAddress = responseObj.getString("ip");
        }
        
		return new ActionResponse<>(new GetIPAddress.Outputs(ipAddress));
	}
}
