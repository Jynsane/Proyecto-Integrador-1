package com.libreria.view;

import com.libreria.controller.UsuarioController;
import com.libreria.model.Usuario;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.util.List;
import com.libreria.util.UIConstants;

public class UsuarioPanel extends BasePanel {
    private final UsuarioController controller;
    private JTable tabla;
    private DefaultTableModel modelo;
    private JTextField txtUsername;
    private JPasswordField txtPassword;
    private JTextField txtNombre;
    private JComboBox<String> cmbRol;
    private JCheckBox chkActivo;
    private JButton btnGuardar;
    private JButton btnNuevo;
    private JButton btnEliminar;
    private Usuario usuarioSeleccionado;

    public UsuarioPanel() {
        this.controller = new UsuarioController();
        initComponents();
        cargarUsuarios();
    }

    private void initComponents() {
        setLayout(new BorderLayout());
        setBackground(UIConstants.BACKGROUND_COLOR);

        // Panel superior con título
        JPanel headerPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        headerPanel.setBackground(Color.WHITE);
        headerPanel.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createMatteBorder(0, 0, 1, 0, UIConstants.BORDER_COLOR),
            BorderFactory.createEmptyBorder(20, 20, 20, 20)
        ));

        JLabel titleLabel = new JLabel("👥 Gestión de Usuarios");
        titleLabel.setFont(UIConstants.SUBTITLE_FONT);
        titleLabel.setForeground(UIConstants.TEXT_COLOR);
        headerPanel.add(titleLabel);

        // Panel de formulario
        JPanel formPanel = createStyledFormPanel();
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(10, 10, 10, 10);
        gbc.fill = GridBagConstraints.HORIZONTAL;

        // Inicializar componentes del formulario
        txtUsername = createStyledTextField(25);
        txtPassword = new JPasswordField(25);
        stylePasswordField(txtPassword);
        txtNombre = createStyledTextField(40);
        cmbRol = new JComboBox<>(new String[]{"ADMINISTRADOR", "VENDEDOR"});
        styleComboBox(cmbRol);
        cmbRol.setPreferredSize(new Dimension(460, UIConstants.INPUT_HEIGHT));
        chkActivo = new JCheckBox("Usuario Activo");
        chkActivo.setSelected(true);
        chkActivo.setFont(UIConstants.NORMAL_FONT);

        // Añadir los campos al formulario
        addFormField(formPanel, "Usuario", txtUsername, gbc, 0);
        addFormField(formPanel, "Contraseña", txtPassword, gbc, 1);
        addFormField(formPanel, "Nombre Completo", txtNombre, gbc, 2);
        addFormField(formPanel, "Rol", cmbRol, gbc, 3);
        addFormField(formPanel, "", chkActivo, gbc, 4);

        // Botones
        btnNuevo = createStyledButton("➕ Nuevo", UIConstants.SECONDARY_COLOR);
        btnGuardar = createStyledButton("💾 Guardar", UIConstants.PRIMARY_COLOR);
        btnEliminar = createStyledButton("🗑️ Eliminar", UIConstants.DANGER_COLOR);
        

        JPanel buttonPanel = createButtonPanel(btnNuevo, btnGuardar, btnEliminar);
        //btnEliminar.setEnabled(false);

        // Tabla
        modelo = new DefaultTableModel(
            new Object[]{"ID", "Usuario", "Nombre Completo", "Rol", "Activo"}, 0
        ) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };

        tabla = new JTable(modelo);
        tabla.setFont(UIConstants.NORMAL_FONT);
        tabla.setRowHeight(40);
        tabla.setShowGrid(true);
        tabla.setGridColor(new Color(220, 220, 220));
        tabla.setBackground(Color.WHITE);
        tabla.setForeground(Color.BLACK);
        tabla.setOpaque(true);
        tabla.setSelectionBackground(new Color(230, 240, 255));
        tabla.setSelectionForeground(Color.BLACK);

        // Header de la tabla
        tabla.getTableHeader().setFont(UIConstants.NORMAL_FONT.deriveFont(Font.BOLD));
        tabla.getTableHeader().setBackground(new Color(240, 240, 240));
        tabla.getTableHeader().setForeground(Color.BLACK);
        tabla.getTableHeader().setPreferredSize(new Dimension(tabla.getTableHeader().getPreferredSize().width, 35));

        // Ajustar ancho de columnas
        tabla.getColumnModel().getColumn(0).setPreferredWidth(50);
        tabla.getColumnModel().getColumn(1).setPreferredWidth(150);
        tabla.getColumnModel().getColumn(2).setPreferredWidth(250);
        tabla.getColumnModel().getColumn(3).setPreferredWidth(150);
        tabla.getColumnModel().getColumn(4).setPreferredWidth(100);

        JScrollPane scrollPane = new JScrollPane(tabla);
        scrollPane.setBorder(BorderFactory.createLineBorder(new Color(200, 200, 200), 1));
        scrollPane.setOpaque(true);
        scrollPane.setBackground(Color.WHITE);
        scrollPane.getViewport().setBackground(Color.WHITE);

        // Panel contenedor del formulario y botones
        JPanel topPanel = new JPanel(new BorderLayout());
        topPanel.setBackground(Color.WHITE);
        topPanel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));
        topPanel.add(formPanel, BorderLayout.CENTER);
        topPanel.add(buttonPanel, BorderLayout.SOUTH);

        // Panel contenedor de la tabla
        JPanel tablePanel = new JPanel(new BorderLayout());
        tablePanel.setBackground(Color.WHITE);
        tablePanel.setBorder(BorderFactory.createEmptyBorder(10, 20, 20, 20));

        JLabel tableTitle = new JLabel("Lista de Usuarios");
        tableTitle.setFont(UIConstants.HEADING_FONT);
        tableTitle.setForeground(UIConstants.TEXT_COLOR);
        tableTitle.setBorder(BorderFactory.createEmptyBorder(0, 0, 10, 0));

        tablePanel.add(tableTitle, BorderLayout.NORTH);
        tablePanel.add(scrollPane, BorderLayout.CENTER);

       
        add(headerPanel, BorderLayout.NORTH);
        add(topPanel, BorderLayout.CENTER);
        add(tablePanel, BorderLayout.SOUTH);

        
        btnNuevo.addActionListener(this::btnNuevoActionPerformed);
        btnGuardar.addActionListener(this::btnGuardarActionPerformed);
        btnEliminar.addActionListener(this::btnEliminarActionPerformed);
        tabla.getSelectionModel().addListSelectionListener(e -> {
            if (!e.getValueIsAdjusting()) {
                int row = tabla.getSelectedRow();
                if (row != -1) {
                    cargarUsuarioSeleccionado(row);
                    btnEliminar.setEnabled(true);
                }
            }
        });
    }

    public void cargarUsuarios() {
        try {
            List<Usuario> usuarios = controller.obtenerTodos();
            actualizarTabla(usuarios);
        } catch (Exception e) {
            showError("Error al cargar usuarios: " + e.getMessage());
        }
    }

    private void actualizarTabla(List<Usuario> usuarios) {
        modelo.setRowCount(0);
        for (Usuario u : usuarios) {
            modelo.addRow(new Object[]{
                u.getId(),
                u.getUsername(),
                u.getNombre(),
                u.getRol(),
                u.isActivo() ? "Sí" : "No"
            });
        }
    }

    private void cargarUsuarioSeleccionado(int row) {
        try {
            int id = (int) modelo.getValueAt(row, 0);
            usuarioSeleccionado = controller.obtenerPorId(id);
            if (usuarioSeleccionado != null) {
                txtUsername.setText(usuarioSeleccionado.getUsername());
                txtPassword.setText(usuarioSeleccionado.getPassword());
                txtNombre.setText(usuarioSeleccionado.getNombre());
                cmbRol.setSelectedItem(usuarioSeleccionado.getRol());
                chkActivo.setSelected(usuarioSeleccionado.isActivo());
            }
        } catch (Exception e) {
            showError("Error al cargar usuario: " + e.getMessage());
        }
    }

    private void btnNuevoActionPerformed(ActionEvent evt) {
        limpiarFormulario();
        usuarioSeleccionado = null;
        btnEliminar.setEnabled(false);
        txtUsername.requestFocus();
    }

    private void btnGuardarActionPerformed(ActionEvent evt) {
        try {
            if (txtUsername.getText().trim().isEmpty() ||
                new String(txtPassword.getPassword()).trim().isEmpty() ||
                txtNombre.getText().trim().isEmpty()) {
                showError("Todos los campos son requeridos");
                return;
            }

            Usuario usuario = new Usuario();
            if (usuarioSeleccionado != null) {
                usuario.setId(usuarioSeleccionado.getId());
            }
            usuario.setUsername(txtUsername.getText().trim());
            usuario.setPassword(new String(txtPassword.getPassword()));
            usuario.setNombre(txtNombre.getText().trim());
            usuario.setRol((String) cmbRol.getSelectedItem());
            usuario.setActivo(chkActivo.isSelected());

            if (usuarioSeleccionado == null) {
                controller.crear(usuario);
                showInfo("✓ Usuario creado exitosamente");
            } else {
                controller.actualizar(usuario);
                showInfo("✓ Usuario actualizado exitosamente");
            }

            cargarUsuarios();
            limpiarFormulario();
            usuarioSeleccionado = null;
            btnEliminar.setEnabled(false);
        } catch (Exception e) {
            showError("Error al guardar usuario: " + e.getMessage());
        }
    }

    private void btnEliminarActionPerformed(ActionEvent evt) {
        if (usuarioSeleccionado != null && showConfirm("¿Está seguro de eliminar este usuario?\n\nUsuario: " + usuarioSeleccionado.getUsername())) {
            try {
                controller.eliminar(usuarioSeleccionado.getId());
                showInfo("✓ Usuario eliminado exitosamente");
                cargarUsuarios();
                limpiarFormulario();
                usuarioSeleccionado = null;
                btnEliminar.setEnabled(false);
            } catch (Exception e) {
                showError("Error al eliminar usuario: " + e.getMessage());
            }
        }
    }

    private void limpiarFormulario() {
        txtUsername.setText("");
        txtPassword.setText("");
        txtNombre.setText("");
        cmbRol.setSelectedIndex(0);
        chkActivo.setSelected(true);
        tabla.clearSelection();
    }
}
