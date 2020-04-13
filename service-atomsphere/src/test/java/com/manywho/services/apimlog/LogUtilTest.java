package com.manywho.services.apimlog;

import static org.junit.Assert.*;
import org.junit.Test;

import com.manywho.services.atomsphere.ServiceConfiguration;
import com.manywho.services.atomsphere.actions.apimclusterlogs.NodeLog;

import java.io.InputStream;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;


public class LogUtilTest {

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
		int secondsBefore = 60;
		int secondsAfter = 180;
		
		String fileName = "2020_04_06.api_gateway.access.10_101_251_5.log";
		InputStream is = getResourceAsStream("apimlogs/"+fileName, this.getClass());

		String response = LogUtil.getLogFileRange(fileName, is, false, startTime, secondsBefore, secondsAfter);
		is.close();
		System.out.println(response);
		assertEquals(8278, response.length());
	}
	
	@Test
	public void testContainerLog() throws Exception {
		
		SimpleDateFormat simpleDateFormat = new SimpleDateFormat(LogUtil.ACCESS_LOG_TIME_STAMP_MASK);
		Date startTime = simpleDateFormat.parse("2020-04-06T05:52:19.862+0000");
		int secondsBefore = 60;
		int secondsAfter = 180;
		
		String fileName = "2020_04_06.container.10_101_251_5.log";
		
		String response;
		InputStream is;
		
		is = getResourceAsStream("apimlogs/"+fileName, this.getClass());
		
		response = LogUtil.getLogFileRange(fileName, is, false, startTime, secondsBefore, secondsAfter);
		is.close();
		System.out.println(response);
		assertEquals(1807, response.length());
		
		fileName = "2020_04_06.api_gateway.10_101_251_5.log";
		is = getResourceAsStream("apimlogs/"+ fileName, this.getClass());

		startTime = simpleDateFormat.parse("2020-04-06T17:52:19.862+0000");
		response = LogUtil.getLogFileRange(fileName, is, false, startTime, 0, 60);
		is.close();
		System.out.println(response);
		assertEquals(4155388, response.length());

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
		ServiceConfiguration configuration = new ServiceConfiguration();
		configuration.setAccount("presales_demochild-WRHG7X");
		configuration.setUsername("dave.hock@dell.com");
		configuration.setPassword("Gopack!24");
		String atomId = "c418d1f7-d9c7-4886-a69d-88a420406fde";
		SimpleDateFormat simpleDateFormat = new SimpleDateFormat(LogUtil.ACCESS_LOG_TIME_STAMP_MASK);
		Date startTime = simpleDateFormat.parse("2020-04-08T00:00:01.862+0000");
		List<NodeLog> logs = LogUtil.getLogFiles(configuration, atomId, false, startTime, 0, 60);
		assertTrue(logs.size()==2);
		assertEquals(50358, logs.get(0).getEntries().length());
	}
	
    static InputStream getResourceAsStream(String resourcePath, Class theClass) throws Exception
    {
    	InputStream is = null;
		try {
			is = theClass.getClassLoader().getResourceAsStream(resourcePath);			
		} catch (Exception e)
		{
			throw new Exception("Error loading resource: "+resourcePath + " " + e.getMessage());
		}
		if (is==null)
			throw new Exception("Error loading resource: "+resourcePath);
		return is;
    }
}
