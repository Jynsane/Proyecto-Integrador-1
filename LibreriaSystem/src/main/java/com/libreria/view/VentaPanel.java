package com.libreria.view;

import com.libreria.controller.ProductoController;
import com.libreria.controller.VentaController;
import com.libreria.model.Producto;
import com.libreria.model.Venta;
import com.libreria.model.DetalleVenta;
import com.libreria.util.SessionManager;
import com.libreria.util.ComprobanteGenerator;
import com.libreria.util.UIConstants;

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
    private JTextField txtBuscarProducto; // NUEVO: Campo de búsqueda
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
        setBackground(UIConstants.BACKGROUND_COLOR);
        
        // Panel de encabezado
        JPanel headerPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        headerPanel.setBackground(Color.WHITE);
        headerPanel.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createMatteBorder(0, 0, 1, 0, UIConstants.BORDER_COLOR),
            BorderFactory.createEmptyBorder(20, 20, 20, 20)
        ));
        
        JLabel titleLabel = new JLabel("🛒 Nueva Venta");
        titleLabel.setFont(UIConstants.SUBTITLE_FONT);
        titleLabel.setForeground(UIConstants.TEXT_COLOR);
        headerPanel.add(titleLabel);

        // Panel superior - Selección de productos CON BUSCADOR
        JPanel topPanel = new JPanel(new BorderLayout());
        topPanel.setBackground(Color.WHITE);
        topPanel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));
        
        JPanel selectionPanel = new JPanel(new GridBagLayout());
        selectionPanel.setBackground(Color.WHITE);
        selectionPanel.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(UIConstants.BORDER_COLOR, 1),
            BorderFactory.createEmptyBorder(15, 15, 15, 15)
        ));
        
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(5, 5, 5, 5);
        gbc.fill = GridBagConstraints.HORIZONTAL;

        // Buscador de productos CON ICONO
        JLabel lblBuscar = new JLabel("🔍 Buscar:");
        lblBuscar.setFont(UIConstants.NORMAL_FONT.deriveFont(Font.BOLD));
        lblBuscar.setForeground(Color.BLACK);
        
        txtBuscarProducto = new JTextField(25);
        txtBuscarProducto.setFont(UIConstants.NORMAL_FONT);
        txtBuscarProducto.setPreferredSize(new Dimension(300, UIConstants.INPUT_HEIGHT));
        txtBuscarProducto.setBackground(Color.WHITE);
        txtBuscarProducto.setForeground(Color.BLACK);
        txtBuscarProducto.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(UIConstants.BORDER_COLOR, 1),
            BorderFactory.createEmptyBorder(5, 10, 5, 10)
        ));
        
        gbc.gridx = 0;
        gbc.gridy = 0;
        gbc.anchor = GridBagConstraints.WEST;
        selectionPanel.add(lblBuscar, gbc);
        
        gbc.gridx = 1;
        gbc.gridwidth = 5;
        selectionPanel.add(txtBuscarProducto, gbc);

        // Selector de producto
        JLabel lblProducto = new JLabel("Producto:");
        lblProducto.setFont(UIConstants.NORMAL_FONT.deriveFont(Font.BOLD));
        cmbProductos = new JComboBox<>();
        cmbProductos.setFont(UIConstants.NORMAL_FONT);
        cmbProductos.setPreferredSize(new Dimension(400, UIConstants.INPUT_HEIGHT));
        styleComboBox(cmbProductos);
        
        gbc.gridx = 0;
        gbc.gridy = 1;
        gbc.gridwidth = 1;
        gbc.anchor = GridBagConstraints.WEST;
        selectionPanel.add(lblProducto, gbc);
        
        gbc.gridx = 1;
        gbc.gridwidth = 2;
        selectionPanel.add(cmbProductos, gbc);

        // Spinner de cantidad
        JLabel lblCantidad = new JLabel("Cantidad:");
        lblCantidad.setFont(UIConstants.NORMAL_FONT.deriveFont(Font.BOLD));
        spnCantidad = new JSpinner(new SpinnerNumberModel(1, 1, 999, 1));
        spnCantidad.setFont(UIConstants.NORMAL_FONT);
        spnCantidad.setPreferredSize(new Dimension(100, UIConstants.INPUT_HEIGHT));
        
        gbc.gridx = 3;
        gbc.gridwidth = 1;
        selectionPanel.add(lblCantidad, gbc);
        
        gbc.gridx = 4;
        selectionPanel.add(spnCantidad, gbc);

        // Botón agregar
        JButton btnAgregar = createStyledButton("➕ Agregar", UIConstants.SECONDARY_COLOR);
        gbc.gridx = 5;
        gbc.insets = new Insets(5, 15, 5, 5);
        selectionPanel.add(btnAgregar, gbc);

        topPanel.add(selectionPanel, BorderLayout.CENTER);
        
        // Evento del buscador para filtrar productos
        txtBuscarProducto.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyReleased(java.awt.event.KeyEvent evt) {
                filtrarProductos(txtBuscarProducto.getText());
            }
        });

        // Tabla de detalles con mejor visualización - TEXTO NEGRO
        modeloDetalle = new DefaultTableModel(
            new Object[]{"Producto", "Cantidad", "Precio Unit.", "Subtotal", ""}, 
            0
        ) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return column == 4;
            }
        };

        tablaDetalle = new JTable(modeloDetalle);
        tablaDetalle.setFont(UIConstants.NORMAL_FONT);
        tablaDetalle.setRowHeight(40);
        tablaDetalle.setShowGrid(true);
        tablaDetalle.setGridColor(new Color(220, 220, 220));
        tablaDetalle.setBackground(Color.WHITE);
        tablaDetalle.setForeground(Color.BLACK);
        tablaDetalle.setOpaque(true);
        tablaDetalle.setSelectionBackground(new Color(230, 240, 255));
        tablaDetalle.setSelectionForeground(Color.BLACK);
        
        // Header con estilo
        tablaDetalle.getTableHeader().setFont(UIConstants.NORMAL_FONT.deriveFont(Font.BOLD));
        tablaDetalle.getTableHeader().setBackground(new Color(240, 240, 240));
        tablaDetalle.getTableHeader().setForeground(Color.BLACK);
        tablaDetalle.getTableHeader().setPreferredSize(new Dimension(tablaDetalle.getTableHeader().getPreferredSize().width, 35));
        
        // Botón eliminar en la tabla
        tablaDetalle.getColumn("").setCellRenderer((table, value, isSelected, hasFocus, row, column) -> {
            JButton btn = new JButton("❌");
            btn.setFont(new Font("Arial", Font.PLAIN, 12));
            btn.setForeground(UIConstants.DANGER_COLOR);
            btn.setBackground(Color.WHITE);
            btn.setBorderPainted(false);
            btn.setCursor(new Cursor(Cursor.HAND_CURSOR));
            return btn;
        });
        
        tablaDetalle.getColumn("").setCellEditor(new DefaultCellEditor(new JCheckBox()) {
            @Override
            public Component getTableCellEditorComponent(JTable table, Object value, boolean isSelected, int row, int column) {
                JButton btn = new JButton("❌");
                btn.setFont(new Font("Arial", Font.PLAIN, 12));
                btn.setForeground(UIConstants.DANGER_COLOR);
                btn.setBackground(Color.WHITE);
                btn.setBorderPainted(false);
                btn.addActionListener(e -> eliminarItem(row));
                return btn;
            }
        });
        
        JScrollPane scrollPane = new JScrollPane(tablaDetalle);
        scrollPane.setBorder(BorderFactory.createLineBorder(new Color(200, 200, 200), 1));
        scrollPane.setOpaque(true);
        scrollPane.setBackground(Color.WHITE);
        scrollPane.getViewport().setBackground(Color.WHITE);

        // Panel central - Detalles
        JPanel centerPanel = new JPanel(new BorderLayout());
        centerPanel.setBackground(UIConstants.BACKGROUND_COLOR);
        centerPanel.setBorder(BorderFactory.createEmptyBorder(10, 20, 20, 20));
        
        JLabel lblDetalles = new JLabel("Detalles de la Venta");
        lblDetalles.setFont(UIConstants.HEADING_FONT);
        lblDetalles.setBorder(BorderFactory.createEmptyBorder(0, 0, 10, 0));
        
        centerPanel.add(lblDetalles, BorderLayout.NORTH);
        centerPanel.add(scrollPane, BorderLayout.CENTER);

        // Panel inferior - Total y finalizar
        JPanel bottomPanel = new JPanel(new BorderLayout());
        bottomPanel.setBackground(Color.WHITE);
        bottomPanel.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createMatteBorder(1, 0, 0, 0, UIConstants.BORDER_COLOR),
            BorderFactory.createEmptyBorder(20, 20, 20, 20)
        ));
        
        JPanel paymentPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT, 15, 0));
        paymentPanel.setBackground(Color.WHITE);
        
        JLabel lblMetodo = new JLabel("Método de Pago:");
        lblMetodo.setFont(UIConstants.NORMAL_FONT.deriveFont(Font.BOLD));
        
        cmbMetodoPago = new JComboBox<>(new String[]{"Efectivo", "YAPE", "Tarjeta"});
        cmbMetodoPago.setFont(UIConstants.NORMAL_FONT);
        cmbMetodoPago.setPreferredSize(new Dimension(150, UIConstants.INPUT_HEIGHT));
        styleComboBox(cmbMetodoPago);
        
        JLabel lblTotal = new JLabel("TOTAL:");
        lblTotal.setFont(new Font("Segoe UI", Font.BOLD, 20));
        lblTotal.setForeground(UIConstants.TEXT_COLOR);
        
        txtTotal = new JTextField("0.00", 12);
        txtTotal.setEditable(false);
        txtTotal.setFont(new Font("Segoe UI", Font.BOLD, 24));
        txtTotal.setForeground(UIConstants.PRIMARY_COLOR);
        txtTotal.setHorizontalAlignment(JTextField.RIGHT);
        txtTotal.setBorder(BorderFactory.createEmptyBorder());
        
        JLabel lblMoneda = new JLabel("S/");
        lblMoneda.setFont(new Font("Segoe UI", Font.BOLD, 18));
        lblMoneda.setForeground(UIConstants.TEXT_SECONDARY);

        JButton btnCancelar = createStyledButton("✗ Cancelar", UIConstants.DANGER_COLOR);
        btnCancelar.setPreferredSize(new Dimension(150, 50));
        btnCancelar.setFont(new Font("Segoe UI", Font.BOLD, 16));

        JButton btnFinalizar = createStyledButton("✓ Finalizar Venta", UIConstants.PRIMARY_COLOR);
        btnFinalizar.setPreferredSize(new Dimension(180, 50));
        btnFinalizar.setFont(new Font("Segoe UI", Font.BOLD, 16));

        paymentPanel.add(lblMetodo);
        paymentPanel.add(cmbMetodoPago);
        paymentPanel.add(Box.createHorizontalStrut(30));
        paymentPanel.add(lblTotal);
        paymentPanel.add(lblMoneda);
        paymentPanel.add(txtTotal);
        paymentPanel.add(Box.createHorizontalStrut(20));
        paymentPanel.add(btnCancelar);
        paymentPanel.add(Box.createHorizontalStrut(10));
        paymentPanel.add(btnFinalizar);
        
        bottomPanel.add(paymentPanel, BorderLayout.EAST);

        // Layout principal
        add(headerPanel, BorderLayout.NORTH);
        add(topPanel, BorderLayout.PAGE_START);
        add(centerPanel, BorderLayout.CENTER);
        add(bottomPanel, BorderLayout.SOUTH);

        // Eventos
        btnAgregar.addActionListener(this::btnAgregarActionPerformed);
        btnFinalizar.addActionListener(this::btnFinalizarActionPerformed);
        btnCancelar.addActionListener(e -> {
            if (detallesVenta.isEmpty() || showConfirm("¿Está seguro de cancelar esta venta?")) {
                limpiarVenta();
            }
        });
    }

    public void cargarProductos() {
        try {
            listaProductos.clear();
            cmbProductos.removeAllItems();
            
            java.util.List<Producto> productos = productoController.obtenerTodos();
            for (Producto p : productos) {
                if (p.getStock() > 0) {
                    listaProductos.add(p);
                    cmbProductos.addItem(p.getNombre() + " - S/ " + String.format("%.2f", p.getPrecio()) 
                        + " (Stock: " + p.getStock() + ")");
                }
            }
        } catch (Exception e) {
            showError("Error al cargar productos: " + e.getMessage());
        }
    }
    
    private void filtrarProductos(String textoBusqueda) {
        try {
            cmbProductos.removeAllItems();
            String busqueda = textoBusqueda.toLowerCase().trim();
            
            for (Producto p : listaProductos) {
                if (busqueda.isEmpty() || 
                    p.getNombre().toLowerCase().contains(busqueda) ||
                    p.getCodigo().toLowerCase().contains(busqueda) ||
                    p.getCategoria().toLowerCase().contains(busqueda)) {
                    cmbProductos.addItem(p.getNombre() + " - S/ " + String.format("%.2f", p.getPrecio()) 
                        + " (Stock: " + p.getStock() + ")");
                }
            }
        } catch (Exception e) {
            showError("Error al filtrar productos: " + e.getMessage());
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
                    spnCantidad.setValue(1);
                } else {
                    showError("Stock insuficiente. Disponible: " + producto.getStock());
                }
            } catch (Exception e) {
                showError("Error al agregar producto: " + e.getMessage());
            }
        }
    }
    
    private void eliminarItem(int row) {
        if (row >= 0 && row < detallesVenta.size()) {
            detallesVenta.remove(row);
            actualizarTabla();
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
            
            String vendedor = SessionManager.getInstance().getNombreUsuario();
            ComprobanteGenerator.mostrarVistaPrevia(venta, vendedor);
            
            showInfo("Venta realizada exitosamente\nN° " + venta.getNumeroVenta());
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
                String.format("S/ %.2f", detalle.getPrecioUnitario()),
                String.format("S/ %.2f", detalle.getSubtotal()),
                ""
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