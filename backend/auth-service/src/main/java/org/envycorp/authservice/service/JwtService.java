package org.envycorp.authservice.service;

import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;
import org.envycorp.authservice.model.entity.UserAuth;
import org.springframework.stereotype.Component;

import javax.crypto.SecretKey;
import java.util.Date;

@Component
public class JwtService {
    private final long EXPIRATION_TIME = 3600 * 1000;
    private final String SECRET_PHRASE = "zparg/U78AiX/gVUrPSryQLT/BCJA+apoj6NlpCadRw=";
    private final SecretKey SECRET_KEY = Keys.hmacShaKeyFor(Decoders.BASE64.decode(SECRET_PHRASE));

    public String generateToken(UserAuth userAuth) {
        String roleName = userAuth.getRole().getName();

        return Jwts.builder()
                .subject(userAuth.getUsername())
                .claim("role", roleName)
                .claim("userId", userAuth.getId())
                .issuedAt(new Date())
                .expiration(new Date(System.currentTimeMillis() + EXPIRATION_TIME))
                .signWith(SECRET_KEY)
                .compact();
    }
}
