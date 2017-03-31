package com.sparkonix.entity.jsonb;

import java.lang.reflect.Type;

import com.google.gson.Gson;
import com.google.gson.JsonObject;
import com.google.gson.reflect.TypeToken;

public class CompanyDetailMetadata {
	private static Gson gson = new Gson();
	private String address;

	public CompanyDetailMetadata() {
		super();
	}

	public CompanyDetailMetadata(String address) {
		super();
		this.address = address;
	}

	public static String toJson(CompanyDetailMetadata userMetadata) {
		if (userMetadata == null) {
			return "{}";
		}
		return gson.toJson(userMetadata);
	}

	public static CompanyDetailMetadata fromJson(String userMetaData) throws Exception {
		try {
			if (userMetaData == null) {
				return new CompanyDetailMetadata();
			}

			JsonObject jsonObject = gson.fromJson(userMetaData, JsonObject.class);

			Type addressType = new TypeToken<String>() {
			}.getType();

			String address = gson.fromJson(jsonObject.get("address"), addressType);

			return new CompanyDetailMetadata(address);
		} catch (Exception ex) {
			throw new Exception("Failed to deserialize metadata. Reason: " + ex.getMessage());
		}
	}

	public String getAddress() {
		return address;
	}

	public void setAddress(String address) {
		this.address = address;
	}

}
