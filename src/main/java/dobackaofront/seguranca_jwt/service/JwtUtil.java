package dobackaofront.seguranca_jwt.service;

import dobackaofront.seguranca_jwt.entity.RefreshToken;
import io.jsonwebtoken.JwtException;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.security.Keys;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;

import java.nio.charset.StandardCharsets;
import java.security.Key;
import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.Date;
import java.util.List;

@Service
public class JwtUtil {
    private final Key secretKey = Keys.hmacShaKeyFor("uma-chave-secreta-de-256-bits-segura!123".getBytes(StandardCharsets.UTF_8));
    // Gera Access Token
    public String generateAccessToken(UserDetails user) {
        return Jwts.builder()
                .setSubject(user.getUsername())
                .claim("roles", user.getAuthorities().stream()
                        .map(GrantedAuthority::getAuthority).toList())
                .setIssuedAt(new Date())
                .setExpiration(Date.from(Instant.now().plus(15, ChronoUnit.MINUTES)))
                .signWith(secretKey, SignatureAlgorithm.HS256)
                .compact();
    }
    // Gera Refresh Token
    public String generateRefreshToken(UserDetails user, RefreshToken refreshToken) {
        // Usa ID do refreshToken para rastrear no BD
        return Jwts.builder()
                .setSubject(user.getUsername())
                .setId(refreshToken.getToken())
                .setIssuedAt(new Date())
                .setExpiration(Date.from(Instant.now().plus(7, ChronoUnit.DAYS)))
                .signWith(secretKey, SignatureAlgorithm.HS256)
                .compact();
    }
    // Valida assinatura e expiração
    public boolean validateToken(String token) {
        try {
            Jwts.parserBuilder().setSigningKey(secretKey).build().parseClaimsJws(token);
            return true;
        } catch (JwtException | IllegalArgumentException e) {
            return false;
        }
    }
    public String getUsername(String token) {
        return Jwts.parserBuilder().setSigningKey(secretKey).build()
                .parseClaimsJws(token).getBody().getSubject();
    }
    @SuppressWarnings("unchecked")
    public List<SimpleGrantedAuthority> getRoles(String token) {
        List<String> roles = (List<String>) Jwts.parserBuilder().setSigningKey(secretKey).build()
                .parseClaimsJws(token).getBody().get("roles");
        return roles.stream().map(SimpleGrantedAuthority::new).toList();
    }

    public String extractTokenId(String token) {
        return Jwts.parserBuilder()
                .setSigningKey(this.secretKey)
                .build()
                .parseClaimsJws(token)
                .getBody()
                .getId(); // pega o `jti`
    }

}
