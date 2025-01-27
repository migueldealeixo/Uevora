package com.t2.t2.Admin;

import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import com.t2.t2.Repository.UtilizadorRepo;
import com.t2.t2.entitys.Utilizador;


@Controller
public class AdminController {
    

    @Autowired
    private UtilizadorRepo utilizadorRepo;

    @GetMapping("/admin")
    public String getAdminPage(org.springframework.ui.Model model){

        List<Utilizador> utilizadores = utilizadorRepo.findAll();
        model.addAttribute("users", utilizadores);
        System.out.println(utilizadores);
        return "admin";

    }

    @PostMapping("/aprovar")
    public String updateStatus(
        @RequestParam(required = false) String username,
        RedirectAttributes redirectAttributes) {
        if (username == null || username.isEmpty()) {
            redirectAttributes.addFlashAttribute("error", "O campo username é obrigatório.");
            return "redirect:/admin";
        }
        Optional<Utilizador> optUser = utilizadorRepo.findByUsername(username);
        if (optUser.isPresent()) {
            Utilizador utilizador = optUser.get();
            utilizador.setActive(true);
            utilizadorRepo.save(utilizador);
            redirectAttributes.addFlashAttribute("success", "Conta validada com sucesso!");
        } else {
            redirectAttributes.addFlashAttribute("error", "Utilizador não encontrado.");
        }
        return "redirect:/admin";
    }
    

       @PostMapping("/desaprovar")
public String removeUser(@RequestParam(required = false) String username, RedirectAttributes redirectAttributes) {
    if (username == null || username.isEmpty()) {
        redirectAttributes.addFlashAttribute("error", "O campo username é obrigatório.");
        return "redirect:/admin";
    }
    Optional<Utilizador> optionalUser = utilizadorRepo.findByUsername(username);
    if (optionalUser.isPresent()) {
        Utilizador utilizador = optionalUser.get();

        if (utilizador.isActive()) {
            utilizador.setActive(false);
            utilizadorRepo.save(utilizador);
            redirectAttributes.addFlashAttribute("success", "Utilizador desativado com sucesso.");
        } else {
            redirectAttributes.addFlashAttribute("error", "O utilizador já se encontra desativado.");
        }
    } else {
        redirectAttributes.addFlashAttribute("error", "Utilizador não encontrado.");
    }
    return "redirect:/admin";
}

}
