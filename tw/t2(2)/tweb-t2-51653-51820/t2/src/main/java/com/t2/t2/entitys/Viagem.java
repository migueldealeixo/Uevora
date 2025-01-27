package com.t2.t2.entitys;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
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
    @Column(name = "condutor_username", nullable = false)
    private String condutorUsername;

    @NotNull(message = "O passageiro é obrigatório.")
    @Column(name = "passageiro_username", nullable = false)
    private String passageiroUsername; 

    @Min(value = 1, message = "A viagem deve ter pelo menos 1 lugar.")
    @Max(value = 4, message = "O máximo de lugares livres deve ser 4.")
    @Column(nullable = false)
    private int lugares;

    @NotNull(message = "A data é obrigatória.")
    @Column(nullable = false, name = "dataViagem")
    private String dataViagem;

    @Column(name = "origemViagem",nullable=false)
    private String origem;

    @Column(name = "destinoViagem", nullable=false)
    private String destino;

    // Getters and Setters
    public int getViagemID() {
        return viagemID;
    }

    public void setViagemID(int viagemID) {
        this.viagemID = viagemID;
    }

    public String getCondutorUsername() {
        return condutorUsername;
    }

    public void setCondutorUsername(String condutorUsername) {
        this.condutorUsername = condutorUsername;
    }

    public String getPassageiroUsername() {
        return passageiroUsername;
    }

    public void setPassageiroUsername(String passageiroUsername) {
        this.passageiroUsername = passageiroUsername;
    }

    public int getLugares() {
        return lugares;
    }

    public void setLugares(int lugares) {
        this.lugares = lugares;
    }

    public String getDataViagem() {
        return dataViagem;
    }

    public void setDataViagem(String dataViagem) {
        this.dataViagem = dataViagem;
    }

    public String getOrigem() {
        return origem;
    }

    public void setOrigem(String origem) {
        this.origem = origem;
    }

    public String getDestino() {
        return destino;
    }

    public void setDestino(String destino) {
        this.destino = destino;
    }

    /**
     * Helper method to add a passenger username to the viagem.
     * @param username The username of the passenger to add.
     */
    public void setPassageiro(String username) {
        this.passageiroUsername = username;
    }

}
