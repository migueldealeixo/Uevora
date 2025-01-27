package com.t2.t2.Security;

import java.util.Arrays;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.stereotype.Service;

import com.t2.t2.Admin.AdminRepo;
import com.t2.t2.Exceptions.UsernameNotFoundException;
import com.t2.t2.Repository.UtilizadorRepo;
import com.t2.t2.entitys.Admin;
import com.t2.t2.entitys.Utilizador;

@Service
public class UserAuthService implements UserDetailsService {
    
    @Autowired
    private UtilizadorRepo utilizadorRepo;

    @Autowired
    private AdminRepo adminRepo;

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        // Primeiro verifica se é um Admin
        Admin admin = adminRepo.findById(username).orElse(null);
        if (admin != null) {
            GrantedAuthority grantedAuthority = new SimpleGrantedAuthority("ROLE_" + admin.getRole());
            return new User(
                admin.getUsername(),
                admin.getPassword(),
                Arrays.asList(grantedAuthority)
            );
        }

        Utilizador utilizador = utilizadorRepo.findByUsername(username)
                .orElseThrow(() -> new UsernameNotFoundException("Utilizador não encontrado"));

        GrantedAuthority grantedAuthority = new SimpleGrantedAuthority("ROLE_" + utilizador.getRole());

        return new User(
            utilizador.getUsername(),
            utilizador.getPassword(),
            Arrays.asList(grantedAuthority)
        );
    }
}
