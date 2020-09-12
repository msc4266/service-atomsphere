package com.manywho.services.apimlog;

import static org.junit.Assert.*;

import org.json.JSONException;
import org.json.JSONObject;
import org.junit.Before;
import org.junit.Test;

import com.manywho.sdk.api.security.AuthenticatedWho;
import com.manywho.services.TestUtil;
import com.manywho.services.atomsphere.ServiceConfiguration;
import com.manywho.services.atomsphere.actions.utility_atomcompare.AtomPropertyCompareItem;
import com.manywho.services.atomsphere.actions.utility_atomcompare.CompareAtomProperties;
import com.manywho.services.atomsphere.actions.utility_apimclusterlogs.GetClusterLogs;
import com.manywho.services.atomsphere.actions.utility_apimclusterlogs.NodeLog;
import com.manywho.services.atomsphere.actions.utility_processloganalysis.ProcessLogItem;
import java.io.InputStream;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

public class LogUtilTest {
	ServiceConfiguration configuration;
	AuthenticatedWho user = new AuthenticatedWho();
	
	@Before
	public void init() throws JSONException, Exception
	{
		JSONObject testCredentials=new JSONObject(TestUtil.readResource("testCredentials.json", this.getClass()));
		configuration = new ServiceConfiguration();
		configuration.setAccount(testCredentials.getString("accountId"));
		configuration.setUsername(testCredentials.getString("username"));
		configuration.setPassword(testCredentials.getString("password"));
	}
	
	@Test
	public void testDateParser() throws ParseException
	{		
		SimpleDateFormat logDateFormat;
		Date logDate;

		String logMask = LogUtil.getLogDateMask("api_gateway");
		logDateFormat = new SimpleDateFormat(logMask);
		SimpleDateFormat targetDateFormat = LogUtil.getTargetDateFormat("PST", null);
//		TimeZone tz = TimeZone.getTimeZone("PST");
		logDate = logDateFormat.parse("Apr 16, 2020 11:23:36 PM UTC");
		assertEquals("Thu Apr 16 16:23:36 PDT 2020", logDate.toString());

		assertEquals("2020-04-16 16:23:36 PDT", targetDateFormat.format(logDate));

		logDate = logDateFormat.parse("Apr 8, 2020 1:21:00 AM UTC");
		assertEquals("Tue Apr 07 18:21:00 PDT 2020", logDate.toString());
		assertEquals("2020-04-07 18:21:00 PDT", targetDateFormat.format(logDate));

		logDate = logDateFormat.parse("Apr 8, 2020 1:21:00 PM UTC");
		assertEquals("Wed Apr 08 06:21:00 PDT 2020", logDate.toString());
		assertEquals("2020-04-08 06:21:00 PDT", targetDateFormat.format(logDate));
		
		
		//		public static String getLogDateMask(String logType)
//		{
//			switch (logType)
//			{
//			case "authentication_service":
//				return "MMM d, yyyy h:mm:ss a Z"; //"Apr 8, 2020 3:53:17 PM +0000";
//			case "container":
//			case  "api_gateway":
//				return "MMM d, yyyy h:mm:ss a z"; //Apr 8, 2020 1:21:00 AM UTC
//			case  "api_gateway.access":
//				return ACCESS_LOG_TIME_STAMP_MASK; //[2020-04-08T00:00:03.620+0000]
//			case  "api_portal.access":
//			case  "shared_http_server":
//				//18.206.164.196 - testuser [08/Apr/2020:00:00:00 +0000]
//				return "dd/MMM/yyyy:hh:mm:ss Z"; //"08/Apr/2020:00:00:00 +0000";
//			}
//			return "";
//		}

	}

