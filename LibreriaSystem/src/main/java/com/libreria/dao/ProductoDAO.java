package com.libreria.dao;

import com.libreria.model.Producto;
import com.libreria.util.DatabaseConnection;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

/**
 * ProductoDAO - Compatible con MySQL (producción) y H2 (tests)
 * 
 * CAMBIO CLAVE: No cierra conexiones en modo test
 * 
 * Ubicación: src/main/java/com/libreria/dao/ProductoDAO.java
 */
public class ProductoDAO implements CrudDAO<Producto> {
    
    @Override
    public void crear(Producto producto) throws SQLException {
        String sql = "INSERT INTO productos (codigo, nombre, categoria, precio, stock, descripcion) VALUES (?, ?, ?, ?, ?, ?)";
        
        Connection conn = DatabaseConnection.getConnection();
        boolean shouldCloseConnection = !DatabaseConnection.isTestMode(); // ← CAMBIO CLAVE
        
        try {
            try (PreparedStatement stmt = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
                stmt.setString(1, producto.getCodigo());
                stmt.setString(2, producto.getNombre());
                stmt.setString(3, producto.getCategoria());
                stmt.setDouble(4, producto.getPrecio());
                stmt.setInt(5, producto.getStock());
                stmt.setString(6, producto.getDescripcion());
                
                stmt.executeUpdate();
                
                try (ResultSet generatedKeys = stmt.getGeneratedKeys()) {
                    if (generatedKeys.next()) {
                        producto.setId(generatedKeys.getInt(1));
                    }
                }
            }
        } finally {
            // Solo cerrar conexión si NO estamos en modo test
            if (shouldCloseConnection && conn != null) {
                conn.close();
            }
        }
    }
    
    @Override
    public Producto obtenerPorId(int id) throws SQLException {
        String sql = "SELECT * FROM productos WHERE id = ?";
        
        Connection conn = DatabaseConnection.getConnection();
        boolean shouldCloseConnection = !DatabaseConnection.isTestMode();
        
        try {
            try (PreparedStatement stmt = conn.prepareStatement(sql)) {
                stmt.setInt(1, id);
                
                try (ResultSet rs = stmt.executeQuery()) {
                    if (rs.next()) {
                        return mapearProducto(rs);
                    }
                }
            }
            return null;
        } finally {
            if (shouldCloseConnection && conn != null) {
                conn.close();
            }
        }
    }
    
    @Override
    public List<Producto> obtenerTodos() throws SQLException {
        List<Producto> productos = new ArrayList<>();
        String sql = "SELECT * FROM productos";
        
        Connection conn = DatabaseConnection.getConnection();
        boolean shouldCloseConnection = !DatabaseConnection.isTestMode();
        
        try {
            try (Statement stmt = conn.createStatement();
                 ResultSet rs = stmt.executeQuery(sql)) {
                
                while (rs.next()) {
                    productos.add(mapearProducto(rs));
                }
            }
            return productos;
        } finally {
            if (shouldCloseConnection && conn != null) {
                conn.close();
            }
        }
    }
    
    @Override
    public void actualizar(Producto producto) throws SQLException {
        String sql = "UPDATE productos SET codigo = ?, nombre = ?, categoria = ?, precio = ?, stock = ?, descripcion = ? WHERE id = ?";
        
        Connection conn = DatabaseConnection.getConnection();
        boolean shouldCloseConnection = !DatabaseConnection.isTestMode();
        
        try {
            try (PreparedStatement stmt = conn.prepareStatement(sql)) {
                stmt.setString(1, producto.getCodigo());
                stmt.setString(2, producto.getNombre());
                stmt.setString(3, producto.getCategoria());
                stmt.setDouble(4, producto.getPrecio());
                stmt.setInt(5, producto.getStock());
                stmt.setString(6, producto.getDescripcion());
                stmt.setInt(7, producto.getId());
                
                stmt.executeUpdate();
            }
        } finally {
            if (shouldCloseConnection && conn != null) {
                conn.close();
            }
        }
    }
    
    @Override
    public void eliminar(int id) throws SQLException {
        String sql = "DELETE FROM productos WHERE id = ?";
        
        Connection conn = DatabaseConnection.getConnection();
        boolean shouldCloseConnection = !DatabaseConnection.isTestMode();
        
        try {
            try (PreparedStatement stmt = conn.prepareStatement(sql)) {
                stmt.setInt(1, id);
                stmt.executeUpdate();
            }
        } finally {
            if (shouldCloseConnection && conn != null) {
                conn.close();
            }
        }
    }
    
    /**
     * Mapea un ResultSet a un objeto Producto
     */
    public Producto mapearProducto(ResultSet rs) throws SQLException {
        Producto producto = new Producto();
        producto.setId(rs.getInt("id"));
        producto.setCodigo(rs.getString("codigo"));
        producto.setNombre(rs.getString("nombre"));
        producto.setCategoria(rs.getString("categoria"));
        producto.setPrecio(rs.getDouble("precio"));
        producto.setStock(rs.getInt("stock"));
        producto.setDescripcion(rs.getString("descripcion"));
        return producto;
    }
}