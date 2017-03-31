package com.sparkonix.utils;

import java.util.HashMap;

import org.apache.commons.codec.binary.Base64;

public class Utils {
	private static HashMap<String, String> mimeTypesMap = new HashMap<>();

	static {
		mimeTypesMap.put(".jpg", "image/jpeg");
		mimeTypesMap.put(".doc", "application/msword");
		mimeTypesMap.put(".docx", "application/msword");
		mimeTypesMap.put(".pdf", "application/pdf");
		mimeTypesMap.put(".zip", "application/zip");
		mimeTypesMap.put(".jpeg", "image/jpeg");
		mimeTypesMap.put(".gif", "image/gif");
		mimeTypesMap.put(".png", "image/png");
		mimeTypesMap.put(".txt", "text/plain");
		mimeTypesMap.put(".jpe", "image/jpeg");
		mimeTypesMap.put(".ppt", "application/vnd.ms-powerpoint");
		mimeTypesMap.put(".xls", "application/excel");
		mimeTypesMap.put(".xlsx", "application/excel");
	}

	public static String encodeBase64(String str) {
		byte[] encodeStr = Base64.encodeBase64(str.getBytes());
		return new String(encodeStr);
	}

	public static String decodeBase64(String str) {
		byte[] decodeStr = Base64.decodeBase64(str);
		return new String(decodeStr);
	}

	public static String getMimeTypeFromFileName(String fName) {
		return mimeTypesMap.get(fName);
	}
}
