/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/GUIForms/JFrame.java to edit this template
 */
package Pantallas;

import com.tienda.grpc.CarritoRequest;
import com.tienda.grpc.CarritoResponse;
import com.tienda.grpc.CarritoServiceGrpc;
import com.tienda.grpc.CatalogoResponse;
import com.tienda.grpc.Producto;
import io.grpc.ManagedChannel;
import io.grpc.ManagedChannelBuilder;
import java.awt.event.ActionEvent;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import javax.swing.BoxLayout;
import javax.swing.JOptionPane;
import javax.swing.JPanel;

/**
 *
 * @author pablo
 */
public class FrmCarritoCompra extends javax.swing.JFrame {

    private static final java.util.logging.Logger logger = java.util.logging.Logger.getLogger(FrmCarritoCompra.class.getName());

    private List<Producto> productosSeleccionados = new ArrayList<>();

    private CatalogoResponse responseC;
    private ManagedChannel channel;
    private CarritoServiceGrpc.CarritoServiceBlockingStub stub;
    private JPanel panelContenedor;
    private String idUsuario;

    /**
     * Creates new form FrmCarritoCompra
     *
     * @param host
     * @param idUsuario
     */
    public FrmCarritoCompra(String host, String idUsuario) {
        initComponents();
        setTitle("Carrito de compra");
        setSize(630, 372);

        panelContenedor = new JPanel();
        panelContenedor.setLayout(new BoxLayout(panelContenedor, BoxLayout.Y_AXIS));
        spProductos.setViewportView(panelContenedor);

        channel = ManagedChannelBuilder.forAddress(host, 50051).usePlaintext().build();
        stub = CarritoServiceGrpc.newBlockingStub(channel);
        responseC = obtenerCatalogo();

        inicializarListaProductos();
        agregarListenerComboBox();

        this.idUsuario = idUsuario;
    }

    private void agregarListenerComboBox() {
        cbProductos.addActionListener((ActionEvent e) -> {
            if (cbProductos.getSelectedIndex() == -1) {
                return;
            }

            String producto = (String) cbProductos.getSelectedItem();

            agregarPanelProductos(producto);

            for (Producto p : responseC.getProductosList()) {
                if (producto.equals(p.getNombre())) {
                    productosSeleccionados.add(p);
                    System.out.println("Producto agregado: " + p);
                }
            }
        });
    }

    private void agregarPanelProductos(String nombre) {
        pnlProducto panel = new pnlProducto(nombre);

        panelContenedor.add(panel);
        panelContenedor.revalidate();
        panelContenedor.repaint();
    }

    private void limpiarCarrito() {
        productosSeleccionados.clear();
        panelContenedor.removeAll();
        panelContenedor.revalidate();
        panelContenedor.repaint();
        cbProductos.setSelectedIndex(-1);
    }

    private CatalogoResponse obtenerCatalogo() {
        return stub.obtenerCatalogo(com.google.protobuf.Empty.newBuilder().build());
    }

    private void inicializarListaProductos() {
        for (Producto p : responseC.getProductosList()) {
            cbProductos.addItem(p.getNombre());
        }
        cbProductos.setSelectedIndex(-1);
    }

    private CarritoRequest.Builder administrarProductos() {
        //Hashes para manejar los productos seleccionados y sus respectivas cantidades
        Map<String, Integer> cantidades = new HashMap<>();
        Map<String, Producto> infoProductos = new HashMap<>();

        //Se itera entre todos los productos seleccionados
        for (Producto p : productosSeleccionados) {
            //Se saca el numero de veces que se repite un producto y lo agrega al hash de cantidades
            cantidades.put(p.getId(), cantidades.getOrDefault(p.getId(), 0) + 1);
            //Se agrega el objeto producto
            infoProductos.putIfAbsent(p.getId(), p);
        }

        //Se crea el builder para el request
        CarritoRequest.Builder requestBuilder = CarritoRequest.newBuilder().setUsuarioId(idUsuario);

        //Se itera en el hash de todas las cantidades para sacar el id de los productos
        for (String id : cantidades.keySet()) {

            //Obtiene el producto junto con su cantidad
            Producto original = infoProductos.get(id);
            int cantidadFinal = cantidades.get(id);

            //Se crea el producto
            Producto itemFinal = Producto.newBuilder()
                    .setId(id)
                    .setNombre(original.getNombre())
                    .setPrecio(original.getPrecio())
                    .setCantidad(cantidadFinal)
                    .build();

            //Se agrega el producto al request
            requestBuilder.addItems(itemFinal);
        }
        return requestBuilder;
    }

