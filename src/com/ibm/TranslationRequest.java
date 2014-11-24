package com.ibm;
import java.util.ArrayList;


import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

@JsonIgnoreProperties(ignoreUnknown = true)
public class TranslationRequest {
	
	@JsonProperty("data")
	private ArrayList<Element> elements; 
	
	@JsonProperty("source_language")
	private String sourceLanguage;	
	
	@JsonProperty("target_language")
	private String targetLanguage;
	
	@JsonProperty("source_language")
	public void setSourceLanguage(String source) {
		sourceLanguage = source;
	}
	
	@JsonProperty("source_language")
	public String getSourceLanguage() {
		return sourceLanguage;
	}
	
	@JsonProperty("target_language")
	public void setTargetLanguage(String target) {
		targetLanguage = target;
	}
	
	@JsonProperty("target_language")
	public String getTargetLanguage() {
		return targetLanguage;
	}
	
	@JsonProperty("data")
	public void setSourceElements(ArrayList<Element> items) {
		elements = items;
	}
	
	@JsonProperty("data")
	public ArrayList<Element> getSourceElements() {
		return elements;
	}
	
}
