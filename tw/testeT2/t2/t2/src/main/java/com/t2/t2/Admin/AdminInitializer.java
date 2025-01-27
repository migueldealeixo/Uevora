package com.t2.t2.Admin;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.crypto.password.PasswordEncoder;

import com.t2.t2.entitys.Admin;

@Configuration
public class AdminInitializer {

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Bean
    public CommandLineRunner createDefaultAdmin(AdminRepo adminRepository) {
        return args -> {
            String defaultUsername = "admin";
            String defaultPassword = "admin";

            if (adminRepository.findById(defaultUsername).isEmpty()) {
                Admin admin = new Admin();
                admin.setUsername(defaultUsername);
                admin.setPassword(passwordEncoder.encode(defaultPassword));
                admin.setRole("ADMIN");

                adminRepository.save(admin);
                System.out.println("Admin padrão criado: " + defaultUsername);
            } else {
                System.out.println("Admin padrão já existe: " + defaultUsername);
            }
        };
    }
}
