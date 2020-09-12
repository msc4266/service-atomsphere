package com.manywho.services.apimlog;

import java.io.InputStream;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Scanner;
import java.util.regex.Pattern;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;

import javax.inject.Inject;

import org.json.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.manywho.sdk.api.security.AuthenticatedWho;
import com.manywho.services.atomsphere.ServiceConfiguration;
import com.manywho.services.atomsphere.actions.utility_processloganalysis.ProcessLogItem;
import com.manywho.services.atomsphere.actions.downloadAtomLog.DownloadAtomLog;
import com.manywho.services.atomsphere.database.AtomsphereAPI;
import com.manywho.services.atomsphere.database.Database;


public class ProcessLogUtil {
    
    private static final Logger LOGGER = LoggerFactory.getLogger(ProcessLogUtil.class);
	public List<ProcessLogItem> analyzeLogStream(InputStream is, Boolean aggregate) throws Exception
	{
//2020-06-12T17:48:25Z	INFO	Records Found		Executing Decision with 1 document(s).
//2020-06-12T17:48:25Z	INFO	Records Found		Shape executed successfully in 1 ms.

		List<ProcessLogItem> items = new ArrayList<ProcessLogItem>();
		Map<String, ProcessLogItem> aggregation = new HashMap<String, ProcessLogItem>();
		String targetTime = "Shape executed successfully in ";
		String targetName = "	INFO	";
		String targetDocs1 = "		Executing ";
		String targetDocs2 = " with ";
		String targetDocs3 = " document(s).";
		
//TODO capture document counts and execution counts		
	    try {
	        Scanner myReader = new Scanner(is);
	        while (myReader.hasNextLine()) {
	          String data = myReader.nextLine();
        	  Long documentCount=0L;
        	  Long duration = 0L;
        	  
	          int t1 = data.indexOf(targetDocs1);
	          int t2 = data.indexOf(targetDocs2);
	          int t3 = data.indexOf(targetDocs3);
        	  if (aggregate && t1>0 && t2>t1 && t3>t2)
        	  {
        		  String docString=data.substring(data.indexOf(targetDocs2)+targetDocs2.length(), data.indexOf(targetDocs3));
        		  documentCount = Long.parseLong(docString);
	        	  String componentName=data.substring(data.indexOf(targetName)+targetName.length(), data.indexOf(targetDocs1)).replaceAll("\t"," ");
        		  ProcessLogItem processLogItem=null;
        		  if (aggregation.containsKey(componentName))
        		  {
        			  processLogItem = aggregation.get(componentName);
        			  processLogItem.setDocumentCount(documentCount+processLogItem.getDocumentCount());
        		  } else {
        			  processLogItem = new ProcessLogItem(componentName, duration, documentCount, 0L);
        			  items.add(processLogItem);
	        		  aggregation.put(componentName, processLogItem);
        		  }
        	  }
	          else if (data.contains(targetTime))
	          {
	        	  String durationString=data.substring(data.indexOf(targetTime)+targetTime.length(), data.indexOf(" ms"));
	        	  duration = Long.parseLong(durationString);
	        	  String componentName=data.substring(data.indexOf(targetName)+targetName.length(), data.indexOf(targetTime)).replaceAll("\t"," ");
	        	  if (aggregate)
	        	  {
	        		  ProcessLogItem processLogItem=null;
	        		  if (aggregation.containsKey(componentName))
	        		  {
	        			  processLogItem = aggregation.get(componentName);
	        			  processLogItem.setExecutionDuration(duration+processLogItem.getExecutionDuration());
	        			  processLogItem.setExecutionCount(processLogItem.getExecutionCount()+1);
	        		  } else {
	        			  processLogItem = new ProcessLogItem(componentName, duration, documentCount, 1L);
	        			  items.add(processLogItem);
		        		  aggregation.put(componentName, processLogItem);
	        		  }
	        	  }
	        	  else
	        		  items.add(new ProcessLogItem(componentName, duration, documentCount, 1L));
	        	  System.out.println(componentName + " " + duration);    	  
	          }
	        }
	        myReader.close();
	      } catch (Exception e) {
	        System.out.println("An error occurred.");
	        e.printStackTrace();
	      }

	    Collections.sort(items, new ProcessLogItemComparator());
	    return items;
	}
	
	class ProcessLogItemComparator implements Comparator<ProcessLogItem> {
		@Override
		public int compare(ProcessLogItem n1, ProcessLogItem n2) {
//			if (!n1.getPropertyName()().contentEquals(n2.getPropertyName())) {
//				return n1.getLogType().compareTo(n2.getLogType());
//			}
			//minus is decending
			return -n1.getExecutionDuration().compareTo(n2.getExecutionDuration());
		}
	}
	
	public List<ProcessLogItem> analyzeLog(ServiceConfiguration configuration, AuthenticatedWho user, String executionId, Boolean aggregate) throws Exception
	{
		JSONObject body = new JSONObject();
		List<ProcessLogItem> items=null;
		body.put("executionId", executionId);
		body.put("logLevel", "INFO");
		JSONObject response = AtomsphereAPI.executeAPI(configuration, user.getToken(), "ProcessLog", "POST", null, body.toString(), false);
		DownloadAtomLog.Outputs outputs = new DownloadAtomLog.Outputs(response);
		InputStream is = LogUtil.executeGetLogs(configuration, user.getToken(), outputs.getUrl());
        ZipInputStream zis = new ZipInputStream(is);
        ZipEntry entry = zis.getNextEntry();

        while(entry != null) {

            if (!entry.isDirectory()) {
                // if the entry is a file, extracts it
            	LOGGER.info(entry.getName());
            	items = this.analyzeLogStream(zis, aggregate);
            	break;
            } else {
               // System.out.println("===Directory===");
            }
            zis.closeEntry();
            entry = zis.getNextEntry();
        }
		return items;
	}
}
