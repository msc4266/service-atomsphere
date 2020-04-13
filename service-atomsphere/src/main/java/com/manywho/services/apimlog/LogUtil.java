package com.manywho.services.apimlog;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.ProtocolException;
import java.net.URL;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Base64;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.TimeZone;
import java.util.UUID;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;

import org.json.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.common.collect.Lists;
import com.manywho.services.atomsphere.ServiceConfiguration;
import com.manywho.services.atomsphere.actions.apimclusterlogs.NodeLog;
import com.manywho.services.atomsphere.actions.downloadAtomLog.DownloadAtomLog;
import com.manywho.services.atomsphere.database.Database;

//BROKER
//2020_04_08.authentication_service.10_101_251_4.log
//Apr 8, 2020 3:53:17 PM +0000 WARN    [org
//2020_04_08.container.10_101_251_5.log
//Apr 8, 2020 1:21:00 AM UTC FINE 

//GATEWAY
//2020_04_08.api_gateway.10_101_251_5.log
//Apr 8, 2020 6:03:40 PM UTC ERROR 

//2020_04_08.api_gateway.access.10_101_251_5.log
//[2020-04-06T00:00:31.721+0000] 64f1c769-8042-403f-b1c7-698042003f9a 64f1c769-8042-403f-b1c7-698042003f9a (10.101.251.5) 168.63.129.16 SYSTEM_HEALTH_CHECK_ENDPOINT - GET /_admin/status 200 134 5 0

//2020_04_08.api_portal.access.10_101_251_5.log
//171.67.70.85 - - [08/Apr/2020:00:27:44 +0000] "GET //40.91.74.9/ HTTP/1.1" 200 1944 "-" "Mozilla/5.0 zgrab/0.x" 19

//2020_04_08.container.10_101_251_5.log
//Apr 8, 2020 12:00:39 AM UTC INFO 

//API MOLECULE
//2020_04_08.container.10_101_251_69.log
//Apr 8, 2020 12:40:00 AM UTC INFO    [

//2020_04_08.shared_http_server.10_101_251_69.log
//18.206.164.196 - testuser [08/Apr/2020:00:00:00 +0000] "GET /ws/rest/customer/getCustomerData/ HTTP/1.1" 200 234 "-" "Apache-HttpClient/4.5.6 (Java/1.8.0_242)" "a02c5356-4521-4e1f-8018-d249ef522c7b" "execution-8bce8ae8-39cb-4743-a4ca-e13ee556f25e-2020.04.08" 5

//API ATOM
//2020_04_08.container.log
//Apr 8, 2020 12:40:00 AM UTC INFO    [
//2020_04_08.shared_http_server.log
//3.217.179.119 -  -  [08/Apr/2020:00:00:00 +0000] "GET /_admin/status HTTP/1.1" 200 0 "-" "-"  "-" "-" 0

public class LogUtil {
	
    private static final Logger LOGGER = LoggerFactory.getLogger(Database.class);
	public static final String ACCESS_LOG_TIME_STAMP_MASK = "yyyy-MM-dd'T'hh:mm:ss.SSSZ";
	public static final int ACCESS_LOG_TIME_STAMP_LENGTH = "2020-04-06T00:01:19.862+0000".length();
	public static final int MAXLOGSIZE = 500000;

	public static String getLogType(String fileName)
	{
		if (fileName.indexOf("authentication_service")>-1)
			return "authentication_service";
		if (fileName.indexOf("container")>-1)
			return "container";
		if (fileName.indexOf("api_gateway.access")>-1) //order counts
			return "api_gateway.access";
		if (fileName.indexOf("api_gateway")>-1) //order counts
			return "api_gateway";
		if (fileName.indexOf("api_portal.access")>-1) //order counts
			return "api_portal.access";
		if (fileName.indexOf("shared_http_server")>-1) 
			return "shared_http_server";
		return"";
	}

	public static String getLogDateMask(String logType)
	{
		switch (logType)
		{
		case "authentication_service":
			return "MMM d, yyyy h:mm:ss a Z"; //"Apr 8, 2020 3:53:17 PM +0000";
		case "container":
		case  "api_gateway":
			return "MMM d, yyyy h:mm:ss a z"; //Apr 8, 2020 1:21:00 AM UTC
		case  "api_gateway.access":
			return ACCESS_LOG_TIME_STAMP_MASK; //[2020-04-08T00:00:03.620+0000]
		case  "api_portal.access":
		case  "shared_http_server":
			//18.206.164.196 - testuser [08/Apr/2020:00:00:00 +0000]
			return "dd/MMM/yyyy:hh:mm:ss Z"; //"08/Apr/2020:00:00:00 +0000";
		}
		return "";
	}
	
