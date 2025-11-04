package com.libreria.controller;

import com.libreria.model.Venta;
import com.libreria.model.Producto;
//import com.libreria.model.DetalleVenta;
import com.libreria.dao.ReporteDAO;
import org.apache.poi.ss.usermodel.*;
import org.apache.poi.xssf.usermodel.*;
import org.apache.poi.ss.util.CellRangeAddress;
//import org.apache.poi.ss.util.CellReference;
import org.apache.poi.xddf.usermodel.chart.*;

import java.io.FileOutputStream;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.*;

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
        
        LocalDateTime inicio = LocalDateTime.now().withHour(0).withMinute(0).withSecond(0);
        LocalDateTime fin = LocalDateTime.now().withHour(23).withMinute(59).withSecond(59);
        Map<String, Object> estadisticasVentas = reporteDAO.obtenerEstadisticasVentas(inicio, fin);
        estadisticas.put("ventasDia", estadisticasVentas.get("montoTotal"));
        
        List<Producto> productoBajoStock = reporteDAO.obtenerProductosBajoStock(10);
        estadisticas.put("productoBajoStock", productoBajoStock);
        
        List<Map<String, Object>> topProductos = reporteDAO.obtenerProductosMasVendidos(5);
        estadisticas.put("topProductos", topProductos);
        
        long totalProductos = productoController.obtenerTodos().stream()
                .mapToInt(Producto::getStock)
                .sum();
        estadisticas.put("totalProductos", totalProductos);
        
        return estadisticas;
    }
    
    public void generarReporteInventarioExcel(String rutaArchivo) throws Exception {
        try (XSSFWorkbook workbook = new XSSFWorkbook()) {
            XSSFSheet sheet = workbook.createSheet("Inventario");
            
            // Estilos
            CellStyle headerStyle = createHeaderStyle(workbook);
            CellStyle titleStyle = createTitleStyle(workbook);
            CellStyle dataStyle = createDataStyle(workbook);
            
            // Título del reporte
            Row titleRow = sheet.createRow(0);
            Cell titleCell = titleRow.createCell(0);
            titleCell.setCellValue("REPORTE DE INVENTARIO");
            titleCell.setCellStyle(titleStyle);
            sheet.addMergedRegion(new CellRangeAddress(0, 0, 0, 4));
            
            // Fecha del reporte
            Row dateRow = sheet.createRow(1);
            Cell dateCell = dateRow.createCell(0);
            dateCell.setCellValue("Fecha: " + LocalDateTime.now().format(DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm")));
            sheet.addMergedRegion(new CellRangeAddress(1, 1, 0, 4));
            
            // Crear encabezados
            Row headerRow = sheet.createRow(3);
            String[] columns = {"Código", "Nombre", "Categoría", "Stock", "Precio"};
            for (int i = 0; i < columns.length; i++) {
                Cell cell = headerRow.createCell(i);
                cell.setCellValue(columns[i]);
                cell.setCellStyle(headerStyle);
            }
            
            // Llenar datos
            List<Producto> productos = productoController.obtenerTodos();
            int rowNum = 4;
            for (Producto producto : productos) {
                Row row = sheet.createRow(rowNum++);
                
                Cell cell0 = row.createCell(0);
                cell0.setCellValue(producto.getCodigo());
                cell0.setCellStyle(dataStyle);
                
                Cell cell1 = row.createCell(1);
                cell1.setCellValue(producto.getNombre());
                cell1.setCellStyle(dataStyle);
                
                Cell cell2 = row.createCell(2);
                cell2.setCellValue(producto.getCategoria());
                cell2.setCellStyle(dataStyle);
                
                Cell cell3 = row.createCell(3);
                cell3.setCellValue(producto.getStock());
                cell3.setCellStyle(dataStyle);
                
                Cell cell4 = row.createCell(4);
                cell4.setCellValue(producto.getPrecio());
                cell4.setCellStyle(dataStyle);
            }
            
            // Autoajustar columnas
            for (int i = 0; i < columns.length; i++) {
                sheet.autoSizeColumn(i);
            }
            
            // Crear gráfico de stock por categoría
            if (!productos.isEmpty()) {
                crearGraficoStockPorCategoria(sheet, productos, rowNum + 2);
            }
            
            // Guardar archivo
            try (FileOutputStream fileOut = new FileOutputStream(rutaArchivo)) {
                workbook.write(fileOut);
            }
        }
    }
    
    public void generarReporteVentasExcel(String rutaArchivo, LocalDateTime inicio, LocalDateTime fin) throws Exception {
        try (XSSFWorkbook workbook = new XSSFWorkbook()) {
            XSSFSheet sheet = workbook.createSheet("Reporte de Ventas");
            
            // Estilos
            CellStyle headerStyle = createHeaderStyle(workbook);
            CellStyle titleStyle = createTitleStyle(workbook);
            CellStyle dataStyle = createDataStyle(workbook);
            CellStyle currencyStyle = createCurrencyStyle(workbook);
            
            // Título del reporte
            Row titleRow = sheet.createRow(0);
            Cell titleCell = titleRow.createCell(0);
            titleCell.setCellValue("REPORTE DE VENTAS");
            titleCell.setCellStyle(titleStyle);
            sheet.addMergedRegion(new CellRangeAddress(0, 0, 0, 3));
            
            // Período del reporte
            Row dateRow = sheet.createRow(1);
            Cell dateCell = dateRow.createCell(0);
            DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd/MM/yyyy");
            dateCell.setCellValue("Período: " + inicio.format(formatter) + " - " + fin.format(formatter));
            sheet.addMergedRegion(new CellRangeAddress(1, 1, 0, 3));
            
            // Estadísticas generales
            Map<String, Object> estadisticas = reporteDAO.obtenerEstadisticasVentas(inicio, fin);
            
            Row statsLabelRow = sheet.createRow(3);
            Cell statsLabel = statsLabelRow.createCell(0);
            statsLabel.setCellValue("RESUMEN GENERAL");
            statsLabel.setCellStyle(headerStyle);
            sheet.addMergedRegion(new CellRangeAddress(3, 3, 0, 3));
            
            Row statsRow = sheet.createRow(4);
            statsRow.createCell(0).setCellValue("Total Ventas:");
            statsRow.createCell(1).setCellValue((Integer) estadisticas.get("totalVentas"));
            statsRow.createCell(2).setCellValue("Monto Total:");
            Cell montoCell = statsRow.createCell(3);
            montoCell.setCellValue((Double) estadisticas.get("montoTotal"));
            montoCell.setCellStyle(currencyStyle);
            
            // Crear encabezados de tabla de ventas
            Row headerRow = sheet.createRow(6);
            String[] columns = {"N° Venta", "Fecha", "Método Pago", "Total"};
            for (int i = 0; i < columns.length; i++) {
                Cell cell = headerRow.createCell(i);
                cell.setCellValue(columns[i]);
                cell.setCellStyle(headerStyle);
            }
            
            // Llenar datos
            List<Venta> ventas = ventaController.obtenerVentasPorFecha(inicio, fin);
            DateTimeFormatter dtFormatter = DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm");
            
            int rowNum = 7;
            for (Venta venta : ventas) {
                Row row = sheet.createRow(rowNum++);
                
                Cell cell0 = row.createCell(0);
                cell0.setCellValue(venta.getNumeroVenta());
                cell0.setCellStyle(dataStyle);
                
                Cell cell1 = row.createCell(1);
                cell1.setCellValue(venta.getFecha().format(dtFormatter));
                cell1.setCellStyle(dataStyle);
                
                Cell cell2 = row.createCell(2);
                cell2.setCellValue(venta.getMetodoPago());
                cell2.setCellStyle(dataStyle);
                
                Cell cell3 = row.createCell(3);
                cell3.setCellValue(venta.getTotal());
                cell3.setCellStyle(currencyStyle);
            }
            
            // Autoajustar columnas
            for (int i = 0; i < columns.length; i++) {
                sheet.autoSizeColumn(i);
            }
            
            // Crear gráfico de ventas
            if (!ventas.isEmpty()) {
                crearGraficoVentas(sheet, ventas, rowNum + 2);
            }
            
            // Crear hoja de detalles por producto
            crearHojaDetalleProductos(workbook, inicio, fin);
            
            // Guardar archivo
            try (FileOutputStream fileOut = new FileOutputStream(rutaArchivo)) {
                workbook.write(fileOut);
            }
        }
    }
    
    private void crearHojaDetalleProductos(XSSFWorkbook workbook, LocalDateTime inicio, LocalDateTime fin) throws Exception {
        XSSFSheet sheet = workbook.createSheet("Detalle por Productos");
        
        CellStyle headerStyle = createHeaderStyle(workbook);
        CellStyle titleStyle = createTitleStyle(workbook);
        CellStyle dataStyle = createDataStyle(workbook);
        CellStyle currencyStyle = createCurrencyStyle(workbook);
        
        // Título
        Row titleRow = sheet.createRow(0);
        Cell titleCell = titleRow.createCell(0);
        titleCell.setCellValue("VENTAS POR PRODUCTO");
        titleCell.setCellStyle(titleStyle);
        sheet.addMergedRegion(new CellRangeAddress(0, 0, 0, 3));
        
        // Encabezados
        Row headerRow = sheet.createRow(2);
        String[] columns = {"Código", "Nombre", "Unidades Vendidas", "Total Ingresos"};
        for (int i = 0; i < columns.length; i++) {
            Cell cell = headerRow.createCell(i);
            cell.setCellValue(columns[i]);
            cell.setCellStyle(headerStyle);
        }
        
        // Datos
        List<Map<String, Object>> topProductos = reporteDAO.obtenerProductosMasVendidos(20);
        int rowNum = 3;
        for (Map<String, Object> producto : topProductos) {
            Row row = sheet.createRow(rowNum++);
            
            Cell cell0 = row.createCell(0);
            cell0.setCellValue((String) producto.get("codigo"));
            cell0.setCellStyle(dataStyle);
            
            Cell cell1 = row.createCell(1);
            cell1.setCellValue((String) producto.get("nombre"));
            cell1.setCellStyle(dataStyle);
            
            Cell cell2 = row.createCell(2);
            cell2.setCellValue((Integer) producto.get("totalVendido"));
            cell2.setCellStyle(dataStyle);
            
            Cell cell3 = row.createCell(3);
            cell3.setCellValue((Double) producto.get("totalIngresos"));
            cell3.setCellStyle(currencyStyle);
        }
        
        // Autoajustar
        for (int i = 0; i < columns.length; i++) {
            sheet.autoSizeColumn(i);
        }
        
        // Crear gráfico de productos más vendidos
        if (!topProductos.isEmpty()) {
            crearGraficoProductosMasVendidos(sheet, topProductos, rowNum + 2);
        }
    }
    
    private void crearGraficoStockPorCategoria(XSSFSheet sheet, List<Producto> productos, int startRow) {
        // Agrupar stock por categoría
        Map<String, Integer> stockPorCategoria = new HashMap<>();
        for (Producto p : productos) {
            String cat = p.getCategoria() != null ? p.getCategoria() : "Sin Categoría";
            stockPorCategoria.merge(cat, p.getStock(), Integer::sum);
        }
        
        // Crear datos para el gráfico
        int row = startRow;
        sheet.createRow(row).createCell(0).setCellValue("Categoría");
        sheet.getRow(row).createCell(1).setCellValue("Stock");
        
        row++;
        for (Map.Entry<String, Integer> entry : stockPorCategoria.entrySet()) {
            Row dataRow = sheet.createRow(row++);
            dataRow.createCell(0).setCellValue(entry.getKey());
            dataRow.createCell(1).setCellValue(entry.getValue());
        }
        
        // Crear gráfico de barras
        XSSFDrawing drawing = sheet.createDrawingPatriarch();
        XSSFClientAnchor anchor = drawing.createAnchor(0, 0, 0, 0, 4, startRow, 12, startRow + 15);
        
        XSSFChart chart = drawing.createChart(anchor);
        chart.setTitleText("Stock por Categoría");
        
        XDDFCategoryAxis bottomAxis = chart.createCategoryAxis(AxisPosition.BOTTOM);
        bottomAxis.setTitle("Categoría");
        XDDFValueAxis leftAxis = chart.createValueAxis(AxisPosition.LEFT);
        leftAxis.setTitle("Cantidad");
        
        XDDFDataSource<String> categories = XDDFDataSourcesFactory.fromStringCellRange(sheet,
                new CellRangeAddress(startRow + 1, row - 1, 0, 0));
        XDDFNumericalDataSource<Double> values = XDDFDataSourcesFactory.fromNumericCellRange(sheet,
                new CellRangeAddress(startRow + 1, row - 1, 1, 1));
        
        XDDFChartData data = chart.createData(ChartTypes.BAR, bottomAxis, leftAxis);
        XDDFChartData.Series series = data.addSeries(categories, values);
        series.setTitle("Stock", null);
        chart.plot(data);
    }
    
    private void crearGraficoVentas(XSSFSheet sheet, List<Venta> ventas, int startRow) {
        // Agrupar ventas por día
        Map<String, Double> ventasPorDia = new TreeMap<>();
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd/MM");
        
        for (Venta v : ventas) {
            String dia = v.getFecha().format(formatter);
            ventasPorDia.merge(dia, v.getTotal(), Double::sum);
        }
        
        // Crear datos
        int row = startRow;
        sheet.createRow(row).createCell(0).setCellValue("Fecha");
        sheet.getRow(row).createCell(1).setCellValue("Total");
        
        row++;
        for (Map.Entry<String, Double> entry : ventasPorDia.entrySet()) {
            Row dataRow = sheet.createRow(row++);
            dataRow.createCell(0).setCellValue(entry.getKey());
            dataRow.createCell(1).setCellValue(entry.getValue());
        }
        
        // Crear gráfico de líneas
        XSSFDrawing drawing = sheet.createDrawingPatriarch();
        XSSFClientAnchor anchor = drawing.createAnchor(0, 0, 0, 0, 6, startRow, 14, startRow + 15);
        
        XSSFChart chart = drawing.createChart(anchor);
        chart.setTitleText("Evolución de Ventas");
        
        XDDFCategoryAxis bottomAxis = chart.createCategoryAxis(AxisPosition.BOTTOM);
        bottomAxis.setTitle("Fecha");
        XDDFValueAxis leftAxis = chart.createValueAxis(AxisPosition.LEFT);
        leftAxis.setTitle("Monto (S/)");
        
        XDDFDataSource<String> dates = XDDFDataSourcesFactory.fromStringCellRange(sheet,
                new CellRangeAddress(startRow + 1, row - 1, 0, 0));
        XDDFNumericalDataSource<Double> amounts = XDDFDataSourcesFactory.fromNumericCellRange(sheet,
                new CellRangeAddress(startRow + 1, row - 1, 1, 1));
        
        XDDFChartData data = chart.createData(ChartTypes.LINE, bottomAxis, leftAxis);
        XDDFChartData.Series series = data.addSeries(dates, amounts);
        series.setTitle("Ventas", null);
        chart.plot(data);
    }
    
    private void crearGraficoProductosMasVendidos(XSSFSheet sheet, List<Map<String, Object>> productos, int startRow) {
        int topN = Math.min(10, productos.size());
        
        int row = startRow;
        sheet.createRow(row).createCell(0).setCellValue("Producto");
        sheet.getRow(row).createCell(1).setCellValue("Unidades");
        
        row++;
        for (int i = 0; i < topN; i++) {
            Map<String, Object> p = productos.get(i);
            Row dataRow = sheet.createRow(row++);
            dataRow.createCell(0).setCellValue((String) p.get("nombre"));
            dataRow.createCell(1).setCellValue((Integer) p.get("totalVendido"));
        }
        
        // Crear gráfico circular (pie)
        XSSFDrawing drawing = sheet.createDrawingPatriarch();
        XSSFClientAnchor anchor = drawing.createAnchor(0, 0, 0, 0, 6, startRow, 14, startRow + 15);
        
        XSSFChart chart = drawing.createChart(anchor);
        chart.setTitleText("Top 10 Productos Más Vendidos");
        
        XDDFDataSource<String> products = XDDFDataSourcesFactory.fromStringCellRange(sheet,
                new CellRangeAddress(startRow + 1, row - 1, 0, 0));
        XDDFNumericalDataSource<Double> units = XDDFDataSourcesFactory.fromNumericCellRange(sheet,
                new CellRangeAddress(startRow + 1, row - 1, 1, 1));
        
        XDDFChartData data = chart.createData(ChartTypes.PIE, null, null);
        data.addSeries(products, units);
        chart.plot(data);
    }
    
    // Métodos auxiliares para estilos
    private CellStyle createHeaderStyle(Workbook workbook) {
        CellStyle style = workbook.createCellStyle();
        Font font = workbook.createFont();
        font.setBold(true);
        font.setFontHeightInPoints((short) 12);
        font.setColor(IndexedColors.WHITE.getIndex());
        style.setFont(font);
        style.setFillForegroundColor(IndexedColors.DARK_BLUE.getIndex());
        style.setFillPattern(FillPatternType.SOLID_FOREGROUND);
        style.setBorderBottom(BorderStyle.THIN);
        style.setBorderTop(BorderStyle.THIN);
        style.setBorderRight(BorderStyle.THIN);
        style.setBorderLeft(BorderStyle.THIN);
        style.setAlignment(HorizontalAlignment.CENTER);
        return style;
    }
    
    private CellStyle createTitleStyle(Workbook workbook) {
        CellStyle style = workbook.createCellStyle();
        Font font = workbook.createFont();
        font.setBold(true);
        font.setFontHeightInPoints((short) 16);
        font.setColor(IndexedColors.DARK_BLUE.getIndex());
        style.setFont(font);
        style.setAlignment(HorizontalAlignment.CENTER);
        return style;
    }
    
    private CellStyle createDataStyle(Workbook workbook) {
        CellStyle style = workbook.createCellStyle();
        style.setBorderBottom(BorderStyle.THIN);
        style.setBorderTop(BorderStyle.THIN);
        style.setBorderRight(BorderStyle.THIN);
        style.setBorderLeft(BorderStyle.THIN);
        return style;
    }
    
    private CellStyle createCurrencyStyle(Workbook workbook) {
        CellStyle style = createDataStyle(workbook);
        style.setDataFormat(workbook.createDataFormat().getFormat("S/. #,##0.00"));
        return style;
    }
}