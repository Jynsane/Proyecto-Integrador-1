package com.libreria.view;

import com.libreria.controller.ReporteController;
import com.libreria.model.Producto;
import com.libreria.util.UIConstants;
import org.jfree.chart.*;
import org.jfree.chart.plot.*;
import org.jfree.chart.renderer.category.BarRenderer;
import org.jfree.chart.labels.ItemLabelAnchor;
import org.jfree.chart.labels.ItemLabelPosition;
import org.jfree.chart.labels.StandardCategoryItemLabelGenerator;
import org.jfree.chart.ui.RectangleEdge;
import org.jfree.chart.ui.TextAnchor;
import org.jfree.data.category.DefaultCategoryDataset;
import org.jfree.data.general.DefaultPieDataset;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.DefaultTableCellRenderer;
import java.awt.*;
import java.io.File;
import java.text.DecimalFormat;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.*;
import java.util.List;

public class DashboardPanel extends BasePanel {
    private final ReporteController reporteController;
    private JLabel lblVentasDia1;
    private JLabel lblVentasDia2;
    private JLabel lblTotalProductos;
    private JLabel lblCategorias;
    private DefaultTableModel modeloBajoStock;
    private JTable tablaBajoStock;
    private JPanel chartDonaContainer;
    private JPanel chartBarrasContainer;
    
    public DashboardPanel() {
        this.reporteController = new ReporteController();
        initComponents();
        cargarDatos();
    }

