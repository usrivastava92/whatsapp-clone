package app.whatsapp.commonweb.utils;

import app.whatsapp.common.constants.CommonConstants;
import com.auth0.jwt.JWT;
import com.auth0.jwt.JWTCreator;
import com.auth0.jwt.algorithms.Algorithm;
import com.auth0.jwt.exceptions.JWTDecodeException;
import com.auth0.jwt.interfaces.Claim;
import com.auth0.jwt.interfaces.DecodedJWT;
import com.auth0.jwt.interfaces.Verification;
import org.apache.commons.lang3.StringUtils;

import javax.servlet.http.HttpServletRequest;
import java.util.Date;
import java.util.Map;
import java.util.Optional;

public class JwtUtil {

    private static final String AUTHORIZATION = "Authorization";
    private static final String BEARER = "Bearer";
    private static final String RANDOM_CYPHER = "randomKey";

    private static DecodedJWT decodeJwt(String jwt) throws JWTDecodeException {
        return JWT.decode(jwt);
    }

    private static String getKey(String key) {
        return RANDOM_CYPHER + key;
    }

    public static Optional<String> getIssuer(String jwt) {
        try {
            return Optional.of(decodeJwt(jwt).getIssuer());
        } catch (Exception e) {
        }
        return Optional.empty();
    }

    public static <T> Optional<T> getClaim(String jwt, String key, Class<T> tClass) {
        try {
            Claim claim = decodeJwt(jwt).getClaim(key);
            if (claim != null && !claim.isNull()) {
                return Optional.of(claim.as(tClass));
            }
        } catch (Exception e) {
        }
        return Optional.empty();
    }

    public static Optional<String> getClaim(String jwt, String key) {
        try {
            return getClaim(jwt, key, String.class);
        } catch (Exception e) {
        }
        return Optional.empty();
    }

    public static Optional<String> getJwtFromHeader(HttpServletRequest httpServletRequest) {
        if (httpServletRequest != null) {
            return getJwtFromHeader(httpServletRequest.getHeader(AUTHORIZATION));
        }
        return Optional.empty();
    }

    public static Optional<String> getJwtFromHeader(String authorizationHeader) {
        if (StringUtils.isNotBlank(authorizationHeader) && authorizationHeader.startsWith(BEARER)) {
            String jwt = authorizationHeader.replace(BEARER, CommonConstants.SpecialChars.BLANK).trim();
            if(StringUtils.isNotBlank(jwt)){
                return Optional.of(jwt);
            }
        }
        return Optional.empty();
    }

    public static Optional<Date> getIssuedAt(String jwt) {
        try {
            return Optional.of(decodeJwt(jwt).getIssuedAt());
        } catch (Exception e) {
        }
        return Optional.empty();
    }

    public static Optional<Date> getExpiresAt(String jwt) {
        try {
            return Optional.of(decodeJwt(jwt).getExpiresAt());
        } catch (Exception e) {
        }
        return Optional.empty();
    }

    public static Optional<String> getSubject(String jwt) {
        try {
            return Optional.of(decodeJwt(jwt).getSubject());
        } catch (Exception e) {
        }
        return Optional.empty();
    }

    public static class Builder {

        private JWTCreator.Builder jwtBuilder;
        private String key;

        private Builder() {
            jwtBuilder = JWT.create();
        }

        public static Builder getInstance() {
            return new Builder();
        }

        public Builder withIssuer(String issuer) {
            jwtBuilder.withIssuer(issuer);
            return this;
        }

        public Builder withSubject(String subject) {
            jwtBuilder.withSubject(subject);
            return this;
        }

        public Builder withValidityInSeconds(long seconds) {
            if (seconds < 1) {
                seconds = 0;
            }
            jwtBuilder.withExpiresAt(new Date(System.currentTimeMillis() + (seconds * 1000)));
            return this;
        }

        public Builder withValidityInMinutes(long minutes) {
            withValidityInSeconds(minutes * 60);
            return this;
        }

        public Builder withValidityInHours(long hours) {
            withValidityInHours(hours * 60);
            return this;
        }

