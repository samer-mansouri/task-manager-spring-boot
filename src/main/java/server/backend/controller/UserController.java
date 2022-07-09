package server.backend.controller;

import com.auth0.jwt.JWT;
import com.auth0.jwt.JWTVerifier;
import com.auth0.jwt.algorithms.Algorithm;
import com.auth0.jwt.interfaces.DecodedJWT;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;
import server.backend.entity.User;
import server.backend.repository.IUserRepo;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.*;

@RestController
@RequestMapping("/api/v1")
public class UserController {

    @Autowired
    IUserRepo userRepo;

    @Autowired
    PasswordEncoder passwordEncoder;




    @PostMapping("/register")
    public ResponseEntity<User> register(@RequestBody User user) {
        System.out.println(user);
        try {
            user.setPassword(passwordEncoder.encode(user.getPassword()));
            User u = userRepo.save(user);
            return new ResponseEntity<>(u, HttpStatus.CREATED);
        } catch (Exception e) {
            return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
        }
    }

    @PostMapping("/token_refresh")
    public void refreshToken(HttpServletRequest request, HttpServletResponse response) throws IOException {
        try {
            String refresh_token = request.getParameter("refresh_token");
            if (refresh_token != null ) {
                try {
                    Algorithm algorithm = Algorithm.HMAC256("secret".getBytes(StandardCharsets.UTF_8));
                    JWTVerifier verifier = JWT.require(algorithm).build();
                    DecodedJWT jwt = verifier.verify(refresh_token);
                    String email = jwt.getSubject();

                    User user = userRepo.findByEmail(email);
                    String access_token = JWT.create()
                            .withSubject(user.getEmail())
                            .withExpiresAt(new Date(System.currentTimeMillis() + 10*60*1000))
                            .withIssuer(request.getRequestURL().toString())
                            .withClaim("role", user.getRole().toString())
                            .withClaim("id", user.getId())
                            .sign(algorithm);

                    Map<String, String> tokens = new HashMap<>();
                    tokens.put("access_token", access_token);
                    tokens.put("refresh_token", refresh_token);
                    response.setContentType("application/json");
                    new ObjectMapper().writeValue(response.getOutputStream(), tokens);


                } catch (Exception e) {
                    response.setStatus(401);
                    Map<String, String> error = new HashMap<>();
                    error.put("error", e.getMessage());
                    response.setContentType("application/json");
                    new ObjectMapper().writeValue(response.getOutputStream(), error);
                }
            }
            else {
                throw new RuntimeException("Refresh token is missing");
            }
        } catch (Exception e) {
                response.setStatus(401);
                Map<String, String> error = new HashMap<>();
                error.put("error", e.getMessage());
                response.setContentType("application/json");
                new ObjectMapper().writeValue(response.getOutputStream(), error);
        }
    }

}
