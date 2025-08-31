package com.assinaaqui.backend.controller;

import com.assinaaqui.backend.dto.SignRequest;
import com.assinaaqui.backend.dto.SignResponse;
import com.assinaaqui.backend.entity.Signature;
import com.assinaaqui.backend.entity.User;
import com.assinaaqui.backend.service.JwtService;
import com.assinaaqui.backend.service.SignatureService;
import com.assinaaqui.backend.service.UserService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/signatures")
@CrossOrigin(origins = "http://localhost:3000")
public class SignatureController {

    @Autowired
    private SignatureService signatureService;

    @Autowired
    private UserService userService;

    @Autowired
    private JwtService jwtService;

    @PostMapping("/sign")
    public ResponseEntity<?> signText(
            @Valid @RequestBody SignRequest request,
            @RequestHeader("Authorization") String authHeader) {
        try {
            // Validar token JWT
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
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(error);
            }

            User user = userOptional.get();

            // Validar token
            if (!jwtService.validateToken(token, email)) {
                Map<String, String> error = new HashMap<>();
                error.put("error", "Token inválido");
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(error);
            }

            // Assinar o texto
            Signature signature = signatureService.signText(user, request.getText());

            // Criar resposta
            SignResponse response = new SignResponse(
                signature.getId(),
                signature.getTextHash(),
                signature.getSignature(),
                signature.getAlgorithm(),
                signature.getCreatedAt()
            );

            return ResponseEntity.status(HttpStatus.CREATED).body(response);
        } catch (Exception e) {
            Map<String, String> error = new HashMap<>();
            error.put("error", "Erro ao assinar texto: " + e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(error);
        }
    }

    @GetMapping("/my-signatures")
    public ResponseEntity<?> getMySignatures(@RequestHeader("Authorization") String authHeader) {
        try {
            // Validar token JWT
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
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(error);
            }

            User user = userOptional.get();

            // Validar token
            if (!jwtService.validateToken(token, email)) {
                Map<String, String> error = new HashMap<>();
                error.put("error", "Token inválido");
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(error);
            }

            // Buscar assinaturas do usuário
            List<Signature> signatures = signatureService.findByUserOrderByCreatedAtDesc(user);

            // Converter para DTO
            List<SignResponse> response = signatures.stream()
                .map(sig -> new SignResponse(
                    sig.getId(),
                    sig.getTextHash(),
                    sig.getSignature(),
                    sig.getAlgorithm(),
                    sig.getCreatedAt()
                ))
                .collect(Collectors.toList());

            return ResponseEntity.ok(response);
        } catch (Exception e) {
            Map<String, String> error = new HashMap<>();
            error.put("error", "Erro ao buscar assinaturas: " + e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(error);
        }
    }
}