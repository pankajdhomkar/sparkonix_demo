package com.sparkonix.entity.jsonb;

import java.lang.reflect.Type;

import com.google.gson.Gson;
import com.google.gson.JsonObject;
import com.google.gson.reflect.TypeToken;

public class CompanyLocationAddress {
	private static Gson gson = new Gson();
	private String locationName;
	private String locationAddress;

	public CompanyLocationAddress() {
		super();
	}

	public CompanyLocationAddress(String locationName, String locationAddress) {
		super();
		this.locationName = locationName;
		this.locationAddress = locationAddress;
	}

	public static String toJson(CompanyLocationAddress companyLocationAddress) {
		if (companyLocationAddress == null) {
			return "{}";
		}
		return gson.toJson(companyLocationAddress);
	}

	public static CompanyLocationAddress fromJson(String address) throws Exception {
		try {
			if (address == null) {
				return new CompanyLocationAddress();
			}

			JsonObject jsonObject = gson.fromJson(address, JsonObject.class);

			Type locationNameType = new TypeToken<String>() {
			}.getType();
			Type locationAddressType = new TypeToken<String>() {
			}.getType();

			String locationName = gson.fromJson(jsonObject.get("locationName"), locationNameType);
			String city = gson.fromJson(jsonObject.get("locationAddress"), locationAddressType);

			return new CompanyLocationAddress(locationName, city);
		} catch (Exception ex) {
			throw new Exception("Failed to deserialize User Additional Info. Reason: " + ex.getMessage());
		}
	}

	public String getLocationName() {
		return locationName;
	}

	public void setLocationName(String locationName) {
		this.locationName = locationName;
	}

	public String getLocationAddress() {
		return locationAddress;
	}

	public void setLocationAddress(String locationAddress) {
		this.locationAddress = locationAddress;
	}

}
