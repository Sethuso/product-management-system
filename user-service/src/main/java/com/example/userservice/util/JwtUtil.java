package com.example.userservice.util;



import com.example.userservice.model.User;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.JwtException;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;
import jakarta.servlet.http.HttpSession;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Component;

import javax.crypto.SecretKey;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.function.Function;

@Component
public class JwtUtil {


    @Value("${jwt.secret}")
    String secretKey;
    public  final long TOKEN_VALIDITY = 12*60*60*1000L;
    public final long  REFRESH_VALIDITY= 13*60*60*1000L;

    public  final String  EMAIL= "email";
    public  final String  USERNAME= "userName";
    public  final String  ROLE= "role";
    public  final String  GET_ID= "getUserId";

    public  String generationKey(Map<String,Object> claims , String email){
        try {
            return Jwts.builder()
                    .setClaims(claims)
                    .setSubject(email)
                    .setIssuedAt(new Date(System.currentTimeMillis()))
                    .setExpiration(new Date(System.currentTimeMillis()+TOKEN_VALIDITY))
                    .signWith(getKey(),SignatureAlgorithm.HS256).compact();
        }catch (Exception e){
            throw new RuntimeException(e.getMessage());
        }
    }

    private  SecretKey getKey() {
        byte[] keyBytes = Decoders.BASE64.decode(secretKey);
        return Keys.hmacShaKeyFor(keyBytes);
    }

    public String generateToken(User user , HttpSession httpSession){
        Map<String,Object> claims =new HashMap<>();
        claims.put(EMAIL,user.getEmail());
        claims.put(USERNAME,user.getUserName());
        claims.put(ROLE,user.getRole().getName());
        httpSession.setAttribute(GET_ID,user.getUserId());
        try {
            return generationKey(claims,user.getEmail());
        }catch (Exception e){
            throw  new JwtException(e.getMessage());
        }
    }

    public  String extractUserName(String token){
        return extractClaim(token,Claims::getSubject);
    }

    public String extractRole(String token){
        return extractClaim(token,claims -> claims.get("role",String.class));
    }

    private <T> T extractClaim(String token, Function<Claims,T> claimsResolver){
        final Claims claims = extractAllClaims(token);
        return claimsResolver.apply(claims);
    }

    private Claims extractAllClaims(String token) {
        return Jwts.parserBuilder()
                .setSigningKey(getKey())
                .build()
                .parseClaimsJws(token)
                .getBody();
    }

    public boolean validateToken(String token, UserDetails userDetails){
        final String userName = extractUserName(token);
        return (userName.equals(userDetails.getUsername())&& !isTokenExpired(token));
    }

    private boolean isTokenExpired(String token) {
        return extractExpiration(token).before(new Date());
    }

    private Date extractExpiration(String token) {
        return  extractClaim(token,Claims::getExpiration);
    }

    public void validateToken(final String token) {
        try {
            Jwts.parserBuilder()
                    .setSigningKey(getKey())
                    .build()
                    .parseClaimsJws(token);
        } catch (JwtException e) {
            throw new RuntimeException("Invalid JWT Token: " + e.getMessage());
        }
    }


    public String refreshToken(String token) {
        try {
            Claims claims = extractAllClaims(token);

            System.out.println(claims+"ccccccccccccccccccccccccccccc");

            if (isTokenExpired(token)) {
                throw new JwtException("Token is expired. Cannot refresh.");
            }

            return Jwts.builder()
                    .setClaims(claims)
                    .setSubject(claims.getSubject())
                    .setIssuedAt(new Date(System.currentTimeMillis()))
                    .setExpiration(new Date(System.currentTimeMillis() + REFRESH_VALIDITY))
                    .signWith(getKey(), SignatureAlgorithm.HS256)
                    .compact();
        } catch (JwtException e) {
            throw new RuntimeException("Could not refresh token: " + e.getMessage());
        }
    }


}


