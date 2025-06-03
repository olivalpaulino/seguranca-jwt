package dobackaofront.seguranca_jwt.controller;

import dobackaofront.seguranca_jwt.dto.AuthRequest;
import dobackaofront.seguranca_jwt.entity.RefreshToken;
import dobackaofront.seguranca_jwt.repository.RefreshTokenRepository;
import dobackaofront.seguranca_jwt.repository.UserRepository;
import dobackaofront.seguranca_jwt.service.JwtUtil;
import io.jsonwebtoken.Jwts;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.util.WebUtils;

import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.List;
import java.util.Map;
import java.util.UUID;

@RestController
@RequestMapping("/api/auth")
public class AuthController {
    @Autowired
    private AuthenticationManager authManager;
    @Autowired private JwtUtil jwtUtil;
    @Autowired private UserRepository userRepo;
    @Autowired private RefreshTokenRepository refreshRepo;
    @Autowired private PasswordEncoder encoder;

    @PostMapping("/login")
    public ResponseEntity<?> login(@RequestBody AuthRequest req, HttpServletResponse res) {
        // Autentica usuário
        Authentication auth = authManager.authenticate(
                new UsernamePasswordAuthenticationToken(req.getUsername(), req.getPassword())
        );
        UserDetails user = (UserDetails) auth.getPrincipal();
        // Cria e salva refresh token
        RefreshToken refresh = new RefreshToken();
        refresh.setToken(UUID.randomUUID().toString());
        refresh.setUser(userRepo.findByUsername(user.getUsername()).get());
        refresh.setExpiresAt(Instant.now().plus(7, ChronoUnit.DAYS));
        refreshRepo.save(refresh);
        // Gera tokens
        String accessToken = jwtUtil.generateAccessToken(user);
        String refreshToken = jwtUtil.generateRefreshToken(user, refresh);
        // Envia refresh em cookie HttpOnly
        Cookie cookie = new Cookie("refresh_token", refreshToken);
        cookie.setHttpOnly(true); cookie.setSecure(true); cookie.setPath("/");
        cookie.setMaxAge(7*24*3600);
        res.addCookie(cookie);
        // Retorna access token no body
        return ResponseEntity.ok(Map.of("accessToken", accessToken));
    }

    @PostMapping("/refresh")
    public ResponseEntity<?> refreshToken(HttpServletRequest req, HttpServletResponse res) {
        Cookie cookie = WebUtils.getCookie(req, "refresh_token");
        if (cookie == null) return ResponseEntity.status(401).body("Sem refresh token");
        String token = cookie.getValue();
        if (!jwtUtil.validateToken(token)) return ResponseEntity.status(401).body("Refresh inválido");
        String username = jwtUtil.getUsername(token);
        // Verificar no BD se existe e não expirou
        String tokenId = jwtUtil.extractTokenId(token);
        RefreshToken stored = refreshRepo.findByToken(tokenId).orElseThrow();

        if (stored.getExpiresAt().isBefore(Instant.now())) {
            return ResponseEntity.status(401).body("Refresh expirado");
        }
        UserDetails user = (UserDetails) userRepo.findByUsername(username).get();
        // (Opcional) revoga old refresh e cria novo
        refreshRepo.delete(stored);
        RefreshToken newRef = new RefreshToken();
        newRef.setToken(UUID.randomUUID().toString());
        newRef.setUser(userRepo.findByUsername(username).get());
        newRef.setExpiresAt(Instant.now().plus(7, ChronoUnit.DAYS));
        refreshRepo.save(newRef);
        String newAccess = jwtUtil.generateAccessToken(user);
        String newRefresh = jwtUtil.generateRefreshToken(user, newRef);
        Cookie newCookie = new Cookie("refresh_token", newRefresh);
        newCookie.setHttpOnly(true); newCookie.setSecure(true); newCookie.setPath("/");
        newCookie.setMaxAge(7*24*3600);
        res.addCookie(newCookie);
        return ResponseEntity.ok(Map.of("accessToken", newAccess));
    }

    @PostMapping("/logout")
    public ResponseEntity<?> logout(HttpServletRequest req, HttpServletResponse res) {
        Cookie cookie = WebUtils.getCookie(req, "refresh_token");
        if (cookie != null) {
            // Remove do BD e limpa cookie
            refreshRepo.deleteByToken(cookie.getValue());
            cookie.setMaxAge(0); cookie.setPath("/"); res.addCookie(cookie);
        }
        return ResponseEntity.ok("Logout realizado");
    }
}

