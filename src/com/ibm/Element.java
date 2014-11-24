package com.ibm;



import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonInclude;

@JsonInclude(JsonInclude.Include.NON_NULL)
public class Element {

	@JsonProperty("language")
	private String language;
	
	@JsonCreator
	public Element(@JsonProperty("key") String k, @JsonProperty("value") String v) {
		key = k;
		value = v;
	}
	
	public Element(String lang, String k, String v) {
		language = lang;
		key = k;
		value = v;
	}
	
	@JsonProperty("key")
	private String key;
	
	@JsonProperty("value")
	private String value;
	
	@JsonProperty("key")
	public void setKey(String k) {
		key = k;
	}
	
	@JsonProperty("key")
	public String getKey() {
		return key;
	}

	@JsonProperty("value")
	public void setValue(String v) {
		value = v;
	}
	
	@JsonProperty("value")
	public String getValue() {
		return value;
	}
	
	@JsonProperty("language")
	public String getLanguage() {
		return language;
	}
	
	@JsonProperty("language")
	protected void setLanguage(String lang) {
		language = lang;
	}
	
	public String toString() {
		return("Language: " + language + " key: " + key + " value: " + value);
	}
}
