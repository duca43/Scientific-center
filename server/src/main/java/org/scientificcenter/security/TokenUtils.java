package org.scientificcenter.security;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Component;

import javax.servlet.http.HttpServletRequest;
import java.util.Date;

@Component
public class TokenUtils {

    @Value("${spring.application.name}")
    private String APP_NAME;

    @Value("${scientific-center.secret}")
    private String SECRET;

    @Value("${scientific-center.expires-in}")
    private int TOKEN_EXPIRES_IN;

    @Value("${scientific-center.auth-header}")
    private String AUTH_HEADER;

    @Value("${scientific-center.auth-header-prefix}")
    private String AUTH_HEADER_PREFIX;

    private static final SignatureAlgorithm SIGNATURE_ALGORITHM = SignatureAlgorithm.HS512;

    public String generateToken(final String username) {
        return Jwts.builder()
                .setIssuer(this.APP_NAME)
                .setSubject(username)
                .setIssuedAt(new Date())
                .setExpiration(this.generateExpirationDate(this.TOKEN_EXPIRES_IN))
                .signWith(TokenUtils.SIGNATURE_ALGORITHM, this.SECRET).compact();
    }

    private Date generateExpirationDate(final int expiresIn) {
        return new Date(new Date().getTime() + expiresIn * 1000);
    }

    public String refreshToken(final String token) {
        String refreshedToken;
        try {
            final Claims claims = this.getAllClaimsFromToken(token);
            claims.setIssuedAt(new Date());
            refreshedToken = Jwts.builder()
                    .setClaims(claims)
                    .setExpiration(this.generateExpirationDate(this.TOKEN_EXPIRES_IN))
                    .signWith(TokenUtils.SIGNATURE_ALGORITHM, this.SECRET).compact();
        } catch (final Exception e) {
            refreshedToken = null;
        }
        return refreshedToken;
    }

    public Boolean canTokenBeRefreshed(final String token) {
        final Date expiration = this.getExpirationDateFromToken(token);
        return !expiration.before(new Date());
    }

    public Boolean validateToken(final String token, final UserDetails userDetails) {
        final String username = this.getUsernameFromToken(token);
        final Date expirationDate = this.getExpirationDateFromToken(token);
        return (username != null && username.equals(userDetails.getUsername()) && expirationDate.after(new Date()));
    }

    private Claims getAllClaimsFromToken(final String token) {
        Claims claims;
        try {
            claims = Jwts.parser()
                    .setSigningKey(this.SECRET)
                    .parseClaimsJws(token)
                    .getBody();
        } catch (final Exception e) {
            claims = null;
        }
        return claims;
    }

    public String getUsernameFromToken(final String token) {
        String username;
        try {
            final Claims claims = this.getAllClaimsFromToken(token);
            username = claims.getSubject();
        } catch (final Exception e) {
            username = null;
        }
        return username;
    }

    public Date getExpirationDateFromToken(final String token) {
        Date expiration;
        try {
            final Claims claims = this.getAllClaimsFromToken(token);
            expiration = claims.getExpiration();
        } catch (final Exception e) {
            expiration = null;
        }
        return expiration;
    }

    public String getToken(final HttpServletRequest request) {
        final String authHeader = request.getHeader(this.AUTH_HEADER);

        if (authHeader != null && authHeader.startsWith(this.AUTH_HEADER_PREFIX.concat(" "))) {
            return authHeader.substring(this.AUTH_HEADER_PREFIX.length() + 1);
        }

        return null;
    }
}