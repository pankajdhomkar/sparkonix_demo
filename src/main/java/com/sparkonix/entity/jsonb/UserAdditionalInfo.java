package com.sparkonix.entity.jsonb;

import java.lang.reflect.Type;

import com.google.gson.Gson;
import com.google.gson.JsonObject;
import com.google.gson.reflect.TypeToken;

public class UserAdditionalInfo {
	private static Gson gson = new Gson();
	private String tempPlaceHolder;

	public UserAdditionalInfo() {
		super();
	}

	public UserAdditionalInfo(String tempPlaceHolder) {
		super();
		this.tempPlaceHolder = tempPlaceHolder;
	}

	public static String toJson(UserAdditionalInfo userAdditionalInfo) {
		if (userAdditionalInfo == null) {
			return "{}";
		}
		return gson.toJson(userAdditionalInfo);
	}

	public static UserAdditionalInfo fromJson(String userMetaData) throws Exception {
		try {
			if (userMetaData == null) {
				return new UserAdditionalInfo();
			}

			JsonObject jsonObject = gson.fromJson(userMetaData, JsonObject.class);

			Type addressType = new TypeToken<String>() {
			}.getType();

			String tempPlaceHolder = gson.fromJson(jsonObject.get("tempPlaceHolder"), addressType);

			return new UserAdditionalInfo(tempPlaceHolder);
		} catch (Exception ex) {
			throw new Exception("Failed to deserialize User Additional Info. Reason: " + ex.getMessage());
		}
	}

	public String getTempPlaceHolder() {
		return tempPlaceHolder;
	}

	public void setTempPlaceHolder(String tempPlaceHolder) {
		this.tempPlaceHolder = tempPlaceHolder;
	}
}
