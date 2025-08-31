package com.assinaaqui.backend.service;

import com.assinaaqui.backend.entity.Signature;
import com.assinaaqui.backend.entity.User;
import com.assinaaqui.backend.repository.SignatureRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Service
public class SignatureService {

    @Autowired
    private SignatureRepository signatureRepository;

    @Autowired
    private CryptographyService cryptographyService;

    public Signature signText(User user, String text) {
        try {
            // Calcular hash SHA-256 do texto
            String textHash = cryptographyService.calculateSHA256Hash(text);
            
            // Assinar o hash com a chave privada do usuário
            String signature = cryptographyService.signHash(textHash, user.getPrivateKey());
            
            // Criar e salvar a assinatura
            Signature signatureEntity = new Signature();
            signatureEntity.setUser(user);
            signatureEntity.setOriginalText(text);
            signatureEntity.setTextHash(textHash);
            signatureEntity.setSignature(signature);
            signatureEntity.setAlgorithm("SHA-256 with RSA");
            
            return signatureRepository.save(signatureEntity);
        } catch (Exception e) {
            throw new RuntimeException("Erro ao assinar texto: " + e.getMessage(), e);
        }
    }

    public Optional<Signature> findById(UUID id) {
        return signatureRepository.findById(id);
    }

    public List<Signature> findByUser(User user) {
        return signatureRepository.findByUser(user);
    }

    public List<Signature> findByUserOrderByCreatedAtDesc(User user) {
        return signatureRepository.findByUserOrderByCreatedAtDesc(user);
    }

    public boolean verifySignature(Signature signature) {
        try {
            return cryptographyService.verifySignature(
                signature.getTextHash(),
                signature.getSignature(),
                signature.getUser().getPublicKey()
            );
        } catch (Exception e) {
            return false;
        }
    }

    public boolean verifySignatureByText(String originalText, String signatureValue, String publicKey) {
        try {
            String textHash = cryptographyService.calculateSHA256Hash(originalText);
            return cryptographyService.verifySignature(textHash, signatureValue, publicKey);
        } catch (Exception e) {
            return false;
        }
    }
}