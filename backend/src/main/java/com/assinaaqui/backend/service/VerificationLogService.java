package com.assinaaqui.backend.service;

import com.assinaaqui.backend.entity.Signature;
import com.assinaaqui.backend.entity.VerificationLog;
import com.assinaaqui.backend.repository.VerificationLogRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class VerificationLogService {

    @Autowired
    private VerificationLogRepository verificationLogRepository;

    public VerificationLog logVerification(Signature signature, String ipAddress, String userAgent, boolean isValid) {
        VerificationLog log = new VerificationLog();
        log.setSignature(signature);
        log.setIpAddress(ipAddress);
        log.setUserAgent(userAgent);
        log.setIsValid(isValid);
        
        return verificationLogRepository.save(log);
    }

    public List<VerificationLog> findBySignature(Signature signature) {
        return verificationLogRepository.findBySignature(signature);
    }

    public List<VerificationLog> findBySignatureOrderByVerifiedAtDesc(Signature signature) {
        return verificationLogRepository.findBySignatureOrderByVerifiedAtDesc(signature);
    }

    public long countBySignature(Signature signature) {
        return verificationLogRepository.countBySignature(signature);
    }
}