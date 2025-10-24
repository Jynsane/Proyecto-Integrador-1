package com.libreria.view;

import com.libreria.controller.ReporteController;
import com.libreria.model.Producto;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.io.File;
import java.time.LocalDateTime;
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
        setLayout(new BorderLayout());
        setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        // Panel superior con indicadores
        JPanel topPanel = new JPanel(new GridLayout(1, 2, 10, 0));
        
        // Panel de ventas del día
        JPanel ventasPanel = createIndicatorPanel("Ventas del Día");
        lblVentasDia = new JLabel("S/. 0.00");
        lblVentasDia.setFont(new Font("Arial", Font.BOLD, 24));
        ventasPanel.add(lblVentasDia);
        
        // Panel de total de productos
        JPanel productosPanel = createIndicatorPanel("Total Productos en Stock");
        lblTotalProductos = new JLabel("0");
        lblTotalProductos.setFont(new Font("Arial", Font.BOLD, 24));
        productosPanel.add(lblTotalProductos);
        
        topPanel.add(ventasPanel);
        topPanel.add(productosPanel);

        // Tabla de productos con bajo stock
        JPanel centerPanel = new JPanel(new BorderLayout());
        // Panel para las tablas (dividido en dos)
        JSplitPane splitPane = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT);
        centerPanel.add(splitPane, BorderLayout.CENTER);
        
        // Panel izquierdo: Productos con bajo stock
        JPanel leftPanel = new JPanel(new BorderLayout());
        leftPanel.setBorder(BorderFactory.createTitledBorder("Productos con Bajo Stock"));
        
        modeloBajoStock = new DefaultTableModel(
            new Object[]{"Código", "Nombre", "Stock Actual"}, 
            0
        ) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };

        tablaBajoStock = new JTable(modeloBajoStock);
        JScrollPane scrollPaneLeft = new JScrollPane(tablaBajoStock);
        leftPanel.add(scrollPaneLeft, BorderLayout.CENTER);
        
        // Panel derecho: Top productos vendidos
        JPanel rightPanel = new JPanel(new BorderLayout());
        rightPanel.setBorder(BorderFactory.createTitledBorder("Top 5 Productos más Vendidos"));
        
        modeloTopProductos = new DefaultTableModel(
            new Object[]{"Código", "Nombre", "Unidades Vendidas", "Total Ingresos"}, 
            0
        ) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };

        tablaTopProductos = new JTable(modeloTopProductos);
        JScrollPane scrollPaneRight = new JScrollPane(tablaTopProductos);
        rightPanel.add(scrollPaneRight, BorderLayout.CENTER);
        
        // Agregar paneles al split
        splitPane.setLeftComponent(leftPanel);
        splitPane.setRightComponent(rightPanel);
        splitPane.setResizeWeight(0.5); // Distribuir el espacio equitativamente

        // Panel de botones para reportes
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        JButton btnReporteInventario = new JButton("Generar Reporte de Inventario");
        JButton btnReporteVentas = new JButton("Generar Reporte de Ventas");
    JButton btnVerVentas = new JButton("Ver Ventas");
        
        configureButton(btnReporteInventario);
        configureButton(btnReporteVentas);
        
        buttonPanel.add(btnReporteInventario);
        buttonPanel.add(btnReporteVentas);
    buttonPanel.add(btnVerVentas);

        // Layout principal
        JPanel mainPanel = new JPanel(new BorderLayout(0, 10));
        mainPanel.add(topPanel, BorderLayout.NORTH);
        mainPanel.add(centerPanel, BorderLayout.CENTER);
        mainPanel.add(buttonPanel, BorderLayout.SOUTH);
        
        add(mainPanel);

        // Eventos
        btnReporteInventario.addActionListener(e -> generarReporteInventario());
        btnReporteVentas.addActionListener(e -> generarReporteVentas());
        btnVerVentas.addActionListener(e -> {
            java.awt.Window win = SwingUtilities.getWindowAncestor(this);
            java.awt.Frame frame = (win instanceof java.awt.Frame) ? (java.awt.Frame) win : null;
            SalesListDialog dlg = new SalesListDialog(frame);
            dlg.setVisible(true);
        });
    }

    private JPanel createIndicatorPanel(String titulo) {
        JPanel panel = new JPanel(new BorderLayout());
        panel.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createTitledBorder(titulo),
            BorderFactory.createEmptyBorder(10, 10, 10, 10)
        ));
        return panel;
    }

    private void cargarDatos() {
        try {
            Map<String, Object> estadisticas = reporteController.generarEstadisticasDashboard();
            
            // Actualizar ventas del día
            double ventasDia = (double) estadisticas.get("ventasDia");
            lblVentasDia.setText(String.format("S/. %.2f", ventasDia));
            
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
                    String.format("S/. %.2f", (Double)p.get("totalIngresos"))
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
                showInfo("Reporte generado exitosamente");
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
                // Por defecto, generamos el reporte del día actual
                LocalDateTime inicio = LocalDateTime.now().withHour(0).withMinute(0);
                LocalDateTime fin = LocalDateTime.now().withHour(23).withMinute(59);
                reporteController.generarReporteVentasExcel(file.getAbsolutePath(), inicio, fin);
                showInfo("Reporte generado exitosamente");
            } catch (Exception e) {
                showError("Error al generar reporte: " + e.getMessage());
            }
        }
    }

    public void actualizarDashboard() {
        cargarDatos();
    }
}