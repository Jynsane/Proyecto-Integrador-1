package com.libreria.util;

import com.libreria.model.Venta;
import com.libreria.model.DetalleVenta;

import javax.swing.*;
import java.awt.*;
import java.awt.print.*;
import java.time.format.DateTimeFormatter;

public class ComprobanteGenerator {
    
    public static void generarEImprimir(Venta venta, String vendedor) {
        PrinterJob job = PrinterJob.getPrinterJob();
        job.setPrintable(new ComprobantePrintable(venta, vendedor));
        
        if (job.printDialog()) {
            try {
                job.print();
            } catch (PrinterException e) {
                JOptionPane.showMessageDialog(null, 
                    "Error al imprimir: " + e.getMessage(),
                    "Error de Impresión",
                    JOptionPane.ERROR_MESSAGE);
            }
        }
    }
    
    public static void mostrarVistaPrevia(Venta venta, String vendedor) {
        JDialog dialog = new JDialog();
        dialog.setTitle("Vista Previa - Comprobante");
        dialog.setSize(400, 600);
        dialog.setLocationRelativeTo(null);
        dialog.setModal(true);
        
        ComprobantePanel panel = new ComprobantePanel(venta, vendedor);
        dialog.add(panel);
        
        JPanel buttonPanel = new JPanel();
        JButton btnImprimir = new JButton("Imprimir");
        JButton btnCerrar = new JButton("Cerrar");
        
        btnImprimir.addActionListener(e -> {
            generarEImprimir(venta, vendedor);
            dialog.dispose();
        });
        
        btnCerrar.addActionListener(e -> dialog.dispose());
        
        buttonPanel.add(btnImprimir);
        buttonPanel.add(btnCerrar);
        
        dialog.add(buttonPanel, BorderLayout.SOUTH);
        dialog.setVisible(true);
    }
    
    static class ComprobantePrintable implements Printable {
        private final Venta venta;
        private final String vendedor;
        
        public ComprobantePrintable(Venta venta, String vendedor) {
            this.venta = venta;
            this.vendedor = vendedor;
        }
        
        @Override
        public int print(Graphics graphics, PageFormat pageFormat, int pageIndex) {
            if (pageIndex > 0) return NO_SUCH_PAGE;
            
            Graphics2D g2d = (Graphics2D) graphics;
            g2d.translate(pageFormat.getImageableX(), pageFormat.getImageableY());
            
            int y = 50;
            int lineHeight = 20;
            
            // Título
            g2d.setFont(new Font("Arial", Font.BOLD, 18));
            g2d.drawString("LibreTools", 200, y);
            y += lineHeight + 10;
            
            g2d.setFont(new Font("Arial", Font.BOLD, 14));
            g2d.drawString("BOLETA DE VENTA", 180, y);
            y += lineHeight + 20;
            
            // Información de la venta
            g2d.setFont(new Font("Arial", Font.PLAIN, 10));
            g2d.drawString("N° Venta: " + venta.getNumeroVenta(), 50, y);
            y += lineHeight;
            
            DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm:ss");
            g2d.drawString("Fecha: " + venta.getFecha().format(formatter), 50, y);
            y += lineHeight;
            
            g2d.drawString("Vendedor: " + vendedor, 50, y);
            y += lineHeight;
            
            g2d.drawString("Método de Pago: " + venta.getMetodoPago(), 50, y);
            y += lineHeight + 20;
            
            // Línea separadora
            g2d.drawLine(50, y, 500, y);
            y += 20;
            
            // Encabezados de la tabla
            g2d.setFont(new Font("Arial", Font.BOLD, 10));
            g2d.drawString("Producto", 50, y);
            g2d.drawString("Cant.", 300, y);
            g2d.drawString("P.Unit", 360, y);
            g2d.drawString("Subtotal", 430, y);
            y += lineHeight;
            
            g2d.drawLine(50, y, 500, y);
            y += 15;
            
            // Detalles de productos
            g2d.setFont(new Font("Arial", Font.PLAIN, 10));
            for (DetalleVenta detalle : venta.getDetalles()) {
                String nombre = detalle.getProducto().getNombre();
                if (nombre.length() > 30) {
                    nombre = nombre.substring(0, 27) + "...";
                }
                g2d.drawString(nombre, 50, y);
                g2d.drawString(String.valueOf(detalle.getCantidad()), 300, y);
                g2d.drawString(String.format("S/ %.2f", detalle.getPrecioUnitario()), 360, y);
                g2d.drawString(String.format("S/ %.2f", detalle.getSubtotal()), 430, y);
                y += lineHeight;
            }
            
            y += 10;
            g2d.drawLine(50, y, 500, y);
            y += 20;
            
            // Total
            g2d.setFont(new Font("Arial", Font.BOLD, 14));
            g2d.drawString("TOTAL:", 360, y);
            g2d.drawString(String.format("S/ %.2f", venta.getTotal()), 430, y);
            y += lineHeight + 30;
            
            // Pie de página
            g2d.setFont(new Font("Arial", Font.ITALIC, 9));
            g2d.drawString("¡Gracias por su compra!", 190, y);
            y += lineHeight;
            g2d.drawString("LibreTools - Sistema de Gestión de Librería", 140, y);
            
            return PAGE_EXISTS;
        }
    }
    
