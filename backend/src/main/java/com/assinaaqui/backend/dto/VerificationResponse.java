package com.assinaaqui.backend.dto;

import java.time.LocalDateTime;

public class VerificationResponse {

    private Long signatureId;
    private boolean isValid;
    private String status; // "VÁLIDA" ou "INVÁLIDA"
    private String signatory;
    private String algorithm;
    private LocalDateTime signedAt;
    private String originalText;
    private long verificationCount;

    // Constructors
    public VerificationResponse() {}

    public VerificationResponse(Long signatureId, boolean isValid, String signatory, 
                              String algorithm, LocalDateTime signedAt, String originalText, 
                              long verificationCount) {
        this.signatureId = signatureId;
        this.isValid = isValid;
        this.status = isValid ? "VÁLIDA" : "INVÁLIDA";
        this.signatory = signatory;
        this.algorithm = algorithm;
        this.signedAt = signedAt;
        this.originalText = originalText;
        this.verificationCount = verificationCount;
    }

    // Getters and Setters
    public Long getSignatureId() {
        return signatureId;
    }

    public void setSignatureId(Long signatureId) {
        this.signatureId = signatureId;
    }

    public boolean isValid() {
        return isValid;
    }

    public void setValid(boolean valid) {
        isValid = valid;
        this.status = valid ? "VÁLIDA" : "INVÁLIDA";
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getSignatory() {
        return signatory;
    }

    public void setSignatory(String signatory) {
        this.signatory = signatory;
    }

    public String getAlgorithm() {
        return algorithm;
    }

    public void setAlgorithm(String algorithm) {
        this.algorithm = algorithm;
    }

    public LocalDateTime getSignedAt() {
        return signedAt;
    }

    public void setSignedAt(LocalDateTime signedAt) {
        this.signedAt = signedAt;
    }

    public String getOriginalText() {
        return originalText;
    }

    public void setOriginalText(String originalText) {
        this.originalText = originalText;
    }

    public long getVerificationCount() {
        return verificationCount;
    }

    public void setVerificationCount(long verificationCount) {
        this.verificationCount = verificationCount;
    }
}