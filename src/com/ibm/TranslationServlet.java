package com.ibm;

import java.io.IOException;
import java.io.InputStream;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.Properties;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.servlet.AsyncContext;
import javax.servlet.ServletException;
import javax.servlet.annotation.MultipartConfig;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import javax.servlet.http.Part;

import org.apache.commons.io.FilenameUtils;

import com.ibm.json.java.JSONObject;

/**
 * Servlet implementation class TranslationServlet
 */
@WebServlet(asyncSupported = true, urlPatterns = { "/TranslationServlet" })
@MultipartConfig(fileSizeThreshold=1024*1024*2, // 2MB
				maxFileSize=1024*1024*10,      // 10MB
				maxRequestSize=1024*1024*50)   // 50MB
public class TranslationServlet extends HttpServlet {
	private static final long serialVersionUID = 1L;
	private static Logger logger = Logger.getLogger(TranslationServlet.class.getName());
       
    /**
     * @see HttpServlet#HttpServlet()
     */
    public TranslationServlet() {
        super();
        // TODO Auto-generated constructor stub
    }
    
    // This gets the filename from the uploaded file
    private String getFileName(Part part) {
        for (String header : part.getHeader("content-disposition").split(";")) {
            if (header.trim().startsWith("filename")) {
                return header.substring(
                        header.indexOf('=') + 1).trim().replace("\"", "");
            }
        }
        return null;
    }
    

	/**
	 * @see HttpServlet#doGet(HttpServletRequest request, HttpServletResponse response)
	 */
	protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		HttpSession session = request.getSession(false);
		String download = request.getParameter("download");
		String translate = request.getParameter("translate");
		
    	// Request to download property file
    	if(download != null && download.equals("true")) {
    		Properties properties = (Properties) 
    				session.getAttribute("properties");
    		
    		String fileName = (String) session.getAttribute("propertyFilename");
    		
    		if(properties != null && fileName != null) {
    			response.setContentType("application/octet-stream");
    			response.setHeader("Content-Disposition", "attachment; filename=\"" + fileName + "\"");
    			properties.store(response.getOutputStream(), "Created by IBM Watson Machine Translation");
    			response.getOutputStream().flush();
    			response.getOutputStream().close();
    		}
    	}
		
    	// Request to just generate the translation as server side events
    	else if (translate != null && translate.equals("true")){
    		TranslationRequest translationRequest = (TranslationRequest) 
    				session.getAttribute("translationRequest");
    		
    		if(translationRequest != null) {
    			response.setContentType("text/event-stream");
    			response.setCharacterEncoding("UTF-8");
    			response.setHeader("Cache-Control", "no-cache, no-store, must-revalidate"); 
    			response.setHeader("Pragma", "no-cache");
    			response.setDateHeader("Expires", 0);
    			
    			AsyncContext ac = request.startAsync();
		
    			// The translation job generates server side events as responses
    			try {
    				TranslationJob job = new TranslationJob(ac, session, translationRequest);
    				// Start the translation job
    				Thread thread = new Thread(job);
    				thread.start();
    			}
    			catch(InvalidTranslationRequest e) {
    				logger.log(Level.SEVERE, "Invalid translation request: ", e);
    			}
    		}
    	}
	}

	/**
	 * @see HttpServlet#doPost(HttpServletRequest request, HttpServletResponse response)
	 */
	protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		HttpSession session = request.getSession();
		String sourceLanguage = request.getParameter("source_language");
		String targetLanguage = request.getParameter("target_language");
		Part filePart = request.getPart("file");
		
		PrintWriter pw = response.getWriter();
		
		TranslationRequest translationRequest = new TranslationRequest();
		translationRequest.setSourceLanguage(sourceLanguage);
		translationRequest.setTargetLanguage(targetLanguage);
		
		InputStream filecontent = filePart.getInputStream();
        Properties properties = new Properties();
        properties.load(filecontent);
        
        // Process all the keys in the Java property file
     	ArrayList<Element> elements = new ArrayList<Element>();
        Enumeration<?> keys = properties.propertyNames();    
        
        while (keys.hasMoreElements()) {
        	String key = (String) keys.nextElement();
        	Element element = new Element(sourceLanguage,
        				(String)key, 
        				properties.getProperty((String)key));
        	elements.add(element);
        }
        
        translationRequest.setSourceElements(elements);
        
        // save the translationRequest so that the request gets processed 
        // when the GET call is made
        session.setAttribute("translationRequest", translationRequest);
        
        String fileName = getFileName(filePart);
        String baseName = FilenameUtils.getBaseName(fileName);
        String extension = FilenameUtils.getExtension(fileName);
        
        // save the name of the file that will be used for the properties file
        // Replace the use of "-" with "_" to have a correct property file name
        String name = baseName + "_" + 
        		translationRequest.getTargetLanguage().replace('-', '_') + "." +
        		extension;
        
        session.setAttribute("propertyFilename", name);
        
        // return the session id to the caller in a JSON object
        JSONObject json = new JSONObject();
        json.put("session", session.getId());
        pw.write(json.toString());
        pw.flush();
        pw.close();
	}

}
