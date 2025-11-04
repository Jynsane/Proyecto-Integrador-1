package com.libreria.view;

import javax.swing.*;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import com.libreria.util.UIConstants;
import com.libreria.util.SessionManager;

public class MainFrame extends JFrame {
    private JPanel mainPanel;
    private JPanel sidebarPanel;
    private JPanel contentPanel;
    private ProductoPanel productoPanel;
    private VentaPanel ventaPanel;
    private DashboardPanel dashboardPanel;
    private UsuarioPanel usuarioPanel;
    private CardLayout cardLayout;
    private JLabel userLabel;
    private JLabel rolLabel;

    public MainFrame() {
        // Verificar que hay una sesión activa
        if (!SessionManager.getInstance().isLoggedIn()) {
            dispose();
            new LoginFrame().setVisible(true);
            return;
        }
        initComponents();
    }

    private void initComponents() {
        setTitle("LibreTools - Sistema de Gestión");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(1400, 850);
        setLocationRelativeTo(null);

        // Panel principal con BorderLayout
        mainPanel = new JPanel(new BorderLayout());
        mainPanel.setBackground(UIConstants.BACKGROUND_COLOR);

        // Crear sidebar 
        createSidebar();

        // Panel de contenido
        contentPanel = new JPanel(new CardLayout());
        contentPanel.setBackground(UIConstants.BACKGROUND_COLOR);
        cardLayout = (CardLayout) contentPanel.getLayout();

        // Inicializar paneles
        productoPanel = new ProductoPanel();
        ventaPanel = new VentaPanel();
        dashboardPanel = new DashboardPanel();

        // Panel de bienvenida mejorado
        JPanel welcomePanel = createWelcomePanel();

        // Agregar paneles al CardLayout
        contentPanel.add(welcomePanel, "welcome");
        contentPanel.add(productoPanel, "productos");
        contentPanel.add(ventaPanel, "ventas");
        contentPanel.add(dashboardPanel, "dashboard");

        // Solo para administradores
        if (SessionManager.getInstance().isAdministrador()) {
            usuarioPanel = new UsuarioPanel();
            contentPanel.add(usuarioPanel, "usuarios");
        }

        // Agregar sidebar y contenido al panel principal
        mainPanel.add(sidebarPanel, BorderLayout.WEST);
        mainPanel.add(contentPanel, BorderLayout.CENTER);

        add(mainPanel);
    }

    // MÉTODOS DEL SIDEBAR MODIFICADOS