    private void initComponents() {
        setLayout(new BorderLayout(0, 0));
        setBackground(UIConstants.BACKGROUND_COLOR);

        // Panel principal con padding
        JPanel mainPanel = new JPanel(new BorderLayout(0, 15));
        mainPanel.setBackground(UIConstants.BACKGROUND_COLOR);
        mainPanel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));

        // Header
        JPanel headerPanel = createHeaderPanel();
        
        // Panel de 2 columnas
        JPanel contentPanel = new JPanel(new GridLayout(1, 2, 15, 0));
        contentPanel.setBackground(UIConstants.BACKGROUND_COLOR);
        
        // Columna izquierda
        JPanel leftColumn = new JPanel();
        leftColumn.setLayout(new BoxLayout(leftColumn, BoxLayout.Y_AXIS));
        leftColumn.setBackground(UIConstants.BACKGROUND_COLOR);
        
        // 4 Cards superiores (2x2)
        JPanel cardsGrid = new JPanel(new GridLayout(2, 2, 15, 15));
        cardsGrid.setBackground(UIConstants.BACKGROUND_COLOR);
        cardsGrid.setMaximumSize(new Dimension(Integer.MAX_VALUE, 200));
        
        // Card 1: Ventas del Día
        JPanel card1 = createSimpleCard("💰 Ventas del Día", "S/ 0.00", new Color(37, 99, 235));
        lblVentasDia1 = extractValueLabel(card1);
        
        // Card 2: Productos en Stock
        JPanel card2 = createSimpleCard("📦 Productos en Stock", "0", new Color(16, 185, 129));
        lblTotalProductos = extractValueLabel(card2);
        
        // Card 3: Ventas del Día (duplicado)
        JPanel card3 = createSimpleCard("📦 Ventas del Día", "S/ 0.00", new Color(156, 163, 175));
        lblVentasDia2 = extractValueLabel(card3);
        
        // Card 4: Categorías Únicas
        JPanel card4 = createSimpleCard("📋 Categorías Únicas", "0", new Color(245, 158, 11));
        lblCategorias = extractValueLabel(card4);
        
        cardsGrid.add(card1);
        cardsGrid.add(card2);
        cardsGrid.add(card3);
        cardsGrid.add(card4);
        
        leftColumn.add(cardsGrid);
        leftColumn.add(Box.createVerticalStrut(15));
        
        // Tabla de bajo stock
        JPanel lowStockPanel = createLowStockPanel();
        lowStockPanel.setPreferredSize(new Dimension(0, 300));
        leftColumn.add(lowStockPanel);
        
        // Columna derecha
        JPanel rightColumn = new JPanel();
        rightColumn.setLayout(new BoxLayout(rightColumn, BoxLayout.Y_AXIS));
        rightColumn.setBackground(UIConstants.BACKGROUND_COLOR);
        
        // Gráfico de dona
        JPanel donaPanel = createDonaPanel();
        donaPanel.setMaximumSize(new Dimension(Integer.MAX_VALUE, 300));
        rightColumn.add(donaPanel);
        rightColumn.add(Box.createVerticalStrut(15));
        
        // Gráfico de barras
        JPanel barrasPanel = createBarrasPanel();
        rightColumn.add(barrasPanel);
        
        contentPanel.add(leftColumn);
        contentPanel.add(rightColumn);
        
        mainPanel.add(headerPanel, BorderLayout.NORTH);
        mainPanel.add(contentPanel, BorderLayout.CENTER);
        
        add(mainPanel);
    }

    private JPanel createHeaderPanel() {
        JPanel panel = new JPanel(new BorderLayout());
        panel.setBackground(Color.WHITE);
        panel.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createMatteBorder(0, 0, 1, 0, new Color(229, 231, 235)),
            BorderFactory.createEmptyBorder(15, 20, 15, 20)
        ));
        
        JLabel title = new JLabel("📊 Dashboard - Resumen General");
        title.setFont(new Font("Segoe UI", Font.BOLD, 20));
        title.setForeground(Color.BLACK);
        
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT, 10, 0));
        buttonPanel.setBackground(Color.WHITE);
        
        JButton btnRecargar = createHeaderButton("🔄 Recargar Datos");
        JButton btnExportar = createHeaderButton("📥 Exportar Reporte");
        
        buttonPanel.add(btnRecargar);
        buttonPanel.add(btnExportar);
        
        panel.add(title, BorderLayout.WEST);
        panel.add(buttonPanel, BorderLayout.EAST);
        
        btnRecargar.addActionListener(e -> cargarDatos());
        btnExportar.addActionListener(e -> exportarReporte());
        
        return panel;
    }
    
    private JButton createHeaderButton(String text) {
        JButton btn = new JButton(text);
        btn.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        btn.setBackground(Color.WHITE);
        btn.setForeground(new Color(107, 114, 128));
        btn.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(new Color(229, 231, 235), 1),
            BorderFactory.createEmptyBorder(8, 15, 8, 15)
        ));
        btn.setFocusPainted(false);
        btn.setCursor(new Cursor(Cursor.HAND_CURSOR));
        
        btn.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseEntered(java.awt.event.MouseEvent evt) {
                btn.setBackground(new Color(249, 250, 251));
            }
            public void mouseExited(java.awt.event.MouseEvent evt) {
                btn.setBackground(Color.WHITE);
            }
        });
        
        return btn;
    }

    private JPanel createSimpleCard(String titulo, String valor, Color iconColor) {
        JPanel card = new JPanel();
        card.setLayout(new BoxLayout(card, BoxLayout.Y_AXIS));
        card.setBackground(Color.WHITE);
        card.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(new Color(229, 231, 235), 1),
            BorderFactory.createEmptyBorder(20, 20, 20, 20)
        ));
        
        // Título
        JLabel lblTitulo = new JLabel(titulo);
        lblTitulo.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        lblTitulo.setForeground(new Color(107, 114, 128));
        lblTitulo.setAlignmentX(Component.LEFT_ALIGNMENT);
        
        // Valor
        JLabel lblValor = new JLabel(valor);
        lblValor.setFont(new Font("Segoe UI", Font.BOLD, 36));
        lblValor.setForeground(iconColor);
        lblValor.setAlignmentX(Component.LEFT_ALIGNMENT);
        
        // Icono en la esquina
        JLabel lblIcono = new JLabel(titulo.split(" ")[0]);
        lblIcono.setFont(new Font("Segoe UI", Font.PLAIN, 32));
        lblIcono.setForeground(new Color(229, 231, 235));
        
        JPanel topPanel = new JPanel(new BorderLayout());
        topPanel.setBackground(Color.WHITE);
        topPanel.add(lblTitulo, BorderLayout.WEST);
        topPanel.add(lblIcono, BorderLayout.EAST);
        topPanel.setMaximumSize(new Dimension(Integer.MAX_VALUE, 25));
        topPanel.setAlignmentX(Component.LEFT_ALIGNMENT);
        
        card.add(topPanel);
        card.add(Box.createVerticalStrut(10));
        card.add(lblValor);
        
        return card;
    }
    
    private JLabel extractValueLabel(JPanel card) {
        for (Component c : card.getComponents()) {
            if (c instanceof JLabel) {
                JLabel lbl = (JLabel) c;
                if (lbl.getFont().getSize() == 36) {
                    return lbl;
                }
            }
        }
        return new JLabel("0");
    }

    private JPanel createLowStockPanel() {
        JPanel panel = new JPanel(new BorderLayout(0, 10));
        panel.setBackground(Color.WHITE);
        panel.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(new Color(229, 231, 235), 1),
            BorderFactory.createEmptyBorder(15, 15, 15, 15)
        ));
        
        JLabel title = new JLabel("⚠️ Productos con Bajo Stock");
        title.setFont(new Font("Segoe UI", Font.BOLD, 14));
        title.setForeground(Color.BLACK);
        
        modeloBajoStock = new DefaultTableModel(
            new Object[]{"Código", "Nombre", "Stock"}, 0
        ) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };

        tablaBajoStock = new JTable(modeloBajoStock);
        tablaBajoStock.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        tablaBajoStock.setRowHeight(40);
        tablaBajoStock.setShowGrid(true);
        tablaBajoStock.setGridColor(new Color(243, 244, 246));
        tablaBajoStock.setBackground(Color.WHITE);
        tablaBajoStock.setSelectionBackground(new Color(243, 244, 246));
        tablaBajoStock.setSelectionForeground(Color.BLACK);
        tablaBajoStock.setIntercellSpacing(new Dimension(10, 1));
        
        tablaBajoStock.getTableHeader().setFont(new Font("Segoe UI", Font.BOLD, 13));
        tablaBajoStock.getTableHeader().setBackground(Color.WHITE);
        tablaBajoStock.getTableHeader().setForeground(Color.BLACK);
        tablaBajoStock.getTableHeader().setPreferredSize(new Dimension(0, 35));
        tablaBajoStock.getTableHeader().setBorder(BorderFactory.createMatteBorder(0, 0, 1, 0, new Color(229, 231, 235)));
        
        // Renderer para centrar y colorear el stock
        DefaultTableCellRenderer centerRenderer = new DefaultTableCellRenderer() {
            @Override
            public Component getTableCellRendererComponent(JTable table, Object value, 
                    boolean isSelected, boolean hasFocus, int row, int column) {
                Component c = super.getTableCellRendererComponent(table, value, isSelected, hasFocus, row, column);
                setHorizontalAlignment(LEFT);
                
                if (column == 2) { // Columna Stock
                    setHorizontalAlignment(CENTER);
                    if (value != null) {
                        int stock = Integer.parseInt(value.toString());
                        if (stock == 0) {
                            setForeground(new Color(239, 68, 68));
                            setFont(getFont().deriveFont(Font.BOLD));
                        } else if (stock <= 10) {
                            setForeground(new Color(245, 158, 11));
                            setFont(getFont().deriveFont(Font.BOLD));
                        } else {
                            setForeground(Color.BLACK);
                            setFont(getFont().deriveFont(Font.PLAIN));
                        }
                    }
                } else {
                    setForeground(Color.BLACK);
                }
                
                setBackground(isSelected ? new Color(243, 244, 246) : Color.WHITE);
                return c;
            }
        };
        
        for (int i = 0; i < tablaBajoStock.getColumnCount(); i++) {
            tablaBajoStock.getColumnModel().getColumn(i).setCellRenderer(centerRenderer);
        }
        
        // Ajustar anchos de columnas
        tablaBajoStock.getColumnModel().getColumn(0).setPreferredWidth(100);
        tablaBajoStock.getColumnModel().getColumn(1).setPreferredWidth(200);
        tablaBajoStock.getColumnModel().getColumn(2).setPreferredWidth(80);
        
        JScrollPane scrollPane = new JScrollPane(tablaBajoStock);
        scrollPane.setBorder(null);
        scrollPane.getViewport().setBackground(Color.WHITE);
        
        // Barra roja de advertencia
        JPanel warningBar = new JPanel();
        warningBar.setBackground(new Color(239, 68, 68));
        warningBar.setPreferredSize(new Dimension(0, 3));
        
        JLabel warningLabel = new JLabel("Stock crítico: productos agotados o por agotarse");
        warningLabel.setFont(new Font("Segoe UI", Font.PLAIN, 11));
        warningLabel.setForeground(new Color(107, 114, 128));
        
        JPanel bottomPanel = new JPanel(new BorderLayout());
        bottomPanel.setBackground(Color.WHITE);
        bottomPanel.add(warningBar, BorderLayout.NORTH);
        bottomPanel.add(warningLabel, BorderLayout.CENTER);
        
        panel.add(title, BorderLayout.NORTH);
        panel.add(scrollPane, BorderLayout.CENTER);
        panel.add(bottomPanel, BorderLayout.SOUTH);
        
        return panel;
    }

    private JPanel createDonaPanel() {
        JPanel panel = new JPanel(new BorderLayout(0, 10));
        panel.setBackground(Color.WHITE);
        panel.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(new Color(229, 231, 235), 1),
            BorderFactory.createEmptyBorder(15, 15, 15, 15)
        ));
        
        JLabel title = new JLabel("📊 Ventas por Categoría (Últimos 30 días)");
        title.setFont(new Font("Segoe UI", Font.BOLD, 14));
        title.setForeground(Color.BLACK);
        
        chartDonaContainer = new JPanel(new BorderLayout());
        chartDonaContainer.setBackground(Color.WHITE);
        chartDonaContainer.setPreferredSize(new Dimension(0, 250));
        
        panel.add(title, BorderLayout.NORTH);
        panel.add(chartDonaContainer, BorderLayout.CENTER);
        
        return panel;
    }

    private JPanel createBarrasPanel() {
        JPanel panel = new JPanel(new BorderLayout(0, 10));
        panel.setBackground(Color.WHITE);
        panel.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(new Color(229, 231, 235), 1),
            BorderFactory.createEmptyBorder(15, 15, 15, 15)
        ));
        
        JLabel title = new JLabel("🏆 Top 5 Productos Más Vendidos");
        title.setFont(new Font("Segoe UI", Font.BOLD, 14));
        title.setForeground(Color.BLACK);
        
        chartBarrasContainer = new JPanel(new BorderLayout());
        chartBarrasContainer.setBackground(Color.WHITE);
        
        panel.add(title, BorderLayout.NORTH);
        panel.add(chartBarrasContainer, BorderLayout.CENTER);
        
        return panel;
    }

    private void cargarDatos() {
        try {
            Map<String, Object> estadisticas = reporteController.generarEstadisticasDashboard();
            
            // Actualizar cards
            double ventasDia = (double) estadisticas.get("ventasDia");
            lblVentasDia1.setText(String.format("S/ %.2f", ventasDia));
            lblVentasDia2.setText(String.format("S/ %.2f", ventasDia));
            
            long totalProductos = (long) estadisticas.get("totalProductos");
            lblTotalProductos.setText(String.valueOf(totalProductos));
            
            // Categorías únicas
            try {
                List<Producto> todosProductos = new com.libreria.controller.ProductoController().obtenerTodos();
                Set<String> categorias = new HashSet<>();
                for (Producto p : todosProductos) {
                    if (p.getCategoria() != null && !p.getCategoria().isEmpty()) {
                        categorias.add(p.getCategoria());
                    }
                }
                lblCategorias.setText(String.valueOf(categorias.size()));
            } catch (Exception e) {
                lblCategorias.setText("0");
            }
            
            // Tabla bajo stock - ACTUALIZACIÓN EN TIEMPO REAL
            modeloBajoStock.setRowCount(0);
            @SuppressWarnings("unchecked")
            List<Producto> productosBajoStock = (List<Producto>) estadisticas.get("productoBajoStock");
            
            if (productosBajoStock != null && !productosBajoStock.isEmpty()) {
                for (Producto p : productosBajoStock) {
                    modeloBajoStock.addRow(new Object[]{
                        p.getCodigo(),
                        p.getNombre(),
                        p.getStock()
                    });
                }
            }
            
            // Cargar gráficos 
            @SuppressWarnings("unchecked")
            List<Map<String, Object>> topProductos = (List<Map<String, Object>>) estadisticas.get("topProductos");
            cargarGraficoBarras(topProductos);
            
            // Cargar gráfico circular
            cargarGraficoDona(estadisticas);
            
        } catch (Exception e) {
            showError("Error al cargar datos: " + e.getMessage());
            e.printStackTrace();
        }
    }
    
    private void cargarGraficoDona(Map<String, Object> estadisticas) {
        try {
            DefaultPieDataset dataset = new DefaultPieDataset();
            
            // Obtener ventas por categoría REALES
            Map<String, Double> ventasPorCategoria = new HashMap<>();
            
            try {
                // Obtener productos y calcular ventas por categoría
                List<Producto> productos = new com.libreria.controller.ProductoController().obtenerTodos();
                
                for (Producto p : productos) {
                    String categoria = p.getCategoria();
                    if (categoria == null || categoria.isEmpty()) {
                        categoria = "Sin Categoría";
                    }
                    
                    // Calcular valor aproximado de ventas (precio * cantidad vendida estimada)
                    double valorVenta = ventasPorCategoria.getOrDefault(categoria, 0.0);
                    
                    // Aquí se puede obtener las ventas reales de la BD
                    // Por ahora usamos el precio como referencia
                    ventasPorCategoria.put(categoria, valorVenta + p.getPrecio());
                }
                
            } catch (Exception e) {
                e.printStackTrace();
            }
            
            // Si hay datos reales, usarlos
            if (!ventasPorCategoria.isEmpty()) {
                // Ordenar por valor descendente
                List<Map.Entry<String, Double>> sorted = new ArrayList<>(ventasPorCategoria.entrySet());
                sorted.sort((a, b) -> b.getValue().compareTo(a.getValue()));
                
                // Tomar las top 5 categorías
                int count = 0;
                for (Map.Entry<String, Double> entry : sorted) {
                    if (count >= 5) break;
                    dataset.setValue(entry.getKey(), entry.getValue());
                    count++;
                }
            } else {
                // Datos de ejemplo si no hay ventas
                dataset.setValue("Escolar", 35);
                dataset.setValue("Papelería", 25);
                dataset.setValue("Arte", 20);
                dataset.setValue("Oficina", 12);
                dataset.setValue("Otros", 8);
            }
            
            JFreeChart chart = ChartFactory.createPieChart(null, dataset, true, true, false);
            chart.setBackgroundPaint(Color.WHITE);
            
            PiePlot plot = (PiePlot) chart.getPlot();
            plot.setBackgroundPaint(Color.WHITE);
            plot.setOutlineVisible(false);
            plot.setLabelFont(new Font("Segoe UI", Font.PLAIN, 10));
            plot.setLabelGenerator(null);
            plot.setSimpleLabels(false);
            plot.setCircular(true);
            plot.setSectionOutlinesVisible(false);
            
            // Colores variados para las categorías
            Color[] colores = {
                new Color(59, 130, 246),   // Azul
                new Color(236, 72, 153),   // Rosa
                new Color(245, 158, 11),   // Naranja
                new Color(34, 197, 94),    // Verde
                new Color(239, 68, 68)     // Rojo
            };
            
            int i = 0;
            for (Object key : dataset.getKeys()) {
                plot.setSectionPaint((Comparable) key, colores[i % colores.length]);
                i++;
            }
            
            // Leyenda personalizada a la derecha
            chart.getLegend().setPosition(RectangleEdge.RIGHT);
            chart.getLegend().setBackgroundPaint(Color.WHITE);
            chart.getLegend().setFrame(org.jfree.chart.block.BlockBorder.NONE);
            chart.getLegend().setItemFont(new Font("Segoe UI", Font.PLAIN, 11));
            
            ChartPanel chartPanel = new ChartPanel(chart);
            chartPanel.setBackground(Color.WHITE);
            chartPanel.setPreferredSize(new Dimension(450, 250));
            chartPanel.setMouseWheelEnabled(false);
            chartPanel.setPopupMenu(null);
            
            chartDonaContainer.removeAll();
            chartDonaContainer.add(chartPanel, BorderLayout.CENTER);
            chartDonaContainer.revalidate();
            chartDonaContainer.repaint();
            
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    
    private void cargarGraficoBarras(List<Map<String, Object>> topProductos) {
        try {
            DefaultCategoryDataset dataset = new DefaultCategoryDataset();
            
            if (topProductos == null || topProductos.isEmpty()) {
                // Si no hay productos, mostrar mensaje
                dataset.addValue(0, "Ventas", "Sin datos");
            } else {
                int limite = Math.min(5, topProductos.size());
                for (int i = 0; i < limite; i++) {
                    Map<String, Object> p = topProductos.get(i);
                    String nombre = (String) p.get("nombre");
                    if (nombre != null) {
                        if (nombre.length() > 20) nombre = nombre.substring(0, 17) + "...";
                    } else {
                        nombre = "Producto " + (i + 1);
                    }
                    
                    Double ingresos = (Double) p.get("totalIngresos");
                    dataset.addValue(ingresos != null ? ingresos : 0, "Ventas", nombre);
                }
            }
            
            JFreeChart chart = ChartFactory.createBarChart(
                null, null, null, dataset, PlotOrientation.HORIZONTAL, false, true, false
            );
            
            chart.setBackgroundPaint(Color.WHITE);
            CategoryPlot plot = chart.getCategoryPlot();
            plot.setBackgroundPaint(Color.WHITE);
            plot.setOutlineVisible(false);
            plot.setRangeGridlinePaint(new Color(243, 244, 246));
            plot.setDomainGridlinesVisible(false);
            plot.setRangeGridlinesVisible(true);
            
            BarRenderer renderer = (BarRenderer) plot.getRenderer();
            renderer.setSeriesPaint(0, new Color(59, 130, 246)); // Azul
            renderer.setBarPainter(new org.jfree.chart.renderer.category.StandardBarPainter());
            renderer.setMaximumBarWidth(0.15);
            renderer.setItemMargin(0.1);
            renderer.setShadowVisible(false);
            
            plot.getDomainAxis().setTickLabelFont(new Font("Segoe UI", Font.PLAIN, 11));
            plot.getDomainAxis().setVisible(true);
            plot.getRangeAxis().setVisible(false);
            
            // Añadir etiquetas de valor al final de cada barra - CORREGIDO
            renderer.setDefaultItemLabelsVisible(true);
            renderer.setDefaultItemLabelGenerator(
                new StandardCategoryItemLabelGenerator("S/ {2}", new DecimalFormat("0.00"))
            );
            renderer.setDefaultItemLabelFont(new Font("Segoe UI", Font.PLAIN, 10));
            renderer.setDefaultItemLabelPaint(new Color(107, 114, 128));
            renderer.setDefaultPositiveItemLabelPosition(
                new ItemLabelPosition(ItemLabelAnchor.OUTSIDE3, TextAnchor.CENTER_LEFT)
            );
            
            ChartPanel chartPanel = new ChartPanel(chart);
            chartPanel.setBackground(Color.WHITE);
            chartPanel.setPreferredSize(new Dimension(450, 280));
            chartPanel.setMouseWheelEnabled(false);
            chartPanel.setPopupMenu(null);
            
            chartBarrasContainer.removeAll();
            chartBarrasContainer.add(chartPanel, BorderLayout.CENTER);
            chartBarrasContainer.revalidate();
            chartBarrasContainer.repaint();
            
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void exportarReporte() {
        JFileChooser fileChooser = new JFileChooser();
        fileChooser.setDialogTitle("Guardar Reporte de Inventario");
        fileChooser.setSelectedFile(new File("ReporteInventario_" + 
            java.time.LocalDateTime.now().format(java.time.format.DateTimeFormatter.ofPattern("yyyyMMdd_HHmmss")) + ".xlsx"));
        
        if (fileChooser.showSaveDialog(this) == JFileChooser.APPROVE_OPTION) {
            try {
                String rutaArchivo = fileChooser.getSelectedFile().getAbsolutePath();
                if (!rutaArchivo.endsWith(".xlsx")) {
                    rutaArchivo += ".xlsx";
                }
                reporteController.generarReporteInventarioExcel(rutaArchivo);
                showInfo("✓ Reporte exportado exitosamente a:\n" + rutaArchivo);
            } catch (Exception e) {
                showError("Error al generar reporte: " + e.getMessage());
            }
        }
    }

    public void actualizarDashboard() {
        cargarDatos();
    }
}