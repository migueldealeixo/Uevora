package com.t2.t2.Services;

import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.t2.t2.Repository.RotasRepo;
import com.t2.t2.entitys.Rotas;

@Service
public class RotasService {
    
    @Autowired
    private RotasRepo rotasRepo;

    public Rotas saveLocal(Rotas local){
        return rotasRepo.save(local);
    }

    public Optional<Rotas> findById(int localID){
        return rotasRepo.findById(localID);
    }

    public List<Rotas> findAll(){
        return rotasRepo.findAll();
    }

    public void deleteById(int localID){
        if(rotasRepo.existsById(localID)){
            rotasRepo.deleteById(localID);
        }
        else{
            throw new RuntimeException("Local não encontrado");
        }
    }


    public Optional<Rotas> findByDestino(String destino){
        return rotasRepo.findByDestino(destino);
    }
    public Optional<Rotas> findByOrigem(String origem){
        return rotasRepo.findByOrigem(origem);
    }
}
