package com.t2.t2.entitys;


import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import jakarta.validation.constraints.Min;

@Entity
@Table(name = "rotas")
public class Rotas {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int localID;

    @ManyToOne(fetch=FetchType.LAZY)
    @JoinColumn(name = "username_condutor", nullable = false, referencedColumnName="username")
    private Utilizador condutor;

    @Column(nullable = false)
    private String origem;

    @Column(nullable = false)
    private String destino;

    @Column(nullable=false)
    private String data;
    

    @Min(value = 1, message = "Lugares disponíveis deve ser pelo menos 1")
    @Column(nullable = false)
    private int lugaresDisponiveis;

    // Getters e Setters
    public int getLocalID() {
        return localID;
    }

    public void setLocalID(int localID) {
        this.localID = localID;
    }

    public Utilizador getCondutor() {
        return condutor;
    }

    public void setCondutor(Utilizador condutor) {
        this.condutor = condutor;
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

    public int getLugaresDisponiveis() {
        return lugaresDisponiveis;
    }

    public void setLugaresDisponiveis(int lugaresDisponiveis) {
        this.lugaresDisponiveis = lugaresDisponiveis;
    }

    public String getData() {
        return data;
    }

    public void setData(String data) {
        this.data = data;
    }

    
}
