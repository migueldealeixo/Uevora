package com.t2.t2.Controllers;

import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

import com.t2.t2.Admin.AdminRepo;
import com.t2.t2.Repository.RotasRepo;
import com.t2.t2.Repository.UtilizadorRepo;
import com.t2.t2.entitys.Admin;
import com.t2.t2.entitys.Rotas;
import com.t2.t2.entitys.Utilizador;

@Controller
public class AcessControlers {

    @Autowired
    private UtilizadorRepo utilizadorRepo;
  
    @Autowired
    private AdminRepo adminRepo;

    @Autowired
    private RotasRepo rotasRepo;

    @Autowired
    private PasswordEncoder encoder;


    
    @GetMapping("/entrar")
    public String loginPage(Model model,
        @RequestParam(value="error", required=false) String error,
        @RequestParam(value="logout", required=false) String logout) {
    
        if (error != null) {
            model.addAttribute("erro", "Username ou Password inválidos");
        }
        if (logout != null) {
            model.addAttribute("msg", "Sessão encerrada com sucesso");
        }
    
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        if (auth != null && auth.isAuthenticated() && !"anonymousUser".equals(auth.getPrincipal())) {
            String username = auth.getName();
    
            Optional<Admin> adminOptional = adminRepo.findById(username);
            if (adminOptional.isPresent()) {
                return "redirect:/admin";
            }
    
            Optional<Utilizador> userOptional = utilizadorRepo.findByUsername(username);
            if (userOptional.isPresent()) {
                Utilizador utilizador = userOptional.get();
                
                if (!utilizador.isActive()) {
                    model.addAttribute("erro", "Sua conta está inativa. Contate o administrador.");  
                }
    
                if ("ROLE_CONDUTOR".equals(utilizador.getRole())) {
                    return "redirect:/condutor";
                }
            }
        }
        return "entrar";  
    }
    
    @GetMapping("/index")
    public String indexPage(){
        return "index";
    }
    @GetMapping("/newuser")
    public String newuserPage(Model model){

    try {
        // Buscar todas as rotas no repositório
        List<Rotas> rotasTodas = rotasRepo.findAll();
        model.addAttribute("rotas", rotasTodas);
        return "newuser"; // Retorna a página com os resultados
    } catch (Exception e) {
        e.printStackTrace();
        model.addAttribute("errorMSG", "Erro ao buscar rotas: " + e.getMessage());
        return "newuser"; // Retorna a mesma página, mesmo em caso de erro
    }   
    }

    @GetMapping("/register")
    public String register(){
        return "register";
    }
    @PostMapping("/register")
    public String registerUser(
            @RequestParam String username,
            @RequestParam String email,
            @RequestParam String password1,
            @RequestParam String password2,
            @RequestParam String role, 
            Model model) {

        if (!password1.equals(password2)) {
            model.addAttribute("error", "As senhas não coincidem.");
            return "register";  
        }

        Utilizador newUser = new Utilizador();
        newUser.setUsername(username);
        newUser.setEmail(email);
        newUser.setPassword(encoder.encode(password1));  
        newUser.setRating(0.0);
        newUser.setActive(false);

        if ("PASSAGEIRO".equals(role)) {
            newUser.setRole("PASSAGEIRO"); 
        } else if ("CONDUTOR".equals(role)) {
            newUser.setRole("CONDUTOR");  
        } else {
            model.addAttribute("error", "Role inválido.");
            return "register";
        }
        utilizadorRepo.save(newUser);
        model.addAttribute("success", "Conta criada com sucesso!");

        return "entrar";  
    }

   

   

   
}

