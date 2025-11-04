package com.libreria.view;

import com.libreria.controller.VentaController;
import com.libreria.model.Venta;
import com.libreria.util.UIConstants;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.DefaultTableCellRenderer;
import java.awt.*;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;

public class SalesListPanel extends JDialog {
    private final VentaController ventaController;
    private JTable tabla;
    private DefaultTableModel modelo;
    private JTextField txtFechaInicio;
    private JTextField txtFechaFin;
    private JLabel lblTotal;
    private JLabel lblCantidad;

    public SalesListPanel(Frame parent) {
        super(parent, "📋 Listado de Ventas", true);
        this.ventaController = new VentaController();
        initComponents();
        cargarVentas();
        
        setSize(1200, 700);
        setLocationRelativeTo(parent);
    }

    private void initComponents() {
        setLayout(new BorderLayout(0, 0));
        getContentPane().setBackground(UIConstants.BACKGROUND_COLOR);

        // Panel superior con título y filtros
        JPanel topPanel = createTopPanel();
        
        // Panel de estadísticas
        JPanel statsPanel = createStatsPanel();
        
        // Tabla de ventas
        JPanel tablePanel = createTablePanel();
        
        // Panel de botones
        JPanel buttonPanel = createButtonPanel();

        // Layout principal
        add(topPanel, BorderLayout.NORTH);
        add(statsPanel, BorderLayout.CENTER);
        add(tablePanel, BorderLayout.SOUTH);
        
        JPanel bottomPanel = new JPanel(new BorderLayout());
        bottomPanel.setBackground(Color.WHITE);
        bottomPanel.add(buttonPanel, BorderLayout.EAST);
        add(bottomPanel, BorderLayout.PAGE_END);
    }

