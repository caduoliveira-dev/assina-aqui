package com.assinaaqui.backend.repository;

import com.assinaaqui.backend.entity.Signature;
import com.assinaaqui.backend.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface SignatureRepository extends JpaRepository<Signature, UUID> {
    
    List<Signature> findByUser(User user);
    
    List<Signature> findByUserOrderByCreatedAtDesc(User user);
    
    Optional<Signature> findByTextHashAndSignature(String textHash, String signature);
}