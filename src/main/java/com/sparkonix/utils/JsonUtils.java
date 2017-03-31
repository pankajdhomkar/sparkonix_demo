package com.sparkonix.utils;

import java.util.logging.Logger;
import com.fasterxml.jackson.annotation.JsonAutoDetect.Visibility;
import com.fasterxml.jackson.annotation.JsonInclude.Include;
import com.fasterxml.jackson.annotation.PropertyAccessor;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.datatype.hibernate5.Hibernate5Module;
import com.google.gson.Gson;
import com.google.gson.JsonObject;

public class JsonUtils {

	public static Logger log = Logger.getLogger(JsonUtils.class.getName());
	
	public static String getErrorJson(String message) {
		JsonObject jsonObject = new JsonObject();
		jsonObject.addProperty("error", message);
		return new Gson().toJson(jsonObject);
	}

	public static String getSuccessJson(String message) {
		JsonObject jsonObject = new JsonObject();
		jsonObject.addProperty("success", message);
		return new Gson().toJson(jsonObject);
	}
	
	public static String getWarningJson(String message) {
		JsonObject jsonObject = new JsonObject();
		jsonObject.addProperty("warning", message);
		return new Gson().toJson(jsonObject);
	}
	
	public static String getJson(Object object) {
		ObjectMapper objectMapper = new ObjectMapper();
		objectMapper.registerModule(new Hibernate5Module()).setSerializationInclusion(Include.NON_NULL);
		objectMapper.configure(SerializationFeature.WRITE_EMPTY_JSON_ARRAYS,
				false);
		objectMapper.setVisibility(PropertyAccessor.ALL, Visibility.ANY);
		String json = null;
		try {
			json = objectMapper.writeValueAsString(object);
		} catch (Exception e) {
			log.severe(e.getMessage());
		}
		return json;
	}
}