    private JPanel createTopPanel() {
        JPanel panel = new JPanel(new BorderLayout());
        panel.setBackground(UIConstants.PRIMARY_COLOR);
        panel.setBorder(BorderFactory.createEmptyBorder(25, 30, 25, 30));

        // Título
        JLabel titleLabel = new JLabel("📋 Listado de Ventas");
        titleLabel.setFont(new Font("Segoe UI", Font.BOLD, 26));
        titleLabel.setForeground(Color.WHITE);

        // Panel de filtros
        JPanel filterPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT, 15, 0));
        filterPanel.setBackground(UIConstants.PRIMARY_COLOR);

        JLabel lblInicio = new JLabel("Inicio:");
        lblInicio.setFont(UIConstants.NORMAL_FONT);
        lblInicio.setForeground(Color.WHITE);

        txtFechaInicio = new JTextField(15);
        txtFechaInicio.setFont(UIConstants.NORMAL_FONT);
        txtFechaInicio.setPreferredSize(new Dimension(150, 35));
        txtFechaInicio.setText(LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd")));

        JLabel lblFin = new JLabel("Fin:");
        lblFin.setFont(UIConstants.NORMAL_FONT);
        lblFin.setForeground(Color.WHITE);

        txtFechaFin = new JTextField(15);
        txtFechaFin.setFont(UIConstants.NORMAL_FONT);
        txtFechaFin.setPreferredSize(new Dimension(150, 35));
        txtFechaFin.setText(LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd")));

        JButton btnFiltrar = new JButton("🔍 Filtrar");
        btnFiltrar.setFont(UIConstants.BUTTON_FONT);
        btnFiltrar.setBackground(Color.WHITE);
        btnFiltrar.setForeground(UIConstants.PRIMARY_COLOR);
        btnFiltrar.setBorderPainted(false);
        btnFiltrar.setFocusPainted(false);
        btnFiltrar.setPreferredSize(new Dimension(120, 35));
        btnFiltrar.setCursor(new Cursor(Cursor.HAND_CURSOR));
        btnFiltrar.addActionListener(e -> filtrarVentas());

        filterPanel.add(lblInicio);
        filterPanel.add(txtFechaInicio);
        filterPanel.add(lblFin);
        filterPanel.add(txtFechaFin);
        filterPanel.add(btnFiltrar);

        panel.add(titleLabel, BorderLayout.WEST);
        panel.add(filterPanel, BorderLayout.EAST);

        return panel;
    }

    private JPanel createStatsPanel() {
        JPanel panel = new JPanel(new GridLayout(1, 2, 20, 0));
        panel.setBackground(UIConstants.BACKGROUND_COLOR);
        panel.setBorder(BorderFactory.createEmptyBorder(20, 30, 10, 30));

        // Card de total ventas
        JPanel totalCard = createStatCard("💰", "Total Ventas", "S/ 0.00", UIConstants.PRIMARY_COLOR);
        lblTotal = (JLabel) ((JPanel) totalCard.getComponent(1)).getComponent(0);

        // Card de cantidad
        JPanel cantidadCard = createStatCard("📊", "Cantidad", "0", UIConstants.SECONDARY_COLOR);
        lblCantidad = (JLabel) ((JPanel) cantidadCard.getComponent(1)).getComponent(0);

        panel.add(totalCard);
        panel.add(cantidadCard);

        return panel;
    }

    private JPanel createStatCard(String icon, String title, String value, Color color) {
        JPanel card = new JPanel(new BorderLayout(15, 10));
        card.setBackground(Color.WHITE);
        card.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(new Color(220, 220, 220), 1),
            BorderFactory.createEmptyBorder(20, 25, 20, 25)
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
        panel.setBorder(BorderFactory.createEmptyBorder(10, 30, 20, 30));

        JLabel tableTitle = new JLabel("Detalle de Ventas");
        tableTitle.setFont(new Font("Segoe UI", Font.BOLD, 16));
        tableTitle.setForeground(UIConstants.TEXT_COLOR);

        // Modelo de tabla
        modelo = new DefaultTableModel(
            new Object[]{"ID", "N° Venta", "Fecha", "Método", "Total"},
            0
        ) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };

        tabla = new JTable(modelo);
        tabla.setFont(new Font("Arial", Font.PLAIN, 14));
        tabla.setRowHeight(45);
        tabla.setShowGrid(true);
        tabla.setGridColor(new Color(230, 230, 230));
        tabla.setBackground(Color.WHITE);
        tabla.setForeground(Color.BLACK);
        tabla.setSelectionBackground(new Color(220, 235, 255));
        tabla.setSelectionForeground(Color.BLACK);

        // Header
        tabla.getTableHeader().setFont(new Font("Arial", Font.BOLD, 14));
        tabla.getTableHeader().setBackground(new Color(245, 245, 245));
        tabla.getTableHeader().setForeground(Color.BLACK);
        tabla.getTableHeader().setPreferredSize(new Dimension(0, 45));

        // Ancho de columnas
        tabla.getColumnModel().getColumn(0).setPreferredWidth(60);
        tabla.getColumnModel().getColumn(0).setMaxWidth(80);
        tabla.getColumnModel().getColumn(1).setPreferredWidth(180);
        tabla.getColumnModel().getColumn(2).setPreferredWidth(200);
        tabla.getColumnModel().getColumn(3).setPreferredWidth(150);
        tabla.getColumnModel().getColumn(4).setPreferredWidth(120);

        // Renderer personalizado para el total
        DefaultTableCellRenderer totalRenderer = new DefaultTableCellRenderer() {
            @Override
            public Component getTableCellRendererComponent(JTable table, Object value,
                    boolean isSelected, boolean hasFocus, int row, int column) {
                Component c = super.getTableCellRendererComponent(table, value, isSelected, hasFocus, row, column);
                if (column == 4) { // Columna Total
                    setFont(new Font("Arial", Font.BOLD, 14));
                    setForeground(UIConstants.PRIMARY_COLOR);
                    setHorizontalAlignment(CENTER);
                } else {
                    setFont(new Font("Arial", Font.PLAIN, 14));
                    setForeground(Color.BLACK);
                    setHorizontalAlignment(CENTER);
                }
                setBackground(isSelected ? new Color(220, 235, 255) : Color.WHITE);
                return c;
            }
        };

        for (int i = 0; i < tabla.getColumnCount(); i++) {
            tabla.getColumnModel().getColumn(i).setCellRenderer(totalRenderer);
        }

        JScrollPane scrollPane = new JScrollPane(tabla);
        scrollPane.setBorder(BorderFactory.createLineBorder(new Color(220, 220, 220), 1));
        scrollPane.setPreferredSize(new Dimension(0, 300));

        panel.add(tableTitle, BorderLayout.NORTH);
        panel.add(scrollPane, BorderLayout.CENTER);

        return panel;
    }

    private JPanel createButtonPanel() {
        JPanel panel = new JPanel(new FlowLayout(FlowLayout.RIGHT, 15, 20));
        panel.setBackground(Color.WHITE);

        JButton btnExportar = createStyledButton("📥 Exportar a Excel", UIConstants.SECONDARY_COLOR);
        JButton btnDetalle = createStyledButton("👁️ Ver Detalle", UIConstants.PRIMARY_COLOR);
        JButton btnCerrar = createStyledButton("❌ Cerrar", UIConstants.DANGER_COLOR);

        btnExportar.addActionListener(e -> exportarExcel());
        btnDetalle.addActionListener(e -> verDetalle());
        btnCerrar.addActionListener(e -> dispose());

        panel.add(btnExportar);
        panel.add(btnDetalle);
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

    private void cargarVentas() {
        try {
            LocalDateTime inicio = LocalDateTime.now().withHour(0).withMinute(0).withSecond(0);
            LocalDateTime fin = LocalDateTime.now().withHour(23).withMinute(59).withSecond(59);
            
            List<Venta> ventas = ventaController.obtenerVentasPorFecha(inicio, fin);
            actualizarTabla(ventas);
            actualizarEstadisticas(ventas);
        } catch (Exception e) {
            JOptionPane.showMessageDialog(this, 
                "Error al cargar ventas: " + e.getMessage(),
                "Error", 
                JOptionPane.ERROR_MESSAGE);
        }
    }

    private void filtrarVentas() {
        try {
            LocalDateTime inicio = LocalDateTime.parse(txtFechaInicio.getText() + "T00:00:00");
            LocalDateTime fin = LocalDateTime.parse(txtFechaFin.getText() + "T23:59:59");
            
            List<Venta> ventas = ventaController.obtenerVentasPorFecha(inicio, fin);
            actualizarTabla(ventas);
            actualizarEstadisticas(ventas);
        } catch (Exception e) {
            JOptionPane.showMessageDialog(this,
                "Error en las fechas. Formato: yyyy-MM-dd",
                "Error",
                JOptionPane.ERROR_MESSAGE);
        }
    }

    private void actualizarTabla(List<Venta> ventas) {
        modelo.setRowCount(0);
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm:ss");
        
        for (Venta v : ventas) {
            modelo.addRow(new Object[]{
                v.getId(),
                v.getNumeroVenta(),
                v.getFecha().format(formatter),
                v.getMetodoPago(),
                String.format("S/ %.2f", v.getTotal())
            });
        }
    }

    private void actualizarEstadisticas(List<Venta> ventas) {
        double total = ventas.stream().mapToDouble(Venta::getTotal).sum();
        lblTotal.setText(String.format("S/ %.2f", total));
        lblCantidad.setText(String.valueOf(ventas.size()));
    }

    private void exportarExcel() {
        try {
            JFileChooser fileChooser = new JFileChooser();
            fileChooser.setDialogTitle("Guardar Reporte de Ventas");
            fileChooser.setSelectedFile(new java.io.File("Reporte_Ventas_" + 
                LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyyMMdd_HHmmss")) + ".xlsx"));
            
            int userSelection = fileChooser.showSaveDialog(this);
            
            if (userSelection == JFileChooser.APPROVE_OPTION) {
                String rutaArchivo = fileChooser.getSelectedFile().getAbsolutePath();
                if (!rutaArchivo.endsWith(".xlsx")) {
                    rutaArchivo += ".xlsx";
                }
                
                // Obtener fechas
                LocalDateTime inicio = LocalDateTime.parse(txtFechaInicio.getText() + "T00:00:00");
                LocalDateTime fin = LocalDateTime.parse(txtFechaFin.getText() + "T23:59:59");
                
                ventaController.generarReporteVentasExcel(rutaArchivo, inicio, fin);
                
                JOptionPane.showMessageDialog(this,
                    "Reporte exportado exitosamente a:\n" + rutaArchivo,
                    "Éxito",
                    JOptionPane.INFORMATION_MESSAGE);
            }
        } catch (Exception e) {
            JOptionPane.showMessageDialog(this,
                "Error al exportar: " + e.getMessage(),
                "Error",
                JOptionPane.ERROR_MESSAGE);
            e.printStackTrace();
        }
    }

    private void verDetalle() {
        int row = tabla.getSelectedRow();
        if (row == -1) {
            JOptionPane.showMessageDialog(this,
                "Seleccione una venta para ver el detalle",
                "Información",
                JOptionPane.INFORMATION_MESSAGE);
            return;
        }
        
        String numeroVenta = (String) modelo.getValueAt(row, 1);
        JOptionPane.showMessageDialog(this,
            "Detalle de venta: " + numeroVenta,
            "Detalle",
            JOptionPane.INFORMATION_MESSAGE);
    }
}