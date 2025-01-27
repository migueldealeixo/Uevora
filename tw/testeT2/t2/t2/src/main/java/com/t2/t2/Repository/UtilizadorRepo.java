package com.t2.t2.Repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import com.t2.t2.entitys.Utilizador;

public interface UtilizadorRepo extends JpaRepository<Utilizador, Integer> {
    Optional<Utilizador> findByEmail(String email);
    Optional<Utilizador> findByUsername(String username);

    boolean existsByUsername(String username);
    boolean existsByEmail(String email);

}
