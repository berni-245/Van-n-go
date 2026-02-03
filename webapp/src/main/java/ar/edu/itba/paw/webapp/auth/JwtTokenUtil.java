package ar.edu.itba.paw.webapp.auth;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import org.springframework.core.io.ClassPathResource;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Component;
import org.springframework.util.FileCopyUtils;

import javax.crypto.SecretKey;
import javax.crypto.spec.SecretKeySpec;
import java.io.IOException;
import java.io.InputStreamReader;

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

    public String generateAccessToken(UserDetails userDetails) {
        return "TODO";
    }

    public String generateRefreshToken(UserDetails userDetails) {
        return "TODO";
    }
}