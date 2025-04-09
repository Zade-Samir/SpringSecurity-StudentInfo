package com.telusko.SpringOauth2Demo;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.web.SecurityFilterChain;

@Configuration
@EnableWebSecurity
public class SecurityConfig {
	
	@Bean
	public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
		
		//every method is authenticated, so restriction for access any resource after login also
		http.authorizeHttpRequests(auth -> auth.anyRequest().authenticated())
			.oauth2Login(Customizer.withDefaults());
		
		return http.build();
	}
}
