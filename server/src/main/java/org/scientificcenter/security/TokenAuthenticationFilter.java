package org.scientificcenter.security;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

@Component
@Slf4j
public class TokenAuthenticationFilter extends OncePerRequestFilter {

    @Value("${frontend.port}")
    private String FRONTEND_PORT;
    @Value("${frontend.address}")
    private String FRONTEND_ADDRESS;
    private static final String HTTP_PREFIX = "http://";
    private final TokenUtils tokenUtils;
    private final UserDetailsService userDetailsService;

    @Autowired
    public TokenAuthenticationFilter(final TokenUtils tokenUtils, @Qualifier("userServiceImpl") final UserDetailsService userDetailsService) {
        this.tokenUtils = tokenUtils;
        this.userDetailsService = userDetailsService;
    }

    @Override
    public void doFilterInternal(final HttpServletRequest request, final HttpServletResponse response, final FilterChain chain) throws IOException, ServletException {
        final String authToken = this.tokenUtils.getToken(request);
//        TokenAuthenticationFilter.log.info("Authentication token: {}", authToken);
        if (authToken != null) {
            final String username = this.tokenUtils.getUsernameFromToken(authToken);

            if (username != null) {
//                TokenAuthenticationFilter.log.info("User to be authenticated: {}", username);
                final UserDetails userDetails = this.userDetailsService.loadUserByUsername(username);

                if (this.tokenUtils.validateToken(authToken, userDetails)) {
//                    TokenAuthenticationFilter.log.info("Token is valid against authenticated user");
                    final TokenBasedAuthentication authentication = new TokenBasedAuthentication(authToken, userDetails);
                    SecurityContextHolder.getContext().setAuthentication(authentication);
                }
            }
        }

//        response.setHeader("Access-Control-Allow-Origin", TokenAuthenticationFilter.HTTP_PREFIX.concat(this.FRONTEND_ADDRESS).concat(":").concat(this.FRONTEND_PORT));
//        response.setHeader("Access-Control-Allow-Methods", "POST, PUT, GET, OPTIONS, DELETE");
//        response.setHeader("Access-Control-Allow-Headers", "x-requested-with");
//        response.setHeader("Access-Control-Max-Age", "3600");
//        response.setHeader("Access-Control-Allow-Credentials", "true");
        chain.doFilter(request, response);
    }
}