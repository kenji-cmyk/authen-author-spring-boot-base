package kna.springsecurity.security.jwt;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;
import kna.springsecurity.entity.User;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Component;
import java.security.Key;
import javax.crypto.SecretKey;
import java.util.Date;
import java.util.HashMap;
import java.util.List;  
import java.util.Map;
import java.util.function.Function;


@Component
public class JwtService {


    @Value("${jwt.expiration}")
    private long expiration;

    @Value("${jwt.secret}")
    private String secret;

    @Value("${jwt.refresh-expiration}")
    private long refreshTokenExpiration;


    public String generateAccessToken(User user) {
       Map<String, Object> claims = new HashMap<>();
       List<String> roles = user.getRoles().stream()
                                         .map(role -> role.toDatabaseRoleName())
                               .toList();
       claims.put("roles", roles);
       return createToken(claims, user.getUsername());
    }

    public String createToken (Map<String, Object> claims, String username){

        return Jwts.builder()
            .setClaims(claims)
            .setSubject(username)
            .setIssuer("Authen-Author-Base")
            .setIssuedAt(new Date())
            .setExpiration(new Date(System.currentTimeMillis() + expiration))
            .signWith(getSignKey())
            .compact();

    }

    private Key getSignKey(){
        byte[] bytes = Decoders.BASE64.decode(secret);
        return Keys.hmacShaKeyFor(bytes);
    }

    public String extractUsername(String token){
        return extractClaim(token, Claims::getSubject);
    }

    public <T> T extractClaim(String token, Function<Claims, T> claimsResolver) {
        final Claims claims = extractAllClaims(token);
        return claimsResolver.apply(claims);
    } 

    public Date extractExpiration(String token) {
        return extractClaim(token, Claims::getExpiration);
    }

    private Claims extractAllClaims(String token) {
        if (token == null || token.trim().isEmpty()) {
            throw new IllegalArgumentException("Token cannot be null or empty");
        }
        return Jwts.parser()
                .verifyWith((SecretKey) getSignKey())
                .build()
                .parseSignedClaims(token)
                .getPayload();
    }

    private String createRefreshToken (Map<String, Object> claims, String username){
        return Jwts.builder()
            .setClaims(claims)
            .setSubject(username)
            .setIssuer("Authen-Author-Base")
            .setIssuedAt(new Date())
            .setExpiration(new Date(System.currentTimeMillis() + refreshTokenExpiration))
            .signWith(getSignKey())
            .compact();

    }

    public String generateRefreshToken(User user) {
        Map<String, Object> claims = new HashMap<>();
        return createRefreshToken(claims, user.getUsername());
    }

    private Boolean isTokenExpired(String token) {
        return extractExpiration(token).before(new Date());
    }

   public Boolean validateToken(String token, UserDetails userDetails) {
        try {
            final String username = extractUsername(token);
            return (username.equals(userDetails.getUsername()) && !isTokenExpired(token));
        } catch (Exception e) {
            return false;
        }
    }

}