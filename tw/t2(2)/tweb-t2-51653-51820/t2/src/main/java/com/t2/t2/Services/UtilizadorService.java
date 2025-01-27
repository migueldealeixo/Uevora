package com.t2.t2.Services;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import com.t2.t2.Exceptions.UserAlreadyExistsException;
import com.t2.t2.Repository.UtilizadorRepo;
import com.t2.t2.entitys.Utilizador;

import jakarta.transaction.Transactional;

@Service
@Transactional
public class UtilizadorService {
    @Autowired
    private UtilizadorRepo utilizadorRepo;

    @Autowired
    private PasswordEncoder passwordEncoder;

    public void registerUser(Utilizador utilizador) throws UserAlreadyExistsException {
        
        if(utilizadorRepo.findByEmail(utilizador.getEmail()).isPresent()){
            throw new UserAlreadyExistsException("O email ja se encontra registado");
        }
        String encodedPassword = passwordEncoder.encode(utilizador.getPassword());
        utilizador.setPassword(encodedPassword);

        utilizador.setActive(false);
        utilizador.setRating(0.0);
        System.out.println("User:" + utilizador);

        if(utilizador.getRole()== null){
            throw new IllegalArgumentException("Deves escolher o tipo de conta!");
        }
        
        utilizadorRepo.save(utilizador);

        System.out.print("Utilizado registado");
    }
    

    public boolean verificarUsername(String username){
        return utilizadorRepo.existsByUsername(username);
    }
    public boolean verificarEmail(String email){
        return utilizadorRepo.existsByEmail(email);
    }
    
    
    




    
    }
        
    
    
    



