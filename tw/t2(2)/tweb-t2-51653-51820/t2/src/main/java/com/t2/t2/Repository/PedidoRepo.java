package com.t2.t2.Repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import com.t2.t2.entitys.Pedido;

public interface PedidoRepo extends JpaRepository<Pedido, Integer> {
    List<Pedido> findByOrigemPedidoAndDestinoPedido(String origem, String destino);
    List<Pedido> findByOrigemPedidoAndDestinoPedidoAndDataPedido(String destinoOrigem,String destinoDestino,String data);
}
