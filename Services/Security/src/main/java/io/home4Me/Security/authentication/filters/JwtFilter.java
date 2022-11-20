package io.home4Me.Security.authentication.filters;

import java.io.IOException;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.util.StringUtils;
import org.springframework.web.filter.OncePerRequestFilter;

import io.home4Me.Security.authentication.identity.user.LoginDetailsService;
import io.home4Me.Security.authentication.token.TokenType;
import io.home4Me.Security.authentication.token.dto.access.AccessToken;
import io.home4Me.Security.authentication.token.service.JwtUtils;

public class JwtFilter extends OncePerRequestFilter  {

    private static final Logger logger = LogManager.getLogger(JwtFilter.class);
	
    private static final String BEARER = "Bearer ";
    private static final String AUTHORIZATION = "Authorization";
    private static final int HEADER_DATA_NUM = 7;
    
    @Autowired
	private JwtUtils jwtUtils;
    
	@Autowired
    private LoginDetailsService loginDetailsService;
		
	@Override
	protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain)
			throws ServletException, IOException {
		
		try {
            String jwt = parseJwt(request);
            if (jwt != null) {
            	
            	AccessToken wrapToken = jwtUtils.wrapToken(jwt);
                String username = wrapToken.getUsername();
                               
                UserDetails userDetails = loginDetailsService.loadUserByUsername(username);
                UsernamePasswordAuthenticationToken authentication = new UsernamePasswordAuthenticationToken(
                        userDetails, null, userDetails.getAuthorities());
                authentication.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));

                SecurityContextHolder.getContext().setAuthentication(authentication);
            }
        } catch (Exception e) {
            logger.error("Cannot set user authentication: {}", e);
        }

        filterChain.doFilter(request, response);
	}
	
	 private String parseJwt(HttpServletRequest request) {
        String headerAuth = request.getHeader(AUTHORIZATION);

        if (StringUtils.hasText(headerAuth) && headerAuth.startsWith(BEARER)) {
            return headerAuth.substring(HEADER_DATA_NUM, headerAuth.length());
        }

        return null;
    }

}
