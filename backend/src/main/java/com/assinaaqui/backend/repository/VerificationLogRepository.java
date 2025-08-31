package com.assinaaqui.backend.repository;

import com.assinaaqui.backend.entity.Signature;
import com.assinaaqui.backend.entity.VerificationLog;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface VerificationLogRepository extends JpaRepository<VerificationLog, Long> {
    
    List<VerificationLog> findBySignature(Signature signature);
    
    List<VerificationLog> findBySignatureOrderByVerifiedAtDesc(Signature signature);
    
    long countBySignature(Signature signature);
}