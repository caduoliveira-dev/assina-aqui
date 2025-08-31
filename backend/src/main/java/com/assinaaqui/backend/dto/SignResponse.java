package com.assinaaqui.backend.dto;

import java.time.LocalDateTime;
import java.util.UUID;

public class SignResponse {

    private UUID signatureId;
    private String hash;
    private String signature;
    private String algorithm;
    private LocalDateTime createdAt;

    // Constructors
    public SignResponse() {}

    public SignResponse(UUID signatureId, String hash, String signature, String algorithm, LocalDateTime createdAt) {
        this.signatureId = signatureId;
        this.hash = hash;
        this.signature = signature;
        this.algorithm = algorithm;
        this.createdAt = createdAt;
    }

    // Getters and Setters
    public UUID getSignatureId() {
        return signatureId;
    }

    public void setSignatureId(UUID signatureId) {
        this.signatureId = signatureId;
    }

    public String getHash() {
        return hash;
    }

    public void setHash(String hash) {
        this.hash = hash;
    }

    public String getSignature() {
        return signature;
    }

    public void setSignature(String signature) {
        this.signature = signature;
    }

    public String getAlgorithm() {
        return algorithm;
    }

    public void setAlgorithm(String algorithm) {
        this.algorithm = algorithm;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }
}