    private void createSidebar() {
        sidebarPanel = new JPanel();
        sidebarPanel.setPreferredSize(new Dimension(UIConstants.SIDEBAR_WIDTH, 0));
        sidebarPanel.setLayout(new BoxLayout(sidebarPanel, BoxLayout.Y_AXIS));
        sidebarPanel.setBackground(UIConstants.SIDEBAR_COLOR);
        // Borde estilizado del segundo código
        sidebarPanel.setBorder(BorderFactory.createMatteBorder(0, 0, 0, 1, UIConstants.BORDER_COLOR));

        // Logo y título (CENTRADO)
        JPanel logoPanel = new JPanel();
        logoPanel.setLayout(new BoxLayout(logoPanel, BoxLayout.Y_AXIS));
        logoPanel.setBackground(UIConstants.SIDEBAR_COLOR);
        logoPanel.setBorder(BorderFactory.createEmptyBorder(30, 20, 20, 20));

        JLabel logoIcon = new JLabel("📚");
        logoIcon.setFont(new Font("Segoe UI", Font.PLAIN, 48));
        logoIcon.setAlignmentX(Component.CENTER_ALIGNMENT);

        JLabel logoLabel = new JLabel("LibreTools");
        logoLabel.setFont(new Font("Segoe UI", Font.BOLD, 24));
        logoLabel.setForeground(Color.WHITE);
        logoLabel.setAlignmentX(Component.CENTER_ALIGNMENT);

        logoPanel.add(logoIcon);
        logoPanel.add(Box.createVerticalStrut(10));
        logoPanel.add(logoLabel);

        // Panel de usuario - Compacto y centrado
        JPanel userPanel = new JPanel();
        userPanel.setLayout(new BoxLayout(userPanel, BoxLayout.Y_AXIS));
        userPanel.setBackground(new Color(31, 41, 55)); // Color oscuro para contraste
        userPanel.setBorder(BorderFactory.createEmptyBorder(12, 10, 12, 10));
        userPanel.setMaximumSize(new Dimension(UIConstants.SIDEBAR_WIDTH - 20, 85));
        userPanel.setAlignmentX(Component.CENTER_ALIGNMENT); // Centra el panel de usuario

        userLabel = new JLabel(SessionManager.getInstance().getNombreUsuario());
        userLabel.setFont(new Font("Segoe UI", Font.BOLD, 13));
        userLabel.setForeground(Color.WHITE);
        userLabel.setAlignmentX(Component.CENTER_ALIGNMENT); // Centra el texto de usuario

        rolLabel = new JLabel(SessionManager.getInstance().getRol());
        rolLabel.setFont(new Font("Segoe UI", Font.PLAIN, 11));
        rolLabel.setForeground(new Color(156, 163, 175));
        rolLabel.setAlignmentX(Component.CENTER_ALIGNMENT); // Centra el texto de rol

        userPanel.add(Box.createVerticalStrut(3));
        userPanel.add(userLabel);
        userPanel.add(Box.createVerticalStrut(3));
        userPanel.add(rolLabel);
        userPanel.add(Box.createVerticalStrut(3));

        // Botones de navegación (Con padding y centrados)
        JPanel navPanel = new JPanel();
        navPanel.setLayout(new BoxLayout(navPanel, BoxLayout.Y_AXIS));
        navPanel.setBackground(UIConstants.SIDEBAR_COLOR);
        navPanel.setBorder(BorderFactory.createEmptyBorder(20, 10, 20, 10)); // Más padding vertical

        navPanel.add(createSidebarButton("🏠 Inicio", "welcome", true));
        navPanel.add(Box.createVerticalStrut(5));
        navPanel.add(createSidebarButton("🛒 Venta", "ventas", true));
        navPanel.add(Box.createVerticalStrut(5));
        navPanel.add(createSidebarButton("📦 Inventario", "productos", true));
        navPanel.add(Box.createVerticalStrut(5));

        if (SessionManager.getInstance().isAdministrador()) {
            navPanel.add(createSidebarButton("📊 Resumen", "dashboard", true));
            navPanel.add(Box.createVerticalStrut(5));
            navPanel.add(createSidebarButton("👥 Usuarios", "usuarios", true));
            navPanel.add(Box.createVerticalStrut(5));
        }

        // Botón de cerrar sesión (Estilizado y centrado)
        JButton btnLogout = createSidebarButton("🚪 Cerrar Sesión", null, false);
        btnLogout.setBackground(UIConstants.DANGER_COLOR);
        // Se asegura el ancho para que quede centrado
        btnLogout.setMaximumSize(new Dimension(UIConstants.SIDEBAR_WIDTH - 20, 40)); 
        // Sobreescribir el estilo del botón
        btnLogout.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseEntered(MouseEvent e) {
                btnLogout.setBackground(UIConstants.DANGER_COLOR.darker());
            }

            @Override
            public void mouseExited(MouseEvent e) {
                btnLogout.setBackground(UIConstants.DANGER_COLOR);
            }
        });
        btnLogout.addActionListener(e -> cerrarSesion());

        // Agregar componentes al sidebar
        sidebarPanel.add(logoPanel);
        sidebarPanel.add(userPanel);
        sidebarPanel.add(Box.createVerticalStrut(10)); // Espacio entre info de usuario y nav
        sidebarPanel.add(navPanel);
        sidebarPanel.add(Box.createVerticalGlue());
        sidebarPanel.add(btnLogout);
        sidebarPanel.add(Box.createVerticalStrut(20)); // Espacio al final
    }

    private JButton createSidebarButton(String text, String cardName, boolean isNavButton) {
        JButton button = new JButton(text);
        button.setFont(UIConstants.NORMAL_FONT);
        button.setForeground(Color.WHITE);
        button.setBackground(UIConstants.SIDEBAR_COLOR);
        button.setBorderPainted(false);
        button.setFocusPainted(false);
        // Ancho reducido para centrarse dentro del sidebar
        button.setMaximumSize(new Dimension(UIConstants.SIDEBAR_WIDTH - 20, 45));
        button.setAlignmentX(Component.CENTER_ALIGNMENT); // Clave para centrar
        button.setCursor(new Cursor(Cursor.HAND_CURSOR));
        button.setHorizontalAlignment(SwingConstants.LEFT);
        button.setBorder(BorderFactory.createEmptyBorder(10, 15, 10, 15));

        // Efecto hover (del segundo código)
        button.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseEntered(MouseEvent e) {
                if (isNavButton) {
                    // Color de hover oscuro para el sidebar
                    button.setBackground(new Color(55, 65, 81)); 
                    button.setForeground(Color.WHITE);
                } else {
                    button.setBackground(UIConstants.DANGER_COLOR.darker());
                }
            }

            @Override
            public void mouseExited(MouseEvent e) {
                if (isNavButton) {
                    button.setBackground(UIConstants.SIDEBAR_COLOR);
                    button.setForeground(Color.WHITE);
                } else {
                    button.setBackground(UIConstants.DANGER_COLOR);
                }
            }
        });

        if (cardName != null) {
            button.addActionListener(e -> {
                // Acciones específicas antes de mostrar la tarjeta (Mantenido del código original)
                switch (cardName) {
                    case "dashboard":
                        if (dashboardPanel != null) dashboardPanel.actualizarDashboard();
                        break;
                    case "ventas":
                        ventaPanel.cargarProductos();
                        break;
                    case "productos":
                        productoPanel.loadProductos();
                        break;
                    case "usuarios":
                        if (usuarioPanel != null) usuarioPanel.cargarUsuarios();
                        break;
                }

                // Mostrar tarjeta
                cardLayout.show(contentPanel, cardName);
            });
        }

        return button;
    }

    private JPanel createWelcomePanel() {
        JPanel panel = new JPanel(new GridBagLayout());
        panel.setBackground(UIConstants.BACKGROUND_COLOR);

        JPanel contentBox = new JPanel();
        contentBox.setLayout(new BoxLayout(contentBox, BoxLayout.Y_AXIS));
        contentBox.setBackground(Color.WHITE);
        contentBox.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(UIConstants.BORDER_COLOR, 1),
            BorderFactory.createEmptyBorder(60, 80, 60, 80)
        ));

        JLabel welcomeIcon = new JLabel("👋");
        welcomeIcon.setFont(new Font("Segoe UI", Font.PLAIN, 72));
        welcomeIcon.setAlignmentX(Component.CENTER_ALIGNMENT);

        JLabel welcomeLabel = new JLabel("¡Bienvenido a LibreTools!");
        welcomeLabel.setFont(UIConstants.TITLE_FONT);
        welcomeLabel.setForeground(UIConstants.TEXT_COLOR);
        welcomeLabel.setAlignmentX(Component.CENTER_ALIGNMENT);

        JLabel userWelcome = new JLabel(SessionManager.getInstance().getNombreUsuario());
        userWelcome.setFont(UIConstants.SUBTITLE_FONT);
        userWelcome.setForeground(UIConstants.PRIMARY_COLOR);
        userWelcome.setAlignmentX(Component.CENTER_ALIGNMENT);

        JLabel description = new JLabel("Sistema de gestión de inventario y ventas");
        description.setFont(UIConstants.NORMAL_FONT);
        description.setForeground(UIConstants.TEXT_SECONDARY);
        description.setAlignmentX(Component.CENTER_ALIGNMENT);

        contentBox.add(welcomeIcon);
        contentBox.add(Box.createVerticalStrut(20));
        contentBox.add(welcomeLabel);
        contentBox.add(Box.createVerticalStrut(10));
        contentBox.add(userWelcome);
        contentBox.add(Box.createVerticalStrut(15));
        contentBox.add(description);

        panel.add(contentBox);
        return panel;
    }

    private void cerrarSesion() {
        int option = JOptionPane.showConfirmDialog(
            this,
            "¿Está seguro de cerrar sesión?",
            "Cerrar Sesión",
            JOptionPane.YES_NO_OPTION
        );

        if (option == JOptionPane.YES_OPTION) {
            SessionManager.getInstance().cerrarSesion();
            dispose();
            // NOTA: Se asume que LoginFrame existe
            new LoginFrame().setVisible(true);
        }
    }
}