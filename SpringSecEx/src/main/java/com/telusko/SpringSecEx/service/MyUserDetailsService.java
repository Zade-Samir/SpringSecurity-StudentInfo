package com.telusko.SpringSecEx.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import com.telusko.SpringSecEx.model.UserPrincipal;
import com.telusko.SpringSecEx.model.Users;
import com.telusko.SpringSecEx.repo.UserRepo;

@Service
public class MyUserDetailsService implements UserDetailsService {
	
	@Autowired
	private UserRepo repo;

	//loadUserByUsername means loading user by it's name, but from the database
	//so we have to connect the service layer to repo layer
	@Override
	public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
		
		Users user = repo.findByUsername(username);
		
		if (user == null) {
			System.out.println("User Not Found");
			throw new UsernameNotFoundException("User Not Found");
		}
		
		return new UserPrincipal(user);
	}

	
}














