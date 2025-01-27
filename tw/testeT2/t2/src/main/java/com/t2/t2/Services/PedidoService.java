package com.t2.t2.Services;

import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.t2.t2.Repository.PedidoRepo;
import com.t2.t2.entitys.Pedido;

@Service
public class PedidoService {
    @Autowired
    private PedidoRepo pedidoRepo;

    public Pedido savePedido(Pedido pedido){
        return pedidoRepo.save(pedido);
    }

    public Optional<Pedido> findById(int pedidoID){
        return pedidoRepo.findById(pedidoID);
    }



}
