package ar.edu.itba.paw.webapp.auth;

import ar.edu.itba.paw.models.User;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import org.springframework.core.io.ClassPathResource;
import org.springframework.stereotype.Component;
import org.springframework.util.FileCopyUtils;

import javax.crypto.SecretKey;
import javax.crypto.spec.SecretKeySpec;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.Date;

// TODO Change this class, It's missing a lot of stuff
@Component
public class JwtTokenUtil {
    private final SecretKey key;

    public JwtTokenUtil() throws IOException {
        ClassPathResource keyRes = new ClassPathResource("secKey.txt");
        key = new SecretKeySpec(
                FileCopyUtils.copyToString(new InputStreamReader(keyRes.getInputStream()))
                        .getBytes(), "HmacSHA256");
    }

    public boolean validate(String token) {
        return true;
    }

    public Claims getPayloadFromToken(String token) {
        return Jwts.parser().verifyWith(key).build().parseSignedClaims(token).getPayload();
    }

    public String generateAccessToken(PawUserDetails userDetails) {
        User user = userDetails.getUser();
        return Jwts.builder()
                .claim("type", user.getIsDriver() ? "driver" : "client")
                .claim("id", user.getId())
                .claim("tokenType", JwtTokenType.ACCESS.getType())
                .issuedAt(new Date())
                .expiration(new Date(System.currentTimeMillis() + 1000 * 60 * 60)) // 1 hora
                .signWith(key)
                .compact();
    }

    public String generateRefreshToken(PawUserDetails userDetails) {
        User user = userDetails.getUser();
        return Jwts.builder()
                .claim("type", user.getIsDriver() ? "driver" : "client")
                .claim("id", user.getId())
                .claim("tokenType", JwtTokenType.REFRESH.getType())
                .issuedAt(new Date())
                .expiration(new Date(System.currentTimeMillis() + 1000 * 60 * 60 * 5)) // 5 horas
                .signWith(key)
                .compact();
    }
}