    static class ComprobantePanel extends JPanel {
        private final Venta venta;
        private final String vendedor;
        
        public ComprobantePanel(Venta venta, String vendedor) {
            this.venta = venta;
            this.vendedor = vendedor;
            setBackground(Color.WHITE);
        }
        
        @Override
        protected void paintComponent(Graphics g) {
            super.paintComponent(g);
            Graphics2D g2d = (Graphics2D) g;
            g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
            
            int y = 30;
            int lineHeight = 20;
            
            // Título
            g2d.setFont(new Font("Arial", Font.BOLD, 20));
            g2d.drawString("LibreTools", 130, y);
            y += lineHeight + 10;
            
            g2d.setFont(new Font("Arial", Font.BOLD, 16));
            g2d.drawString("BOLETA DE VENTA", 110, y);
            y += lineHeight + 20;
            
            // Información
            g2d.setFont(new Font("Arial", Font.PLAIN, 12));
            g2d.drawString("N° Venta: " + venta.getNumeroVenta(), 20, y);
            y += lineHeight;
            
            DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm:ss");
            g2d.drawString("Fecha: " + venta.getFecha().format(formatter), 20, y);
            y += lineHeight;
            
            g2d.drawString("Vendedor: " + vendedor, 20, y);
            y += lineHeight;
            
            g2d.drawString("Método de Pago: " + venta.getMetodoPago(), 20, y);
            y += lineHeight + 20;
            
            // Línea separadora
            g2d.drawLine(20, y, 360, y);
            y += 20;
            
            // Encabezados
            g2d.setFont(new Font("Arial", Font.BOLD, 12));
            g2d.drawString("Producto", 20, y);
            g2d.drawString("Cant.", 220, y);
            g2d.drawString("P.Unit", 260, y);
            g2d.drawString("Subtotal", 310, y);
            y += lineHeight;
            
            g2d.drawLine(20, y, 360, y);
            y += 15;
            
            // Detalles
            g2d.setFont(new Font("Arial", Font.PLAIN, 11));
            for (DetalleVenta detalle : venta.getDetalles()) {
                String nombre = detalle.getProducto().getNombre();
                if (nombre.length() > 25) {
                    nombre = nombre.substring(0, 22) + "...";
                }
                g2d.drawString(nombre, 20, y);
                g2d.drawString(String.valueOf(detalle.getCantidad()), 230, y);
                g2d.drawString(String.format("%.2f", detalle.getPrecioUnitario()), 260, y);
                g2d.drawString(String.format("%.2f", detalle.getSubtotal()), 310, y);
                y += lineHeight;
            }
            
            y += 10;
            g2d.drawLine(20, y, 360, y);
            y += 20;
            
            // Total
            g2d.setFont(new Font("Arial", Font.BOLD, 16));
            g2d.drawString("TOTAL:", 220, y);
            g2d.drawString(String.format("S/ %.2f", venta.getTotal()), 290, y);
            y += lineHeight + 30;
            
            // Pie
            g2d.setFont(new Font("Arial", Font.ITALIC, 10));
            g2d.drawString("¡Gracias por su compra!", 110, y);
        }
    }
}