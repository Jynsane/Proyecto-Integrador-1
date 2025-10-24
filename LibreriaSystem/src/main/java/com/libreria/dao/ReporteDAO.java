package com.libreria.dao;

import com.libreria.model.Producto;
import com.libreria.model.Venta;
import com.libreria.util.DatabaseConnection;

import java.sql.*;
import java.time.LocalDateTime;
import java.util.*;

public class ReporteDAO {
    private final ProductoDAO productoDAO;
    private final VentaDAO ventaDAO;
    
    public ReporteDAO() {
        this.productoDAO = new ProductoDAO();
        this.ventaDAO = new VentaDAO();
    }
    
    public Map<String, Object> obtenerEstadisticasVentas(LocalDateTime inicio, LocalDateTime fin) throws SQLException {
        Map<String, Object> estadisticas = new HashMap<>();
        
        String sql = """
            SELECT 
                COUNT(*) as total_ventas,
                SUM(total) as monto_total,
                AVG(total) as promedio_venta,
                MIN(total) as venta_minima,
                MAX(total) as venta_maxima
            FROM ventas 
            WHERE fecha BETWEEN ? AND ?
        """;
        
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            stmt.setTimestamp(1, Timestamp.valueOf(inicio));
            stmt.setTimestamp(2, Timestamp.valueOf(fin));
            
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    estadisticas.put("totalVentas", rs.getInt("total_ventas"));
                    estadisticas.put("montoTotal", rs.getDouble("monto_total"));
                    estadisticas.put("promedioVenta", rs.getDouble("promedio_venta"));
                    estadisticas.put("ventaMinima", rs.getDouble("venta_minima"));
                    estadisticas.put("ventaMaxima", rs.getDouble("venta_maxima"));
                }
            }
        }
        
        return estadisticas;
    }
    
    public List<Map<String, Object>> obtenerProductosMasVendidos(int limite) throws SQLException {
        List<Map<String, Object>> productos = new ArrayList<>();
        
        String sql = """
            SELECT 
                p.id,
                p.codigo,
                p.nombre,
                SUM(d.cantidad) as total_vendido,
                SUM(d.cantidad * d.precio_unitario) as total_ingresos
            FROM productos p
            JOIN detalles_venta d ON p.id = d.producto_id
            GROUP BY p.id, p.codigo, p.nombre
            ORDER BY total_vendido DESC
            LIMIT ?
        """;
        
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            stmt.setInt(1, limite);
            
            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    Map<String, Object> producto = new HashMap<>();
                    producto.put("id", rs.getInt("id"));
                    producto.put("codigo", rs.getString("codigo"));
                    producto.put("nombre", rs.getString("nombre"));
                    producto.put("totalVendido", rs.getInt("total_vendido"));
                    producto.put("totalIngresos", rs.getDouble("total_ingresos"));
                    productos.add(producto);
                }
            }
        }
        
        return productos;
    }
    
    public List<Map<String, Object>> obtenerVentasPorCategoria() throws SQLException {
        List<Map<String, Object>> ventas = new ArrayList<>();
        
        String sql = """
            SELECT 
                p.categoria,
                COUNT(DISTINCT v.id) as total_ventas,
                SUM(d.cantidad) as total_unidades,
                SUM(d.cantidad * d.precio_unitario) as total_ingresos
            FROM productos p
            JOIN detalles_venta d ON p.id = d.producto_id
            JOIN ventas v ON d.venta_id = v.id
            GROUP BY p.categoria
            ORDER BY total_ingresos DESC
        """;
        
        try (Connection conn = DatabaseConnection.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {
            
            while (rs.next()) {
                Map<String, Object> categoria = new HashMap<>();
                categoria.put("categoria", rs.getString("categoria"));
                categoria.put("totalVentas", rs.getInt("total_ventas"));
                categoria.put("totalUnidades", rs.getInt("total_unidades"));
                categoria.put("totalIngresos", rs.getDouble("total_ingresos"));
                ventas.add(categoria);
            }
        }
        
        return ventas;
    }
    
    public List<Producto> obtenerProductosBajoStock(int umbral) throws SQLException {
        List<Producto> productos = new ArrayList<>();
        String sql = "SELECT * FROM productos WHERE stock <= ? ORDER BY stock ASC";
        
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            stmt.setInt(1, umbral);
            
            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    Producto producto = productoDAO.mapearProducto(rs);
                    productos.add(producto);
                }
            }
        }
        
        return productos;
    }
    
    public Map<String, Double> obtenerRendimientoProducto(int productoId) throws SQLException {
        Map<String, Double> rendimiento = new HashMap<>();
        
        String sql = """
            SELECT 
                COUNT(DISTINCT v.id) as frecuencia_venta,
                AVG(d.cantidad) as promedio_cantidad,
                SUM(d.cantidad * d.precio_unitario) as total_ingresos
            FROM detalles_venta d
            JOIN ventas v ON d.venta_id = v.id
            WHERE d.producto_id = ?
        """;
        
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            stmt.setInt(1, productoId);
            
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    rendimiento.put("frecuenciaVenta", rs.getDouble("frecuencia_venta"));
                    rendimiento.put("promedioCantidad", rs.getDouble("promedio_cantidad"));
                    rendimiento.put("totalIngresos", rs.getDouble("total_ingresos"));
                }
            }
        }
        
        return rendimiento;
    }
}