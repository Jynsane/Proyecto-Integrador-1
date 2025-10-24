package com.libreria.view;

import com.libreria.controller.ProductoController;
import com.libreria.controller.VentaController;
import com.libreria.model.Producto;
import com.libreria.model.Venta;
import com.libreria.model.DetalleVenta;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.util.ArrayList;

public class VentaPanel extends BasePanel {
    private final VentaController ventaController;
    private final ProductoController productoController;
    private DefaultTableModel modeloDetalle;
    private JTable tablaDetalle;
    private JComboBox<String> cmbProductos;
    private JSpinner spnCantidad;
    private JTextField txtTotal;
    private JComboBox<String> cmbMetodoPago;
    private final java.util.List<Producto> listaProductos;
    private final java.util.List<DetalleVenta> detallesVenta;

    public VentaPanel() {
        this.ventaController = new VentaController();
        this.productoController = new ProductoController();
        this.listaProductos = new ArrayList<>();
        this.detallesVenta = new ArrayList<>();
        initComponents();
        cargarProductos();
    }

    private void initComponents() {
        setLayout(new BorderLayout());

        // Panel superior
        JPanel topPanel = new JPanel(new GridBagLayout());
        topPanel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(5, 5, 5, 5);

        // Selector de producto
        cmbProductos = new JComboBox<>();
        gbc.gridx = 0;
        gbc.gridy = 0;
        gbc.gridwidth = 2;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        topPanel.add(new JLabel("Producto:"), gbc);
        gbc.gridy = 1;
        topPanel.add(cmbProductos, gbc);

        // Spinner de cantidad
        spnCantidad = new JSpinner(new SpinnerNumberModel(1, 1, 999, 1));
        gbc.gridx = 2;
        gbc.gridy = 0;
        gbc.gridwidth = 1;
        topPanel.add(new JLabel("Cantidad:"), gbc);
        gbc.gridy = 1;
        topPanel.add(spnCantidad, gbc);

        // Botón agregar
        JButton btnAgregar = new JButton("Agregar");
        configureButton(btnAgregar);
        gbc.gridx = 3;
        gbc.gridy = 1;
        topPanel.add(btnAgregar, gbc);

        // Tabla de detalles
        modeloDetalle = new DefaultTableModel(
            new Object[]{"Producto", "Cantidad", "Precio Unit.", "Subtotal"}, 
            0
        ) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };

        tablaDetalle = new JTable(modeloDetalle);
        JScrollPane scrollPane = new JScrollPane(tablaDetalle);

        // Panel inferior
        JPanel bottomPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        txtTotal = new JTextField(10);
        txtTotal.setEditable(false);
        txtTotal.setFont(new Font("Arial", Font.BOLD, 14));

    cmbMetodoPago = new JComboBox<>(new String[]{"Efectivo", "YAPE"});
        JButton btnFinalizar = new JButton("Finalizar Venta");
        configureButton(btnFinalizar);

        bottomPanel.add(new JLabel("Método de Pago:"));
        bottomPanel.add(cmbMetodoPago);
        bottomPanel.add(new JLabel("Total: S/."));
        bottomPanel.add(txtTotal);
        bottomPanel.add(btnFinalizar);

        // Layout principal
        add(topPanel, BorderLayout.NORTH);
        add(scrollPane, BorderLayout.CENTER);
        add(bottomPanel, BorderLayout.SOUTH);

        // Eventos
        btnAgregar.addActionListener(this::btnAgregarActionPerformed);
        btnFinalizar.addActionListener(this::btnFinalizarActionPerformed);
    }

    public void cargarProductos() {
        try {
            listaProductos.clear();
            cmbProductos.removeAllItems();
            
            java.util.List<Producto> productos = productoController.obtenerTodos();
            for (Producto p : productos) {
                if (p.getStock() > 0) {
                    listaProductos.add(p);
                    cmbProductos.addItem(p.getNombre() + " - S/." + p.getPrecio());
                }
            }
        } catch (Exception e) {
            showError("Error al cargar productos: " + e.getMessage());
        }
    }

    private void btnAgregarActionPerformed(ActionEvent evt) {
        int index = cmbProductos.getSelectedIndex();
        if (index != -1) {
            Producto producto = listaProductos.get(index);
            int cantidad = (int) spnCantidad.getValue();

            try {
                if (productoController.verificarStockDisponible(producto.getId(), cantidad)) {
                    DetalleVenta detalle = new DetalleVenta(producto, cantidad, producto.getPrecio());
                    detallesVenta.add(detalle);
                    actualizarTabla();
                } else {
                    showError("Stock insuficiente");
                }
            } catch (Exception e) {
                showError("Error al agregar producto: " + e.getMessage());
            }
        }
    }

    private void btnFinalizarActionPerformed(ActionEvent evt) {
        if (detallesVenta.isEmpty()) {
            showError("Agregue al menos un producto");
            return;
        }

        try {
            Venta venta = new Venta();
            venta.setMetodoPago(cmbMetodoPago.getSelectedItem().toString());
            venta.setDetalles(detallesVenta);

            ventaController.crear(venta);
            showInfo("Venta realizada exitosamente");
            limpiarVenta();
            cargarProductos();
        } catch (Exception e) {
            showError("Error al procesar la venta: " + e.getMessage());
        }
    }

    private void actualizarTabla() {
        modeloDetalle.setRowCount(0);
        double total = 0;

        for (DetalleVenta detalle : detallesVenta) {
            modeloDetalle.addRow(new Object[]{
                detalle.getProducto().getNombre(),
                detalle.getCantidad(),
                detalle.getPrecioUnitario(),
                detalle.getSubtotal()
            });
            total += detalle.getSubtotal();
        }

        txtTotal.setText(String.format("%.2f", total));
    }

    private void limpiarVenta() {
        detallesVenta.clear();
        modeloDetalle.setRowCount(0);
        txtTotal.setText("0.00");
        spnCantidad.setValue(1);
        if (cmbProductos.getItemCount() > 0) {
            cmbProductos.setSelectedIndex(0);
        }
    }
}