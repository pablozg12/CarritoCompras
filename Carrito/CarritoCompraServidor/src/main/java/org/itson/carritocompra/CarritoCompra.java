/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 */

package org.itson.carritocompra;

import io.grpc.Server;
import io.grpc.ServerBuilder;
import java.io.IOException;

/**
 *
 * @author pablo
 */
public class CarritoCompra {

    public static void main(String[] args) throws InterruptedException{
         Server server = ServerBuilder.forPort(50051)
                 .addService(new CarritoCompraImpl())
                .build();
        try {
            server.start();
            System.out.println("Servidor de Carrito iniciado en el puerto 50051...");
            server.awaitTermination();
        } catch (IOException ex) {
            System.getLogger(CarritoCompraImpl.class.getName()).log(System.Logger.Level.ERROR, (String) null, ex);
        }     
    }
}
