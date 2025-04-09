package com.telusko.SpringSecEx.config;

import java.awt.image.renderable.ContextualRenderedImageFactory;
import java.io.IOException;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import com.telusko.SpringSecEx.service.JWTService;
import com.telusko.SpringSecEx.service.MyUserDetailsService;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

@Component
public class JwtFilter extends OncePerRequestFilter {
	
	@Autowired
	private JWTService jwtService;
	
	@Autowired
	private ApplicationContext context;

	@Override
	protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain)
			throws ServletException, IOException {
		
		//retrieves the value of the Authorization header from the request
		//like 'Bearer eyJhbGciOiJIUzI1...' from ---> 'Authorization: Bearer eyJhbGciOiJIUzI1...'
		String authHeader = request.getHeader("Authorization");
		String token = null;
		String username = null;
		
		//token always start with 'Bearer eyJhbGciOiJIUzI1NiJ9.eyJzdWIiOiJydWRyYSIsImlhdCI6MTc0'
		//so remove 'Bearer ' and then used remain as JWT
		if(authHeader != null && authHeader.startsWith("Bearer ")) {
			token = authHeader.substring(7);
			
			username = jwtService.extractUserName(token); // extract username
		}
		
		if (username != null && SecurityContextHolder //it is checking token is already authenticate or not
								.getContext() //if not then go else exit as already authenticate so why do again
								.getAuthentication() == null) {
			
			//provide the userdetails based on username
			UserDetails userDetails = context.getBean(MyUserDetailsService.class).loadUserByUsername(username);
			
			if (jwtService.validateToken(token, userDetails)) {
				//check the second layer --> UsernamePasswordAuthentication
				UsernamePasswordAuthenticationToken authToken = 
								new UsernamePasswordAuthenticationToken(
														userDetails,  //object principal
														null, //object credentials
														userDetails.getAuthorities()); //object authorities
				
				//this object should also know about request object, as it knows about the 'user' above code line
				//request object has lot's of information about data, hence authToken must know about it.
				authToken.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));
				
				//this adding the token into the  Spring Security’s filter chain, as it is authenticate above.
				SecurityContextHolder.getContext().setAuthentication(authToken);
			}
		}
		
		//go for next filter else process will stop here
		filterChain.doFilter(request, response);
	}

}











