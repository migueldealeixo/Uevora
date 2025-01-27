package com.t2.t2.Controllers;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

import com.t2.t2.Repository.RotasRepo;
import com.t2.t2.Repository.ViagemRepo;
import com.t2.t2.entitys.Rotas;
import com.t2.t2.entitys.Viagem;

@Controller
public class ViagemController {

    @Autowired
    private RotasRepo rotasRepo;

    @GetMapping("/search")
    public String verViagensIndex(Model model,
    @RequestParam String origem,
    @RequestParam String destino,
    @RequestParam String data){

      try{
        List<Rotas> rotas = rotasRepo.findByOrigemAndDestinoAndData(origem, destino, data);
        model.addAttribute("rotas", rotas);
        return "index";

      }catch(Exception e){
        model.addAttribute("errorMSG", "Erro" + e.getMessage());
        return "index";
      }
    }

    


}
