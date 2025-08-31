package com.assinaaqui.backend.controller;

import com.assinaaqui.backend.dto.VerificationResponse;
import com.assinaaqui.backend.entity.Signature;
import com.assinaaqui.backend.service.SignatureService;
import com.assinaaqui.backend.service.VerificationLogService;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;

@RestController
@RequestMapping("/verify")
@CrossOrigin(origins = "http://localhost:3000")
public class VerificationController {

    @Autowired
    private SignatureService signatureService;

    @Autowired
    private VerificationLogService verificationLogService;

    @GetMapping("/{id}")
    public ResponseEntity<?> verifySignature(
            @PathVariable UUID id,
            HttpServletRequest request) {
        try {
            // Buscar assinatura por ID
            Optional<Signature> signatureOptional = signatureService.findById(id);
            
            if (signatureOptional.isEmpty()) {
                Map<String, String> error = new HashMap<>();
                error.put("error", "Assinatura não encontrada");
                return ResponseEntity.status(HttpStatus.NOT_FOUND).body(error);
            }
            
            Signature signature = signatureOptional.get();
            
            // Verificar a validade da assinatura
            boolean isValid = signatureService.verifySignature(signature);
            
            // Obter informações da requisição para log
            String ipAddress = getClientIpAddress(request);
            String userAgent = request.getHeader("User-Agent");
            
            // Registrar log de verificação
            verificationLogService.logVerification(signature, ipAddress, userAgent, isValid);
            
            // Contar total de verificações
            long verificationCount = verificationLogService.countBySignature(signature);
            
            // Criar resposta
            VerificationResponse response = new VerificationResponse(
                signature.getId(),
                isValid,
                signature.getUser().getName(),
                signature.getAlgorithm(),
                signature.getCreatedAt(),
                signature.getOriginalText(),
                verificationCount
            );
            
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            Map<String, String> error = new HashMap<>();
            error.put("error", "Erro ao verificar assinatura: " + e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(error);
        }
    }

    @PostMapping("/text")
    public ResponseEntity<?> verifySignatureByText(
            @RequestBody Map<String, String> request,
            HttpServletRequest httpRequest) {
        try {
            String originalText = request.get("text");
            String signatureValue = request.get("signature");
            
            if (originalText == null || signatureValue == null) {
                Map<String, String> error = new HashMap<>();
                error.put("error", "Texto e assinatura são obrigatórios");
                return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(error);
            }
            
            // Buscar assinatura no banco de dados pelo valor da assinatura
            // Nota: Este é um método simplificado. Em produção, seria melhor ter um índice ou método mais eficiente
            Optional<Signature> signatureOptional = signatureService.findById(UUID.fromString(request.get("id")));
            
            if (signatureOptional.isEmpty()) {
                Map<String, String> error = new HashMap<>();
                error.put("error", "Assinatura não encontrada");
                return ResponseEntity.status(HttpStatus.NOT_FOUND).body(error);
            }
            
            Signature signature = signatureOptional.get();
            
            // Verificar se o texto original corresponde
            if (!signature.getOriginalText().equals(originalText)) {
                Map<String, String> error = new HashMap<>();
                error.put("error", "Texto não corresponde à assinatura");
                return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(error);
            }
            
            // Verificar a validade da assinatura
            boolean isValid = signatureService.verifySignatureByText(
                originalText, 
                signatureValue, 
                signature.getUser().getPublicKey()
            );
            
            // Obter informações da requisição para log
            String ipAddress = getClientIpAddress(httpRequest);
            String userAgent = httpRequest.getHeader("User-Agent");
            
            // Registrar log de verificação
            verificationLogService.logVerification(signature, ipAddress, userAgent, isValid);
            
            // Contar total de verificações
            long verificationCount = verificationLogService.countBySignature(signature);
            
            // Criar resposta
            VerificationResponse response = new VerificationResponse(
                signature.getId(),
                isValid,
                signature.getUser().getName(),
                signature.getAlgorithm(),
                signature.getCreatedAt(),
                signature.getOriginalText(),
                verificationCount
            );
            
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            Map<String, String> error = new HashMap<>();
            error.put("error", "Erro ao verificar assinatura: " + e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(error);
        }
    }

    private String getClientIpAddress(HttpServletRequest request) {
        String xForwardedFor = request.getHeader("X-Forwarded-For");
        if (xForwardedFor != null && !xForwardedFor.isEmpty()) {
            return xForwardedFor.split(",")[0].trim();
        }
        
        String xRealIp = request.getHeader("X-Real-IP");
        if (xRealIp != null && !xRealIp.isEmpty()) {
            return xRealIp;
        }
        
        return request.getRemoteAddr();
    }
}