package com.telusko.SpringSecEx.service;

import java.security.Key;
import java.security.NoSuchAlgorithmException;
import java.security.PrivateKey;
import java.security.PublicKey;
import java.util.Base64;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap.KeySetView;
import java.util.function.Function;

import javax.crypto.KeyGenerator;
import javax.crypto.SecretKey;

import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;

import com.telusko.SpringSecEx.model.Users;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;

@Service
public class JWTService {
	
	//creating the token
	public String generateToken(String username) {
		
		Map<String, Object> claims = new HashMap<>();
		
		return Jwts.builder()
					.claims()
					.add(claims) //adding the above map 'claims'
					.subject(username)
					.issuedAt(new Date(System.currentTimeMillis()))
					.expiration(new Date(System.currentTimeMillis() + 30 * 60 * 1000))
					.and()
					.signWith(getKey())
					.compact();
	}

	//token req 'getkey' so creating it to use above
	private SecretKey getKey() {
		byte[] keyBytes = Decoders.BASE64.decode(secretKey);
		return Keys.hmacShaKeyFor(keyBytes);
	}

	//getKey req. 'secretkey' so creating it to use above
	private String secretKey = "";
	
	public JWTService() {
		try {
			KeyGenerator keyGen = KeyGenerator.getInstance("HmacSHA256");
			SecretKey sk = keyGen.generateKey();
			secretKey = Base64.getEncoder().encodeToString(sk.getEncoded());
		} 
		
		catch (NoSuchAlgorithmException e) {
			
			e.printStackTrace();
		}
	}

	//extracting the username
	public String extractUserName(String token) {
		//extract the username from the jwt token
		return extractClaim(token, Claims::getSubject); 
	}

	private <T> T extractClaim(String token, Function<Claims, T> claimResolver) {
		final Claims claims = extractAllClaims(token); //extract all usernames
		return claimResolver.apply(claims);
	}

	private Claims extractAllClaims(String token) {
		return Jwts.parser()
					.verifyWith(getKey())
					.build()
					.parseSignedClaims(token)
					.getPayload();
	}
	
	
	//validate the token
	public boolean validateToken(String token, UserDetails userDetails) {
		final String userName = extractUserName(token); //extract the username
		return (userName.equals(userDetails.getUsername()) && !isTokenExpired(token));
	}

	//check token passed the expired date or not
	private boolean isTokenExpired(String token) {
		return extractExpiration(token).before(new Date());
	}

	//extract expiry date from token
	private Date extractExpiration(String token) {
		return extractClaim(token, Claims::getExpiration);
	}
}










