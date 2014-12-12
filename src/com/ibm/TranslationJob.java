package com.ibm;


import java.io.IOException;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.Locale;
import java.util.Properties;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.servlet.AsyncContext;
import javax.servlet.http.HttpSession;

import com.ibm.json.java.JSONObject;


public class TranslationJob implements Runnable {
	private static Logger logger = Logger.getLogger(TranslationJob.class.getName());
	private AsyncContext ac;
	private TranslationRequest request;
	private HttpSession session;
	private String fileType = null;
	
	public TranslationJob(AsyncContext context, HttpSession client, TranslationRequest translationRequest, String format) throws InvalidTranslationRequest {
		ac = context;
		request = translationRequest;
		session = client;	
		fileType = format;
	}
	
	public void startJob() {
		PrintWriter writer = null;
		
		try {
			Object resources = null;
			
			if(fileType.equalsIgnoreCase("properties")) {
				// We create a property file while we process the strings so that 
				// the file can be downloaded later
				resources = new Properties();
			}
			else if(fileType.equalsIgnoreCase("json")) {
				// We create a JSON file while we process the strings so that 
				// the file can be downloaded later
				resources = new JSONObject();
			}
			
			writer = ac.getResponse().getWriter();
			String targetLang = request.getTargetLanguage();
			
			WatsonAdapter engine = new WatsonAdapter();
			
			// see if there are source elements in the request
			ArrayList<Element> elements = request.getSourceElements();
			
			JSONObject json = new JSONObject();
			int id = 0;
			
			for (Element element : elements) {
				String translatedString = engine.translateString(element.getLanguage(), 
						targetLang, 
						element.getValue());
				
				String displayLang = Locale.forLanguageTag(targetLang).
						getDisplayLanguage(ac.getRequest().getLocale());
				
				json.put("language", displayLang);
				json.put("key", element.getKey());
				json.put("value", translatedString);
				
				writer.write("retry: 2000\n");
				writer.write("id: " + id + "\n");
				writer.write("data: " + json.toString() + "\n\n");
    			writer.flush();
    			
    			if(fileType.equalsIgnoreCase("properties")) {
    				// Add the translated string to the property file
    				((Properties) resources).setProperty(element.getKey(), translatedString);
    			}
    			else if(fileType.equalsIgnoreCase("json")) {
    				((JSONObject) resources).put(element.getKey(), translatedString);
    			}
    			++id;
			}
			
			// Finished sending all the data
			writer.write("id: " + id + "\n");
			writer.write("event: finished\n");
			writer.write("data: \n\n");
			writer.flush();
        	writer.close();
        	
        	// save the properties in the session for future download
        	session.setAttribute("resources", resources);
        	ac.complete();
		}
		
		catch (IOException e) {
			logger.log(Level.SEVERE, "Could not write response: ", e);
	    }
		
		catch(MachineTranslationException c) {
			logger.log(Level.SEVERE, "Machine translation failed: ", c);
		}
		
	}
		
	
	@Override
	public void run() {
		startJob();
	}
	
}
