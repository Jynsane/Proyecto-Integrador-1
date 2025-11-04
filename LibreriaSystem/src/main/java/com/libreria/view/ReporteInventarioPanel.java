package com.libreria.view;

import com.libreria.controller.ReporteController;
import com.libreria.controller.ProductoController;
import com.libreria.model.Producto;
import com.libreria.util.UIConstants;
import org.jfree.chart.*;
import org.jfree.chart.plot.CategoryPlot;
import org.jfree.data.category.DefaultCategoryDataset;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.List;

public class ReporteInventarioPanel extends JDialog {
    private final ReporteController reporteController;
    private final ProductoController productoController;
    private JTable tabla;
    private DefaultTableModel modelo;
    private JLabel lblTotalProductos;
    private JLabel lblTotalValor;
    private JLabel lblCategorias;

    public ReporteInventarioPanel(Frame parent) {
        super(parent, "📊 Reporte de Inventario", true);
        this.reporteController = new ReporteController();
        this.productoController = new ProductoController();
        initComponents();
        cargarDatos();
        
        setSize(1400, 800);
        setLocationRelativeTo(parent);
    }

    private void initComponents() {
        setLayout(new BorderLayout(0, 0));
        getContentPane().setBackground(UIConstants.BACKGROUND_COLOR);

        // Header
        JPanel headerPanel = createHeaderPanel();
        
        // Stats Cards
        JPanel statsPanel = createStatsPanel();
        
        // Panel central con tabla y gráfico
        JPanel centerPanel = new JPanel(new GridLayout(1, 2, 15, 0));
        centerPanel.setBackground(UIConstants.BACKGROUND_COLOR);
        centerPanel.setBorder(BorderFactory.createEmptyBorder(0, 20, 20, 20));
        
        // Tabla
        JPanel tablePanel = createTablePanel();
        
        // Gráfico
        JPanel chartPanel = createChartPanel();
        
        centerPanel.add(tablePanel);
        centerPanel.add(chartPanel);
        
        // Botones
        JPanel buttonPanel = createButtonPanel();

        add(headerPanel, BorderLayout.NORTH);
        add(statsPanel, BorderLayout.CENTER);
        add(centerPanel, BorderLayout.SOUTH);
        
        JPanel bottomPanel = new JPanel(new BorderLayout());
        bottomPanel.setBackground(Color.WHITE);
        bottomPanel.setBorder(BorderFactory.createMatteBorder(1, 0, 0, 0, UIConstants.BORDER_COLOR));
        bottomPanel.add(buttonPanel, BorderLayout.EAST);
        add(bottomPanel, BorderLayout.PAGE_END);
    }

    private JPanel createHeaderPanel() {
        JPanel panel = new JPanel(new BorderLayout());
        panel.setBackground(UIConstants.SIDEBAR_COLOR);
        panel.setBorder(BorderFactory.createEmptyBorder(25, 30, 25, 30));

        JLabel titleLabel = new JLabel("📊 Reporte de Inventario");
        titleLabel.setFont(new Font("Segoe UI", Font.BOLD, 26));
        titleLabel.setForeground(Color.WHITE);

        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm");
        JLabel fechaLabel = new JLabel("Fecha: " + LocalDateTime.now().format(formatter));
        fechaLabel.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        fechaLabel.setForeground(Color.WHITE);

        panel.add(titleLabel, BorderLayout.WEST);
        panel.add(fechaLabel, BorderLayout.EAST);

        return panel;
    }

