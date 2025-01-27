package com.t2.t2.entitys;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;

@Entity
@Table(name = "pedidos")
public class Pedido {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int pedidoID;

   @Column(name="destinoPedido", nullable = false)
   private String origemPedido;

   @Column(nullable=false,name = "destinoOrigem")
   private String destinoPedido;

    @Column(nullable = false, name="data")
    private String dataPedido;

    @ManyToOne
    @JoinColumn(name = "utilizador_id", nullable = false)
    private Utilizador utilizador;

    // Getters e Setters
    public int getPedidoID() {
        return pedidoID;
    }

    public void setPedidoID(int pedidoID) {
        this.pedidoID = pedidoID;
    }
    public Utilizador getUtilizador() {
        return utilizador;
    }

    public void setUtilizador(Utilizador utilizador) {
        this.utilizador = utilizador;
    }

    public String getOrigemPedido() {
        return origemPedido;
    }

    public void setOrigemPedido(String origemPedido) {
        this.origemPedido = origemPedido;
    }

    public String getDestinoPedido() {
        return destinoPedido;
    }

    public void setDestinoPedido(String destinoPedido) {
        this.destinoPedido = destinoPedido;
    }

    public String getDataPedido() {
        return dataPedido;
    }

    public void setDataPedido(String dataPedido) {
        this.dataPedido = dataPedido;
    }

 
}
