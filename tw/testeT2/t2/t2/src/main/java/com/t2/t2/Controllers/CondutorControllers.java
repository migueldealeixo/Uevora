package com.t2.t2.Controllers;

import java.util.ArrayList;
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

import com.t2.t2.Repository.PedidoRepo;
import com.t2.t2.Repository.RotasRepo;
import com.t2.t2.Repository.UtilizadorRepo;
import com.t2.t2.Repository.ViagemRepo;
import com.t2.t2.entitys.Pedido;
import com.t2.t2.entitys.Rotas;
import com.t2.t2.entitys.Utilizador;
import com.t2.t2.entitys.Viagem;


@Controller
public class CondutorControllers {

     @Autowired
    private UtilizadorRepo utilizadorRepo;

    @Autowired
    private RotasRepo locaisRepo;

    @Autowired
    private PedidoRepo pedidoRepo;

    @Autowired
    private ViagemRepo viagemRepo;

    @Autowired
    private PasswordEncoder encoder;

 @GetMapping("/condutor")
    public String driver(Model model){
        try{
            Authentication auth = SecurityContextHolder.getContext().getAuthentication();
            String username = auth.getName();
            Optional<Utilizador> userList = utilizadorRepo.findByUsername(username);
            if(userList.isEmpty()){
                return "redirect:/entrar";
            }
            Utilizador condutor = userList.get();

                List<Rotas> rotasDoCondutor = locaisRepo.findByCondutor(condutor);
                List<Pedido> pedidosCorrespondentes = new ArrayList<>();

                for(Rotas rota : rotasDoCondutor){
                    List<Pedido> pedidos = pedidoRepo.findByOrigemPedidoAndDestinoPedido(rota.getOrigem(), rota.getDestino());
                    pedidosCorrespondentes.addAll(pedidos);
                }

                model.addAttribute("pedidos", pedidosCorrespondentes);
        }catch(Exception e){
            e.printStackTrace();
        }
        return "condutor";
    }


  @PostMapping("/condutor")
    public String addRota(
    @RequestParam String origem,
    @RequestParam String destino,
    @RequestParam int lugaresDisponiveis,
    @RequestParam String dataPedido,
    Model model) {
    try {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        String username = auth.getName();
        Optional<Utilizador> userList = utilizadorRepo.findByUsername(username);
        if(userList.isEmpty()){
            return "condutor";
        }

        Utilizador condutor = userList.get();
        if(!"CONDUTOR".equals(condutor.getRole())){
            model.addAttribute("error1", "Apenas condutores podem registar rotas! ");
            return "entrar";
        }

        if(lugaresDisponiveis < 1){
            model.addAttribute("error2", "Deve haver pelo menos um lugar vago!");
        }
       
        Rotas novaRota = new Rotas();
        novaRota.setOrigem(origem);
        novaRota.setDestino(destino);
        novaRota.setLugaresDisponiveis(lugaresDisponiveis);
        novaRota.setCondutor(condutor);
        novaRota.setData(dataPedido);

        locaisRepo.save(novaRota);
        model.addAttribute("sucess", "Rota registada!");
        return "redirect:/condutor";
    } catch (Exception e) {
        return "condutor";
    }
}

@PostMapping("/aceitarViagem")
    @SuppressWarnings("CallToPrintStackTrace")
public String aceitarViagem(
    @RequestParam("pedidoID") int pedidoID, 
    Model model
) {
    try {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        String condutorUsername = auth.getName();

        Optional<Utilizador> optionalCondutor = utilizadorRepo.findByUsername(condutorUsername);
        if (optionalCondutor.isEmpty()) {
            return "redirect:/entrar"; 
        }

        Optional<Pedido> optionalPedido = pedidoRepo.findById(pedidoID);
        if (optionalPedido.isEmpty()) {
            return "redirect:/condutor"; 
        }

        Pedido pedido = optionalPedido.get();

        Viagem novaViagem = new Viagem();
        novaViagem.setCondutorUsername(condutorUsername);
        novaViagem.setOrigem(pedido.getOrigemPedido());
        novaViagem.setDestino(pedido.getDestinoPedido());
        novaViagem.setDataViagem(pedido.getDataPedido());
        novaViagem.setLugares(3); 
        novaViagem.setPassageiroUsername(pedido.getUtilizador().getUsername());

        viagemRepo.save(novaViagem);

        return "redirect:/condutor";
    } catch (Exception e) {
        e.printStackTrace();
        return "redirect:/erro"; 
    }
}
@PostMapping("/remover")
public String removerRota(@RequestParam String origem,
                           @RequestParam String destino,
                           @RequestParam String data,
                           Model model) {
    Authentication auth = SecurityContextHolder.getContext().getAuthentication();
    String username = auth.getName();
    Optional<Utilizador> userList = utilizadorRepo.findByUsername(username);
    if (userList.isEmpty()) {
        model.addAttribute("errorMessage", "Utilizador não encontrado!");
        return "redirect:/condutor";  // Redirect to the main page (or another page)
    }
    Utilizador condutor = userList.get();
    Optional<Rotas> rotaOptional = locaisRepo.findByOrigemAndDestinoAndData(origem, destino, data).stream().findFirst();
    if (rotaOptional.isEmpty()) {
        model.addAttribute("errorMessage", "Rota não encontrada!");
        return "redirect:/condutor";  // Redirect to the main page (or another page)
    }
    Rotas rota = rotaOptional.get();
    if (!rota.getCondutor().equals(condutor)) {
        model.addAttribute("errorMessage", "A rota apenas pode ser eliminada por quem a criou!");
        return "redirect:/condutor";  // Redirect to the main page (or another page)
    }
    locaisRepo.delete(rota);
    model.addAttribute("successMessage", "Rota eliminada com sucesso!");
    return "redirect:/condutor";  // Redirect to the main page (or another page)
}


@PostMapping("/procurarPassageiro")
public String procurarPassageiro(
    @RequestParam String username,
    Model model){

        Optional<Utilizador> passageiro = utilizadorRepo.findByUsername(username);
        if(!"PASSAGEIRO".equals(passageiro.get().getRole())){
            model.addAttribute("error", "Passageiro não encontrado");
        }

        if(passageiro.isPresent()){
            model.addAttribute("username", passageiro.get().getUsername());
            model.addAttribute("rating", passageiro.get().getRating());
        }else{
            model.addAttribute("error", "Passageiro não encontrado");
        }

        return "perfilpassageiro";

    }

}