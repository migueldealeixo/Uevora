package com.t2.t2.Services;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.t2.t2.Repository.ViagemRepo;
import com.t2.t2.entitys.Viagem;

@Service
public class ViagemService {
    @Autowired
    private ViagemRepo viagemRepo;

    public Viagem saveViagem(Viagem viagem){
        return viagemRepo.save(viagem);
    }

    public List<Viagem> getAll(){
        return viagemRepo.findAll();
    }
}
