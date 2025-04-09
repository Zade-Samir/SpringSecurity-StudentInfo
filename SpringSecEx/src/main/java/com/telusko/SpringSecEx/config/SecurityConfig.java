package com.telusko.SpringSecEx .config;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
//import org.springframework.security.crypto.password.NoOpPasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

@Configuration //state this file is configuration for spring.
@EnableWebSecurity //state don't go for default flow, go for what i have provided below.
public class SecurityConfig {
	
	@Autowired
	private UserDetailsService userDetailsService;	
	
	@Autowired
	private JwtFilter jwtFilter;
	
	
	@Bean //used to change from default to customization of security filter chain
	public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
		
//		http.csrf(customizer -> customizer.disable()); //disable the csrf
//		http.authorizeHttpRequests(request ->request.anyRequest().authenticated()); 
//		//disable the login for localhost, no one enter without authentication login
//		
//		//http.formLogin(Customizer.withDefaults()); //it enables form based authentication.
//		http.httpBasic(Customizer.withDefaults()); //for basic postman like accessors
//		
//		//it will return your login form to again login form because it change session everytime we click
//		http.sessionManagement(session -> session.sessionCreationPolicy(SessionCreationPolicy.STATELESS)); 
//		
//		return http.build(); //return the object for securityFilterChain
		
		//this will authenticate every request by user except 'register' and 'login'
		return http.csrf(customizer -> customizer.disable())
					.authorizeHttpRequests(request ->request
							.requestMatchers("register", "login") //for this two don't asked me to passed the authentication
							.permitAll() //as we are creating the account or login it, so don't req. two times auth.
							.anyRequest().authenticated())
					.httpBasic(Customizer.withDefaults())
					.sessionManagement(session -> session.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
					.addFilterBefore(jwtFilter, UsernamePasswordAuthenticationFilter.class)
					.build();
	}
	
	//connect to database to check userName and Password correct or not
	@Bean
	public AuthenticationProvider authenticationProvider() {
		
		DaoAuthenticationProvider provider = new DaoAuthenticationProvider();
		
		provider.setPasswordEncoder(new BCryptPasswordEncoder(12));
		provider.setUserDetailsService(userDetailsService);
		
		return provider;
	}
	
	//step 1- hold on authentication manager
	@Bean
	public AuthenticationManager authenticationManager(AuthenticationConfiguration config) throws Exception {
		return config.getAuthenticationManager();
	}
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
//--------Don't used it, as it is hardcore the password and userName--------//
	//created to verify username and password from the database
//	@Bean
//	public UserDetailsService userDetailsService() {
//		
//		UserDetails user1 = User.withDefaultPasswordEncoder()
//								.username("harsh")
//								.password("0000")
//								.roles("Tester")
//								.build();
//		
//		UserDetails user2 = User.withDefaultPasswordEncoder()
//								.username("Navin")
//								.password("1111")
//								.roles("Dev")
//								.build();
//		
//		return new InMemoryUserDetailsManager(user1, user2); //returning the userDetailsService object
//	}
//--------Don't used it, as it is hardcore the password and userName--------//

}













