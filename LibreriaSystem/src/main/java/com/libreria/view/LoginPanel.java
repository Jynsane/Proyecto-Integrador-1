package com.libreria.view;

import com.libreria.controller.UsuarioController;
import com.libreria.model.Usuario;
import com.libreria.util.SessionManager;
import com.libreria.util.UIConstants;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

public class LoginPanel extends JPanel {
    private final UsuarioController usuarioController;
    private JTextField txtUsername;
    private JPasswordField txtPassword;
    private JButton btnLogin;
    private JLabel lblError;
    private JFrame parentFrame;
    private JToggleButton btnShowPassword; 
    
    public LoginPanel(JFrame parent) {
        this.parentFrame = parent;
        this.usuarioController = new UsuarioController();
        initComponents();
    }
    
    private void initComponents() {
        setLayout(new GridBagLayout());
        setBackground(UIConstants.SIDEBAR_COLOR); 
        
        // Panel central con el formulario
        JPanel loginCard = new JPanel();
        loginCard.setLayout(new BoxLayout(loginCard, BoxLayout.Y_AXIS));
        loginCard.setBackground(Color.WHITE);
        loginCard.setBorder(new EmptyBorder(50, 60, 50, 60));
        loginCard.setPreferredSize(new Dimension(480, 620));
        
        // Logo y título
        JLabel lblLogo = new JLabel("📚");
        lblLogo.setFont(new Font("Segoe UI", Font.PLAIN, 64));
        lblLogo.setAlignmentX(Component.CENTER_ALIGNMENT);
        
        JLabel lblTitle = new JLabel("LibreTools");
        lblTitle.setFont(UIConstants.TITLE_FONT);
        lblTitle.setForeground(UIConstants.SIDEBAR_COLOR); // Verde
        lblTitle.setAlignmentX(Component.CENTER_ALIGNMENT);
        
        JLabel lblSubtitle = new JLabel("Sistema de Gestión de Librería");
        lblSubtitle.setFont(UIConstants.NORMAL_FONT);
        lblSubtitle.setForeground(UIConstants.TEXT_SECONDARY);
        lblSubtitle.setAlignmentX(Component.CENTER_ALIGNMENT);
        
        // Mensaje de error
        lblError = new JLabel(" ");
        lblError.setFont(UIConstants.SMALL_FONT);
        lblError.setForeground(UIConstants.DANGER_COLOR);
        lblError.setAlignmentX(Component.CENTER_ALIGNMENT);
      
        // Panel para usuario 
        JPanel usernameContainer = new JPanel();
        usernameContainer.setLayout(new BoxLayout(usernameContainer, BoxLayout.Y_AXIS));
        usernameContainer.setBackground(Color.WHITE);
        usernameContainer.setAlignmentX(Component.CENTER_ALIGNMENT);
        usernameContainer.setMaximumSize(new Dimension(360, 80)); // Ancho de 360px para centrar
        
        JLabel lblUsername = new JLabel("Usuario");
        lblUsername.setFont(UIConstants.NORMAL_FONT.deriveFont(Font.BOLD));
        lblUsername.setForeground(UIConstants.TEXT_COLOR);
        lblUsername.setAlignmentX(Component.LEFT_ALIGNMENT); // Alineado a la izquierda dentro del contenedor

        txtUsername = new JTextField();
        txtUsername.setFont(UIConstants.NORMAL_FONT);
        txtUsername.setPreferredSize(new Dimension(360, UIConstants.INPUT_HEIGHT));
        txtUsername.setMaximumSize(new Dimension(360, UIConstants.INPUT_HEIGHT)); // Ancho fijo
        txtUsername.setAlignmentX(Component.LEFT_ALIGNMENT);
        txtUsername.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(UIConstants.BORDER_COLOR, 1),
            BorderFactory.createEmptyBorder(8, 12, 8, 12)
        ));
        
        usernameContainer.add(lblUsername);
        usernameContainer.add(Box.createVerticalStrut(5));
        usernameContainer.add(txtUsername);

       
        JPanel passwordContainer = new JPanel();
        passwordContainer.setLayout(new BoxLayout(passwordContainer, BoxLayout.Y_AXIS));
        passwordContainer.setBackground(Color.WHITE);
        passwordContainer.setAlignmentX(Component.CENTER_ALIGNMENT);
        passwordContainer.setMaximumSize(new Dimension(360, 80)); 

        JLabel lblPassword = new JLabel("Contraseña");
        lblPassword.setFont(UIConstants.NORMAL_FONT.deriveFont(Font.BOLD));
        lblPassword.setForeground(UIConstants.TEXT_COLOR);
        lblPassword.setAlignmentX(Component.LEFT_ALIGNMENT); 
        
        // Panel que contiene el campo de texto y el botón "ojo"
        JPanel passwordPanel = new JPanel(new BorderLayout(5, 0));
        passwordPanel.setBackground(Color.WHITE);
        passwordPanel.setMaximumSize(new Dimension(360, UIConstants.INPUT_HEIGHT)); 
        passwordPanel.setPreferredSize(new Dimension(360, UIConstants.INPUT_HEIGHT)); 
        passwordPanel.setAlignmentX(Component.LEFT_ALIGNMENT);
        
        txtPassword = new JPasswordField();
        txtPassword.setFont(UIConstants.NORMAL_FONT);
        txtPassword.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(UIConstants.BORDER_COLOR, 1),
            BorderFactory.createEmptyBorder(8, 12, 8, 12)
        ));
        
        // Botón de ojito para mostrar/ocultar contraseña
        btnShowPassword = new JToggleButton("👁");
        btnShowPassword.setFont(new Font("Segoe UI", Font.PLAIN, 16));
        btnShowPassword.setPreferredSize(new Dimension(45, UIConstants.INPUT_HEIGHT));
        btnShowPassword.setBorderPainted(false);
        btnShowPassword.setFocusPainted(false);
        btnShowPassword.setBackground(new Color(240, 240, 240));
        btnShowPassword.setCursor(new Cursor(Cursor.HAND_CURSOR));
        btnShowPassword.setToolTipText("Mostrar/Ocultar contraseña");
        
        btnShowPassword.addActionListener(e -> {
            if (btnShowPassword.isSelected()) {
                txtPassword.setEchoChar((char) 0); // Mostrar
                btnShowPassword.setText("🙈"); 
            } else {
                txtPassword.setEchoChar('•'); // Ocultar
                btnShowPassword.setText("👁"); 
            }
        });
        
        passwordPanel.add(txtPassword, BorderLayout.CENTER);
        passwordPanel.add(btnShowPassword, BorderLayout.EAST);
        
        passwordContainer.add(lblPassword);
        passwordContainer.add(Box.createVerticalStrut(5));
        passwordContainer.add(passwordPanel);
        
        // Botón de login 
        btnLogin = createStyledButton("Iniciar Sesión", UIConstants.SIDEBAR_COLOR);
        btnLogin.setAlignmentX(Component.CENTER_ALIGNMENT);
        
        // Panel de información 
        JPanel infoPanel = new JPanel();
        infoPanel.setLayout(new BoxLayout(infoPanel, BoxLayout.Y_AXIS));
        infoPanel.setBackground(UIConstants.HOVER_COLOR);
        infoPanel.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(UIConstants.BORDER_COLOR, 1),
            BorderFactory.createEmptyBorder(15, 15, 15, 15)
        ));
        infoPanel.setAlignmentX(Component.CENTER_ALIGNMENT);
        infoPanel.setMaximumSize(new Dimension(360, 130)); // Ancho fijo
        
        JLabel lblInfo = new JLabel("👤 Usuarios de prueba:");
        lblInfo.setFont(UIConstants.SMALL_FONT.deriveFont(Font.BOLD));
        lblInfo.setAlignmentX(Component.LEFT_ALIGNMENT);
        
        JLabel lblAdmin = new JLabel("• Administrador:");
        lblAdmin.setFont(UIConstants.SMALL_FONT);
        lblAdmin.setForeground(UIConstants.TEXT_SECONDARY);
        lblAdmin.setAlignmentX(Component.LEFT_ALIGNMENT);
        
        JLabel lblAdminData = new JLabel("  Usuario: admin  |  Contraseña: admin123");
        lblAdminData.setFont(new Font("Segoe UI", Font.PLAIN, 11));
        lblAdminData.setForeground(UIConstants.TEXT_COLOR);
        lblAdminData.setAlignmentX(Component.LEFT_ALIGNMENT);
        
        JLabel lblVendedor = new JLabel("• Vendedor:");
        lblVendedor.setFont(UIConstants.SMALL_FONT);
        lblVendedor.setForeground(UIConstants.TEXT_SECONDARY);
        lblVendedor.setAlignmentX(Component.LEFT_ALIGNMENT);
        
        JLabel lblVendedorData = new JLabel("  Usuario: vendedor  |  Contraseña: vendedor123");
        lblVendedorData.setFont(new Font("Segoe UI", Font.PLAIN, 11));
        lblVendedorData.setForeground(UIConstants.TEXT_COLOR);
        lblVendedorData.setAlignmentX(Component.LEFT_ALIGNMENT);
        
        infoPanel.add(lblInfo);
        infoPanel.add(Box.createVerticalStrut(8));
        infoPanel.add(lblAdmin);
        infoPanel.add(Box.createVerticalStrut(3));
        infoPanel.add(lblAdminData);
        infoPanel.add(Box.createVerticalStrut(8));
        infoPanel.add(lblVendedor);
        infoPanel.add(Box.createVerticalStrut(3));
        infoPanel.add(lblVendedorData);
        
        // Agregar componentes al panel
        loginCard.add(lblLogo);
        loginCard.add(Box.createVerticalStrut(10));
        loginCard.add(lblTitle);
        loginCard.add(Box.createVerticalStrut(5));
        loginCard.add(lblSubtitle);
        loginCard.add(Box.createVerticalStrut(25));
        loginCard.add(lblError);
        loginCard.add(Box.createVerticalStrut(10));
        loginCard.add(usernameContainer); 
        loginCard.add(Box.createVerticalStrut(15));
        loginCard.add(passwordContainer); // Contenedor centrado con ojito
        loginCard.add(Box.createVerticalStrut(25));
        loginCard.add(btnLogin);
        loginCard.add(Box.createVerticalStrut(20));
        loginCard.add(infoPanel);
        
        add(loginCard);
        
        // Eventos
        btnLogin.addActionListener(e -> login());
        txtPassword.addActionListener(e -> login());
        
        SwingUtilities.invokeLater(() -> txtUsername.requestFocus());
    }
    
    private JButton createStyledButton(String text, Color bgColor) {
        JButton button = new JButton(text);
        button.setFont(UIConstants.BUTTON_FONT);
        button.setForeground(Color.WHITE);
        button.setBackground(bgColor);
        button.setBorderPainted(false);
        button.setFocusPainted(false);
        button.setCursor(new Cursor(Cursor.HAND_CURSOR));
        button.setPreferredSize(new Dimension(360, 45)); 
        button.setMaximumSize(new Dimension(360, 45)); 
        
        // Efecto hover
        button.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseEntered(MouseEvent e) {
                button.setBackground(bgColor.darker());
            }
            
            @Override
            public void mouseExited(MouseEvent e) {
                button.setBackground(bgColor);
            }
        });
        
        return button;
    }
    
    private void login() {
        String username = txtUsername.getText().trim();
        String password = new String(txtPassword.getPassword());
        
        if (username.isEmpty() || password.isEmpty()) {
            mostrarError("Por favor, ingrese usuario y contraseña");
            return;
        }
        
        try {
            Usuario usuario = usuarioController.autenticar(username, password);
            SessionManager.getInstance().iniciarSesion(usuario);
            
            // Cerrar ventana de login y abrir MainFrame
            SwingUtilities.invokeLater(() -> {
                parentFrame.dispose();
              
                MainFrame mainFrame = new MainFrame(); 
                mainFrame.setVisible(true);
            });
            
        } catch (Exception e) {
            mostrarError(e.getMessage());
        }
    }
    
    private void mostrarError(String mensaje) {
        lblError.setText(mensaje);
        txtPassword.setText("");
        txtPassword.requestFocus();
    }
}