package com.assinaaqui.backend.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import org.hibernate.annotations.CreationTimestamp;

import java.time.LocalDateTime;

@Entity
@Table(name = "verification_logs")
public class VerificationLog {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotNull(message = "Assinatura é obrigatória")
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "signature_id", nullable = false)
    private Signature signature;

    @CreationTimestamp
    @Column(nullable = false, updatable = false)
    private LocalDateTime verifiedAt;

    @Column(length = 45)
    private String ipAddress;

    @Column(nullable = false)
    private Boolean isValid;

    @Column(length = 500)
    private String userAgent;

    // Constructors
    public VerificationLog() {}

    public VerificationLog(Signature signature, String ipAddress, Boolean isValid, String userAgent) {
        this.signature = signature;
        this.ipAddress = ipAddress;
        this.isValid = isValid;
        this.userAgent = userAgent;
    }

    // Getters and Setters
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Signature getSignature() {
        return signature;
    }

    public void setSignature(Signature signature) {
        this.signature = signature;
    }

    public LocalDateTime getVerifiedAt() {
        return verifiedAt;
    }

    public void setVerifiedAt(LocalDateTime verifiedAt) {
        this.verifiedAt = verifiedAt;
    }

    public String getIpAddress() {
        return ipAddress;
    }

    public void setIpAddress(String ipAddress) {
        this.ipAddress = ipAddress;
    }

    public Boolean getIsValid() {
        return isValid;
    }

    public void setIsValid(Boolean isValid) {
        this.isValid = isValid;
    }

    public String getUserAgent() {
        return userAgent;
    }

    public void setUserAgent(String userAgent) {
        this.userAgent = userAgent;
    }
}