package com.libreria.controller;

import com.libreria.model.Venta;
import com.libreria.model.Producto;
import com.libreria.dao.ReporteDAO;
import org.apache.poi.ss.usermodel.*;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

import java.io.FileOutputStream;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.stream.Collectors;

public class ReporteController {
    private final VentaController ventaController;
    private final ProductoController productoController;
    private final ReporteDAO reporteDAO;
    
    public ReporteController() {
        this.ventaController = new VentaController();
        this.productoController = new ProductoController();
        this.reporteDAO = new ReporteDAO();
    }
    
    public Map<String, Object> generarEstadisticasDashboard() throws Exception {
        Map<String, Object> estadisticas = new HashMap<>();
        
        // Obtener estadísticas del día
        LocalDateTime inicio = LocalDateTime.now().withHour(0).withMinute(0).withSecond(0);
        LocalDateTime fin = LocalDateTime.now().withHour(23).withMinute(59).withSecond(59);
        Map<String, Object> estadisticasVentas = reporteDAO.obtenerEstadisticasVentas(inicio, fin);
        estadisticas.put("ventasDia", estadisticasVentas.get("montoTotal"));
        
        // Productos con bajo stock (menos de 10 unidades)
        List<Producto> productoBajoStock = reporteDAO.obtenerProductosBajoStock(10);
        estadisticas.put("productoBajoStock", productoBajoStock);
        
        // Top 5 productos más vendidos
        List<Map<String, Object>> topProductos = reporteDAO.obtenerProductosMasVendidos(5);
        estadisticas.put("topProductos", topProductos);
        
        // Total de productos en inventario
        long totalProductos = productoController.obtenerTodos().stream()
                .mapToInt(Producto::getStock)
                .sum();
        estadisticas.put("totalProductos", totalProductos);
        
        return estadisticas;
    }
    
    public void generarReporteInventarioExcel(String rutaArchivo) throws Exception {
        try (Workbook workbook = new XSSFWorkbook()) {
            Sheet sheet = workbook.createSheet("Inventario");
            
            // Estilos para el encabezado
            CellStyle headerStyle = workbook.createCellStyle();
            Font headerFont = workbook.createFont();
            headerFont.setBold(true);
            headerStyle.setFont(headerFont);
            
            // Crear encabezados
            Row headerRow = sheet.createRow(0);
            String[] columns = {"Código", "Nombre", "Categoría", "Stock", "Precio"};
            for (int i = 0; i < columns.length; i++) {
                Cell cell = headerRow.createCell(i);
                cell.setCellValue(columns[i]);
                cell.setCellStyle(headerStyle);
            }
            
            // Llenar datos
            List<Producto> productos = productoController.obtenerTodos();
            int rowNum = 1;
            for (Producto producto : productos) {
                Row row = sheet.createRow(rowNum++);
                row.createCell(0).setCellValue(producto.getCodigo());
                row.createCell(1).setCellValue(producto.getNombre());
                row.createCell(2).setCellValue(producto.getCategoria());
                row.createCell(3).setCellValue(producto.getStock());
                row.createCell(4).setCellValue(producto.getPrecio());
            }
            
            // Autoajustar columnas
            for (int i = 0; i < columns.length; i++) {
                sheet.autoSizeColumn(i);
            }
            
            // Guardar archivo
            try (FileOutputStream fileOut = new FileOutputStream(rutaArchivo)) {
                workbook.write(fileOut);
            }
        }
    }
    
    public void generarReporteVentasExcel(String rutaArchivo, LocalDateTime inicio, LocalDateTime fin) throws Exception {
        try (Workbook workbook = new XSSFWorkbook()) {
            Sheet sheet = workbook.createSheet("Reporte de Ventas");
            
            // Estilos para el encabezado
            CellStyle headerStyle = workbook.createCellStyle();
            Font headerFont = workbook.createFont();
            headerFont.setBold(true);
            headerStyle.setFont(headerFont);
            
            // Crear encabezados
            Row headerRow = sheet.createRow(0);
            String[] columns = {"N° Venta", "Fecha", "Método Pago", "Total"};
            for (int i = 0; i < columns.length; i++) {
                Cell cell = headerRow.createCell(i);
                cell.setCellValue(columns[i]);
                cell.setCellStyle(headerStyle);
            }
            
            // Llenar datos
            List<Venta> ventas = ventaController.obtenerVentasPorFecha(inicio, fin);
            DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm");
            
            int rowNum = 1;
            for (Venta venta : ventas) {
                Row row = sheet.createRow(rowNum++);
                row.createCell(0).setCellValue(venta.getNumeroVenta());
                row.createCell(1).setCellValue(venta.getFecha().format(formatter));
                row.createCell(2).setCellValue(venta.getMetodoPago());
                row.createCell(3).setCellValue(venta.getTotal());
            }
            
            // Autoajustar columnas
            for (int i = 0; i < columns.length; i++) {
                sheet.autoSizeColumn(i);
            }
            
            // Guardar archivo
            try (FileOutputStream fileOut = new FileOutputStream(rutaArchivo)) {
                workbook.write(fileOut);
            }
        }
    }
}