        public Builder signWith(String key) {
            this.key = getKey(key);
            return this;
        }

        public Builder withClaim(String key, Object value) {
            if (StringUtils.isNotBlank(key) && value != null) {
                if (value instanceof String) {
                    jwtBuilder.withClaim(key, (String) value);
                } else if (value instanceof Long) {
                    jwtBuilder.withClaim(key, (Long) value);
                } else if (value instanceof Date) {
                    jwtBuilder.withClaim(key, (Date) value);
                } else if (value instanceof Double) {
                    jwtBuilder.withClaim(key, (Double) value);
                } else if (value instanceof Integer) {
                    jwtBuilder.withClaim(key, (Integer) value);
                } else if (value instanceof Boolean) {
                    jwtBuilder.withClaim(key, (Boolean) value);
                }
            }
            return this;
        }

        public Builder withClaims(Map<String, Object> claims) {
            addClaimsToBuilder(jwtBuilder, claims);
            return this;
        }

        public String build() {
            return jwtBuilder.withIssuedAt(new Date()).sign(Algorithm.HMAC256(key));
        }

        private void addClaimsToBuilder(JWTCreator.Builder builder, Map<String, Object> claims) {
            for (Map.Entry<String, Object> entry : claims.entrySet()) {
                withClaim(entry.getKey(), entry.getValue());
            }
        }
    }


    public static class Verifier {

        private Verification verification;
        private String jwt;
        private Long expiresAt;

        private Verifier(String jwt, String key) {
            this.jwt = jwt;
            verification = JWT.require(Algorithm.HMAC256(key));
            withIssuer();
            withSubject();
            withExpiresAt();
            withIssuedAt();
        }

        private void withIssuedAt() {
            Optional<Date> issuedAt = getIssuedAt(jwt);
            if (issuedAt.isPresent()) {
                verification.acceptIssuedAt(issuedAt.get().getTime());
            }
        }

        private void withExpiresAt() {
            Optional<Date> expiresAt = getExpiresAt(jwt);
            if (expiresAt.isPresent()) {
                verification.acceptExpiresAt(expiresAt.get().getTime());
                this.expiresAt = expiresAt.get().getTime();
            }
        }

        public static Verifier getInstance(String jwt, String key) {
            return new Verifier(jwt, getKey(key));
        }

        private void withIssuer() {
            Optional<String> issuer = getIssuer(jwt);
            if (issuer.isPresent() && StringUtils.isNotBlank(issuer.get())) {
                verification.withIssuer(issuer.get());
            }
        }

        private void withSubject() {
            Optional<String> subject = getSubject(jwt);
            if (subject.isPresent() && StringUtils.isNotBlank(subject.get())) {
                verification.withSubject(subject.get());
            }
        }

        public Verifier withClaim(String key, Object value) {
            if (StringUtils.isNotBlank(key) && value != null) {
                if (value instanceof String) {
                    verification.withClaim(key, (String) value);
                } else if (value instanceof Long) {
                    verification.withClaim(key, (Long) value);
                } else if (value instanceof Date) {
                    verification.withClaim(key, (Date) value);
                } else if (value instanceof Double) {
                    verification.withClaim(key, (Double) value);
                } else if (value instanceof Integer) {
                    verification.withClaim(key, (Integer) value);
                } else if (value instanceof Boolean) {
                    verification.withClaim(key, (Boolean) value);
                }
            }
            return this;
        }

        public Verifier withClaims(Map<String, Object> claims) {
            addClaimsToBuilder(verification, claims);
            return this;
        }

        private void addClaimsToBuilder(Verification builder, Map<String, Object> claims) {
            for (Map.Entry<String, Object> entry : claims.entrySet()) {
                withClaim(entry.getKey(), entry.getValue());
            }
        }

        public boolean verify() {
            try {
                if (expiresAt != null && System.currentTimeMillis() > expiresAt) {
                    return false;
                }
                verification.build().verify(jwt);
                return true;
            } catch (Exception e) {
                return false;
            }
        }

    }
}