package br.com.erudio.security.jwt;

import br.com.erudio.data.dto.security.TokenDTO;
import br.com.erudio.exception.handler.InvalidJwtAuthenticationException;
import com.auth0.jwt.JWT;
import com.auth0.jwt.JWTVerifier;
import com.auth0.jwt.algorithms.Algorithm;
import com.auth0.jwt.interfaces.DecodedJWT;
import jakarta.annotation.PostConstruct;
import jakarta.servlet.http.HttpServletRequest;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.stereotype.Service;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import java.util.Base64;
import java.util.Date;
import java.util.List;

@Service
public class JwtTokenProvider {

    @Value("${security.jwt.token.secret-key:secret}")
    private String secretKey = "secret";

    @Value("${security.jwt.token.expire-lenght:3600000}")
    private long validityInMilliseconds;

    @Autowired
    private UserDetailsService service;

    Algorithm algorithm = null;

    @PostConstruct
    protected void init() {
        secretKey = Base64.getEncoder().encodeToString(secretKey.getBytes());
        algorithm = Algorithm.HMAC256(secretKey.getBytes());
    }

    public TokenDTO createAccessToken(String username, List<String> roles) {
        Date now = new Date();
        Date validity = getValidity();
        String accessToken = getAccessToken(username, roles, now, validity);
        String refreshToken = getRefreshToken(username, roles, now);

        return new TokenDTO(username, true, now, validity, accessToken, refreshToken);
    }

    public TokenDTO refreshToken(String refreshToken) throws Exception {
        String rawToken = "";
            if (isTokenContainsBearer(refreshToken)) rawToken = getRawToken(refreshToken);
            JWTVerifier verifier = JWT.require(algorithm).build();
            DecodedJWT decodedJWT = verifier.verify(rawToken);
            String username = decodedJWT.getSubject();

            List<String> roles = decodedJWT.getClaim("roles").asList(String.class);
            return createAccessToken(username, roles);
    }

    private String getRefreshToken(String username, List<String> roles, Date now) {
        return JWT.create()
                .withClaim("roles", roles)
                .withIssuedAt(now)
                .withExpiresAt(getValidity())
                .withSubject(username)
                .sign(algorithm);
    }

    private String getAccessToken(String username, List<String> roles, Date now, Date validity) {
        String issueUrl = ServletUriComponentsBuilder.fromCurrentContextPath().build().toUriString();
        return JWT.create()
                .withClaim("roles", roles)
                .withIssuedAt(now)
                .withExpiresAt(validity)
                .withSubject(username)
                .withIssuer(issueUrl)
                .sign(algorithm);
    }

    private Date getValidity() {
        return new Date(new Date().getTime() + validityInMilliseconds);
    }

    public Authentication getAuthentication(String token) {
        DecodedJWT decodedJWT = decodeToken(token);
        UserDetails userDetails = this.service
                .loadUserByUsername(decodedJWT.getSubject());
        return new UsernamePasswordAuthenticationToken(userDetails, "", userDetails.getAuthorities());
    }

    private DecodedJWT decodeToken(String token) {
        Algorithm alg = Algorithm.HMAC256(secretKey.getBytes());
        JWTVerifier verifier = JWT.require(alg).build();
        return verifier.verify(token);
    }

    public String resolveToken(HttpServletRequest request) {
        String bearerToken = request.getHeader("Authorization");

        if (isTokenContainsBearer(bearerToken))
            return getRawToken(bearerToken);
        return null;
    }

    public Boolean validateToken(String token) {
        DecodedJWT decodedJWT = decodeToken(token);
        try {
            return !decodedJWT.getExpiresAt().before(new Date());

        } catch (Exception e) {
            throw new InvalidJwtAuthenticationException("Expired or Invalid JWT Token");
        }
    }

    private static boolean isTokenContainsBearer(String token) {
        return (StringUtils.isNotBlank(token) && token.startsWith("Bearer "));
    }

    private static String getRawToken(String token) {
        return token.substring("Bearer ".length());
    }
}
