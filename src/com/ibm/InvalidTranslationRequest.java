package com.ibm;

public class InvalidTranslationRequest extends Exception {
	private static final long serialVersionUID = 1L;
	private String language;
	
	public InvalidTranslationRequest(String failedLang) {
		language = failedLang;
	}
	
	public String getFailedLanguage() {
		return language;
	}
	
	public void setFailedLanguage(String lang) {
		language= lang;
	}

}
