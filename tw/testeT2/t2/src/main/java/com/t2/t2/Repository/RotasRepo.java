package com.t2.t2.Repository;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.t2.t2.entitys.Rotas;
@Repository
public interface  RotasRepo  extends JpaRepository<Rotas,Integer> {
    
    Optional<Rotas> findByDestino(String destino);
    Optional<Rotas> findByOrigem(String origem); 
}
