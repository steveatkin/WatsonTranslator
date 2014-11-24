package com.ibm;

public class MachineTranslationException extends Exception {
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private String language;
	
	public MachineTranslationException() {
		// TODO Auto-generated constructor stub
	}
	
	public String getLanguage() {
		return language;
	}

	public MachineTranslationException(String lang, String message) {
		super(message);
		language = lang;
		// TODO Auto-generated constructor stub
	}

	public MachineTranslationException(Throwable cause) {
		super(cause);
		// TODO Auto-generated constructor stub
	}

	public MachineTranslationException(String message, Throwable cause) {
		super(message, cause);
		// TODO Auto-generated constructor stub
	}

	public MachineTranslationException(String message, Throwable cause,
			boolean enableSuppression, boolean writableStackTrace) {
		super(message, cause, enableSuppression, writableStackTrace);
		// TODO Auto-generated constructor stub
	}

}
