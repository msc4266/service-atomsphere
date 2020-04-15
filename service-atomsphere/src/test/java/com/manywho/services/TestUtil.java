package com.manywho.services;

import java.io.IOException;
import java.io.InputStream;
import java.util.Scanner;

public class TestUtil {
    public static InputStream getResourceAsStream(String resourcePath, Class theClass) throws Exception
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
    
    public static String inputStreamToString(InputStream is) throws IOException
    {
    	try (Scanner scanner = new Scanner(is, "UTF-8")) {
    		return scanner.useDelimiter("\\A").next();
    	}
    }
    
    public static String readResource(String resourcePath, Class theClass) throws Exception
	{
		String resource = null;
		try {
			resource = inputStreamToString(getResourceAsStream(resourcePath, theClass));
			
		} catch (Exception e)
		{
			throw new Exception("Error loading resource: "+resourcePath + " " + e.getMessage());
		}
		return resource;
	}
}
