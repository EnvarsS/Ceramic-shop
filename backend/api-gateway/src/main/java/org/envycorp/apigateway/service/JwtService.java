package org.envycorp.apigateway.service;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;
import org.springframework.stereotype.Service;

import javax.crypto.SecretKey;
import java.util.Date;

@Service
public class JwtService {
    private final String SECRET_PHRASE = "zparg/U78AiX/gVUrPSryQLT/BCJA+apoj6NlpCadRw=";
    private final SecretKey SECRET_KEY = Keys.hmacShaKeyFor(Decoders.BASE64.decode(SECRET_PHRASE));

    public Claims parseToken(String token) {
        Claims claims = Jwts.parser()
                .verifyWith(SECRET_KEY)
                .build()
                .parseClaimsJws(token)
                .getPayload();
        if(claims.getExpiration().before(new Date())) {
            throw new RuntimeException("Token is expired");
        }

        return claims;
    }


}
