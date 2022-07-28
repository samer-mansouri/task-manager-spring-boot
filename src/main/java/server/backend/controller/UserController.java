package server.backend.controller;

import com.auth0.jwt.JWT;
import com.auth0.jwt.JWTVerifier;
import com.auth0.jwt.algorithms.Algorithm;
import com.auth0.jwt.interfaces.DecodedJWT;
import com.fasterxml.jackson.core.exc.StreamWriteException;
import com.fasterxml.jackson.databind.DatabindException;
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


    @GetMapping("/users")
    public List<User> getAllUsers() {
        return userRepo.findAll();
    }


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

    @PutMapping("/user")
    public void update(@RequestBody User user, HttpServletRequest request, HttpServletResponse response) throws StreamWriteException, DatabindException, IOException {
        try {
            User u = userRepo.findById(user.getId()).orElse(null);
            if (u == null) {
                Map<String, String> map = new HashMap<>();
                map.put("message", "User not found");
                response.setStatus(HttpServletResponse.SC_NOT_FOUND);
                new ObjectMapper().writeValue(response.getWriter(), map);
            
            }
            u.setEmail(user.getEmail());
            u.setFirstName(user.getFirstName());
            u.setLastName(user.getLastName());
            //u.setPassword(passwordEncoder.encode(user.getPassword()));
            u.setAddress(user.getAddress());
            if(user.getRole() != null) {
                u.setRole(user.getRole());
            }
            u.setPhone(user.getPhone());
            u.setFonction(user.getFonction());
            User updatedUser = userRepo.save(u);
            Map<String, Object> map = new HashMap<>();
            map.put("message", "User updated");
            map.put("user", updatedUser);
            response.setStatus(HttpStatus.OK.value());
            response.setContentType("application/json");
            new ObjectMapper().writeValue(response.getWriter(), map);

        } catch (Exception e) {
            Map<String, String> map = new HashMap<>();
            map.put("message", e.getMessage());
            response.setStatus(HttpStatus.BAD_REQUEST.value());
            response.setContentType("application/json");
            new ObjectMapper().writeValue(response.getWriter(), map);
        }
    }

    @DeleteMapping("/user/{id}")
    public void delete(@PathVariable Long id, HttpServletResponse response) throws StreamWriteException, DatabindException, IOException {
        try {
            User u = userRepo.findById(id).orElse(null);
            if (u == null) {
                Map<String, String> map = new HashMap<>();
                map.put("message", "User not found");
                response.setStatus(HttpServletResponse.SC_NOT_FOUND);
                new ObjectMapper().writeValue(response.getWriter(), map);
            }
            userRepo.delete(u);
            Map<String, String> map = new HashMap<>();
            map.put("message", "User deleted");
            response.setStatus(HttpStatus.OK.value());
            response.setContentType("application/json");
            new ObjectMapper().writeValue(response.getWriter(), map);
        } catch (Exception e) {
            Map<String, String> map = new HashMap<>();
            map.put("message", e.getMessage());
            response.setStatus(HttpStatus.BAD_REQUEST.value());
            response.setContentType("application/json");
            new ObjectMapper().writeValue(response.getWriter(), map);
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
                            .withExpiresAt(new Date(System.currentTimeMillis() + 10*60*1000000))
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


    @GetMapping("/user/{id}")
    public User getUser(@PathVariable Long id) {
        return userRepo.findById(id).orElse(null);
    }


    @PostMapping("update_password")
    public void updatePassword(HttpServletRequest request, HttpServletResponse response) throws IOException {
        try {
            String oldPassword = request.getParameter("oldPassword");
            String newPassword = request.getParameter("newPassword");
            String userId = request.getParameter("userId");
            User user = userRepo.findById(Long.parseLong(userId)).orElse(null);
            if (user == null) {
                Map<String, String> map = new HashMap<>();
                map.put("message", "User not found");
                response.setStatus(HttpServletResponse.SC_NOT_FOUND);
                new ObjectMapper().writeValue(response.getWriter(), map);
            }

            if (passwordEncoder.matches(oldPassword, user.getPassword())) {
                user.setPassword(passwordEncoder.encode(newPassword));
                userRepo.save(user);
                Map<String, String> map = new HashMap<>();
                map.put("message", "Password updated");
                response.setStatus(HttpServletResponse.SC_OK);
                new ObjectMapper().writeValue(response.getWriter(), map);
            } else {
                Map<String, String> map = new HashMap<>();
                map.put("message", "Wrong password");
                response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
                new ObjectMapper().writeValue(response.getWriter(), map);
            }
            
        } catch (Exception e) {
            Map<String, String> map = new HashMap<>();
            map.put("message", e.getMessage());
            response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
            new ObjectMapper().writeValue(response.getWriter(), map);
        }
    }
}
