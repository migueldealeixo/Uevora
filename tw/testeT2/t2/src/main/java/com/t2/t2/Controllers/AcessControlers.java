package com.t2.t2.Controllers;

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

import com.t2.t2.Repository.PedidoRepo;
import com.t2.t2.Repository.RotasRepo;
import com.t2.t2.Repository.UtilizadorRepo;
import com.t2.t2.entitys.Pedido;
import com.t2.t2.entitys.Rotas;
import com.t2.t2.entitys.Utilizador;


@Controller
public class AcessControlers {

    @Autowired
    private UtilizadorRepo utilizadorRepo;

    @Autowired
    private RotasRepo locaisRepo;

    @Autowired
    private PedidoRepo pedidoRepo;

    @Autowired
    private PasswordEncoder encoder;





    @GetMapping("/entrar")
    public String loginPage(Model model, @RequestParam(value="error", required=false)String error,@RequestParam(value = "logout", required = false) String logout){
        if(error != null){
            model.addAttribute("erro","Username ou Password invalidos");
            return "entrar";
        }
        if(logout!= null){
            model.addAttribute("msg", "Sessão encerrada com sucesso");
            return "entrar";
        }

        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        if (auth != null && auth.isAuthenticated() && !"anonymousUser".equals(auth.getPrincipal())) {
            String username = auth.getName();
    
            // Procurar o utilizador no repositório
            Optional<Utilizador> userOptional = utilizadorRepo.findByUsername(username);
            if (userOptional.isPresent()) {
                Utilizador utilizador = userOptional.get();
    
                // Redirecionar com base no role
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

    @GetMapping("/condutor")
    public String driver(){
        return "condutor";
    }

  @PostMapping("/condutor")
public String addRota(
    @RequestParam String origem,
    @RequestParam String destino,
    @RequestParam int lugaresDisponiveis,
    @RequestParam String dataRota
    ) {
    try {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        String username = auth.getName();
        Optional<Utilizador> userList = utilizadorRepo.findByUsername(username);
        if(userList.isEmpty()){
            return "condutor";
        }

        Utilizador condutor = userList.get();
        if(!"CONDUTOR".equals(condutor.getRole())){
            return "condutor";
        }

        if(lugaresDisponiveis < 1){
            return "condutor";
        }
       
        Rotas novaRota = new Rotas();
        novaRota.setOrigem(origem);
        novaRota.setDestino(destino);
        novaRota.setLugaresDisponiveis(lugaresDisponiveis);
        novaRota.setDataRotas(dataRota);
        novaRota.setCondutor(condutor);

        locaisRepo.save(novaRota);
        return "redirect:/condutor";
    } catch (Exception e) {
        return "condutor";
    }
}
    @GetMapping("/passageiro")
    public String passageiro(){
        return "passageiro";
    }



    @PostMapping("/passageiro")
    public String addPedido(
    @RequestParam String origem,
    @RequestParam String destino,
    @RequestParam String data){

        try{
            Authentication auth = SecurityContextHolder.getContext().getAuthentication();
            String username = auth.getName();
            Optional<Utilizador> userList = utilizadorRepo.findByUsername(username);
            if(userList.isEmpty()){
                return "passageiro";
            }
            Utilizador passageiro = userList.get();
            if(!"PASSAGEIRO".equals(passageiro.getRole())){
                return "entrar";
            }

            Pedido novoPedido = new Pedido();
            novoPedido.setOrigemPedido(origem);
            novoPedido.setDestinoPedido(destino);
            novoPedido.setDataPedido(data);
            novoPedido.setUtilizador(passageiro);
            pedidoRepo.save(novoPedido);
            return "redirect:/index";
        }catch(Exception e){
            e.printStackTrace();
            return "passageiro";
        }
    }


    @GetMapping("/newuser")
    public String newUser(){
        return "newuser";
    }

    @GetMapping("/static/css")
    public String getCss(){
        return "static/css";
    }

}