	@Test
	public void testParseFileName()
	{
		assertEquals("authentication_service",LogUtil.getLogType("2020_04_08.authentication_service.10_101_251_4.log"));
		assertEquals("10_101_251_4",LogUtil.getHost("2020_04_08.authentication_service.10_101_251_4.log"));
		assertEquals("container",LogUtil.getLogType("2020_04_08.container.10_101_251_5.log"));
		assertEquals("10_101_251_5",LogUtil.getHost("2020_04_08.container.10_101_251_5.log"));
		assertEquals("api_gateway",LogUtil.getLogType("2020_04_08.api_gateway.10_101_251_5.log"));
		assertEquals("10_101_251_5",LogUtil.getHost("2020_04_08.api_gateway.10_101_251_5.log"));
		assertEquals("api_gateway.access",LogUtil.getLogType("2020_04_08.api_gateway.access.10_101_251_5.log"));
		assertEquals("10_101_251_5",LogUtil.getHost("2020_04_08.api_gateway.access.10_101_251_5.log"));
		assertEquals("api_portal.access",LogUtil.getLogType("2020_04_08.api_portal.access.10_101_251_5.log"));
		assertEquals("10_101_251_5",LogUtil.getHost("2020_04_08.api_portal.access.10_101_251_5.log"));
		assertEquals("container",LogUtil.getLogType("2020_04_08.container.10_101_251_5.log"));
		assertEquals("10_101_251_5",LogUtil.getHost("2020_04_08.container.10_101_251_5.log"));
		assertEquals("container",LogUtil.getLogType("2020_04_08.container.10_101_251_69.log"));
		assertEquals("10_101_251_69",LogUtil.getHost("2020_04_08.container.10_101_251_69.log"));
		assertEquals("shared_http_server",LogUtil.getLogType("2020_04_08.shared_http_server.10_101_251_69.log"));
		assertEquals("10_101_251_69",LogUtil.getHost("2020_04_08.shared_http_server.10_101_251_69.log"));
		assertEquals("container",LogUtil.getLogType("2020_04_08.container.log"));
		assertEquals("",LogUtil.getHost("2020_04_08.container.log"));
		assertEquals("shared_http_server",LogUtil.getLogType("2020_04_08.shared_http_server.log"));
		assertEquals("",LogUtil.getHost("2020_04_08.shared_http_server.log"));
	}
	
	@Test
	public void testAccessLog() throws Exception {
		
		SimpleDateFormat simpleDateFormat = new SimpleDateFormat(LogUtil.ACCESS_LOG_TIME_STAMP_MASK);
		Date startTime = simpleDateFormat.parse("2020-04-06T05:01:19.862+0000");
		GetClusterLogs.Inputs input = new GetClusterLogs.Inputs();
		input.setAtomId("c418d1f7-d9c7-4886-a69d-88a420406fde");
		input.setStartTime(startTime);
		input.setErrorsOnly(false);
		input.setSecondsBefore(60);
		input.setSecondsAfter(180);
		
		String fileName = "2020_04_06.api_gateway.access.10_101_251_5.log";
		InputStream is = TestUtil.getResourceAsStream("apimlogs/"+fileName, this.getClass());

		String response = LogUtil.getLogFileRange(fileName, is, input);
		is.close();
		System.out.println(response);
		assertEquals(8278, response.length());
	}
	
