package com.assinaaqui.backend;

import com.assinaaqui.backend.dto.RegisterRequest;
import com.assinaaqui.backend.dto.SignRequest;
import com.assinaaqui.backend.entity.Signature;
import com.assinaaqui.backend.entity.User;
import com.assinaaqui.backend.repository.SignatureRepository;
import com.assinaaqui.backend.repository.UserRepository;
import com.assinaaqui.backend.service.CryptographyService;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureWebMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.transaction.annotation.Transactional;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureWebMvc
@Transactional
@ActiveProfiles("test")
class DigitalSignatureIntegrationTest {

    private MockMvc mockMvc;

    @Autowired
    private WebApplicationContext webApplicationContext;

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private SignatureRepository signatureRepository;

    @Autowired
    private CryptographyService cryptographyService;

    private String authToken;

    @BeforeEach
    void setUp() throws Exception {
        mockMvc = MockMvcBuilders.webAppContextSetup(webApplicationContext).build();
        signatureRepository.deleteAll();
        userRepository.deleteAll();
        
        RegisterRequest registerRequest = new RegisterRequest();
        registerRequest.setName("Test User");
        registerRequest.setEmail("test@example.com");
        registerRequest.setPassword("password123");
        
        MvcResult registerResult = mockMvc.perform(post("/auth/register")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(registerRequest)))
                .andExpect(status().isCreated())
                .andReturn();
        
        String registerResponse = registerResult.getResponse().getContentAsString();
        JsonNode registerJson = objectMapper.readTree(registerResponse);
        authToken = registerJson.get("token").asText();

        User testUser = userRepository.findByEmail("test@example.com").orElseThrow();
    }

    @Test
    void testCompleteDigitalSignatureFlow_ValidSignature() throws Exception {
        String documentText = "Este é um documento importante que precisa ser assinado digitalmente.";
        
        SignRequest signRequest = new SignRequest();
        signRequest.setText(documentText);
        
        MvcResult signResult = mockMvc.perform(post("/signatures/sign")
                .header("Authorization", "Bearer " + authToken)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(signRequest)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.signatureId").exists())
                .andExpect(jsonPath("$.hash").exists())
                .andExpect(jsonPath("$.signature").exists())
                .andExpect(jsonPath("$.algorithm").value("SHA-256 with RSA"))
                .andReturn();
        
        String signResponse = signResult.getResponse().getContentAsString();
        JsonNode signJson = objectMapper.readTree(signResponse);
        UUID signatureId = UUID.fromString(signJson.get("signatureId").asText());
        String hash = signJson.get("hash").asText();
        String signature = signJson.get("signature").asText();
        
        mockMvc.perform(get("/verify/" + signatureId))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.signatureId").value(signatureId.toString()))
                .andExpect(jsonPath("$.valid").value(true))
                .andExpect(jsonPath("$.status").value("VALID"))
                .andExpect(jsonPath("$.signatory").value("Test User"))
                .andExpect(jsonPath("$.algorithm").value("SHA-256 with RSA"))
                .andExpect(jsonPath("$.originalText").value(documentText));
        
        Map<String, String> verifyRequest = new HashMap<>();
        verifyRequest.put("text", documentText);
        verifyRequest.put("signature", signature);
        
        mockMvc.perform(post("/verify/text")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(verifyRequest)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.valid").value(true))
                .andExpect(jsonPath("$.status").value("VALID"))
                .andExpect(jsonPath("$.signatory").value("Test User"));
        
        // 4. Verificar que a assinatura foi persistida no banco
        Optional<Signature> savedSignature = signatureRepository.findById(signatureId);
        assertTrue(savedSignature.isPresent(), "Assinatura deve ser persistida no banco");
        assertEquals(documentText, savedSignature.get().getOriginalText());
        assertEquals(hash, savedSignature.get().getTextHash());
        assertEquals(signature, savedSignature.get().getSignature());
    }

    @Test
    void testCompleteDigitalSignatureFlow_InvalidSignature() throws Exception {
        String originalText = "Documento original para teste de adulteração";
        
        SignRequest signRequest = new SignRequest();
        signRequest.setText(originalText);
        
        MvcResult signResult = mockMvc.perform(post("/signatures/sign")
                .header("Authorization", "Bearer " + authToken)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(signRequest)))
                .andExpect(status().isCreated())
                .andReturn();
        
        String signResponse = signResult.getResponse().getContentAsString();
        JsonNode signJson = objectMapper.readTree(signResponse);
        UUID signatureId = UUID.fromString(signJson.get("signatureId").asText());
        String originalSignature = signJson.get("signature").asText();
        
        String tamperedSignature = originalSignature.substring(0, originalSignature.length() - 10) + "TAMPERED123";
        
        Map<String, String> verifyRequest = new HashMap<>();
        verifyRequest.put("text", originalText);
        verifyRequest.put("signature", tamperedSignature);
        
        mockMvc.perform(post("/verify/text")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(verifyRequest)))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.error").value("Assinatura não encontrada no sistema"));
        
        mockMvc.perform(get("/verify/" + signatureId))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.valid").value(true))
                .andExpect(jsonPath("$.status").value("VALID"));
    }

    @Test
    void testCompleteDigitalSignatureFlow_AlteredText() throws Exception {
        String originalText = "Contrato de prestação de serviços entre as partes.";
        
        SignRequest signRequest = new SignRequest();
        signRequest.setText(originalText);
        
        MvcResult signResult = mockMvc.perform(post("/signatures/sign")
                .header("Authorization", "Bearer " + authToken)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(signRequest)))
                .andExpect(status().isCreated())
                .andReturn();
        
        String signResponse = signResult.getResponse().getContentAsString();
        JsonNode signJson = objectMapper.readTree(signResponse);
        String signature = signJson.get("signature").asText();
        
        String alteredText = "Contrato de prestação de serviços ALTERADO entre as partes.";
        
        Map<String, String> verifyRequest = new HashMap<>();
        verifyRequest.put("text", alteredText);
        verifyRequest.put("signature", signature);
        
        mockMvc.perform(post("/verify/text")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(verifyRequest)))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.error").value("Assinatura não encontrada no sistema"));
    }

    @Test
    void testVerificationLogging() throws Exception {
        SignRequest signRequest = new SignRequest();
        signRequest.setText("Documento para teste de log");
        
        MvcResult signResult = mockMvc.perform(post("/signatures/sign")
                .header("Authorization", "Bearer " + authToken)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(signRequest)))
                .andExpect(status().isCreated())
                .andReturn();
        
        String signResponse = signResult.getResponse().getContentAsString();
        JsonNode signJson = objectMapper.readTree(signResponse);
        UUID signatureId = UUID.fromString(signJson.get("signatureId").asText());
        
        mockMvc.perform(get("/verify/" + signatureId))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.verificationCount").value(1));
        
        mockMvc.perform(get("/verify/" + signatureId))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.verificationCount").value(2));
        
        mockMvc.perform(get("/verify/" + signatureId))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.verificationCount").value(3));
    }
}