	public static boolean hasError(String logType, String line)
	{
		switch (logType)
		{
		case "authentication_service":
		case "container":
		case  "api_gateway":
			return line.contains(" ERROR ") || line.contains(" WARN ")  || line.contains(" WARNING ")  || line.contains(" error ")  || line.contains(" Error ");
		case  "api_gateway.access":
		case  "api_portal.access":
		case  "shared_http_server":
			return !getAccessLogStatusCode(line).contentEquals("200");
		}
		return true;
	}
	
	private static String getLogEntryTime(String logType, String line)
	{
		String dateString="";
		int dateEnd;
		String timeEndMarker="";
		switch (logType)
		{
		case "authentication_service":
			//Apr 8, 2020 3:53:17 PM +0000 WARN    [org
			timeEndMarker = " +0000 ";
			dateEnd = line.indexOf(timeEndMarker);
			if (dateEnd>-1)
			{
				dateEnd += timeEndMarker.length()-1;
				dateString = line.substring(0, dateEnd);
			}
			break;
		case "container":
		case  "api_gateway":
			//Apr 8, 2020 6:03:40 PM UTC ERROR 
			timeEndMarker = " UTC ";
			dateEnd = line.indexOf(timeEndMarker);
			if (dateEnd>-1)
			{
				dateEnd += timeEndMarker.length()-1;
				dateString = line.substring(0, dateEnd);
			}
			break;
		case  "api_gateway.access":
			//[2020-04-08T00:00:03.620+0000]
			dateEnd = line.indexOf("]");
			if (dateEnd > 0)
				dateString = line.substring(1, dateEnd);
			break;
		case  "api_portal.access":
		case  "shared_http_server":
			//18.206.164.196 - testuser [08/Apr/2020:00:00:00 +0000]
			int dateStart = line.indexOf("[");
			if (dateStart > 0)
			{
				dateEnd = line.indexOf("]");
				if (dateEnd > dateStart)
					dateString = line.substring(dateStart+1, dateEnd);
			}
			break;
		}
		return dateString;
	}	

	public static String getHost(String fileName)
	{
		String logType = getLogType(fileName);
		if (logType.length()>0)
		{
			int hostPos = fileName.indexOf(logType)+logType.length()+1;
			int hostPosEnd = fileName.lastIndexOf(".log");
			if (hostPos<hostPosEnd)
				return fileName.substring(hostPos, hostPosEnd);
		}
		return "";
	}
	
	public static String getLogFileRange(String fileName, InputStream is, boolean errorsOnly, Date startTime, int secondsBefore, int secondsAfter) throws IOException, ParseException
	{
//		if (accessLogEntry!=null && accessLogEntry.trim().length()>0)
//			startTime = getStartTimeFromLogEntry(accessLogEntry);
		StringBuilder results = new StringBuilder();
		String logType = getLogType(fileName);
		
		SimpleDateFormat simpleDateFormat = new SimpleDateFormat(getLogDateMask(logType));
		SimpleDateFormat targetDateFormat = new SimpleDateFormat("yyyy-MM-dd h:mm:ss.SSS a z");
		
		Calendar startRange = Calendar.getInstance();
		Calendar endRange = Calendar.getInstance();
		Calendar logCalendar = Calendar.getInstance();
		startRange.setTime(startTime);
		startRange.add(Calendar.SECOND,-secondsBefore);
		endRange.setTime(startTime);
		endRange.add(Calendar.SECOND, secondsAfter);
		
		BufferedReader reader;
		boolean inRange=false; //we want non timestamped trailer entries if we are in range
		reader = new BufferedReader(new InputStreamReader(is, "UTF-8"));
		String line = reader.readLine();
		int noTimestampCount = 0;
		int size=0;
		while (line != null) {
			if (size>MAXLOGSIZE)
			{
				results.append("...");
				break;
			}
			String dateString = getLogEntryTime(logType, line);
			if (dateString.length()>0)
			{
				Date logDate = simpleDateFormat.parse(dateString);
				logCalendar.setTime(logDate);
				if (logCalendar.after(startRange))
				{
					inRange = true;
					noTimestampCount = 0;
					if (logCalendar.before(endRange))
					{
						boolean doInclude = true;
						if (errorsOnly && !hasError(logType,line))
						{
							doInclude=false;
							inRange=false;
						}
						if (doInclude)
						{
							results.append(targetDateFormat.format(logDate) + " " + line.replace(dateString, "")+"\r\n");
							size+=line.length();
						}
					} else {
						break;
					}
				}			
			} else if (inRange){
				//include entry only if last timestamp within range
				noTimestampCount++;
				if (noTimestampCount<5)
				{
					results.append(line+"\r\n");
					size+=line.length();
				}
			}

			// read next line
			line = reader.readLine();
		}
//		reader.close();
		return results.toString();
	}
	
	public static Date getStartTimeFromLogEntry(String logEntry) throws ParseException
	{
		String logType="api_gateway.access";
		SimpleDateFormat simpleDateFormat = new SimpleDateFormat(getLogDateMask(logType));
		String dateString = getLogEntryTime(logType, logEntry);
		return simpleDateFormat.parse(dateString);
	}

