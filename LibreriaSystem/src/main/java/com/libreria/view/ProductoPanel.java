package com.libreria.view;

import com.libreria.controller.ProductoController;
import com.libreria.model.Producto;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.util.List;

public class ProductoPanel extends BasePanel {
    private final ProductoController controller;
    private JTable tabla;
    private DefaultTableModel modelo;
    private JTextField txtCodigo;
    private JTextField txtNombre;
    private JComboBox<String> cmbCategoria;
    private JTextField txtPrecio;
    private JTextField txtStock;
    private JTextArea txtDescripcion;
    private JButton btnGuardar;
    private JButton btnNuevo;
    private JButton btnEliminar;
    private Producto productoSeleccionado;

    public ProductoPanel() {
        this.controller = new ProductoController();
        initComponents();
        loadProductos();
    }

    private void initComponents() {
        setLayout(new BorderLayout());

        // Panel de búsqueda
        SearchPanel searchPanel = new SearchPanel(new String[]{"Código", "Nombre", "Categoría"});
        searchPanel.addSearchListener(e -> buscarProductos(searchPanel.getSearchText(), searchPanel.getSelectedFilter()));

        // Panel de formulario
        JPanel formPanel = createFormPanel();
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(5, 5, 5, 5);
        gbc.fill = GridBagConstraints.HORIZONTAL;

    txtCodigo = createTextField(15);
    txtCodigo.setEditable(false); // Código generado automáticamente
    txtNombre = createTextField(20);
    cmbCategoria = new JComboBox<>();
    cmbCategoria.setEditable(true);
    txtPrecio = createTextField(10);
    txtStock = createTextField(10);
        txtDescripcion = new JTextArea(3, 20);

        // Configurar validadores
    InputValidator.setMaxLength(txtCodigo, 50);
    InputValidator.setMaxLength(txtNombre, 100);
        InputValidator.setDecimalOnly(txtPrecio);
        InputValidator.setNumericOnly(txtStock);
        txtDescripcion.setLineWrap(true);
        JScrollPane scrollDescripcion = new JScrollPane(txtDescripcion);

    addFormField(formPanel, "Código", txtCodigo, gbc, 0);
    addFormField(formPanel, "Nombre", txtNombre, gbc, 1);
    addFormField(formPanel, "Categoría", cmbCategoria, gbc, 2);
        addFormField(formPanel, "Precio", txtPrecio, gbc, 3);
        addFormField(formPanel, "Stock", txtStock, gbc, 4);
        addFormField(formPanel, "Descripción", scrollDescripcion, gbc, 5);

        // Botones
        btnNuevo = new JButton("Nuevo");
        btnGuardar = new JButton("Guardar");
        btnEliminar = new JButton("Eliminar");

        JPanel buttonPanel = createButtonPanel(btnNuevo, btnGuardar, btnEliminar);
        btnEliminar.setEnabled(false);

        // Tabla
        modelo = new DefaultTableModel(
            new Object[]{"ID", "Código", "Nombre", "Categoría", "Precio", "Stock"}, 
            0
        ) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };

        tabla = new JTable(modelo);
        JScrollPane scrollPane = new JScrollPane(tabla);

        // Layout
        JPanel topPanel = new JPanel(new BorderLayout());
        topPanel.add(formPanel, BorderLayout.CENTER);
        topPanel.add(buttonPanel, BorderLayout.SOUTH);

        add(topPanel, BorderLayout.NORTH);
        add(scrollPane, BorderLayout.CENTER);

