package com.libreria.view;

import com.libreria.controller.ReporteController;
import com.libreria.model.Producto;
import com.libreria.util.UIConstants;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.io.File;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.List;
import java.util.Map;

public class DashboardPanel extends BasePanel {
    private final ReporteController reporteController;
    private JLabel lblVentasDia;
    private JLabel lblTotalProductos;
    private DefaultTableModel modeloBajoStock;
    private DefaultTableModel modeloTopProductos;
    private JTable tablaBajoStock;
    private JTable tablaTopProductos;

    public DashboardPanel() {
        this.reporteController = new ReporteController();
        initComponents();
        cargarDatos();
    }

    private void initComponents() {
        setLayout(new BorderLayout(10, 10));
        setBackground(UIConstants.BACKGROUND_COLOR);
        setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));

        // Panel superior con título
        JPanel headerPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        headerPanel.setBackground(Color.WHITE);
        headerPanel.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createMatteBorder(0, 0, 1, 0, new Color(220, 220, 220)),
            BorderFactory.createEmptyBorder(15, 15, 15, 15)
        ));
        
        JLabel titleLabel = new JLabel("📊 Dashboard - Resumen General");
        titleLabel.setFont(UIConstants.SUBTITLE_FONT);
        titleLabel.setForeground(Color.BLACK);
        headerPanel.add(titleLabel);

        // Panel de estadísticas (cards)
        JPanel statsPanel = new JPanel(new GridLayout(1, 2, 15, 0));
        statsPanel.setBackground(UIConstants.BACKGROUND_COLOR);
        
        // Card de ventas del día
        JPanel ventasCard = createStatsCard("💰 Ventas del Día", "S/ 0.00", UIConstants.PRIMARY_COLOR);
        lblVentasDia = (JLabel) ((JPanel) ventasCard.getComponent(0)).getComponent(2); // El JLabel de valor es el índice 2 (después de título y strut)
        
        // Card de productos en stock
        JPanel productosCard = createStatsCard("📦 Productos en Stock", "0", UIConstants.SECONDARY_COLOR);
        lblTotalProductos = (JLabel) ((JPanel) productosCard.getComponent(0)).getComponent(2); // El JLabel de valor es el índice 2
        
        statsPanel.add(ventasCard);
        statsPanel.add(productosCard);

        // Panel central con tablas
        JPanel centerPanel = new JPanel(new GridLayout(1, 2, 15, 0));
        centerPanel.setBackground(UIConstants.BACKGROUND_COLOR);
        
        // Panel izquierdo: Productos con bajo stock
        JPanel leftPanel = createTablePanel("⚠️ Productos con Bajo Stock");
        
        modeloBajoStock = new DefaultTableModel(
            new Object[]{"Código", "Nombre", "Stock"}, 
            0
        ) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };

        tablaBajoStock = createStyledTable(modeloBajoStock);
        JScrollPane scrollPaneLeft = new JScrollPane(tablaBajoStock);
        styleScrollPane(scrollPaneLeft);
        leftPanel.add(scrollPaneLeft, BorderLayout.CENTER);
        
        // Panel derecho: Top productos vendidos
        JPanel rightPanel = createTablePanel("🏆 Top 5 Productos Más Vendidos");
        
        modeloTopProductos = new DefaultTableModel(
            new Object[]{"Código", "Nombre", "Unidades", "Ingresos"}, 
            0
        ) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };

        tablaTopProductos = createStyledTable(modeloTopProductos);
        JScrollPane scrollPaneRight = new JScrollPane(tablaTopProductos);
        styleScrollPane(scrollPaneRight);
        rightPanel.add(scrollPaneRight, BorderLayout.CENTER);
        
        centerPanel.add(leftPanel);
        centerPanel.add(rightPanel);

        // Panel de botones
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT, 10, 0));
        buttonPanel.setBackground(Color.WHITE);
        buttonPanel.setBorder(BorderFactory.createEmptyBorder(15, 15, 15, 15));
        
        JButton btnReporteInventario = createStyledButton("📄 Reporte de Inventario", UIConstants.SECONDARY_COLOR);
        JButton btnReporteVentas = createStyledButton("📈 Reporte de Ventas", UIConstants.PRIMARY_COLOR);
        JButton btnVerVentas = createStyledButton("🛒 Ver Listado de Ventas", new Color(100, 100, 100));
        
        buttonPanel.add(btnReporteInventario);
        buttonPanel.add(btnReporteVentas);
        buttonPanel.add(btnVerVentas);

        // Layout principal
        JPanel mainPanel = new JPanel(new BorderLayout(0, 15));
        mainPanel.setBackground(UIConstants.BACKGROUND_COLOR);
        mainPanel.add(statsPanel, BorderLayout.NORTH);
        mainPanel.add(centerPanel, BorderLayout.CENTER);
        mainPanel.add(buttonPanel, BorderLayout.SOUTH);
        
        add(headerPanel, BorderLayout.NORTH);
        add(mainPanel, BorderLayout.CENTER);

        // Eventos
        btnReporteInventario.addActionListener(e -> generarReporteInventario());
        btnReporteVentas.addActionListener(e -> generarReporteVentas());
        btnVerVentas.addActionListener(e -> {
            Window win = SwingUtilities.getWindowAncestor(this);
            Frame frame = (win instanceof Frame) ? (Frame) win : null;
            SalesListDialog dlg = new SalesListDialog(frame);
            dlg.setVisible(true);
        });
    }

    private JPanel createStatsCard(String titulo, String valor, Color color) {
        JPanel card = new JPanel(new BorderLayout());
        card.setBackground(Color.WHITE);
        card.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(new Color(220, 220, 220), 1),
            BorderFactory.createEmptyBorder(20, 20, 20, 20)
        ));
        
        JPanel contentPanel = new JPanel();
        contentPanel.setLayout(new BoxLayout(contentPanel, BoxLayout.Y_AXIS));
        contentPanel.setBackground(Color.WHITE);
        
        JLabel lblTitulo = new JLabel(titulo);
        lblTitulo.setFont(new Font("Segoe UI", Font.BOLD, 14));
        lblTitulo.setForeground(new Color(100, 100, 100));
        lblTitulo.setAlignmentX(Component.LEFT_ALIGNMENT);
        
        JLabel lblValor = new JLabel(valor);
        lblValor.setFont(new Font("Segoe UI", Font.BOLD, 32));
        lblValor.setForeground(color);
        lblValor.setAlignmentX(Component.LEFT_ALIGNMENT);
        
        contentPanel.add(lblTitulo);
        contentPanel.add(Box.createVerticalStrut(10));
        contentPanel.add(lblValor);
        
        card.add(contentPanel, BorderLayout.CENTER);
        return card;
    }

    private JPanel createTablePanel(String titulo) {
        JPanel panel = new JPanel(new BorderLayout());
        panel.setBackground(Color.WHITE);
        panel.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(new Color(220, 220, 220), 1),
            BorderFactory.createEmptyBorder(15, 15, 15, 15)
        ));
        
        JLabel titleLabel = new JLabel(titulo);
        titleLabel.setFont(new Font("Segoe UI", Font.BOLD, 14));
        titleLabel.setForeground(Color.BLACK);
        titleLabel.setBorder(BorderFactory.createEmptyBorder(0, 0, 10, 0));
        
        panel.add(titleLabel, BorderLayout.NORTH);
        return panel;
    }

    private JTable createStyledTable(DefaultTableModel model) {
        JTable table = new JTable(model);
        table.setFont(UIConstants.NORMAL_FONT);
        table.setRowHeight(35);
        table.setShowGrid(true);
        table.setGridColor(new Color(230, 230, 230));
        table.setBackground(Color.WHITE);
        table.setForeground(Color.BLACK);
        table.setSelectionBackground(new Color(230, 240, 255));
        table.setSelectionForeground(Color.BLACK);
        
        table.getTableHeader().setFont(UIConstants.NORMAL_FONT.deriveFont(Font.BOLD));
        table.getTableHeader().setBackground(new Color(245, 245, 245));
        table.getTableHeader().setForeground(Color.BLACK);
        table.getTableHeader().setPreferredSize(new Dimension(table.getTableHeader().getPreferredSize().width, 35));
        
        return table;
    }

    private void styleScrollPane(JScrollPane scrollPane) {
        scrollPane.setBorder(BorderFactory.createLineBorder(new Color(220, 220, 220), 1));
        scrollPane.setBackground(Color.WHITE);
        scrollPane.getViewport().setBackground(Color.WHITE);
    }

    private void cargarDatos() {
        try {
            Map<String, Object> estadisticas = reporteController.generarEstadisticasDashboard();
            
            // Actualizar ventas del día
            double ventasDia = (double) estadisticas.get("ventasDia");
            lblVentasDia.setText(String.format("S/ %.2f", ventasDia));
            
            // Actualizar total de productos
            long totalProductos = (long) estadisticas.get("totalProductos");
            lblTotalProductos.setText(String.valueOf(totalProductos));
            
            // Actualizar tabla de productos con bajo stock
            modeloBajoStock.setRowCount(0);
            @SuppressWarnings("unchecked")
            List<Producto> productosBajoStock = (List<Producto>) estadisticas.get("productoBajoStock");
            for (Producto p : productosBajoStock) {
                modeloBajoStock.addRow(new Object[]{
                    p.getCodigo(),
                    p.getNombre(),
                    p.getStock()
                });
            }
            
            // Actualizar tabla de productos más vendidos
            modeloTopProductos.setRowCount(0);
            @SuppressWarnings("unchecked")
            List<Map<String, Object>> topProductos = (List<Map<String, Object>>) estadisticas.get("topProductos");
            for (Map<String, Object> p : topProductos) {
                modeloTopProductos.addRow(new Object[]{
                    p.get("codigo"),
                    p.get("nombre"),
                    p.get("totalVendido"),
                    String.format("S/ %.2f", (Double) p.get("totalIngresos"))
                });
            }
        } catch (Exception e) {
            showError("Error al cargar datos del dashboard: " + e.getMessage());
        }
    }

    private void generarReporteInventario() {
        JFileChooser fileChooser = new JFileChooser();
        fileChooser.setDialogTitle("Guardar Reporte de Inventario");
        fileChooser.setSelectedFile(new File("ReporteInventario.xlsx"));
        
        if (fileChooser.showSaveDialog(this) == JFileChooser.APPROVE_OPTION) {
            try {
                File file = fileChooser.getSelectedFile();
                reporteController.generarReporteInventarioExcel(file.getAbsolutePath());
                showInfo("✓ Reporte generado exitosamente");
            } catch (Exception e) {
                showError("Error al generar reporte: " + e.getMessage());
            }
        }
    }

    private void generarReporteVentas() {
        JFileChooser fileChooser = new JFileChooser();
        fileChooser.setDialogTitle("Guardar Reporte de Ventas");
        fileChooser.setSelectedFile(new File("ReporteVentas.xlsx"));
        
        if (fileChooser.showSaveDialog(this) == JFileChooser.APPROVE_OPTION) {
            try {
                File file = fileChooser.getSelectedFile();
                // Usar el día actual completo: desde medianoche hasta el final del día
                LocalDate today = LocalDate.now();
                LocalDateTime inicio = LocalDateTime.of(today, LocalTime.MIN); // 00:00:00.000
                LocalDateTime fin = LocalDateTime.of(today, LocalTime.MAX);    // 23:59:59.999999999
                reporteController.generarReporteVentasExcel(file.getAbsolutePath(), inicio, fin);
                showInfo("✓ Reporte generado exitosamente");
            } catch (Exception e) {
                showError("Error al generar reporte: " + e.getMessage());
            }
        }
    }

    public void actualizarDashboard() {
        cargarDatos();
    }
}
