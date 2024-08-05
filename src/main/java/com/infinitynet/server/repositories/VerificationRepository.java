package com.infinitynet.server.repositories;

import com.infinitynet.server.entities.User;
import com.infinitynet.server.entities.Verification;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface VerificationRepository extends JpaRepository<Verification, Long> {

    Optional<Verification> findByToken(String token);

    Optional<Verification> findByCode(String code);

    Optional<Verification> findByUserAndVerificationType(User user, Verification.VerificationType type);

}
