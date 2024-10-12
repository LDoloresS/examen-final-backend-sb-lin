package com.codigo.examen_ss.repository;

import com.codigo.examen_ss.entity.UserEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface UserRepository extends JpaRepository<UserEntity, Long> {
    boolean existsByEmail(String email);

    Optional<UserEntity> findByNumDoc(String numDoc);

    List<UserEntity> findByIsEnabled(boolean estado);

    Optional<UserEntity> findByEmail(String email);
}
