package server.backend.filter;

import com.auth0.jwt.JWT;
import com.auth0.jwt.JWTVerifier;
import com.auth0.jwt.algorithms.Algorithm;
import com.auth0.jwt.interfaces.DecodedJWT;

import java.nio.charset.StandardCharsets;

public class TokenAccessor {

    public Long accessTokenId(String token)  {
        try {
            if (token != null && token.startsWith("Bearer ")) {

                String accessToken = token.substring("Bearer ".length());
                Algorithm algorithm = Algorithm.HMAC256("secret".getBytes(StandardCharsets.UTF_8));
                JWTVerifier verifier = JWT.require(algorithm).build();
                DecodedJWT jwt = verifier.verify(accessToken);
                Long id = jwt.getClaim("id").asLong();

                return id;

            } else {
                return null;
            }
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }
}
