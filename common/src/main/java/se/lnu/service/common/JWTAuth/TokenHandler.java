package se.lnu.service.common.JWTAuth;

import java.io.UnsupportedEncodingException;

import se.lnu.service.common.message.User;

import com.auth0.jwt.JWT;
import com.auth0.jwt.JWTVerifier;
import com.auth0.jwt.algorithms.Algorithm;
import com.auth0.jwt.exceptions.JWTCreationException;
import com.auth0.jwt.exceptions.JWTVerificationException;
import com.auth0.jwt.interfaces.DecodedJWT;

public class TokenHandler {
	private static String secret = "qwertyasdfgh1234567890";
	
	public static String generateToken(User user) {
		try {
		    Algorithm algorithm = Algorithm.HMAC256(secret);
		    String token = JWT.create().withClaim("user", user.getEmail()).withIssuer("auth0").sign(algorithm);
		    return token;
		} catch (UnsupportedEncodingException exception) {
			return null;
		} catch (JWTCreationException exception){
			return null;
		}
	}
	
	public static String validateTokenAndGetEmail(String token) {
		try {
		    Algorithm algorithm = Algorithm.HMAC256(secret);
		    JWTVerifier verifier = JWT.require(algorithm).withIssuer("auth0").acceptExpiresAt(86400).build();
		    DecodedJWT jwt = verifier.verify(token);
		    
		    String email = jwt.getClaim("user").asString();
		    return email;
		} catch (UnsupportedEncodingException exception) {
			return null;
		} catch (JWTVerificationException exception) {
			return null;
		} catch (Exception exception) {
			return null;
		}
	}
}
