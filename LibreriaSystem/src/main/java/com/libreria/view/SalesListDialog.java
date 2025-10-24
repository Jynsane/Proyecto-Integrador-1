package com.libreria.view;

import com.libreria.controller.ReporteController;
import com.libreria.controller.VentaController;
import com.libreria.model.DetalleVenta;
import com.libreria.model.Venta;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.List;

public class SalesListDialog extends JDialog {
    private final VentaController ventaController;
    private final DefaultTableModel model;
    private final JTable table;
    private final JTextField txtInicio;
    private final JTextField txtFin;
    private final JButton btnPrev;
    private final JButton btnNext;
    private final JLabel lblPageInfo;
    private int page = 0;
    private final int pageSize = 20;
    private List<Venta> currentList = null;
    private final ReporteController reporteController;

    public SalesListDialog(Frame owner) {
        super(owner, "Listado de Ventas", true);
        this.ventaController = new VentaController();

        model = new DefaultTableModel(new Object[]{"ID", "N° Venta", "Fecha", "Método", "Total"}, 0) {
            @Override
            public boolean isCellEditable(int row, int column) { return false; }
        };

        table = new JTable(model);
        JScrollPane scroll = new JScrollPane(table);

        // Panel superior con filtros
        JPanel top = new JPanel(new FlowLayout(FlowLayout.LEFT));
        top.add(new JLabel("Inicio (yyyy-MM-dd o dd/MM/yyyy):"));
        txtInicio = new JTextField(10);
        top.add(txtInicio);
        top.add(new JLabel("Fin (yyyy-MM-dd o dd/MM/yyyy):"));
        txtFin = new JTextField(10);
        top.add(txtFin);

        JButton btnFiltrar = new JButton("Filtrar");
        JButton btnAyer = new JButton("Ayer");
        JButton btnVerDetalle = new JButton("Ver Detalle");
        JButton btnClose = new JButton("Cerrar");

        top.add(btnFiltrar);
        top.add(btnAyer);
        top.add(btnVerDetalle);
        top.add(btnClose);

    setLayout(new BorderLayout());
    add(top, BorderLayout.NORTH);
    add(scroll, BorderLayout.CENTER);

    // Panel de paginación y export
    JPanel bottomPanel = new JPanel(new BorderLayout());
    JPanel pager = new JPanel(new FlowLayout(FlowLayout.LEFT));
    btnPrev = new JButton("Anterior");
    btnNext = new JButton("Siguiente");
    lblPageInfo = new JLabel("Página 0/0");
    pager.add(btnPrev);
    pager.add(btnNext);
    pager.add(lblPageInfo);

    JPanel exportPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
    JButton btnExport = new JButton("Exportar a Excel");
    exportPanel.add(btnExport);

    bottomPanel.add(pager, BorderLayout.WEST);
    bottomPanel.add(exportPanel, BorderLayout.EAST);
    add(bottomPanel, BorderLayout.SOUTH);

    this.reporteController = new ReporteController();

    btnClose.addActionListener(e -> dispose());

        btnAyer.addActionListener(e -> {
            LocalDate ayer = LocalDate.now().minusDays(1);
            txtInicio.setText(ayer.toString());
            txtFin.setText(ayer.toString());
        });

    btnFiltrar.addActionListener(e -> cargarPorFiltro());

        btnVerDetalle.addActionListener(e -> mostrarDetalleSeleccionado());

        btnPrev.addActionListener(e -> {
            if (page > 0) { page--; renderPage(); }
        });
        btnNext.addActionListener(e -> {
            if (currentList != null && (page+1)*pageSize < currentList.size()) { page++; renderPage(); }
        });

        btnExport.addActionListener(e -> {
            try {
                LocalDate inicioDate = parseDate(txtInicio.getText().trim());
                LocalDate finDate = parseDate(txtFin.getText().trim());
                if (inicioDate == null || finDate == null) {
                    JOptionPane.showMessageDialog(this, "Ingrese fechas válidas para exportar", "Error", JOptionPane.ERROR_MESSAGE);
                    return;
                }
                LocalDateTime inicio = inicioDate.atStartOfDay();
                LocalDateTime fin = finDate.atTime(23,59,59);

                JFileChooser fc = new JFileChooser();
                fc.setSelectedFile(new java.io.File("ReporteVentas.xlsx"));
                if (fc.showSaveDialog(this) == JFileChooser.APPROVE_OPTION) {
                    reporteController.generarReporteVentasExcel(fc.getSelectedFile().getAbsolutePath(), inicio, fin);
                    JOptionPane.showMessageDialog(this, "Exportado exitosamente", "OK", JOptionPane.INFORMATION_MESSAGE);
                }
            } catch (Exception ex) {
                JOptionPane.showMessageDialog(this, "Error al exportar: " + ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
            }
        });

        setSize(900, 500);
        setLocationRelativeTo(owner);
        loadData();
    }

    private void loadData() {
        try {
            model.setRowCount(0);
            List<Venta> ventas = ventaController.obtenerTodos();
            this.currentList = ventas;
            this.page = 0;
            renderPage();
        } catch (Exception e) {
            JOptionPane.showMessageDialog(this, "Error al cargar ventas: " + e.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void cargarPorFiltro() {
        try {
            LocalDate inicioDate = parseDate(txtInicio.getText().trim());
            LocalDate finDate = parseDate(txtFin.getText().trim());

            if (inicioDate == null || finDate == null) {
                JOptionPane.showMessageDialog(this, "Ingrese fechas válidas en formato yyyy-MM-dd o dd/MM/yyyy", "Error", JOptionPane.ERROR_MESSAGE);
                return;
            }

            LocalDateTime inicio = inicioDate.atStartOfDay();
            LocalDateTime fin = finDate.atTime(23, 59, 59);

            List<Venta> ventas = ventaController.obtenerVentasPorFecha(inicio, fin);
            this.currentList = ventas;
            this.page = 0;
            renderPage();
        } catch (Exception e) {
            JOptionPane.showMessageDialog(this, "Error al filtrar ventas: " + e.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void renderPage() {
        model.setRowCount(0);
        if (currentList == null || currentList.isEmpty()) {
            lblPageInfo.setText("Página 0/0");
            return;
        }
        int total = currentList.size();
        int totalPages = (total + pageSize - 1) / pageSize;
        int start = page * pageSize;
        int end = Math.min(start + pageSize, total);
        for (int i = start; i < end; i++) {
            Venta v = currentList.get(i);
            model.addRow(new Object[]{v.getId(), v.getNumeroVenta(), v.getFecha(), v.getMetodoPago(), v.getTotal()});
        }
        lblPageInfo.setText(String.format("Página %d/%d", page+1, totalPages));
    }

    private LocalDate parseDate(String text) {
        if (text == null || text.isEmpty()) return null;
        // probar ISO yyyy-MM-dd
        try {
            return LocalDate.parse(text, DateTimeFormatter.ISO_LOCAL_DATE);
        } catch (DateTimeParseException ignored) {}
        // probar dd/MM/yyyy
        try {
            return LocalDate.parse(text, DateTimeFormatter.ofPattern("dd/MM/yyyy"));
        } catch (DateTimeParseException ignored) {}
        return null;
    }

    private void mostrarDetalleSeleccionado() {
        int row = table.getSelectedRow();
        if (row < 0) {
            JOptionPane.showMessageDialog(this, "Seleccione una venta", "Info", JOptionPane.INFORMATION_MESSAGE);
            return;
        }

        Integer id = (Integer) model.getValueAt(row, 0);
        try {
            Venta v = ventaController.obtenerPorId(id);
            if (v == null) {
                JOptionPane.showMessageDialog(this, "Venta no encontrada", "Info", JOptionPane.INFORMATION_MESSAGE);
                return;
            }

            StringBuilder sb = new StringBuilder();
            sb.append("N° Venta: ").append(v.getNumeroVenta()).append("\n");
            sb.append("Fecha: ").append(v.getFecha()).append("\n");
            sb.append("Método: ").append(v.getMetodoPago()).append("\n");
            sb.append("Total: S/. ").append(String.format("%.2f", v.getTotal())).append("\n\n");
            sb.append("Detalles:\n");
            for (DetalleVenta d : v.getDetalles()) {
                sb.append(String.format("- %s x%d @ S/. %.2f = S/. %.2f\n",
                        d.getProducto().getNombre(), d.getCantidad(), d.getPrecioUnitario(), d.getSubtotal()));
            }

            JTextArea area = new JTextArea(sb.toString());
            area.setEditable(false);
            area.setRows(10);
            area.setColumns(50);
            JOptionPane.showMessageDialog(this, new JScrollPane(area), "Detalle de Venta", JOptionPane.INFORMATION_MESSAGE);
        } catch (Exception e) {
            JOptionPane.showMessageDialog(this, "Error al obtener detalle: " + e.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
        }
    }
}
