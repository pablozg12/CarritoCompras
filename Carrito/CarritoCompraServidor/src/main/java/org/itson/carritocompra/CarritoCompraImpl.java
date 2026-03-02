/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package org.itson.carritocompra;

import com.tienda.grpc.CarritoRequest;
import com.tienda.grpc.CarritoResponse;
import com.tienda.grpc.Producto;
import io.grpc.stub.StreamObserver;
import com.tienda.grpc.CarritoServiceGrpc.CarritoServiceImplBase;
import com.tienda.grpc.CatalogoResponse;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

/**
 *
 * @author pablo
 */
public class CarritoCompraImpl extends CarritoServiceImplBase {

    private final Map<String, Integer> inventario = new ConcurrentHashMap<>();
    public List<Producto> catalogo = new ArrayList<>();

    public CarritoCompraImpl() {
        inventario();
    }

    public void inventario() {
        Producto p1 = Producto.newBuilder().setId("1").setNombre("Laptop").setPrecio(15000).build();
        Producto p2 = Producto.newBuilder().setId("2").setNombre("Mouse").setPrecio(250).build();
        Producto p3 = Producto.newBuilder().setId("3").setNombre("Teclado").setPrecio(500).build();
        Producto p4 = Producto.newBuilder().setId("4").setNombre("Pantalla").setPrecio(750).build();
        Producto p5 = Producto.newBuilder().setId("5").setNombre("Impresora").setPrecio(3200).build();
        Producto p6 = Producto.newBuilder().setId("6").setNombre("USB 32GB").setPrecio(180).build();
        Producto p7 = Producto.newBuilder().setId("7").setNombre("Disco Duro").setPrecio(1200).build();
        Producto p8 = Producto.newBuilder().setId("8").setNombre("Silla Gamer").setPrecio(4500).build();
        Producto p9 = Producto.newBuilder().setId("9").setNombre("Escritorio").setPrecio(3800).build();
        Producto p10 = Producto.newBuilder().setId("10").setNombre("Audífonos").setPrecio(900).build();
        Producto p11 = Producto.newBuilder().setId("11").setNombre("Webcam").setPrecio(1100).build();
        Producto p12 = Producto.newBuilder().setId("12").setNombre("Router WiFi").setPrecio(1400).build();
        Producto p13 = Producto.newBuilder().setId("13").setNombre("Tablet").setPrecio(8000).build();
        Producto p14 = Producto.newBuilder().setId("14").setNombre("Smartphone").setPrecio(12000).build();
        Producto p15 = Producto.newBuilder().setId("15").setNombre("Monitor").setPrecio(5200).build();

        catalogo.add(p1);
        catalogo.add(p2);
        catalogo.add(p3);
        catalogo.add(p4);
        catalogo.add(p5);
        catalogo.add(p6);
        catalogo.add(p7);
        catalogo.add(p8);
        catalogo.add(p9);
        catalogo.add(p10);
        catalogo.add(p11);
        catalogo.add(p12);
        catalogo.add(p13);
        catalogo.add(p14);
        catalogo.add(p15);

        inventario.put("1", 5);   // Laptop
        inventario.put("2", 5);   // Mouse
        inventario.put("3", 5);   // Teclado
        inventario.put("4", 5);    // Pantalla
        inventario.put("5", 5);    // Impresora
        inventario.put("6", 5);  // USB 32GB
        inventario.put("7", 5);    // Disco Duro 
        inventario.put("8", 5);    // Silla Gamer
        inventario.put("9", 5);    // Escritorio
        inventario.put("10", 5);  // Audífonos
        inventario.put("11", 5);   // Webcam
        inventario.put("12", 5);   // Router WiFi
        inventario.put("13", 5);   // Tablet
        inventario.put("14", 5);   // Smartphone
        inventario.put("15", 5);   // Monitor
    }

    @Override
    public void procesarCarrito(CarritoRequest request, StreamObserver<CarritoResponse> responseObserver) {

        if (request.getItemsCount() == 0) {
            responseObserver.onNext(crearRespuestaError("El carrito está vacío"));
            responseObserver.onCompleted();
            return;
        }

        for (Producto p : request.getItemsList()) {
            if (p.getCantidad() <= 0 || p.getPrecio() <= 0) {
                responseObserver.onNext(crearRespuestaError("Producto " + p.getNombre() + " tiene cantidad o precio inválido"));
                responseObserver.onCompleted();
                return;
            }

            int stockDisponible = inventario.getOrDefault(p.getId(), 0);
            if (p.getCantidad() > stockDisponible) {
                responseObserver.onNext(crearRespuestaError("No hay suficiente stock de: " + p.getNombre()));
                responseObserver.onCompleted();
                return;
            }
        }

        double subtotal = 0;
        for (Producto p : request.getItemsList()) {
            int stockActual = inventario.get(p.getId());
            inventario.put(p.getId(), stockActual - p.getCantidad());
            subtotal += p.getPrecio() * p.getCantidad();
        };

        double impuestos = subtotal * 0.16; // IVA del 16%
        double total = subtotal + impuestos;
        //Construimos la respuesta usando el Builder 
        //generado por Protobuf 
        
        // para que el id de la compra se vea mas bonito :)
        String id = "FAC-" + System.currentTimeMillis() % 100000 + "-" + (int) (Math.random() * 900 + 100);

        CarritoResponse response = CarritoResponse.newBuilder()
                .setTransaccionId(id) 
                .setTotalNeto(subtotal)
                .setImpuestos(impuestos)
                .setTotalPagar(total)
                .setEstado("EXITOSO")
                .build();
        responseObserver.onNext(response); // Enviamos al cliente
        responseObserver.onCompleted();
        System.out.println("Enviando respuesta del carrito para el usuario: " + request.getUsuarioId());
    }

    @Override
    public void obtenerCatalogo(com.google.protobuf.Empty request, StreamObserver<CatalogoResponse> responseObserver) {
        System.out.println("Obteniendo productos del catalogo");
        CatalogoResponse.Builder responseBuilder = CatalogoResponse.newBuilder();
        for (Producto producto : catalogo) {
            int stockActual = inventario.getOrDefault(producto.getId(), 0);
            Producto p = Producto.newBuilder()
                    .setId(producto.getId())
                    .setNombre(producto.getNombre())
                    .setPrecio(producto.getPrecio())
                    .setCantidad(stockActual)
                    .build();
            responseBuilder.addProductos(p);
        }

        responseObserver.onNext(responseBuilder.build());
        responseObserver.onCompleted();
    }

    private CarritoResponse crearRespuestaError(String mensaje) {
        return CarritoResponse.newBuilder()
                .setEstado(mensaje)
                .setTransaccionId("N/A")
                .build();
    }
}
