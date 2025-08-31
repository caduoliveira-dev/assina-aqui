package com.assinaaqui.backend.repository;

import com.assinaaqui.backend.entity.Signature;
import com.assinaaqui.backend.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface SignatureRepository extends JpaRepository<Signature, Long> {
    
    List<Signature> findByUser(User user);
    
    List<Signature> findByUserOrderByCreatedAtDesc(User user);
}