	@Test
	public void testContainerLog() throws Exception {
		
		SimpleDateFormat simpleDateFormat = new SimpleDateFormat(LogUtil.ACCESS_LOG_TIME_STAMP_MASK);
		Date startTime;
		GetClusterLogs.Inputs input = new GetClusterLogs.Inputs();
		input.setAtomId("c418d1f7-d9c7-4886-a69d-88a420406fde");
		input.setErrorsOnly(false);
		
		String fileName;
		String response;
		InputStream is;
		
		fileName = "2020_07_08.container.10_241_16_124.log";
		is = TestUtil.getResourceAsStream("apimlogs/"+ fileName, this.getClass());
		startTime = simpleDateFormat.parse("2020-07-08T00:00:00.000+0000");
		input.setStartTime(startTime);
		input.setSecondsBefore(0);
		input.setSecondsAfter(60*60*24);
		input.setErrorsOnly(false);

		response = LogUtil.getLogFileRange(fileName, is, input);
		is.close();
		System.out.println(response);
		assertEquals(11781, response.length());
		
		fileName = "2020_04_06.container.10_101_251_5.log";
		is = TestUtil.getResourceAsStream("apimlogs/"+fileName, this.getClass());
		startTime = simpleDateFormat.parse("2020-04-06T05:01:19.862+0000");
		input.setStartTime(startTime);
		input.setSecondsBefore(60);
		input.setSecondsAfter(180);
		input.setErrorsOnly(false);
		
		response = LogUtil.getLogFileRange(fileName, is, input);
		is.close();
		System.out.println(response);
		assertEquals(1285, response.length());
		
		fileName = "2020_04_06.api_gateway.10_101_251_5.log";
		is = TestUtil.getResourceAsStream("apimlogs/"+ fileName, this.getClass());
		startTime = simpleDateFormat.parse("2020-04-06T17:52:19.862+0000");
		input.setStartTime(startTime);
		input.setSecondsBefore(60);
		input.setSecondsAfter(180);
		input.setErrorsOnly(false);

		response = LogUtil.getLogFileRange(fileName, is, input);
		is.close();
		System.out.println(response);
		assertEquals(507848, response.length());
		
		//		fileName = "2020_04_06.api_gateway.10_101_251_5.log";
//		is = getResourceAsStream("apimlogs/" + fileName, this.getClass());
//		response = LogUtil.getLogFileRange(fileName, is, "[2020-04-06T17:52:12.498+0000] af930cc3-3f55-47b6-930c-c33f5597b639 af930cc3-3f55-47b6-930c-c33f5597b639 (10.101.251.5) 18.206.164.196 33c02da9-dc0e-40b1-8fde-7d1ef6cbfa29 55f50b4c-4f5f-4030-9e29-3ddc37ad3998 GET /ws/rest/v1/healthcheck/ 200 28 8 0\r\n" 
//				, null, startTime, 0, 60);
//		is.close();
//		System.out.println(response);
//		assertEquals(3587275, response.length());
	}
	
	@Test
	public void testGetZip() throws Exception {

		GetClusterLogs.Inputs input = new GetClusterLogs.Inputs();
		SimpleDateFormat simpleDateFormat = new SimpleDateFormat(LogUtil.ACCESS_LOG_TIME_STAMP_MASK);
		Date startTime = simpleDateFormat.parse("2020-04-08T00:00:01.862+0000");
		input.setAtomId("f494aabc-d1ee-4d28-97f9-7a1d7558a0f2");
		input.setStartTime(startTime);
		input.setErrorsOnly(false);
		input.setSecondsBefore(0);
		input.setSecondsAfter(60);
		List<NodeLog> logs = LogUtil.getLogFiles(configuration, user, input);
		assertTrue(logs.size()==2);
		assertEquals(50358, logs.get(0).getEntries().length());
	}

	@Test 
	public void testCompareProps()
	{
		CompareAtomProperties.Inputs input = new CompareAtomProperties.Inputs();
		input.setAtomId1("f494aabc-d1ee-4d28-97f9-7a1d7558a0f2");
		input.setAtomId2("f494aabc-d1ee-4d28-97f9-7a1d7558a0f2");
		List<AtomPropertyCompareItem> results = LogUtil.compareAtomProperties(configuration, user, input);
		assertTrue(results.size()>5);
	}
	
	@Test 
	public void testAnalyzeLogStream() throws Exception
	{
		ProcessLogUtil util = new ProcessLogUtil();
		InputStream is = TestUtil.getResourceAsStream("apimlogs/ProcessLog_6740968493451489588.log", this.getClass());
		List<ProcessLogItem> aggregatedItems = util.analyzeLogStream(is, true);
		assertTrue(aggregatedItems.size()>0);
	}
	
	@Test 
	public void testAnalyzeLogs() throws Exception
	{
		ProcessLogUtil util = new ProcessLogUtil();
		List<ProcessLogItem> items = util.analyzeLog(configuration, user, "execution-e009c44b-cf71-4dff-b74d-4d9048837ed6-2020.04.27", false);
		assertTrue(items.size()>0);
	}
}
