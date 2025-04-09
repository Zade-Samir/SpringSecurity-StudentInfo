package com.telusko.SpringSecEx.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

import com.telusko.SpringSecEx.model.Users;
import com.telusko.SpringSecEx.repo.UserRepo;

@Service
public class UserService {
	
	@Autowired
	private UserRepo repo;
	
	@Autowired
	private JWTService jwtService;
	
	@Autowired
	AuthenticationManager authManager;
	
	//giving the hashing round like 12 times must rounded to password
	private BCryptPasswordEncoder encoder = new BCryptPasswordEncoder(12);

	public Users register(Users user) {
		
		//converting the password into bcrypt hashing
		user.setPassword(encoder.encode(user.getPassword()));
		
		return repo.save(user);
	}

	public String verify(Users user) {
		
		Authentication authentication = 
				authManager.authenticate(new UsernamePasswordAuthenticationToken(
						user.getUsername(), user.getPassword())
						);
		
		if(authentication.isAuthenticated()) {
			return jwtService.generateToken(user.getUsername());
		}
		return "fail";
	}
}
