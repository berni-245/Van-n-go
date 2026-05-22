package ar.edu.itba.paw.webapp.auth;

import ar.edu.itba.paw.exceptions.UnauthorizedException;
import ar.edu.itba.paw.models.User;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import org.springframework.core.io.ClassPathResource;
import org.springframework.stereotype.Component;
import org.springframework.util.FileCopyUtils;

import javax.crypto.SecretKey;
import javax.crypto.spec.SecretKeySpec;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.InputStreamReader;
import java.time.Duration;
import java.util.Date;

@Component
public class JwtTokenUtil {
    private final SecretKey key;
    public static final String ACCESS_TOKEN_HEADER_NAME = "vango-auth-token";
    public static final String REFRESH_TOKEN_HEADER_NAME = "vango-refresh-token";

    public JwtTokenUtil() throws IOException {
        ClassPathResource keyRes = new ClassPathResource("secKey.txt");
        key = new SecretKeySpec(
                FileCopyUtils.copyToString(new InputStreamReader(keyRes.getInputStream()))
                        .getBytes(), "HmacSHA256");
    }

    public Claims getPayloadFromToken(String token) {
        Claims claims;
        try {
            claims = Jwts.parser().verifyWith(key).build().parseSignedClaims(token).getPayload();
        } catch (Exception e) {
            throw new UnauthorizedException("Invalid token or it was tempered with");
        }
        return claims;
    }

    public String generateAuthToken(PawUserDetails userDetails) {
        User user = userDetails.getUser();
        return Jwts.builder()
                .claim("type", user.getIsDriver() ? "driver" : "client")
                .claim("id", user.getId())
                .claim("tokenType", JwtTokenType.AUTH.getType())
                .issuedAt(new Date())
                .expiration(new Date(System.currentTimeMillis() + Duration.ofHours(1).toMillis()))
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
                .expiration(new Date(System.currentTimeMillis() + Duration.ofHours(5).toMillis()))
                .signWith(key)
                .compact();
    }

    public void addTokensToResponse(PawUserDetails userDetails, HttpServletResponse response) {
        String authToken = generateAuthToken(userDetails);
        String refreshToken = generateRefreshToken(userDetails);
        response.setHeader(ACCESS_TOKEN_HEADER_NAME, authToken);
        response.setHeader(REFRESH_TOKEN_HEADER_NAME, refreshToken);
    }
}