package com.t2.t2.entitys;

import java.util.List;
import java.util.Set;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;
import jakarta.validation.constraints.DecimalMax;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;

@Entity
@Table(name="users")
public class Utilizador {
    
    @Id
    @GeneratedValue(strategy= GenerationType.IDENTITY)
    private int ID;

    @Column(unique=true,nullable=false)
    @NotBlank(message="O username não pode estar vazio! ")
    private String username;

    @Email
    @Column(unique = true,nullable = false)
    private String email;

    @Column(nullable=false)
    private String password;

    @Column(nullable = false)
    private boolean isActive;

    @Column
    @DecimalMin(value="0.0",inclusive=true)
    @DecimalMax(value = "5.0",inclusive = true)
    private Double rating;

    @Column(nullable=false)
    private String role;



    @OneToMany(mappedBy = "condutorUsername", fetch = FetchType.LAZY) 
    private List<Viagem> viagensComoCondutor;

    @OneToMany(mappedBy="utilizador",fetch=FetchType.LAZY)
    private Set<Pedido> pedidos;

    public int getID() {
        return ID;
    }

    public void setID(int iD) {
        ID = iD;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public boolean isActive() {
        return isActive;
    }

    public String getRole() {
        return role;
    }

    public void setRole(String role) {
        this.role = role;
    }

    public Set<Pedido> getPedidos() {
        return pedidos;
    }

    public void setPedidos(Set<Pedido> pedidos) {
        this.pedidos = pedidos;
    }

    public void setActive(boolean isActive) {
        this.isActive = isActive;
    }

    public Double getRating() {
        return rating;
    }

    public void setRating(Double rating) {
        this.rating = rating;
    }


    public List<Viagem> getViagensComoCondutor() {
        return viagensComoCondutor;
    }

    public void setViagensComoCondutor(List<Viagem> viagensComoCondutor) {
        this.viagensComoCondutor = viagensComoCondutor;
    }
}
