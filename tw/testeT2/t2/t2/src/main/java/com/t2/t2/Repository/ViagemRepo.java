package com.t2.t2.Repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import com.t2.t2.entitys.Viagem;

public interface ViagemRepo extends  JpaRepository<Viagem, Integer> {


    Optional<Viagem> findByOrigemAndDestinoAndDataViagem(String origem, String destino,String data);
}
