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
import java.awt.Color;
import java.awt.event.ActionEvent;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import javax.swing.BoxLayout;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.event.ListSelectionEvent;
import javax.swing.table.DefaultTableModel;

/**
 *
 * @author pablo
 */
public class FrmCarritoCompra extends javax.swing.JFrame {

    private static final java.util.logging.Logger logger = java.util.logging.Logger.getLogger(FrmCarritoCompra.class.getName());

    private List<Producto> productosSeleccionados = new ArrayList<>();

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

        inicializarTablaProductos();
        agregarListenerTabla();

        this.idUsuario = idUsuario;
        setLocationRelativeTo(null);
    }

    private void agregarListenerTabla() {
        CatalogoResponse Response = obtenerCatalogo();

        jTable1.getSelectionModel().addListSelectionListener((ListSelectionEvent e) -> {
            if (!e.getValueIsAdjusting()) {
                int fila = jTable1.getSelectedRow();
                if (fila != -1) {
                    String nombreProducto = jTable1.getValueAt(fila, 0).toString();
                    int stockDisponible = (int) jTable1.getValueAt(fila, 1);

                    // para que se pueda modificar la contidad
                    String input = JOptionPane.showInputDialog(this,
                            "¿Cuántas unidades de " + nombreProducto + " desea?", "1");

                    try {
                        int cantidadPedida = Integer.parseInt(input);

                        if (cantidadPedida > 0 && cantidadPedida <= stockDisponible) {
                            // Buscar el producto en el catálogo para obtener lod datos
                            for (Producto p : Response.getProductosList()) {
                                if (nombreProducto.equals(p.getNombre())) {
                                    // copia del producto con la cantidad seleccionada
                                    Producto pConCantidad = Producto.newBuilder(p)
                                            .setCantidad(cantidadPedida)
                                            .build();

                                    productosSeleccionados.add(pConCantidad);
                                    agregarPanelProductos(nombreProducto + " (x" + cantidadPedida + ")");
                                    break;
                                }
                            }
                        } else {
                            JOptionPane.showMessageDialog(this, "Cantidad no válida o stock insuficiente");
                        }
                    } catch (NumberFormatException ex) {
                        // Si el usuario cancela o mete texto, no hacemos nada
                    }
                    jTable1.clearSelection(); // limpiar para permitir volver a seleccionar el mismo
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
        inicializarTablaProductos();
    }

    private CatalogoResponse obtenerCatalogo() {
        return stub.obtenerCatalogo(com.google.protobuf.Empty.newBuilder().build());
    }

    private void inicializarTablaProductos() {
        CatalogoResponse Response = obtenerCatalogo();

        //Para que la tabla no pueda ser editada manualmente
        DefaultTableModel model = new DefaultTableModel(new Object[]{"Producto", "Stock"}, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };

        jTable1.setModel(model);
        model.setRowCount(0);

        for (Producto p : Response.getProductosList()) {
            Object[] nuevaFila = {p.getNombre(), p.getCantidad()};
            model.addRow(nuevaFila);
        }
    }

    private CarritoRequest.Builder administrarProductos() {
        //Hashes para manejar los productos seleccionados y sus respectivas cantidades
        Map<String, Integer> cantidades = new HashMap<>();
        Map<String, Producto> infoProductos = new HashMap<>();

        // Se itera entre todos los productos seleccionados
        for (Producto p : productosSeleccionados) {
            cantidades.put(p.getId(), cantidades.getOrDefault(p.getId(), 0) + p.getCantidad());

            // Se agrega el objeto producto
            infoProductos.putIfAbsent(p.getId(), p);
        }

        //builder para el request 
        CarritoRequest.Builder requestBuilder = CarritoRequest.newBuilder().setUsuarioId(idUsuario);

        // Se itera en el hash de todas las cantidades para sacar el id de los productos
        for (String id : cantidades.keySet()) {

            // Obtiene el producto junto con su cantidad
            Producto original = infoProductos.get(id);
            int cantidadFinal = cantidades.get(id);

            // Se crea el producto final con la cantidad acumulada
            Producto itemFinal = Producto.newBuilder()
                    .setId(id)
                    .setNombre(original.getNombre())
                    .setPrecio(original.getPrecio())
                    .setCantidad(cantidadFinal)
                    .build();

            // Se agrega el producto al request
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
        lbCatalogo = new javax.swing.JLabel();
        spProductos = new javax.swing.JScrollPane();
        btnComprar = new javax.swing.JButton();
        spTablaProductos = new javax.swing.JScrollPane();
        jTable1 = new javax.swing.JTable();

        setDefaultCloseOperation(javax.swing.WindowConstants.EXIT_ON_CLOSE);
        setBackground(new java.awt.Color(0, 102, 102));

        lbTitulo.setFont(new java.awt.Font("HP Simplified", 1, 36)); // NOI18N
        lbTitulo.setForeground(new java.awt.Color(0, 51, 102));
        lbTitulo.setText("Carrito de compras");

        lbCatalogo.setText("Catálogo");

        spProductos.setBackground(new java.awt.Color(255, 255, 255));

        btnComprar.setBackground(new java.awt.Color(0, 51, 102));
        btnComprar.setFont(new java.awt.Font("HP Simplified", 1, 14)); // NOI18N
        btnComprar.setForeground(new java.awt.Color(255, 255, 255));
        btnComprar.setText("Comprar");
        btnComprar.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnComprarActionPerformed(evt);
            }
        });

        spTablaProductos.setBorder(null);
        spTablaProductos.setForeground(new java.awt.Color(0, 51, 102));

        jTable1.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {

            },
            new String [] {
                "Nombre de producto", "Stock"
            }
        ) {
            Class[] types = new Class [] {
                java.lang.String.class, java.lang.Integer.class
            };

            public Class getColumnClass(int columnIndex) {
                return types [columnIndex];
            }
        });
        spTablaProductos.setViewportView(jTable1);

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addGap(44, 44, 44)
                .addComponent(lbCatalogo)
                .addGap(104, 104, 104)
                .addComponent(lbTitulo)
                .addGap(0, 0, Short.MAX_VALUE))
            .addGroup(layout.createSequentialGroup()
                .addGap(36, 36, 36)
                .addComponent(spTablaProductos, javax.swing.GroupLayout.PREFERRED_SIZE, 356, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, 59, Short.MAX_VALUE)
                .addComponent(spProductos, javax.swing.GroupLayout.PREFERRED_SIZE, 200, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(43, 43, 43))
            .addGroup(layout.createSequentialGroup()
                .addGap(279, 279, 279)
                .addComponent(btnComprar, javax.swing.GroupLayout.PREFERRED_SIZE, 135, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, layout.createSequentialGroup()
                .addContainerGap(18, Short.MAX_VALUE)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(lbCatalogo, javax.swing.GroupLayout.Alignment.TRAILING)
                    .addComponent(lbTitulo, javax.swing.GroupLayout.Alignment.TRAILING))
                .addGap(18, 18, 18)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                    .addComponent(spTablaProductos, javax.swing.GroupLayout.PREFERRED_SIZE, 0, Short.MAX_VALUE)
                    .addComponent(spProductos, javax.swing.GroupLayout.DEFAULT_SIZE, 146, Short.MAX_VALUE))
                .addGap(29, 29, 29)
                .addComponent(btnComprar, javax.swing.GroupLayout.PREFERRED_SIZE, 43, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(39, 39, 39))
        );

        pack();
    }// </editor-fold>//GEN-END:initComponents

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
    private javax.swing.JTable jTable1;
    private javax.swing.JLabel lbCatalogo;
    private javax.swing.JLabel lbTitulo;
    private javax.swing.JScrollPane spProductos;
    private javax.swing.JScrollPane spTablaProductos;
    // End of variables declaration//GEN-END:variables
}
