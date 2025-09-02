package com.assinaaqui.backend.controller;

import com.assinaaqui.backend.entity.Signature;
import com.assinaaqui.backend.entity.User;
import com.assinaaqui.backend.service.JwtService;
import com.assinaaqui.backend.service.SignatureService;
import com.assinaaqui.backend.service.UserService;
import com.assinaaqui.backend.service.VerificationLogService;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.MockitoAnnotations;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.context.annotation.Import;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;

import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(controllers = VerificationController.class, excludeAutoConfiguration = {org.springframework.boot.autoconfigure.security.servlet.SecurityAutoConfiguration.class})
class VerificationControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private SignatureService signatureService;

    @MockBean
    private VerificationLogService verificationLogService;

    @MockBean
    private JwtService jwtService;

    @MockBean
    private UserService userService;

    @Autowired
    private ObjectMapper objectMapper;

    private Signature testSignature;
    private UUID signatureId;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        
        signatureId = UUID.randomUUID();

        User testUser = new User();
        testUser.setId(1L);
        testUser.setName("Test User");
        testUser.setEmail("test@example.com");
        testUser.setPublicKey("testPublicKey");
        testUser.setCreatedAt(LocalDateTime.now());
        
        testSignature = new Signature();
        testSignature.setId(signatureId);
        testSignature.setUser(testUser);
        testSignature.setOriginalText("Test document for verification");
        testSignature.setTextHash("abcdef1234567890");
        testSignature.setSignature("validSignatureValue");
        testSignature.setAlgorithm("SHA-256 with RSA");
        testSignature.setCreatedAt(LocalDateTime.now());
    }

    @Test
    void testVerifySignatureById_ValidSignature() throws Exception {
        when(signatureService.findById(signatureId)).thenReturn(Optional.of(testSignature));
        when(signatureService.verifySignature(testSignature)).thenReturn(true);
        when(verificationLogService.countBySignature(testSignature)).thenReturn(5L);
        
        mockMvc.perform(get("/verify/" + signatureId))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.signatureId").value(signatureId.toString()))
                .andExpect(jsonPath("$.valid").value(true))
                .andExpect(jsonPath("$.status").value("VALID"))
                .andExpect(jsonPath("$.signatory").value("Test User"))
                .andExpect(jsonPath("$.algorithm").value("SHA-256 with RSA"))
                .andExpect(jsonPath("$.originalText").value("Test document for verification"))
                .andExpect(jsonPath("$.verificationCount").value(5));
    }

    @Test
    void testVerifySignatureById_InvalidSignature() throws Exception {
        when(signatureService.findById(signatureId)).thenReturn(Optional.of(testSignature));
        when(signatureService.verifySignature(testSignature)).thenReturn(false);
        when(verificationLogService.countBySignature(testSignature)).thenReturn(3L);
        
        mockMvc.perform(get("/verify/" + signatureId))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.valid").value(false))
                .andExpect(jsonPath("$.status").value("INVALID"));
    }

    @Test
    void testVerifySignatureById_NotFound() throws Exception {
        UUID nonExistentId = UUID.randomUUID();
        when(signatureService.findById(nonExistentId)).thenReturn(Optional.empty());
        
        mockMvc.perform(get("/verify/" + nonExistentId))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.error").value("Assinatura não encontrada"));
    }

    @Test
    void testVerifySignatureByText_ValidSignature() throws Exception {
        Map<String, String> request = new HashMap<>();
        request.put("text", "Test document for verification");
        request.put("signature", "validSignatureValue");
        
        when(signatureService.calculateTextHash("Test document for verification")).thenReturn("abcdef1234567890");
        when(signatureService.findByHashAndSignature("abcdef1234567890", "validSignatureValue"))
                .thenReturn(Optional.of(testSignature));
        when(signatureService.verifySignatureByText(
                eq("Test document for verification"), 
                eq("validSignatureValue"), 
                eq("testPublicKey"))).thenReturn(true);
        when(verificationLogService.countBySignature(testSignature)).thenReturn(2L);
        
        mockMvc.perform(post("/verify/text")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.valid").value(true))
                .andExpect(jsonPath("$.status").value("VALID"))
                .andExpect(jsonPath("$.signatory").value("Test User"));
    }

    @Test
    void testVerifySignatureByText_InvalidSignature() throws Exception {
        Map<String, String> request = new HashMap<>();
        request.put("text", "Test document for verification");
        request.put("signature", "tamperedSignatureValue");
        
        when(signatureService.calculateTextHash("Test document for verification")).thenReturn("abcdef1234567890");
        when(signatureService.findByHashAndSignature("abcdef1234567890", "tamperedSignatureValue"))
                .thenReturn(Optional.of(testSignature));
        when(signatureService.verifySignatureByText(
                eq("Test document for verification"), 
                eq("tamperedSignatureValue"), 
                eq("testPublicKey"))).thenReturn(false);
        when(verificationLogService.countBySignature(testSignature)).thenReturn(1L);
        
        mockMvc.perform(post("/verify/text")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.valid").value(false))
                .andExpect(jsonPath("$.status").value("INVALID"));
    }

    @Test
    void testVerifySignatureByText_MissingFields() throws Exception {
        Map<String, String> request = new HashMap<>();
        request.put("text", "Test document");
        // Missing signature field
        
        mockMvc.perform(post("/verify/text")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.error").value("Texto e assinatura são obrigatórios"));
    }

    @Test
    void testVerifySignatureByText_SignatureNotFound() throws Exception {
        Map<String, String> request = new HashMap<>();
        request.put("text", "Test document");
        request.put("signature", "unknownSignature");
        
        when(signatureService.calculateTextHash("Test document")).thenReturn("somehash");
        when(signatureService.findByHashAndSignature("somehash", "unknownSignature"))
                .thenReturn(Optional.empty());
        
        mockMvc.perform(post("/verify/text")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.error").value("Assinatura não encontrada no sistema"));
    }
}