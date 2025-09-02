package com.assinaaqui.backend.controller;

import com.assinaaqui.backend.dto.SignRequest;
import com.assinaaqui.backend.entity.Signature;
import com.assinaaqui.backend.entity.User;
import com.assinaaqui.backend.service.JwtService;
import com.assinaaqui.backend.service.SignatureService;
import com.assinaaqui.backend.service.UserService;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.MockitoAnnotations;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.time.LocalDateTime;
import java.util.Optional;
import java.util.UUID;

import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(controllers = SignatureController.class, excludeAutoConfiguration = {org.springframework.boot.autoconfigure.security.servlet.SecurityAutoConfiguration.class})
class SignatureControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private SignatureService signatureService;

    @MockBean
    private UserService userService;

    @MockBean
    private JwtService jwtService;

    @Autowired
    private ObjectMapper objectMapper;

    private User testUser;
    private Signature testSignature;
    private String validToken;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        
        testUser = new User();
        testUser.setId(1L);
        testUser.setName("Test User");
        testUser.setEmail("test@example.com");
        testUser.setPassword("encodedPassword");
        testUser.setPublicKey("testPublicKey");
        testUser.setPrivateKey("testPrivateKey");
        testUser.setCreatedAt(LocalDateTime.now());
        
        testSignature = new Signature();
        testSignature.setId(UUID.randomUUID());
        testSignature.setUser(testUser);
        testSignature.setOriginalText("Test document");
        testSignature.setTextHash("testhash");
        testSignature.setSignature("testsignature");
        testSignature.setAlgorithm("SHA-256 with RSA");
        testSignature.setCreatedAt(LocalDateTime.now());
        
        validToken = "valid-jwt-token";
    }

    @Test
    void testSignText_ValidRequest() throws Exception {
        SignRequest signRequest = new SignRequest();
        signRequest.setText("Document to be signed");
        
        when(jwtService.extractUsername(validToken)).thenReturn("test@example.com");
        when(userService.findByEmail("test@example.com")).thenReturn(Optional.of(testUser));
        when(jwtService.validateToken(validToken, "test@example.com")).thenReturn(true);
        when(signatureService.signText(eq(testUser), eq("Document to be signed"))).thenReturn(testSignature);
        
        mockMvc.perform(post("/signatures/sign")
                .header("Authorization", "Bearer " + validToken)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(signRequest)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.signatureId").value(testSignature.getId().toString()))
                .andExpect(jsonPath("$.hash").value("testhash"))
                .andExpect(jsonPath("$.signature").value("testsignature"))
                .andExpect(jsonPath("$.algorithm").value("SHA-256 with RSA"));
    }

    @Test
    void testSignText_UnauthorizedNoToken() throws Exception {
        SignRequest signRequest = new SignRequest();
        signRequest.setText("Document to be signed");
        
        mockMvc.perform(post("/signatures/sign")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(signRequest)))
                .andExpect(status().isBadRequest());
    }

    @Test
    void testSignText_InvalidToken() throws Exception {
        SignRequest signRequest = new SignRequest();
        signRequest.setText("Document to be signed");
        
        when(jwtService.extractUsername("invalid-token")).thenReturn("test@example.com");
        when(userService.findByEmail("test@example.com")).thenReturn(Optional.of(testUser));
        when(jwtService.validateToken("invalid-token", "test@example.com")).thenReturn(false);
        
        mockMvc.perform(post("/signatures/sign")
                .header("Authorization", "Bearer invalid-token")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(signRequest)))
                .andExpect(status().isUnauthorized())
                .andExpect(jsonPath("$.error").value("Token inválido"));
    }

    @Test
    void testSignText_UserNotFound() throws Exception {
        SignRequest signRequest = new SignRequest();
        signRequest.setText("Document to be signed");
        
        when(jwtService.extractUsername(validToken)).thenReturn("nonexistent@example.com");
        when(userService.findByEmail("nonexistent@example.com")).thenReturn(Optional.empty());
        
        mockMvc.perform(post("/signatures/sign")
                .header("Authorization", "Bearer " + validToken)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(signRequest)))
                .andExpect(status().isUnauthorized())
                .andExpect(jsonPath("$.error").value("Usuário não encontrado"));
    }
}