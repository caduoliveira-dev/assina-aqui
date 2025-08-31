package com.assinaaqui.backend.controller;

import com.assinaaqui.backend.dto.AuthResponse;
import com.assinaaqui.backend.dto.LoginRequest;
import com.assinaaqui.backend.dto.RegisterRequest;
import com.assinaaqui.backend.entity.User;
import com.assinaaqui.backend.service.JwtService;
import com.assinaaqui.backend.service.UserService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

@RestController
@RequestMapping("/auth")
@CrossOrigin(origins = "http://localhost:3000")
public class AuthController {

    @Autowired
    private UserService userService;

    @Autowired
    private JwtService jwtService;

    @PostMapping("/register")
    public ResponseEntity<?> register(@Valid @RequestBody RegisterRequest request) {
        try {
            User user = userService.createUser(request.getName(), request.getEmail(), request.getPassword());
            
            // Gerar token JWT
            String token = jwtService.generateToken(user.getEmail());
            
            AuthResponse response = new AuthResponse(token, user.getId(), user.getName(), user.getEmail());
            
            return ResponseEntity.status(HttpStatus.CREATED).body(response);
        } catch (RuntimeException e) {
            Map<String, String> error = new HashMap<>();
            error.put("error", e.getMessage());
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(error);
        }
    }

    @PostMapping("/login")
    public ResponseEntity<?> login(@Valid @RequestBody LoginRequest request) {
        try {
            Optional<User> userOptional = userService.findByEmail(request.getEmail());
            
            if (userOptional.isEmpty()) {
                Map<String, String> error = new HashMap<>();
                error.put("error", "Credenciais inválidas");
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(error);
            }
            
            User user = userOptional.get();
            
            if (!userService.validatePassword(request.getPassword(), user.getPassword())) {
                Map<String, String> error = new HashMap<>();
                error.put("error", "Credenciais inválidas");
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(error);
            }
            
            // Gerar token JWT
            String token = jwtService.generateToken(user.getEmail());
            
            AuthResponse response = new AuthResponse(token, user.getId(), user.getName(), user.getEmail());
            
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            Map<String, String> error = new HashMap<>();
            error.put("error", "Erro interno do servidor");
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(error);
        }
    }

    @GetMapping("/me")
    public ResponseEntity<?> getCurrentUser(@RequestHeader("Authorization") String authHeader) {
        try {
            if (authHeader == null || !authHeader.startsWith("Bearer ")) {
                Map<String, String> error = new HashMap<>();
                error.put("error", "Token não fornecido");
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(error);
            }
            
            String token = authHeader.substring(7);
            String email = jwtService.extractUsername(token);
            
            Optional<User> userOptional = userService.findByEmail(email);
            
            if (userOptional.isEmpty()) {
                Map<String, String> error = new HashMap<>();
                error.put("error", "Usuário não encontrado");
                return ResponseEntity.status(HttpStatus.NOT_FOUND).body(error);
            }
            
            User user = userOptional.get();
            
            Map<String, Object> userInfo = new HashMap<>();
            userInfo.put("id", user.getId());
            userInfo.put("name", user.getName());
            userInfo.put("email", user.getEmail());
            userInfo.put("createdAt", user.getCreatedAt());
            
            return ResponseEntity.ok(userInfo);
        } catch (Exception e) {
            Map<String, String> error = new HashMap<>();
            error.put("error", "Token inválido");
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(error);
        }
    }
}