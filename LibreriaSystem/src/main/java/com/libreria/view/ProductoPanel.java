package com.libreria.view;

import com.libreria.controller.ProductoController;
import com.libreria.model.Producto;
import com.libreria.util.SessionManager;
import com.libreria.util.UIConstants;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.TableRowSorter;
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
    private SearchPanel searchPanel;
    private TableRowSorter<DefaultTableModel> sorter;

    // Categorías 
    private final String[] CATEGORIAS = {
        "Libros", "Novelas", "Cuentos", "Poesía", "Cuadernos", "Libretas", "Blocks",
        "Lápices", "Lapiceros", "Bolígrafos", "Marcadores", "Colores", "Crayones",
        "Témperas", "Acuarelas", "Papel", "Cartulina", "Folders", "Mochilas",
        "Cartucheras", "Loncheras", "Calculadoras", "Reglas", "Compases",
        "Pegamento", "Tijeras", "Correctores", "Arte y Manualidades",
        "Tecnología", "Oficina", "Escolar", "Otros"
    };

    public ProductoPanel() {
        this.controller = new ProductoController();
        initComponents();
        configurarPermisos();
        loadProductos();
    }

    private void initComponents() {
        setLayout(new BorderLayout(10, 10));
        setBackground(UIConstants.BACKGROUND_COLOR);

        // Panel de encabezado
        JPanel headerPanel = createHeaderPanel();

        // Panel de búsqueda
        searchPanel = new SearchPanel(new String[]{"Código", "Nombre", "Categoría"});
        searchPanel.addSearchListener(e -> buscarProductos(searchPanel.getSearchText(), searchPanel.getSelectedFilter()));

        // Panel de formulario 
        JPanel formPanel = createFormPanel();

        // Botones
        btnNuevo = createStyledButton("➕ Nuevo", UIConstants.SECONDARY_COLOR);
        btnGuardar = createStyledButton("💾 Guardar", UIConstants.PRIMARY_COLOR);
        btnEliminar = createStyledButton("🗑️ Eliminar", UIConstants.DANGER_COLOR);

        JPanel buttonPanel = createButtonPanel(btnNuevo, btnGuardar, btnEliminar);
        //btnEliminar.setEnabled(false);

        // Panel superior completo 
        JPanel topPanel = new JPanel(new BorderLayout(0, 10));
        topPanel.setBackground(Color.WHITE);
        topPanel.setBorder(BorderFactory.createEmptyBorder(20, 20, 10, 20));

        if (SessionManager.getInstance().isAdministrador()) {
            topPanel.add(formPanel, BorderLayout.CENTER);
            topPanel.add(buttonPanel, BorderLayout.SOUTH);
        } else {
            topPanel.add(createReadOnlyPanel(), BorderLayout.CENTER);
        }

        JPanel tablePanel = createTablePanel();

        JPanel fixedContent = new JPanel(new BorderLayout(0, 0));
        fixedContent.setBackground(UIConstants.BACKGROUND_COLOR);
        fixedContent.add(searchPanel, BorderLayout.NORTH);
        fixedContent.add(topPanel, BorderLayout.CENTER);

        // Layout principal con scroll
        add(headerPanel, BorderLayout.NORTH);
        add(fixedContent, BorderLayout.CENTER);
        add(tablePanel, BorderLayout.SOUTH);

        setupEventListeners();
    }

    private JPanel createHeaderPanel() {
        JPanel headerPanel = new JPanel(new BorderLayout());
        headerPanel.setBackground(Color.WHITE);
        headerPanel.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createMatteBorder(0, 0, 1, 0, UIConstants.BORDER_COLOR),
            BorderFactory.createEmptyBorder(20, 20, 20, 20)
        ));

        JLabel titleLabel = new JLabel("📦 Gestión de Inventario");
        titleLabel.setFont(UIConstants.SUBTITLE_FONT);
        titleLabel.setForeground(UIConstants.TEXT_COLOR);

        JLabel modeLabel = new JLabel();
        modeLabel.setFont(UIConstants.SMALL_FONT);
        if (SessionManager.getInstance().isVendedor()) {
            modeLabel.setText("Modo: Solo Lectura");
            modeLabel.setForeground(UIConstants.TEXT_SECONDARY);
        } else {
            modeLabel.setText("Modo: Control Total");
            modeLabel.setForeground(UIConstants.SECONDARY_COLOR);
        }

        JPanel titlePanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 15, 0));
        titlePanel.setBackground(Color.WHITE);
        titlePanel.add(titleLabel);
        titlePanel.add(modeLabel);

        headerPanel.add(titlePanel, BorderLayout.WEST);
        return headerPanel;
    }

    protected JPanel createFormPanel() {
        JPanel formPanel = new JPanel(new GridBagLayout());
        formPanel.setBackground(Color.WHITE);
        formPanel.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(UIConstants.BORDER_COLOR, 1),
            BorderFactory.createEmptyBorder(20, 25, 20, 25)
        ));

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.insets = new Insets(8, 10, 8, 10);
        gbc.anchor = GridBagConstraints.WEST;

        // Inicializar componentes
        txtCodigo = createStyledTextField(20);
        txtCodigo.setEditable(false);
        txtCodigo.setBackground(UIConstants.HOVER_COLOR);
        txtCodigo.setVisible(false); 

        txtNombre = createStyledTextField(40);
        InputValidator.setMaxLength(txtNombre, 100);

        cmbCategoria = new JComboBox<>(CATEGORIAS);
        cmbCategoria.setEditable(true);
        cmbCategoria.setFont(UIConstants.NORMAL_FONT);
        cmbCategoria.setPreferredSize(new Dimension(300, UIConstants.INPUT_HEIGHT));
        cmbCategoria.setBackground(Color.WHITE);

        txtPrecio = createStyledTextField(15);
        InputValidator.setDecimalOnly(txtPrecio);

        txtStock = createStyledTextField(15);
        InputValidator.setNumericOnly(txtStock);

        txtDescripcion = new JTextArea(3, 40);
        txtDescripcion.setFont(UIConstants.NORMAL_FONT);
        txtDescripcion.setLineWrap(true);
        txtDescripcion.setWrapStyleWord(true);
        txtDescripcion.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(new Color(200, 200, 200), 1),
            BorderFactory.createEmptyBorder(8, 10, 8, 10)
        ));
        JScrollPane scrollDesc = new JScrollPane(txtDescripcion);
        scrollDesc.setPreferredSize(new Dimension(500, 80));

        // Fila 1: Categoria
        gbc.gridx = 0;
        gbc.gridy = 0;
        gbc.weightx = 0.0;
        gbc.gridwidth = 1;
        JLabel lblCategoria = createLabel("Categoría:");
        formPanel.add(lblCategoria, gbc);

        gbc.gridx = 1;
        gbc.weightx = 1.0;
        gbc.gridwidth = 3;
        formPanel.add(cmbCategoria, gbc);

        // Fila 2: Nombre 
        gbc.gridx = 0;
        gbc.gridy = 1;
        gbc.weightx = 0.0;
        gbc.gridwidth = 1;
        JLabel lblNombre = createLabel("Nombre:");
        formPanel.add(lblNombre, gbc);

        gbc.gridx = 1;
        gbc.weightx = 1.0;
        gbc.gridwidth = 3;
        formPanel.add(txtNombre, gbc);

        // Fila 3: Precio y Stock 
        gbc.gridx = 0;
        gbc.gridy = 2;
        gbc.weightx = 0.0;
        gbc.gridwidth = 1;
        JLabel lblPrecio = createLabel("Precio (S/):");
        formPanel.add(lblPrecio, gbc);

        gbc.gridx = 1;
        gbc.weightx = 0.3;
        formPanel.add(txtPrecio, gbc);

        gbc.gridx = 2;
        gbc.weightx = 0.0;
        JLabel lblStock = createLabel("Stock:");
        formPanel.add(lblStock, gbc);

        gbc.gridx = 3;
        gbc.weightx = 0.3;
        formPanel.add(txtStock, gbc);

        // Fila 4: Descripción (oculta)
        gbc.gridx = 0;
        gbc.gridy = 3;
        gbc.weightx = 0.0;
        gbc.gridwidth = 1;
        gbc.anchor = GridBagConstraints.NORTHWEST;
        JLabel lblDesc = createLabel("Descripción:");
        lblDesc.setVisible(false);
        formPanel.add(lblDesc, gbc);

        gbc.gridx = 1;
        gbc.weightx = 1.0;
        gbc.gridwidth = 3;
        gbc.anchor = GridBagConstraints.WEST;
        scrollDesc.setVisible(false);
        formPanel.add(scrollDesc, gbc);

        return formPanel;
    }

    private JLabel createLabel(String text) {
        JLabel label = new JLabel(text);
        label.setFont(UIConstants.NORMAL_FONT.deriveFont(Font.BOLD));
        label.setForeground(UIConstants.TEXT_COLOR);
        return label;
    }

    private JPanel createReadOnlyPanel() {
        JPanel infoPanel = new JPanel(new GridBagLayout());
        infoPanel.setBackground(UIConstants.HOVER_COLOR);
        infoPanel.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(UIConstants.BORDER_COLOR, 1),
            BorderFactory.createEmptyBorder(30, 30, 30, 30)
        ));

        JLabel infoLabel = new JLabel("<html><div style='text-align: center;'>" +
            "ℹ️<br><br>" +
            "<b style='font-size: 16px;'>Modo de Consulta</b><br><br>" +
            "Solo puedes visualizar el inventario.<br>" +
            "Para realizar cambios, contacta al administrador." +
            "</div></html>");
        infoLabel.setFont(UIConstants.NORMAL_FONT);
        infoLabel.setForeground(UIConstants.TEXT_SECONDARY);
        infoLabel.setHorizontalAlignment(SwingConstants.CENTER);

        infoPanel.add(infoLabel);
        return infoPanel;
    }

    private JPanel createTablePanel() {
        // Modelo de tabla
        modelo = new DefaultTableModel(
            new Object[]{"ID", "Código", "Nombre", "Categoría", "Precio", "Stock"}, 
            0
        ) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };

        // Tabla
        tabla = new JTable(modelo);
        tabla.setFont(new Font("Arial", Font.PLAIN, 14));
        tabla.setRowHeight(45);
        tabla.setShowGrid(true);
        tabla.setGridColor(new Color(220, 220, 220));
        
        tabla.setBackground(Color.WHITE);
        tabla.setForeground(Color.BLACK);
        tabla.setOpaque(true);
        
        tabla.setSelectionBackground(new Color(200, 220, 255));
        tabla.setSelectionForeground(Color.BLACK);

        tabla.setIntercellSpacing(new Dimension(1, 1));

        // Header de la tabla 
        tabla.getTableHeader().setFont(new Font("Arial", Font.BOLD, 14));
        tabla.getTableHeader().setBackground(new Color(240, 240, 240));
        tabla.getTableHeader().setForeground(Color.BLACK);
        tabla.getTableHeader().setPreferredSize(new Dimension(0, 45));
        tabla.getTableHeader().setOpaque(true);
        tabla.getTableHeader().setBorder(BorderFactory.createMatteBorder(0, 0, 2, 0, new Color(200, 200, 200)));

        // Configurar ancho de columnas
        tabla.getColumnModel().getColumn(0).setPreferredWidth(60);   // ID
        tabla.getColumnModel().getColumn(0).setMaxWidth(80);
        tabla.getColumnModel().getColumn(1).setPreferredWidth(120);  // Código
        tabla.getColumnModel().getColumn(2).setPreferredWidth(350);  // Nombre
        tabla.getColumnModel().getColumn(3).setPreferredWidth(180);  // Categoría
        tabla.getColumnModel().getColumn(4).setPreferredWidth(120);  // Precio
        tabla.getColumnModel().getColumn(5).setPreferredWidth(100);  // Stock

        // Alineación central para todas las columnas
        javax.swing.table.DefaultTableCellRenderer centerRenderer = new javax.swing.table.DefaultTableCellRenderer();
        centerRenderer.setHorizontalAlignment(javax.swing.JLabel.CENTER);
        centerRenderer.setBackground(Color.WHITE);
        centerRenderer.setForeground(Color.BLACK);
        centerRenderer.setOpaque(true);
        
        for (int i = 0; i < tabla.getColumnCount(); i++) {
            tabla.getColumnModel().getColumn(i).setCellRenderer(centerRenderer);
        }

        // Ordenamiento
        sorter = new TableRowSorter<>(modelo);
        tabla.setRowSorter(sorter);

        // ScrollPane con fondo blanco
        JScrollPane scrollPane = new JScrollPane(tabla);
        scrollPane.setBorder(BorderFactory.createLineBorder(new Color(200, 200, 200), 1));
        scrollPane.setPreferredSize(new Dimension(0, 350));
        scrollPane.setBackground(Color.WHITE);
        scrollPane.getViewport().setBackground(Color.WHITE);
        scrollPane.setOpaque(true);

        // Panel contenedor con fondo blanco
        JPanel tablePanel = new JPanel(new BorderLayout(0, 10));
        tablePanel.setBackground(Color.WHITE);
        tablePanel.setBorder(BorderFactory.createEmptyBorder(10, 20, 20, 20));
        tablePanel.setOpaque(true);

        JLabel tableTitle = new JLabel("Lista de Productos");
        tableTitle.setFont(new Font("Arial", Font.BOLD, 16));
        tableTitle.setForeground(Color.BLACK);

        tablePanel.add(tableTitle, BorderLayout.NORTH);
        tablePanel.add(scrollPane, BorderLayout.CENTER);

        return tablePanel;
    }

    private void setupEventListeners() {
        btnNuevo.addActionListener(this::btnNuevoActionPerformed);
        btnGuardar.addActionListener(this::btnGuardarActionPerformed);
        btnEliminar.addActionListener(this::btnEliminarActionPerformed);
        
        tabla.getSelectionModel().addListSelectionListener(e -> {
            if (!e.getValueIsAdjusting()) {
                int row = tabla.getSelectedRow();
                if (row != -1 && SessionManager.getInstance().isAdministrador()) {
                    cargarProductoSeleccionado(row);
                    btnEliminar.setEnabled(true);
                }
            }
        });
    }

    private void configurarPermisos() {
        boolean esAdmin = SessionManager.getInstance().isAdministrador();
        if (!esAdmin) {
            btnNuevo.setEnabled(false);
            btnGuardar.setEnabled(false);
            btnEliminar.setEnabled(false);
            txtNombre.setEditable(false);
            txtPrecio.setEditable(false);
            txtStock.setEditable(false);
            cmbCategoria.setEnabled(false);
        }
    }

    public void loadProductos() {
        try {
            List<Producto> productos = controller.obtenerTodos();
            actualizarTabla(productos);
            
            if (productos.isEmpty() && SessionManager.getInstance().isAdministrador()) {
                SwingUtilities.invokeLater(() -> {
                    int option = JOptionPane.showConfirmDialog(
                        this,
                        "El inventario está vacío.\n¿Desea agregar el primer producto?",
                        "Inventario Vacío",
                        JOptionPane.YES_NO_OPTION,
                        JOptionPane.INFORMATION_MESSAGE
                    );
                    if (option == JOptionPane.YES_OPTION) {
                        btnNuevoActionPerformed(null);
                    }
                });
            }
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
                String.format("S/ %.2f", p.getPrecio()),
                p.getStock()
            });
        }
    }

    private void cargarProductoSeleccionado(int row) {
        try {
            int modelRow = tabla.convertRowIndexToModel(row);
            int id = (int) modelo.getValueAt(modelRow, 0);
            productoSeleccionado = controller.obtenerPorId(id);
            
            if (productoSeleccionado != null) {
                txtCodigo.setText(productoSeleccionado.getCodigo());
                txtNombre.setText(productoSeleccionado.getNombre());
                cmbCategoria.setSelectedItem(productoSeleccionado.getCategoria());
                txtPrecio.setText(String.valueOf(productoSeleccionado.getPrecio()));
                txtStock.setText(String.valueOf(productoSeleccionado.getStock()));
                if (txtDescripcion != null) {
                    txtDescripcion.setText(productoSeleccionado.getDescripcion());
                }
            }
        } catch (Exception e) {
            showError("Error al cargar producto: " + e.getMessage());
        }
    }

    private void btnNuevoActionPerformed(ActionEvent evt) {
        if (!SessionManager.getInstance().isAdministrador()) {
            showError("No tienes permisos para realizar esta acción");
            return;
        }
        limpiarFormulario();
        productoSeleccionado = null;
        btnEliminar.setEnabled(false);
        cmbCategoria.requestFocus();
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
                    return valor != null && valor.toLowerCase().contains(texto.toLowerCase());
                })
                .collect(java.util.stream.Collectors.toList());
            actualizarTabla(resultados);
        } catch (Exception e) {
            showError("Error al buscar productos: " + e.getMessage());
        }
    }

    private void btnGuardarActionPerformed(ActionEvent evt) {
        if (!SessionManager.getInstance().isAdministrador()) {
            showError("No tienes permisos para realizar esta acción");
            return;
        }

        try {
            if (!InputValidator.validateRequired(txtNombre, "Nombre") ||
                !InputValidator.validatePositiveNumber(txtPrecio, "Precio") ||
                !InputValidator.validateNonNegativeNumber(txtStock, "Stock")) {
                return;
            }

            String categoriaTexto = "";
            if (cmbCategoria.getEditor() != null) {
                Object item = cmbCategoria.getEditor().getItem();
                if (item != null) categoriaTexto = item.toString().trim();
            }
            if (categoriaTexto.isEmpty()) {
                showError("Categoría es un campo requerido");
                cmbCategoria.requestFocus();
                return;
            }

            Producto producto = new Producto();
            if (productoSeleccionado != null) {
                producto.setId(productoSeleccionado.getId());
            }
            producto.setCodigo(txtCodigo.getText());
            producto.setNombre(txtNombre.getText().trim());
            producto.setCategoria(categoriaTexto);
            producto.setPrecio(Double.parseDouble(txtPrecio.getText()));
            producto.setStock(Integer.parseInt(txtStock.getText()));
            producto.setDescripcion(txtDescripcion.getText() != null ? txtDescripcion.getText().trim() : "");

            if (productoSeleccionado == null) {
                controller.crear(producto);
                txtCodigo.setText(producto.getCodigo());
                showInfo("✓ Producto creado exitosamente\nCódigo: " + producto.getCodigo());
            } else {
                controller.actualizar(producto);
                showInfo("✓ Producto actualizado exitosamente");
            }

            loadProductos();
            limpiarFormulario();
            productoSeleccionado = null;
            btnEliminar.setEnabled(false);
        } catch (NumberFormatException e) {
            showError("Por favor, ingrese valores numéricos válidos");
        } catch (Exception e) {
            showError("Error al guardar producto: " + e.getMessage());
        }
    }

    private void btnEliminarActionPerformed(ActionEvent evt) {
        if (!SessionManager.getInstance().isAdministrador()) {
            showError("No tienes permisos para realizar esta acción");
            return;
        }

        if (productoSeleccionado != null && showConfirm(
            "¿Está seguro de eliminar este producto?\n\n" +
            "Producto: " + productoSeleccionado.getNombre() + "\n" +
            "Código: " + productoSeleccionado.getCodigo())) {
            try {
                controller.eliminar(productoSeleccionado.getId());
                showInfo("✓ Producto eliminado exitosamente");
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
        cmbCategoria.setSelectedIndex(-1);
        txtPrecio.setText("");
        txtStock.setText("");
        if (txtDescripcion != null) txtDescripcion.setText("");
        tabla.clearSelection();
    }
}