        // Eventos
        btnNuevo.addActionListener(this::btnNuevoActionPerformed);
        btnGuardar.addActionListener(this::btnGuardarActionPerformed);
        btnEliminar.addActionListener(this::btnEliminarActionPerformed);
        tabla.getSelectionModel().addListSelectionListener(e -> {
            if (!e.getValueIsAdjusting()) {
                int row = tabla.getSelectedRow();
                if (row != -1) {
                    cargarProductoSeleccionado(row);
                    btnEliminar.setEnabled(true);
                }
            }
        });
    }

    public void loadProductos() {
        try {
            List<Producto> productos = controller.obtenerTodos();
            actualizarTabla(productos);
                // Actualizar lista de categorías (deduplicada, case-insensitive)
                java.util.Set<String> categorias = new java.util.TreeSet<>(String.CASE_INSENSITIVE_ORDER);
                for (Producto p : productos) {
                    if (p.getCategoria() != null && !p.getCategoria().trim().isEmpty()) {
                        categorias.add(p.getCategoria().trim());
                    }
                }
                cmbCategoria.removeAllItems();
                for (String c : categorias) cmbCategoria.addItem(c);
        } catch (Exception e) {
            showError("Error al cargar productos: " + e.getMessage());
        }
    }

    private void actualizarTabla(List<Producto> productos) {
        modelo.setRowCount(0);
        for (Producto p : productos) {
            modelo.addRow(new Object[]{
                p.getId(),
                p.getCodigo(),
                p.getNombre(),
                p.getCategoria(),
                p.getPrecio(),
                p.getStock()
            });
        }
    }

    private void cargarProductoSeleccionado(int row) {
        try {
            int id = (int) modelo.getValueAt(row, 0);
            productoSeleccionado = controller.obtenerPorId(id);
            if (productoSeleccionado != null) {
                txtCodigo.setText(productoSeleccionado.getCodigo());
                txtNombre.setText(productoSeleccionado.getNombre());
                cmbCategoria.setSelectedItem(productoSeleccionado.getCategoria());
                txtPrecio.setText(String.valueOf(productoSeleccionado.getPrecio()));
                txtStock.setText(String.valueOf(productoSeleccionado.getStock()));
                txtDescripcion.setText(productoSeleccionado.getDescripcion());
            }
        } catch (Exception e) {
            showError("Error al cargar producto: " + e.getMessage());
        }
    }

    private void btnNuevoActionPerformed(ActionEvent evt) {
        limpiarFormulario();
        productoSeleccionado = null;
        btnEliminar.setEnabled(false);
    }

    private void buscarProductos(String texto, String filtro) {
        try {
            List<Producto> productos = controller.obtenerTodos();
            List<Producto> resultados = productos.stream()
                .filter(p -> {
                    String valor = switch (filtro) {
                        case "Código" -> p.getCodigo();
                        case "Nombre" -> p.getNombre();
                        case "Categoría" -> p.getCategoria();
                        default -> "";
                    };
                    return valor.toLowerCase().contains(texto.toLowerCase());
                })
                .collect(java.util.stream.Collectors.toList());
            actualizarTabla(resultados);
        } catch (Exception e) {
            showError("Error al buscar productos: " + e.getMessage());
        }
    }

    private void btnGuardarActionPerformed(ActionEvent evt) {
        try {
            // Validar campos requeridos
            if (!InputValidator.validateRequired(txtNombre, "Nombre") ||
                !InputValidator.validatePositiveNumber(txtPrecio, "Precio") ||
                !InputValidator.validateNonNegativeNumber(txtStock, "Stock")) {
                return;
            }

            // Validar categoría (editable combobox)
            String categoriaTexto = "";
            if (cmbCategoria.getEditor() != null) {
                Object item = cmbCategoria.getEditor().getItem();
                if (item != null) categoriaTexto = item.toString().trim();
            }
            if (categoriaTexto.isEmpty()) {
                showError("Categoría es un campo requerido");
                return;
            }

            Producto producto = new Producto();
            if (productoSeleccionado != null) {
                producto.setId(productoSeleccionado.getId());
            }
            producto.setCodigo(txtCodigo.getText());
            producto.setNombre(txtNombre.getText());
            producto.setCategoria(categoriaTexto);
            producto.setPrecio(Double.parseDouble(txtPrecio.getText()));
            producto.setStock(Integer.parseInt(txtStock.getText()));
            producto.setDescripcion(txtDescripcion.getText());

            if (productoSeleccionado == null) {
                controller.crear(producto);
                // Actualizar campo código con el generado por el controlador/DAO
                txtCodigo.setText(producto.getCodigo());
                showInfo("Producto creado exitosamente");
            } else {
                controller.actualizar(producto);
                showInfo("Producto actualizado exitosamente");
            }

            loadProductos();
            limpiarFormulario();
            productoSeleccionado = null;
            btnEliminar.setEnabled(false);
        } catch (NumberFormatException e) {
            showError("Por favor, ingrese valores numéricos válidos para precio y stock");
        } catch (Exception e) {
            showError("Error al guardar producto: " + e.getMessage());
        }
    }

    private void btnEliminarActionPerformed(ActionEvent evt) {
        if (productoSeleccionado != null && showConfirm("¿Está seguro de eliminar este producto?")) {
            try {
                controller.eliminar(productoSeleccionado.getId());
                showInfo("Producto eliminado exitosamente");
                loadProductos();
                limpiarFormulario();
                productoSeleccionado = null;
                btnEliminar.setEnabled(false);
            } catch (Exception e) {
                showError("Error al eliminar producto: " + e.getMessage());
            }
        }
    }

    private void limpiarFormulario() {
        txtCodigo.setText("");
        txtNombre.setText("");
        if (cmbCategoria != null) cmbCategoria.setSelectedIndex(-1);
        txtPrecio.setText("");
        txtStock.setText("");
        txtDescripcion.setText("");
        tabla.clearSelection();
    }
}