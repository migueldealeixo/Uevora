package com.t2.t2.Controllers;

import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

import com.t2.t2.Repository.PedidoRepo;
import com.t2.t2.Repository.UtilizadorRepo;
import com.t2.t2.Repository.ViagemRepo;
import com.t2.t2.entitys.Pedido;
import com.t2.t2.entitys.Utilizador;
import com.t2.t2.entitys.Viagem;

@Controller
public class PassageiroControllers {
    
    @Autowired
    private UtilizadorRepo utilizadorRepo;

    @Autowired
    private ViagemRepo viagemRepo;

    @Autowired
    private PedidoRepo pedidoRepo;
    @GetMapping("/passageiro")
    public String passageiro(Model model, Authentication authentication) {
        try {
            String username = authentication.getName();
    
            List<Viagem> todasViagens = viagemRepo.findAll();
    
            List<Integer> viagensDoUtilizador = todasViagens.stream()
                    .filter(viagem -> viagem.getPassageiroUsername().equals(username))
                    .map(Viagem::getViagemID)
                    .toList();
    
            model.addAttribute("viagens", todasViagens);
            model.addAttribute("viagensDoUtilizador", viagensDoUtilizador); 
        } catch (Exception e) {
            e.printStackTrace();
            model.addAttribute("erro", "Erro ao carregar as viagens. Tente novamente mais tarde.");
        }
    
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
                return "redirect:/entrar";
            }
            Utilizador passageiro = userList.get();
            if(!"PASSAGEIRO".equals(passageiro.getRole())){
                return "redirect:/entrar";
            }

            Pedido novoPedido = new Pedido();
            novoPedido.setOrigemPedido(origem);
            novoPedido.setDestinoPedido(destino);
            novoPedido.setDataPedido(data);
            novoPedido.setUtilizador(passageiro);
            pedidoRepo.save(novoPedido);
            return "passageiro";
        }catch(Exception e){
            e.printStackTrace();
            return "passageiro";
        }
    }

  
    
    
    @PostMapping("/removerPedido")
    public String removerPedido(
        @RequestParam String origem,
        @RequestParam String destino,
        @RequestParam String data,
        Model model) {
    
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        String username = auth.getName();
        Optional<Utilizador> userList = utilizadorRepo.findByUsername(username);
        if (userList.isEmpty()) {
            model.addAttribute("errorMessage", "Utilizador não encontrado!");
            return "redirect:/passageiro";  
        }
        Utilizador passageiro = userList.get();
        Optional<Pedido> optionalPedido = pedidoRepo.findByOrigemPedidoAndDestinoPedidoAndDataPedido(origem, destino, data).stream().findFirst();
        if (optionalPedido.isEmpty()) {
            model.addAttribute("errorMessage", "Pedido não encontrado!");
            return "redirect:/passageiro";  
        }
        Pedido pedido = optionalPedido.get();
        if (!pedido.getUtilizador().equals(passageiro)) {
            model.addAttribute("errorMessage", "O pedido apenas pode ser eliminado por quem o criou!");
            return "redirect:/passageiro";  
        }
        pedidoRepo.delete(pedido);
        model.addAttribute("successMessage", "Pedido eliminado com sucesso!");
        return "redirect:/passageiro";  
    }
    

@PostMapping("/procurarCondutor")
public String procurarCondutor(@RequestParam String username, Model model) {
    Optional<Utilizador> condutorOptional = utilizadorRepo.findByUsername(username);

    if (condutorOptional.isEmpty()) {
        model.addAttribute("error", "Condutor não encontrado");
        return "passageiro"; 
    }

    Utilizador condutor = condutorOptional.get();

    if (!"CONDUTOR".equals(condutor.getRole())) {
        model.addAttribute("error", "Utilizador encontrado não é um condutor");
        return "passageiro"; 
    }

    model.addAttribute("username", condutor.getUsername());
    model.addAttribute("rating", condutor.getRating()); 
    return "perfilcondutor"; 

}


@PostMapping("/join")
public String joinViagem(@RequestParam("viagemID") int viagemID, Authentication auth){
    try{

        String username = auth.getName();

        Optional<Viagem> viagemOpt = viagemRepo.findById(viagemID);

        if(viagemOpt.isPresent()){
            Viagem viagemOriginal = viagemOpt.get();

            Viagem novaViagem = new Viagem();
            novaViagem.setCondutorUsername(viagemOriginal.getCondutorUsername());
            novaViagem.setOrigem(viagemOriginal.getOrigem());
            novaViagem.setDestino(viagemOriginal.getDestino());
            novaViagem.setDataViagem(viagemOriginal.getDataViagem());
            novaViagem.setLugares(viagemOriginal.getLugares() -1);
            novaViagem.setPassageiroUsername(username); 

            viagemRepo.save(novaViagem);

            return "redirect:/passageiro";
        }else{
            return "redirect:/passageiro?erro=Viagem não encontrada";
        }
        }catch(Exception e){
            e.printStackTrace();
            return "redirect:/passageiro?erro=Erro ao juntar-se à viagem";
    }
}

@PostMapping("/leave")
public String leaveViagem(@RequestParam("viagemID") int viagemID, Authentication authentication) {
    try {
        String username = authentication.getName();

        Optional<Viagem> viagemOptional = viagemRepo.findById(viagemID);

        if (viagemOptional.isPresent()) {
            Viagem viagem = viagemOptional.get();

            if (viagem.getPassageiroUsername().equals(username)) {
                // Remover a instância da viagem
                viagemRepo.delete(viagem);
                return "redirect:/passageiro?sucesso=Você abandonou a viagem com sucesso.";
            } else {
                return "redirect:/passageiro?erro=Você não pode abandonar esta viagem.";
            }
        } else {
            return "redirect:/passageiro?erro=Viagem não encontrada.";
        }

    } catch (Exception e) {
        e.printStackTrace();
        return "redirect:/passageiro?erro=Erro ao abandonar a viagem.";
    }
}





}