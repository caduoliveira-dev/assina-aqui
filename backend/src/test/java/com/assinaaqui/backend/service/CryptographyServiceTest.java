package com.assinaaqui.backend.service;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
class CryptographyServiceTest {

    private CryptographyService cryptographyService;

    @BeforeEach
    void setUp() {
        cryptographyService = new CryptographyService();
    }

    @Test
    void testGenerateKeyPair() {
        Map<String, String> keyPair = cryptographyService.generateKeyPair();
        
        assertNotNull(keyPair);
        assertNotNull(keyPair.get("publicKey"));
        assertNotNull(keyPair.get("privateKey"));
        assertNotEquals(keyPair.get("publicKey"), keyPair.get("privateKey"));
    }

    @Test
    void testCalculateSHA256Hash() {
        String text = "Hello, World!";
        String hash = cryptographyService.calculateSHA256Hash(text);
        
        assertNotNull(hash);
        assertEquals(64, hash.length()); // SHA-256 produces 64 character hex string
        
        // Same text should produce same hash
        String hash2 = cryptographyService.calculateSHA256Hash(text);
        assertEquals(hash, hash2);
        
        // Different text should produce different hash
        String differentHash = cryptographyService.calculateSHA256Hash("Different text");
        assertNotEquals(hash, differentHash);
    }

    @Test
    void testSignAndVerifyHash_ValidSignature() {
        // Gerar par de chaves
        Map<String, String> keyPair = cryptographyService.generateKeyPair();
        String publicKey = keyPair.get("publicKey");
        String privateKey = keyPair.get("privateKey");
        
        // Texto de teste
        String text = "This is a test document for digital signature";
        String hash = cryptographyService.calculateSHA256Hash(text);
        
        // Assinar o hash
        String signature = cryptographyService.signHash(hash, privateKey);
        assertNotNull(signature);
        
        // Verificar assinatura (deve ser válida)
        boolean isValid = cryptographyService.verifySignature(hash, signature, publicKey);
        assertTrue(isValid, "Assinatura válida deve ser verificada como verdadeira");
    }

    @Test
    void testSignAndVerifyHash_InvalidSignature() {
        // Gerar par de chaves
        Map<String, String> keyPair = cryptographyService.generateKeyPair();
        String publicKey = keyPair.get("publicKey");
        String privateKey = keyPair.get("privateKey");
        
        // Texto de teste
        String text = "This is a test document for digital signature";
        String hash = cryptographyService.calculateSHA256Hash(text);
        
        // Assinar o hash
        String signature = cryptographyService.signHash(hash, privateKey);
        
        // Alterar a assinatura (simular adulteração)
        String tamperedSignature = signature.substring(0, signature.length() - 10) + "TAMPERED123";
        
        // Verificar assinatura alterada (deve ser inválida)
        boolean isValid = cryptographyService.verifySignature(hash, tamperedSignature, publicKey);
        assertFalse(isValid, "Assinatura alterada deve ser verificada como falsa");
    }

    @Test
    void testSignAndVerifyText_ValidSignature() {
        // Gerar par de chaves
        Map<String, String> keyPair = cryptographyService.generateKeyPair();
        String publicKey = keyPair.get("publicKey");
        String privateKey = keyPair.get("privateKey");
        
        // Texto de teste
        String text = "Document to be signed digitally";
        
        // Assinar texto completo
        Map<String, String> signResult = cryptographyService.signText(text, privateKey);
        assertNotNull(signResult.get("hash"));
        assertNotNull(signResult.get("signature"));
        assertEquals("SHA-256", signResult.get("algorithm"));
        
        // Verificar assinatura
        boolean isValid = cryptographyService.verifyText(text, signResult.get("signature"), publicKey);
        assertTrue(isValid, "Assinatura de texto válida deve ser verificada como verdadeira");
    }

    @Test
    void testSignAndVerifyText_InvalidText() {
        // Gerar par de chaves
        Map<String, String> keyPair = cryptographyService.generateKeyPair();
        String publicKey = keyPair.get("publicKey");
        String privateKey = keyPair.get("privateKey");
        
        // Texto original
        String originalText = "Original document content";
        
        // Assinar texto
        Map<String, String> signResult = cryptographyService.signText(originalText, privateKey);
        
        // Tentar verificar com texto alterado
        String alteredText = "Altered document content";
        boolean isValid = cryptographyService.verifyText(alteredText, signResult.get("signature"), publicKey);
        assertFalse(isValid, "Assinatura com texto alterado deve ser verificada como falsa");
    }

    @Test
    void testSignWithDifferentKeys() {
        // Gerar dois pares de chaves diferentes
        Map<String, String> keyPair1 = cryptographyService.generateKeyPair();
        Map<String, String> keyPair2 = cryptographyService.generateKeyPair();
        
        String text = "Test document";
        
        // Assinar com primeira chave privada
        Map<String, String> signResult = cryptographyService.signText(text, keyPair1.get("privateKey"));
        
        // Tentar verificar com segunda chave pública (deve falhar)
        boolean isValid = cryptographyService.verifyText(text, signResult.get("signature"), keyPair2.get("publicKey"));
        assertFalse(isValid, "Assinatura verificada com chave pública diferente deve ser falsa");
        
        // Verificar com chave correta (deve funcionar)
        boolean isValidCorrectKey = cryptographyService.verifyText(text, signResult.get("signature"), keyPair1.get("publicKey"));
        assertTrue(isValidCorrectKey, "Assinatura verificada com chave pública correta deve ser verdadeira");
    }
}