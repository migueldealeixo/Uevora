package com.t2.t2.entitys;

import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.Set;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.JoinTable;
import jakarta.persistence.ManyToMany;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;

@Entity
@Table(name = "viagem")
public class Viagem {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int viagemID;

    @NotNull(message = "O condutor é obrigatório.")
    @ManyToOne
    @JoinColumn(name = "condutor_id", nullable = false)
    private Utilizador condutor;

    @NotNull(message = "Pelo menos um passageiro é obrigatório.")
    @ManyToMany(cascade = CascadeType.ALL)
    @JoinTable(
        name = "viagem_user",
        joinColumns = @JoinColumn(name = "viagem_id"),
        inverseJoinColumns = @JoinColumn(name = "user_id")
    )
    private Set<Utilizador> passageiros = new HashSet<>();

    @Min(value = 1, message = "A viagem deve ter pelo menos 1 lugar.")
    @Max(value=4, message="O maximo de lugares livres deve ser 4 ")
    @Column(nullable = false)
    private int lugares;

    @NotNull(message = "A data é obrigatória.")
    @Column(nullable = false)
    private LocalDateTime dataViagem;

    @NotNull(message = "A origem é obrigatória.")
    @ManyToOne
    @JoinColumn(name = "origem_id", nullable = false)
    private Pedido origem;

    @NotNull(message = "O destino é obrigatório.")
    @ManyToOne
    @JoinColumn(name = "destino_id", nullable = false)
    private Pedido destino;

    // Getters e Setters
    public int getViagemID() {
        return viagemID;
    }

    public void setViagemID(int viagemID) {
        this.viagemID = viagemID;
    }

    public Utilizador getCondutor() {
        return condutor;
    }

    public void setCondutor(Utilizador condutor) {
        this.condutor = condutor;
    }

    public Set<Utilizador> getPassageiros() {
        return passageiros;
    }

    public void setPassageiros(Set<Utilizador> passageiros) {
        this.passageiros = passageiros;
    }

    public int getLugares() {
        return lugares;
    }

    public void setLugares(int lugares) {
        this.lugares = lugares;
    }

   

    public Pedido getOrigem() {
        return origem;
    }

    public void setOrigem(Pedido origem) {
        this.origem = origem;
    }

    public Pedido getDestino() {
        return destino;
    }

    public void setDestino(Pedido destino) {
        this.destino = destino;
    }

    public LocalDateTime getDataViagem() {
        return dataViagem;
    }

    public void setDataViagem(LocalDateTime dataViagem) {
        this.dataViagem = dataViagem;
    }

   
}
