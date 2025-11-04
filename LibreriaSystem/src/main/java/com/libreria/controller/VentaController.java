package com.libreria.controller;

import com.libreria.model.Venta;
import com.libreria.model.DetalleVenta;
import com.libreria.dao.VentaDAO;
import org.apache.poi.ss.usermodel.*;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

import java.io.FileOutputStream;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;

public class VentaController extends BaseController<Venta> {
    private final ProductoController productoController;
    private final VentaDAO ventaDAO;
    
    public VentaController() {
        this.productoController = new ProductoController();
        this.ventaDAO = new VentaDAO();
    }

    @Override
    public void crear(Venta venta) throws Exception {
        validarDatos(venta);
        validarVenta(venta);
        
        // Generar número de venta consultando la base de datos
        venta.setNumeroVenta(ventaDAO.generarNumeroVenta());
        
        // Persistir la venta
        ventaDAO.crear(venta);
        
        // Actualizar stock de productos
        for (DetalleVenta detalle : venta.getDetalles()) {
            productoController.actualizarStock(detalle.getProducto().getId(), -detalle.getCantidad());
        }
    }
    
    @Override
    public void actualizar(Venta venta) throws Exception {
        throw new UnsupportedOperationException("No se permite actualizar ventas");
    }
    
    @Override
    public void eliminar(int id) throws Exception {
        throw new UnsupportedOperationException("No se permite eliminar ventas");
    }
    
    @Override
    public Venta obtenerPorId(int id) throws Exception {
        return ventaDAO.obtenerPorId(id);
    }
    
    @Override
    public List<Venta> obtenerTodos() throws Exception {
        return ventaDAO.obtenerTodos();
    }
    
    private void validarVenta(Venta venta) throws Exception {
        if (venta.getDetalles() == null || venta.getDetalles().isEmpty()) {
            throw new Exception("La venta debe tener al menos un producto");
        }
        
        if (venta.getMetodoPago() == null || venta.getMetodoPago().trim().isEmpty()) {
            throw new Exception("El método de pago es requerido");
        }
        
        // Validar stock disponible
        for (DetalleVenta detalle : venta.getDetalles()) {
            if (!productoController.verificarStockDisponible(
                    detalle.getProducto().getId(), 
                    detalle.getCantidad())) {
                throw new Exception("Stock insuficiente para el producto: " + 
                        detalle.getProducto().getNombre());
            }
        }
    }
    
    public List<Venta> obtenerVentasPorFecha(LocalDateTime inicio, LocalDateTime fin) throws Exception {
        return ventaDAO.obtenerPorFecha(inicio, fin);
    }
    
    public double calcularTotalVentasDia() throws Exception {
        LocalDateTime inicio = LocalDateTime.now().withHour(0).withMinute(0).withSecond(0);
        LocalDateTime fin = LocalDateTime.now().withHour(23).withMinute(59).withSecond(59);
        
        return obtenerVentasPorFecha(inicio, fin).stream()
                .mapToDouble(Venta::getTotal)
                .sum();
    }
    
    public void generarReporteVentasExcel(String rutaArchivo, LocalDateTime inicio, LocalDateTime fin) throws Exception {
        try (Workbook workbook = new XSSFWorkbook()) {
            Sheet sheet = workbook.createSheet("Reporte de Ventas");
            
            // Estilos
            CellStyle headerStyle = workbook.createCellStyle();
            Font headerFont = workbook.createFont();
            headerFont.setBold(true);
            headerFont.setColor(IndexedColors.WHITE.getIndex());
            headerStyle.setFont(headerFont);
            headerStyle.setFillForegroundColor(IndexedColors.DARK_BLUE.getIndex());
            headerStyle.setFillPattern(FillPatternType.SOLID_FOREGROUND);
            
            // Título
            Row titleRow = sheet.createRow(0);
            Cell titleCell = titleRow.createCell(0);
            titleCell.setCellValue("REPORTE DE VENTAS");
            
            // Período
            Row periodRow = sheet.createRow(1);
            Cell periodCell = periodRow.createCell(0);
            DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd/MM/yyyy");
            periodCell.setCellValue("Período: " + inicio.format(formatter) + " - " + fin.format(formatter));
            
            // Espacio
            sheet.createRow(2);
            
            // Encabezados
            Row headerRow = sheet.createRow(3);
            String[] columns = {"N° Venta", "Fecha", "Método Pago", "Total"};
            for (int i = 0; i < columns.length; i++) {
                Cell cell = headerRow.createCell(i);
                cell.setCellValue(columns[i]);
                cell.setCellStyle(headerStyle);
            }
            
            // Datos
            List<Venta> ventas = obtenerVentasPorFecha(inicio, fin);
            DateTimeFormatter dateFormatter = DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm");
            
            int rowNum = 4;
            double totalGeneral = 0;
            
            for (Venta venta : ventas) {
                Row row = sheet.createRow(rowNum++);
                row.createCell(0).setCellValue(venta.getNumeroVenta());
                row.createCell(1).setCellValue(venta.getFecha().format(dateFormatter));
                row.createCell(2).setCellValue(venta.getMetodoPago());
                row.createCell(3).setCellValue(venta.getTotal());
                totalGeneral += venta.getTotal();
            }
            
            // Total
            rowNum++;
            Row totalRow = sheet.createRow(rowNum);
            Cell totalLabelCell = totalRow.createCell(2);
            totalLabelCell.setCellValue("TOTAL:");
            
            CellStyle totalStyle = workbook.createCellStyle();
            Font totalFont = workbook.createFont();
            totalFont.setBold(true);
            totalStyle.setFont(totalFont);
            totalLabelCell.setCellStyle(totalStyle);
            
            Cell totalValueCell = totalRow.createCell(3);
            totalValueCell.setCellValue(totalGeneral);
            totalValueCell.setCellStyle(totalStyle);
            
            // Autoajustar columnas
            for (int i = 0; i < columns.length; i++) {
                sheet.autoSizeColumn(i);
            }
            
            // Guardar
            try (FileOutputStream fileOut = new FileOutputStream(rutaArchivo)) {
                workbook.write(fileOut);
            }
        }
    }
}