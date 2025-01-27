package com.t2.t2.Repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.t2.t2.entitys.Viagem;

public interface ViagemRepo extends  JpaRepository<Viagem, Integer> {
    
}
