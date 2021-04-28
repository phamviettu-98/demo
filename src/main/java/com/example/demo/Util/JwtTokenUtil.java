package com.example.demo.Util;


import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Component;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.function.Function;

@Component
public class JwtTokenUtil {
    private static final long serialVersionUID = -2550185165626007488L;

    public static final long JWT_TOKEN_VALIDITY = 5 * 60 * 60;
    private String secret = "phamviettu";
    /// lay thong tin username va thoi gian tao

    //lay username
    public String getUsernameFromToken(String token) {
        return getClaimfromToken(token, Claims::getSubject);
    }

    //lay time tao jwt
    public Date getExpirationDateFromtoken(String token) {
        return getClaimfromToken(token, Claims::getExpiration);
    }

    public <T> T getClaimfromToken(String token, Function<Claims, T> claimsResolver) {
        final Claims claims = getAllClaimsFromToken(token);
        return claimsResolver.apply(claims);

    }

    private Claims getAllClaimsFromToken(String token) {
        return Jwts.parser().setSigningKey(secret).parseClaimsJws(token).getBody();
    }

    // check time
    public Boolean checkTokenTime(String token) {
        final Date time = getExpirationDateFromtoken(token);
        return time.before(new Date());
    }

    public String creatToken(UserDetails userDetails) {
        Map<String, Object> map = new HashMap<>();
        return doCreateken(map, userDetails.getUsername());
    }

    //// creat token
    private String doCreateken(Map<String, Object> claims, String subject) {

        return Jwts.builder().setClaims(claims).setSubject(subject).setIssuedAt(new Date(System.currentTimeMillis()))
                .setExpiration(new Date(System.currentTimeMillis() + JWT_TOKEN_VALIDITY * 1000))
                .signWith(SignatureAlgorithm.HS512, secret).compact();
    }

    public Boolean validateToken(String token, UserDetails userDetails) {
        final String username = getUsernameFromToken(token);
        return (username.equals(userDetails.getUsername()) && !checkTokenTime(token));
    }

}
