package com.example.trabalho_t2.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.example.trabalho_t2.service.UserService;

@RestController
@RequestMapping("/login")
public class AuthController {

    @Autowired
    private UserService userService;

    @PostMapping("/register")
    public String registerUser(@RequestParam String username, 
                               @RequestParam String password, 
                               @RequestParam String roles) {
        try {
            userService.registerUser(username, password, roles);
            return "Utilizador registrado com sucesso!";
        } catch (IllegalArgumentException e) {
            return "Erro: " + e.getMessage();
        }
    }
}