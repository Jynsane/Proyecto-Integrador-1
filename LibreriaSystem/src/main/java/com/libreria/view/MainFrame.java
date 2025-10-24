package com.libreria.view;

import javax.swing.*;
import java.awt.*;
import com.libreria.util.UIConstants;

public class MainFrame extends JFrame {
    private JPanel mainPanel;
    private JPanel sidebarPanel;
    private JPanel contentPanel;
    private ProductoPanel productoPanel;
    private VentaPanel ventaPanel;
    private DashboardPanel dashboardPanel;
    private CardLayout cardLayout;
    private JLabel userLabel;
    
    public MainFrame() {
        initComponents();
    }
    
    private void initComponents() {
        setTitle("LibreTools");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(1200, 800);
        setLocationRelativeTo(null);
        
        // Configurar colores y fuentes base
        UIManager.put("Panel.background", UIConstants.BACKGROUND_COLOR);
        UIManager.put("OptionPane.background", UIConstants.BACKGROUND_COLOR);
        UIManager.put("Button.font", UIConstants.NORMAL_FONT);
        
        // Crear panel principal con BorderLayout
        mainPanel = new JPanel(new BorderLayout());
        mainPanel.setBackground(UIConstants.BACKGROUND_COLOR);
        
        // Crear sidebar
        sidebarPanel = new JPanel();
        sidebarPanel.setPreferredSize(new Dimension(200, 0));
        sidebarPanel.setLayout(new BoxLayout(sidebarPanel, BoxLayout.Y_AXIS));
        sidebarPanel.setBackground(UIConstants.PRIMARY_COLOR);
        
        // Logo y título
        JPanel logoPanel = new JPanel(new FlowLayout(FlowLayout.CENTER));
        logoPanel.setBackground(UIConstants.PRIMARY_COLOR);
        JLabel logoLabel = new JLabel("LibreTools");
        logoLabel.setFont(UIConstants.TITLE_FONT);
        logoLabel.setForeground(Color.WHITE);
        logoPanel.add(logoLabel);
        
        // Usuario
        JPanel userPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        userPanel.setBackground(UIConstants.PRIMARY_COLOR);
        userLabel = new JLabel("Jair Garcia");
        userLabel.setFont(UIConstants.NORMAL_FONT);
        userLabel.setForeground(Color.WHITE);
        userPanel.add(userLabel);
        
        // Botones de navegación
        JButton btnInicio = createSidebarButton("Inicio", "welcome");
        JButton btnVentas = createSidebarButton("Venta", "ventas");
        JButton btnInventario = createSidebarButton("Inventario", "productos");
        JButton btnResumen = createSidebarButton("Resumen", "dashboard");
        
        // Agregar componentes al sidebar
        sidebarPanel.add(Box.createVerticalStrut(20));
        sidebarPanel.add(logoPanel);
        sidebarPanel.add(Box.createVerticalStrut(20));
        sidebarPanel.add(userPanel);
        sidebarPanel.add(Box.createVerticalStrut(40));
        sidebarPanel.add(btnInicio);
        sidebarPanel.add(Box.createVerticalStrut(10));
        sidebarPanel.add(btnVentas);
        sidebarPanel.add(Box.createVerticalStrut(10));
        sidebarPanel.add(btnInventario);
        sidebarPanel.add(Box.createVerticalStrut(10));
        sidebarPanel.add(btnResumen);
        
        // Panel de contenido
        contentPanel = new JPanel(new CardLayout());
        contentPanel.setBackground(UIConstants.BACKGROUND_COLOR);
        cardLayout = (CardLayout) contentPanel.getLayout();
        
        // Inicializar paneles
        productoPanel = new ProductoPanel();
        ventaPanel = new VentaPanel();
        dashboardPanel = new DashboardPanel();
        
        // Panel de bienvenida
        JPanel welcomePanel = new JPanel(new GridBagLayout());
        welcomePanel.setBackground(UIConstants.BACKGROUND_COLOR);
        JLabel welcomeLabel = new JLabel("Bienvenido a LibreTools");
        welcomeLabel.setFont(UIConstants.TITLE_FONT);
        welcomeLabel.setForeground(UIConstants.TEXT_COLOR);
        welcomePanel.add(welcomeLabel);
        
        // Agregar paneles al CardLayout
        contentPanel.add(welcomePanel, "welcome");
        contentPanel.add(productoPanel, "productos");
        contentPanel.add(ventaPanel, "ventas");
        contentPanel.add(dashboardPanel, "dashboard");
        
    // Agregar sidebar y contenido al panel principal
    mainPanel.add(sidebarPanel, BorderLayout.WEST);
    mainPanel.add(contentPanel, BorderLayout.CENTER);
        
        add(mainPanel);
    }
    
    private JButton createSidebarButton(String text, String cardName) {
        JButton button = new JButton(text);
        button.setFont(UIConstants.NORMAL_FONT);
        button.setForeground(Color.WHITE);
        button.setBackground(UIConstants.PRIMARY_COLOR);
        button.setBorderPainted(false);
        button.setFocusPainted(false);
        button.setMaximumSize(new Dimension(200, 40));
        button.setAlignmentX(Component.CENTER_ALIGNMENT);
        button.setCursor(new Cursor(Cursor.HAND_CURSOR));
        
        button.addActionListener(e -> {
            // Acciones específicas antes de mostrar la tarjeta
            if (cardName.equals("dashboard")) {
                dashboardPanel.actualizarDashboard();
            } else if (cardName.equals("ventas")) {
                ventaPanel.cargarProductos();
            } else if (cardName.equals("productos")) {
                productoPanel.loadProductos();
            }

            // Mostrar tarjeta
            cardLayout.show(contentPanel, cardName);
        });
        
        return button;
    }
    
    public static void main(String[] args) {
        try {
            // Usar Nimbus Look and Feel para un aspecto más moderno
            for (UIManager.LookAndFeelInfo info : UIManager.getInstalledLookAndFeels()) {
                if ("Nimbus".equals(info.getName())) {
                    UIManager.setLookAndFeel(info.getClassName());
                    break;
                }
            }
        } catch (Exception e) {
            try {
                UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
            } catch (Exception ex) {
                ex.printStackTrace();
            }
        }
        
        SwingUtilities.invokeLater(() -> {
            MainFrame frame = new MainFrame();
            frame.setVisible(true);
        });
    }
}