	//TODO improve this with regex?
	private static String getAccessLogStatusCode(String line)
	{
//		int STATUS_CODE_START = 184;
//		int STATUS_CODE_LENGTH = 3;
		
		String status="";
		int statusPos = line.indexOf(" 200 ");
		if (statusPos == -1)
			statusPos = line.indexOf(" 401 ");
		if (statusPos == -1)
			statusPos = line.indexOf(" 502 ");
		if (statusPos == -1)
			statusPos = line.indexOf(" 503 ");
		if (statusPos == -1)
			statusPos = line.indexOf(" 400 ");
		
		if (statusPos != -1)
			status = line.substring(statusPos, statusPos+5).trim();
				
		return status;
	}
	
	public static List<NodeLog> getLogFiles(ServiceConfiguration configuration, String atomId, boolean errorsOnly, Date startTime, int secondsBefore, int secondsAfter) throws Exception
	{
		List<NodeLog> logs=Lists.newArrayList();
		JSONObject body = new JSONObject();
		body.put("atomId", atomId);
		SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd");
		simpleDateFormat.setTimeZone(TimeZone.getTimeZone("UTC"));
		body.put("logDate", simpleDateFormat.format(startTime)); //need logDate in UTC
		JSONObject response = Database.executeAPI(configuration, "AtomLog", "POST", null, body);
		DownloadAtomLog.Outputs outputs = new DownloadAtomLog.Outputs(response);
		
		LOGGER.info("Errors Only: " + errorsOnly);
		LOGGER.info("Start Time: " + startTime);
		LOGGER.info("Before: " + secondsBefore);
		LOGGER.info("After: " + secondsAfter);
		
		InputStream is = executeGetLogs(configuration, outputs.getUrl());
        ZipInputStream zis = new ZipInputStream(is);
        ZipEntry entry = zis.getNextEntry();

        while(entry != null) {

//            System.out.println(entry.getName());
            String logType = getLogType(entry.getName());
            String host = getHost(entry.getName());
            if (!entry.isDirectory()) {
                // if the entry is a file, extracts it
                String entries = LogUtil.getLogFileRange(entry.getName(), zis, errorsOnly, startTime, secondsBefore, secondsAfter);
                NodeLog log = new NodeLog();
                log.setEntries(entries);
                log.setGuid(UUID.randomUUID().toString());
                log.setLogType(logType);
                log.setNode(host);
                log.setFileName(entry.getName());
                log.setEntrySize(log.getEntries().length());
                logs.add(log);
                LOGGER.info("entry: "+ log.getEntries().length() + " " + log.getFileName());
//                System.out.println(logs);
            } else {
               // System.out.println("===Directory===");
            }
            zis.closeEntry();
            entry = zis.getNextEntry();
        }
        return logs;
	}
	
	public static InputStream executeGetLogs(ServiceConfiguration configuration, String urlString) throws Exception 
	{
        HttpURLConnection conn=null;
        StringBuffer response= new StringBuffer();
        int downloadCheckIterations=0;
        int sleepTime=1000;
        int maxIterations = 60;
		
        try {
    		URL url = new URL(urlString);
    		boolean done=false;
    		do
    		{
                conn = (HttpURLConnection) url.openConnection();
    			conn.setRequestMethod("GET");
    	        conn.setDoOutput(true);
    	    	String userpass = configuration.getUsername() + ":" + configuration.getPassword();
    	    	String basicAuth = "Basic " + new String(Base64.getEncoder().encode(userpass.getBytes()));
    	    	conn.setRequestProperty ("Authorization", basicAuth);
    	    	if (conn.getResponseCode()==202)
    	    	{
    	    		//TODO close inputstream??
    	    		conn.getInputStream().close();
    	    		downloadCheckIterations++;
    	    		if(downloadCheckIterations>maxIterations)
    	    			throw new Exception("Log download wait time exceeded " + sleepTime * maxIterations);
    	    		Thread.sleep(sleepTime);
    	    	} else {
    	    		done=true;
    	    	}
    		} while(!done);
	    	
    		return conn.getInputStream();
		} catch (ProtocolException e1) {
			throw new RuntimeException(e1);
		} catch (IOException e) {
			try {
				if (conn!=null)
				{
					response.append("Error code: " + conn.getResponseCode());
					response.append(" " + conn.getResponseMessage() + " ");
					if (conn.getErrorStream()!=null)
					{
						BufferedReader br = new BufferedReader(new InputStreamReader(conn.getErrorStream())) ;
						String responseLine = null;
						while ((responseLine = br.readLine()) != null) {
						    response.append(responseLine.trim());
						}
					}
//					LOGGER.error(response.toString());
					throw new RuntimeException(response.toString());
				} else throw new RuntimeException(e);
			} catch (IOException e1) {
				throw new RuntimeException(e1);
			}
		}
//	    LOGGER.info(response.toString());
	}
}
