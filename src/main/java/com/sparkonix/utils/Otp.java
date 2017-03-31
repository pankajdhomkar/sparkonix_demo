package com.sparkonix.utils;

import java.security.SecureRandom;

public class Otp {

	private static final Integer OTP_LENGTH = 6;

	public static String generate() {
		String pool = "0123456789ABCDEFGHIJKLMNOPQRSTUVWXYZ";
		SecureRandom rnd = new SecureRandom();

		StringBuilder sb = new StringBuilder(OTP_LENGTH);
		for (int i = 0; i < OTP_LENGTH; i++) {
			sb.append(pool.charAt(rnd.nextInt(pool.length())));
		}
		return sb.toString();
	}
}
