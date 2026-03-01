package org.envycorp.apigateway.filter;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.JwtException;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.envycorp.apigateway.http.MutableHttpServletRequest;
import org.envycorp.apigateway.service.JwtService;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.List;

@Component
@RequiredArgsConstructor
public class JwtAuthFilter extends OncePerRequestFilter {
    private final JwtService jwtService;

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException {
        String authHeader = request.getHeader("Authorization");
        if(authHeader == null || !authHeader.startsWith("Bearer ")){
            filterChain.doFilter(request, response);
            return;
        }
        String token = authHeader.substring(7);
        try {
            Claims claims = jwtService.parseToken(token);
            String role = claims.get("role", String.class);
            System.out.println("JwtAuthFilter: role = " + role);
            String username = claims.getSubject();
            System.out.println("JwtAuthFilter: username = " + username);

            UsernamePasswordAuthenticationToken authentication =
                    new UsernamePasswordAuthenticationToken(username, null, List.of(new SimpleGrantedAuthority(role)));

            SecurityContextHolder.getContext().setAuthentication(authentication);

            request.setAttribute("X-User-Id", claims.get("userId", String.class));
            request.setAttribute("X-User-Role", role);
            request.setAttribute("X-User-Username", username);

            filterChain.doFilter(request, response);
        }
        catch(JwtException jwtException){
            SecurityContextHolder.clearContext();
            response.sendError(HttpServletResponse.SC_UNAUTHORIZED);
        }

    }
}
