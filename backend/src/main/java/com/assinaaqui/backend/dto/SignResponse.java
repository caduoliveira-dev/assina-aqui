package com.assinaaqui.backend.dto;

import java.time.LocalDateTime;

public class SignResponse {

    private Long signatureId;
    private String hash;
    private String signature;
    private String algorithm;
    private LocalDateTime createdAt;

    // Constructors
    public SignResponse() {}

    public SignResponse(Long signatureId, String hash, String signature, String algorithm, LocalDateTime createdAt) {
        this.signatureId = signatureId;
        this.hash = hash;
        this.signature = signature;
        this.algorithm = algorithm;
        this.createdAt = createdAt;
    }

    // Getters and Setters
    public Long getSignatureId() {
        return signatureId;
    }

    public void setSignatureId(Long signatureId) {
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