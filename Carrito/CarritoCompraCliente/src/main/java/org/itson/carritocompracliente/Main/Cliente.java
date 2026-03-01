/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Main.java to edit this template
 */
package org.itson.carritocompracliente.Main;

import Pantallas.FrmCarritoCompra;

/**
 *
 * @author pablo
 */
public class Cliente {

    /**
     * @param args the command line arguments
     */
    public static void main(String[] args) {
        String host = "127.0.0.1";
        String id = "USER-123";
        FrmCarritoCompra frm = new FrmCarritoCompra(host,id);
        frm.setVisible(true);
    }
}