    private JPanel createStatsPanel() {
        JPanel panel = new JPanel(new GridLayout(1, 3, 20, 0));
        panel.setBackground(UIConstants.BACKGROUND_COLOR);
        panel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));

        // Total Productos
        JPanel totalCard = createStatCard("📦", "Total Productos", "0", UIConstants.PRIMARY_COLOR);
        lblTotalProductos = (JLabel) ((JPanel) totalCard.getComponent(1)).getComponent(1);

        // Valor Total
        JPanel valorCard = createStatCard("💰", "Valor Total", "S/ 0.00", UIConstants.SECONDARY_COLOR);
        lblTotalValor = (JLabel) ((JPanel) valorCard.getComponent(1)).getComponent(1);

        // Categorías
        JPanel catCard = createStatCard("🏷️", "Categorías", "0", UIConstants.ACCENT_COLOR);
        lblCategorias = (JLabel) ((JPanel) catCard.getComponent(1)).getComponent(1);

        panel.add(totalCard);
        panel.add(valorCard);
        panel.add(catCard);

        return panel;
    }

    private JPanel createStatCard(String icon, String title, String value, Color color) {
        JPanel card = new JPanel(new BorderLayout(15, 10));
        card.setBackground(Color.WHITE);
        card.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(new Color(220, 220, 220), 1),
            BorderFactory.createEmptyBorder(25, 30, 25, 30)
        ));

        JLabel iconLabel = new JLabel(icon);
        iconLabel.setFont(new Font("Segoe UI", Font.PLAIN, 48));

        JPanel textPanel = new JPanel();
        textPanel.setLayout(new BoxLayout(textPanel, BoxLayout.Y_AXIS));
        textPanel.setBackground(Color.WHITE);

        JLabel titleLabel = new JLabel(title);
        titleLabel.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        titleLabel.setForeground(new Color(120, 120, 120));

        JLabel valueLabel = new JLabel(value);
        valueLabel.setFont(new Font("Segoe UI", Font.BOLD, 32));
        valueLabel.setForeground(color);

        textPanel.add(titleLabel);
        textPanel.add(Box.createVerticalStrut(5));
        textPanel.add(valueLabel);

        card.add(iconLabel, BorderLayout.WEST);
        card.add(textPanel, BorderLayout.CENTER);

        return card;
    }

    private JPanel createTablePanel() {
        JPanel panel = new JPanel(new BorderLayout(0, 10));
        panel.setBackground(Color.WHITE);
        panel.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(new Color(220, 220, 220), 1),
            BorderFactory.createEmptyBorder(15, 15, 15, 15)
        ));

        JLabel title = new JLabel("Detalle de Productos");
        title.setFont(new Font("Segoe UI", Font.BOLD, 16));
        title.setForeground(UIConstants.TEXT_COLOR);

        modelo = new DefaultTableModel(
            new Object[]{"Código", "Nombre", "Categoría", "Stock", "Precio"},
            0
        ) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };

        tabla = new JTable(modelo);
        tabla.setFont(new Font("Arial", Font.PLAIN, 12));
        tabla.setRowHeight(35);
        tabla.setShowGrid(true);
        tabla.setGridColor(new Color(230, 230, 230));
        tabla.setBackground(Color.WHITE);
        tabla.setForeground(Color.BLACK);

        tabla.getTableHeader().setFont(new Font("Arial", Font.BOLD, 12));
        tabla.getTableHeader().setBackground(new Color(245, 245, 245));
        tabla.getTableHeader().setForeground(Color.BLACK);
        tabla.getTableHeader().setPreferredSize(new Dimension(0, 35));

        JScrollPane scrollPane = new JScrollPane(tabla);
        scrollPane.setBorder(null);
        scrollPane.setPreferredSize(new Dimension(0, 300));

        panel.add(title, BorderLayout.NORTH);
        panel.add(scrollPane, BorderLayout.CENTER);

        return panel;
    }

    private JPanel createChartPanel() {
        JPanel panel = new JPanel(new BorderLayout());
        panel.setBackground(Color.WHITE);
        panel.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(new Color(220, 220, 220), 1),
            BorderFactory.createEmptyBorder(15, 15, 15, 15)
        ));

        JLabel title = new JLabel("Stock por Categoría");
        title.setFont(new Font("Segoe UI", Font.BOLD, 16));
        title.setForeground(UIConstants.TEXT_COLOR);

        panel.add(title, BorderLayout.NORTH);

        return panel;
    }

    private JPanel createButtonPanel() {
        JPanel panel = new JPanel(new FlowLayout(FlowLayout.RIGHT, 15, 20));
        panel.setBackground(Color.WHITE);

        JButton btnExportar = createStyledButton("📥 Exportar a Excel", UIConstants.SECONDARY_COLOR);
        JButton btnImprimir = createStyledButton("🖨️ Imprimir", UIConstants.PRIMARY_COLOR);
        JButton btnCerrar = createStyledButton("❌ Cerrar", UIConstants.DANGER_COLOR);

        btnExportar.addActionListener(e -> exportarExcel());
        btnImprimir.addActionListener(e -> imprimir());
        btnCerrar.addActionListener(e -> dispose());

        panel.add(btnExportar);
        panel.add(btnImprimir);
        panel.add(btnCerrar);

        return panel;
    }

    private JButton createStyledButton(String text, Color bgColor) {
        JButton button = new JButton(text);
        button.setFont(UIConstants.BUTTON_FONT);
        button.setForeground(Color.WHITE);
        button.setBackground(bgColor);
        button.setBorderPainted(false);
        button.setFocusPainted(false);
        button.setPreferredSize(new Dimension(180, 40));
        button.setCursor(new Cursor(Cursor.HAND_CURSOR));
        
        button.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseEntered(java.awt.event.MouseEvent evt) {
                button.setBackground(bgColor.darker());
            }
            public void mouseExited(java.awt.event.MouseEvent evt) {
                button.setBackground(bgColor);
            }
        });
        
        return button;
    }

    private void cargarDatos() {
        try {
            List<Producto> productos = productoController.obtenerTodos();
            
            // Actualizar tabla
            modelo.setRowCount(0);
            for (Producto p : productos) {
                modelo.addRow(new Object[]{
                    p.getCodigo(),
                    p.getNombre(),
                    p.getCategoria(),
                    p.getStock(),
                    String.format("S/ %.2f", p.getPrecio())
                });
            }

            // Calcular estadísticas
            int totalStock = productos.stream().mapToInt(Producto::getStock).sum();
            double valorTotal = productos.stream()
                .mapToDouble(p -> p.getPrecio() * p.getStock()).sum();
            Set<String> categorias = new HashSet<>();
            productos.forEach(p -> categorias.add(p.getCategoria()));

            lblTotalProductos.setText(String.valueOf(totalStock));
            lblTotalValor.setText(String.format("S/ %.2f", valorTotal));
            lblCategorias.setText(String.valueOf(categorias.size()));

        } catch (Exception e) {
            JOptionPane.showMessageDialog(this,
                "Error al cargar datos: " + e.getMessage(),
                "Error",
                JOptionPane.ERROR_MESSAGE);
        }
    }

    private void exportarExcel() {
        try {
            JFileChooser fileChooser = new JFileChooser();
            fileChooser.setDialogTitle("Guardar Reporte");
            fileChooser.setSelectedFile(new java.io.File("Reporte_Inventario_" + 
                LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyyMMdd_HHmmss")) + ".xlsx"));
            
            int userSelection = fileChooser.showSaveDialog(this);
            
            if (userSelection == JFileChooser.APPROVE_OPTION) {
                String rutaArchivo = fileChooser.getSelectedFile().getAbsolutePath();
                if (!rutaArchivo.endsWith(".xlsx")) {
                    rutaArchivo += ".xlsx";
                }
                
                reporteController.generarReporteInventarioExcel(rutaArchivo);
                
                JOptionPane.showMessageDialog(this,
                    "Reporte exportado exitosamente",
                    "Éxito",
                    JOptionPane.INFORMATION_MESSAGE);
            }
        } catch (Exception e) {
            JOptionPane.showMessageDialog(this,
                "Error al exportar: " + e.getMessage(),
                "Error",
                JOptionPane.ERROR_MESSAGE);
        }
    }

    private void imprimir() {
        try {
            tabla.print();
        } catch (Exception e) {
            JOptionPane.showMessageDialog(this,
                "Error al imprimir: " + e.getMessage(),
                "Error",
                JOptionPane.ERROR_MESSAGE);
        }
    }
}