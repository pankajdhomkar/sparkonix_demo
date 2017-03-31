package com.sparkonix.auth;

import java.security.Key;
import java.util.Arrays;
import javax.crypto.spec.SecretKeySpec;
import org.apache.commons.codec.binary.Base64;
import com.google.gson.Gson;
import com.sparkonix.entity.User;
import io.dropwizard.auth.AuthenticationException;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jws;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;

public class JwtToken {
	private static String delimiter = "~!@#";

	private static String keyStr = "4A78E27539AA42C6BAF739454C26B552";
	private static Key key = new SecretKeySpec(Arrays.copyOf(Base64.decodeBase64(keyStr), 16), "AES");
	private static Gson gson = new Gson();

	public static String decryptHeader(String encryptedMessage) throws Exception {
		try {
			Jws<Claims> ans = Jwts.parser().setSigningKey(key).parseClaimsJws(encryptedMessage);
			return gson.toJson(ans.getHeader().get("authorization"));
		} catch (Exception ex) {
			throw new AuthenticationException(ex);
		}
	}

	public static String generateToken(String email, User user) throws Exception {
		try {
			return Jwts.builder().setHeaderParam("authorization", email).setPayload(gson.toJson(user))
					.signWith(SignatureAlgorithm.HS256, key).compact();

		} catch (Exception ex) {
			throw new AuthenticationException(ex);
		}
	}

	public static User decryptPayload(String encryptedMessage) throws Exception {
		try {
			Jws<Claims> ans = Jwts.parser().setSigningKey(key).parseClaimsJws(encryptedMessage);
			String str = gson.toJson(ans.getBody());
			return gson.fromJson(str, User.class);
		} catch (Exception ex) {
			throw new AuthenticationException(ex);
		}
	}

	public static String[] getUserInfo(String decryptedHeader) {
		return decryptedHeader.split(delimiter);
	}

}