    /**
     * This method is called from within the constructor to initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is always
     * regenerated by the Form Editor.
     */
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        lbTitulo = new javax.swing.JLabel();
        cbProductos = new javax.swing.JComboBox<>();
        lbCatalogo = new javax.swing.JLabel();
        spProductos = new javax.swing.JScrollPane();
        btnComprar = new javax.swing.JButton();

        setDefaultCloseOperation(javax.swing.WindowConstants.EXIT_ON_CLOSE);

        lbTitulo.setFont(new java.awt.Font("Segoe UI", 1, 36)); // NOI18N
        lbTitulo.setText("Carrito de compras");

        cbProductos.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                cbProductosActionPerformed(evt);
            }
        });

        lbCatalogo.setText("Catálogo");

        btnComprar.setText("Comprar");
        btnComprar.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnComprarActionPerformed(evt);
            }
        });

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addGap(49, 49, 49)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(cbProductos, javax.swing.GroupLayout.PREFERRED_SIZE, 225, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(lbCatalogo))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, 36, Short.MAX_VALUE)
                .addComponent(spProductos, javax.swing.GroupLayout.PREFERRED_SIZE, 200, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(120, 120, 120))
            .addGroup(layout.createSequentialGroup()
                .addGap(144, 144, 144)
                .addComponent(lbTitulo)
                .addGap(0, 0, Short.MAX_VALUE))
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, layout.createSequentialGroup()
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addComponent(btnComprar, javax.swing.GroupLayout.PREFERRED_SIZE, 135, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(149, 149, 149))
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, layout.createSequentialGroup()
                .addContainerGap(23, Short.MAX_VALUE)
                .addComponent(lbTitulo)
                .addGap(24, 24, 24)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(layout.createSequentialGroup()
                        .addComponent(lbCatalogo)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(cbProductos, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addComponent(spProductos, javax.swing.GroupLayout.PREFERRED_SIZE, 180, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGap(18, 18, 18)
                .addComponent(btnComprar, javax.swing.GroupLayout.PREFERRED_SIZE, 43, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(36, 36, 36))
        );

        pack();
    }// </editor-fold>//GEN-END:initComponents

    /**
     * @param args the command line arguments
     */
//    public static void main(String args[]) {
//        /* Set the Nimbus look and feel */
//        //<editor-fold defaultstate="collapsed" desc=" Look and feel setting code (optional) ">
//        /* If Nimbus (introduced in Java SE 6) is not available, stay with the default look and feel.
//         * For details see http://download.oracle.com/javase/tutorial/uiswing/lookandfeel/plaf.html 
//         */
//        try {
//            for (javax.swing.UIManager.LookAndFeelInfo info : javax.swing.UIManager.getInstalledLookAndFeels()) {
//                if ("Nimbus".equals(info.getName())) {
//                    javax.swing.UIManager.setLookAndFeel(info.getClassName());
//                    break;
//                }
//            }
//        } catch (ReflectiveOperationException | javax.swing.UnsupportedLookAndFeelException ex) {
//            logger.log(java.util.logging.Level.SEVERE, null, ex);
//        }
//        //</editor-fold>
//
//        /* Create and display the form */
//        java.awt.EventQueue.invokeLater(() -> new FrmCarritoCompra().setVisible(true));
//    }

    private void cbProductosActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_cbProductosActionPerformed

    }//GEN-LAST:event_cbProductosActionPerformed

    private void btnComprarActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnComprarActionPerformed

        CarritoRequest.Builder request = administrarProductos();

        try {
            CarritoResponse response = stub.procesarCarrito(request.build());

            if (response.getEstado().equals("EXITOSO")) {
                JOptionPane.showMessageDialog(this, "Éxito: " + response.getTransaccionId() + "\nTotal: " + response.getTotalPagar());
                limpiarCarrito();
            } else {
                JOptionPane.showMessageDialog(this, "Error: " + response.getEstado(), "Error de Inventario", JOptionPane.ERROR_MESSAGE);
                limpiarCarrito();
            }
        } catch (Exception e) {
            JOptionPane.showMessageDialog(this, "Error de conexión: " + e.getMessage());
        }
    }//GEN-LAST:event_btnComprarActionPerformed


    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton btnComprar;
    private javax.swing.JComboBox<String> cbProductos;
    private javax.swing.JLabel lbCatalogo;
    private javax.swing.JLabel lbTitulo;
    private javax.swing.JScrollPane spProductos;
    // End of variables declaration//GEN-END:variables
}
