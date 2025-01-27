package com.t2.t2.Admin;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import com.t2.t2.entitys.Admin;

@Service
public class AdminLoader {
    
    @Autowired
    private PasswordEncoder encoder;
    
    @Autowired
    private AdminRepo adminRepo;
    
    public Admin load(){
        Admin admin = new Admin();
        admin.setUsername("ADMIN");
        admin.setPassword(encoder.encode("ADMIN"));
        admin.setRole("ROLE_ADMIN");
        return adminRepo.save(admin);
    }





}
