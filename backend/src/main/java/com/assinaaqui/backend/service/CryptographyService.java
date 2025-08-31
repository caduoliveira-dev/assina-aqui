package com.assinaaqui.backend.service;

import org.springframework.stereotype.Service;

import java.nio.charset.StandardCharsets;
import java.security.*;
import java.security.spec.PKCS8EncodedKeySpec;
import java.security.spec.X509EncodedKeySpec;
import java.util.Base64;
import java.util.HashMap;
import java.util.Map;

@Service
public class CryptographyService {

    private static final String ALGORITHM = "RSA";
    private static final String SIGNATURE_ALGORITHM = "SHA256withRSA";
    private static final String HASH_ALGORITHM = "SHA-256";
    private static final int KEY_SIZE = 2048;

    /**
     * Gera um par de chaves RSA (pública e privada)
     * @return Map contendo as chaves pública e privada em formato Base64
     */
    public Map<String, String> generateKeyPair() {
        try {
            KeyPairGenerator keyPairGenerator = KeyPairGenerator.getInstance(ALGORITHM);
            keyPairGenerator.initialize(KEY_SIZE);
            KeyPair keyPair = keyPairGenerator.generateKeyPair();

            String publicKey = Base64.getEncoder().encodeToString(keyPair.getPublic().getEncoded());
            String privateKey = Base64.getEncoder().encodeToString(keyPair.getPrivate().getEncoded());

            Map<String, String> keys = new HashMap<>();
            keys.put("publicKey", publicKey);
            keys.put("privateKey", privateKey);

            return keys;
        } catch (NoSuchAlgorithmException e) {
            throw new RuntimeException("Erro ao gerar par de chaves: " + e.getMessage(), e);
        }
    }

    /**
     * Calcula o hash SHA-256 de um texto
     * @param text Texto para calcular o hash
     * @return Hash SHA-256 em formato hexadecimal
     */
    public String calculateSHA256Hash(String text) {
        try {
            MessageDigest digest = MessageDigest.getInstance(HASH_ALGORITHM);
            byte[] hashBytes = digest.digest(text.getBytes(StandardCharsets.UTF_8));
            
            StringBuilder hexString = new StringBuilder();
            for (byte b : hashBytes) {
                String hex = Integer.toHexString(0xff & b);
                if (hex.length() == 1) {
                    hexString.append('0');
                }
                hexString.append(hex);
            }
            return hexString.toString();
        } catch (NoSuchAlgorithmException e) {
            throw new RuntimeException("Erro ao calcular hash SHA-256: " + e.getMessage(), e);
        }
    }

    /**
     * Assina um hash usando a chave privada
     * @param hash Hash a ser assinado
     * @param privateKeyBase64 Chave privada em formato Base64
     * @return Assinatura em formato Base64
     */
    public String signHash(String hash, String privateKeyBase64) {
        try {
            byte[] privateKeyBytes = Base64.getDecoder().decode(privateKeyBase64);
            PKCS8EncodedKeySpec keySpec = new PKCS8EncodedKeySpec(privateKeyBytes);
            KeyFactory keyFactory = KeyFactory.getInstance(ALGORITHM);
            PrivateKey privateKey = keyFactory.generatePrivate(keySpec);

            Signature signature = Signature.getInstance(SIGNATURE_ALGORITHM);
            signature.initSign(privateKey);
            signature.update(hash.getBytes(StandardCharsets.UTF_8));

            byte[] signatureBytes = signature.sign();
            return Base64.getEncoder().encodeToString(signatureBytes);
        } catch (Exception e) {
            throw new RuntimeException("Erro ao assinar hash: " + e.getMessage(), e);
        }
    }

    /**
     * Verifica uma assinatura usando a chave pública
     * @param hash Hash original
     * @param signatureBase64 Assinatura em formato Base64
     * @param publicKeyBase64 Chave pública em formato Base64
     * @return true se a assinatura for válida, false caso contrário
     */
    public boolean verifySignature(String hash, String signatureBase64, String publicKeyBase64) {
        try {
            byte[] publicKeyBytes = Base64.getDecoder().decode(publicKeyBase64);
            X509EncodedKeySpec keySpec = new X509EncodedKeySpec(publicKeyBytes);
            KeyFactory keyFactory = KeyFactory.getInstance(ALGORITHM);
            PublicKey publicKey = keyFactory.generatePublic(keySpec);

            Signature signature = Signature.getInstance(SIGNATURE_ALGORITHM);
            signature.initVerify(publicKey);
            signature.update(hash.getBytes(StandardCharsets.UTF_8));

            byte[] signatureBytes = Base64.getDecoder().decode(signatureBase64);
            return signature.verify(signatureBytes);
        } catch (Exception e) {
            throw new RuntimeException("Erro ao verificar assinatura: " + e.getMessage(), e);
        }
    }

    /**
     * Assina um texto completo (calcula o hash e assina)
     * @param text Texto a ser assinado
     * @param privateKeyBase64 Chave privada em formato Base64
     * @return Map contendo o hash e a assinatura
     */
    public Map<String, String> signText(String text, String privateKeyBase64) {
        String hash = calculateSHA256Hash(text);
        String signature = signHash(hash, privateKeyBase64);

        Map<String, String> result = new HashMap<>();
        result.put("hash", hash);
        result.put("signature", signature);
        result.put("algorithm", HASH_ALGORITHM);

        return result;
    }

    /**
     * Verifica um texto assinado
     * @param originalText Texto original
     * @param signatureBase64 Assinatura em formato Base64
     * @param publicKeyBase64 Chave pública em formato Base64
     * @return true se a assinatura for válida, false caso contrário
     */
    public boolean verifyText(String originalText, String signatureBase64, String publicKeyBase64) {
        String hash = calculateSHA256Hash(originalText);
        return verifySignature(hash, signatureBase64, publicKeyBase64);
    }
}