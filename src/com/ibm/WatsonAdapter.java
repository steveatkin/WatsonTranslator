package com.ibm;

import java.net.URI;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.apache.commons.codec.binary.Base64;
import org.apache.http.NameValuePair;
import org.apache.http.client.fluent.Executor;
import org.apache.http.client.fluent.Request;
import org.apache.http.client.utils.URLEncodedUtils;
import org.apache.http.entity.ContentType;
import org.apache.http.message.BasicNameValuePair;

import com.ibm.json.java.JSONArray;
import com.ibm.json.java.JSONObject;

public class WatsonAdapter {
	private static Logger logger = Logger.getLogger(WatsonAdapter.class.getName());
	private static String translationService = "machine_translation";

	private static String baseURLTranslation = "put the service url here";
	private static String usernameTranslation = "put your username here";
	private static String passwordTranslation = "put your password here";

	static {
		JSONObject sysEnv = getVcapServices();
        if (sysEnv != null) {
        	//logger.info("Looking for: "+ translationService);

        	if (sysEnv.containsKey(translationService)) {
        		JSONArray services = (JSONArray)sysEnv.get(translationService);
				JSONObject service = (JSONObject)services.get(0);
				JSONObject credentials = (JSONObject)service.get("credentials");
				baseURLTranslation = (String)credentials.get("url");
				usernameTranslation = (String)credentials.get("username");
				passwordTranslation = (String)credentials.get("password");
				logger.info("baseURL  = "+baseURLTranslation);
				logger.info("username   = "+usernameTranslation);
				logger.info("password = "+passwordTranslation);
    		}
        	else {
        		logger.warning(translationService + " is not available in VCAP_SERVICES, "
        		+ "please bind the service to your application");
        	}
        }
	}

	private static JSONObject getVcapServices() {
        String envServices = System.getenv("VCAP_SERVICES");
        JSONObject sysEnv = null;

        if (envServices == null) {
        	return null;
        }

        try {
        	sysEnv = JSONObject.parse(envServices);
        }
        catch(Exception e) {
        	logger.log(Level.SEVERE, "Error parsing VCAP_SERVICES: "+e.getMessage(), e);
        }

        return sysEnv;
    }

	public WatsonAdapter() {

	}

	public String toString() {
		return "IBM Watson Machine Translation";
	}

	private String translate(String text, String sid) throws MachineTranslationException{
		String translation = "";
		List<NameValuePair> qparams = new ArrayList<NameValuePair>();
		qparams.add(new BasicNameValuePair("txt",text ));
		qparams.add(new BasicNameValuePair("sid",sid ));
		qparams.add(new BasicNameValuePair("rt","text" ));

		try {
			Executor executor = Executor.newInstance();
    		URI serviceURI = new URI(baseURLTranslation).normalize();
    	    String auth = usernameTranslation + ":" + passwordTranslation;

    	    byte[] response = executor.execute(Request.Post(serviceURI)
			    .addHeader("Authorization", "Basic "+ Base64.encodeBase64String(auth.getBytes()))
			    .bodyString(URLEncodedUtils.format(qparams, "utf-8"),
			    		ContentType.APPLICATION_FORM_URLENCODED)
			    ).returnContent().asBytes();

    	    translation = new String(response, "UTF-8");
		}
		catch(Exception e) {
			logger.log(Level.SEVERE, "Watson error: "+e.getMessage(), e);
			throw new MachineTranslationException(sid, "Watson does not support this source language");
		}

		return translation;
	}

	public String translateString(String sourceLang, String targetLang, String source) throws MachineTranslationException {
		String sid = "mt-";

		// Only English source is supported
		if(sourceLang.equals("en") || sourceLang.equals("en-US")) {
			sid = sid + "enus-";
		}
		else {
			throw new MachineTranslationException(targetLang, "Watson does not support this source language");
		}

		if(targetLang.equals("fr")) {
			sid = sid + "frfr";
		}
		else if(targetLang.equals("es")){
			sid = sid + "eses";
		}
		else if(targetLang.equals("pt-BR")) {
			sid = sid + "ptbr";
		}
		else {
			throw new MachineTranslationException(targetLang, "Watson does not support this target language");
		}

		return translate(source, sid);
	}
}
