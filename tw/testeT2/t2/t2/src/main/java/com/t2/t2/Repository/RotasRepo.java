package com.t2.t2.Repository;
import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.t2.t2.entitys.Rotas;
import com.t2.t2.entitys.Utilizador;

@Repository
public interface  RotasRepo  extends JpaRepository<Rotas,Integer> {
    List<Rotas> findByCondutor(Utilizador condutor);
    List<Rotas> findByOrigemAndDestinoAndData(String origem,String destino,String data);
}
