package com.sparkonix.utils;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

import org.apache.log4j.Logger;

public class MD5 {

	private static final Logger log = Logger.getLogger(MD5.class);

	public String generateMD5(String password) {
		try {
			MessageDigest md = MessageDigest.getInstance("MD5");
			md.update(password.getBytes());

			byte byteData[] = md.digest();

			// convert the byte to hex format
			StringBuffer sb = new StringBuffer();
			for (int i = 0; i < byteData.length; i++) {
				sb.append(Integer.toString((byteData[i] & 0xff) + 0x100, 16).substring(1));
			}
			log.info("generated MD5 hash");
			return sb.toString();
		} catch (NoSuchAlgorithmException e) {
			log.error(e.getMessage());
			return null;
		}

	}
}
