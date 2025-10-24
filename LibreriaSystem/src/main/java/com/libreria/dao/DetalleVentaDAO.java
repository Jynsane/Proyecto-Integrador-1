package com.libreria.dao;

import com.libreria.model.DetalleVenta;
import com.libreria.model.Producto;
import com.libreria.model.Venta;
import com.libreria.util.DatabaseConnection;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class DetalleVentaDAO implements CrudDAO<DetalleVenta> {
    private final ProductoDAO productoDAO;
    
    public DetalleVentaDAO() {
        this.productoDAO = new ProductoDAO();
    }
    
    @Override
    public void crear(DetalleVenta detalle) throws SQLException {
        // Delegar a la sobrecarga que crea su propia conexión
        try (Connection conn = DatabaseConnection.getConnection()) {
            crear(detalle, conn);
        }
    }

    /**
     * Inserta un detalle de venta usando la conexión proporcionada. No cierra la conexión.
     * Esta variante permite que VentaDAO comparta la misma conexión/transaction.
     */
    public void crear(DetalleVenta detalle, Connection conn) throws SQLException {
        String sql = "INSERT INTO detalles_venta (venta_id, producto_id, cantidad, precio_unitario) VALUES (?, ?, ?, ?)";

        try (PreparedStatement stmt = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            stmt.setInt(1, detalle.getVenta().getId());
            stmt.setInt(2, detalle.getProducto().getId());
            stmt.setInt(3, detalle.getCantidad());
            stmt.setDouble(4, detalle.getPrecioUnitario());

            stmt.executeUpdate();

            try (ResultSet generatedKeys = stmt.getGeneratedKeys()) {
                if (generatedKeys.next()) {
                    detalle.setId(generatedKeys.getInt(1));
                }
            }
        }
    }
    
    @Override
    public DetalleVenta obtenerPorId(int id) throws SQLException {
        String sql = "SELECT * FROM detalles_venta WHERE id = ?";
        
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            stmt.setInt(1, id);
            
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    return mapearDetalleVenta(rs);
                }
            }
        }
        return null;
    }
    
    @Override
    public List<DetalleVenta> obtenerTodos() throws SQLException {
        List<DetalleVenta> detalles = new ArrayList<>();
        String sql = "SELECT * FROM detalles_venta";
        
        try (Connection conn = DatabaseConnection.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {
            
            while (rs.next()) {
                detalles.add(mapearDetalleVenta(rs));
            }
        }
        return detalles;
    }
    
    @Override
    public void actualizar(DetalleVenta detalle) throws SQLException {
        throw new UnsupportedOperationException("No se permite actualizar detalles de venta");
    }
    
    @Override
    public void eliminar(int id) throws SQLException {
        throw new UnsupportedOperationException("No se permite eliminar detalles de venta");
    }
    
    public List<DetalleVenta> obtenerPorVenta(int ventaId) throws SQLException {
        List<DetalleVenta> detalles = new ArrayList<>();
        String sql = "SELECT * FROM detalles_venta WHERE venta_id = ?";
        
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            stmt.setInt(1, ventaId);
            
            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    detalles.add(mapearDetalleVenta(rs));
                }
            }
        }
        return detalles;
    }
    
    private DetalleVenta mapearDetalleVenta(ResultSet rs) throws SQLException {
        DetalleVenta detalle = new DetalleVenta();
        detalle.setId(rs.getInt("id"));
        
        // Obtener el producto
        Producto producto = productoDAO.obtenerPorId(rs.getInt("producto_id"));
        detalle.setProducto(producto);
        
        // Crear objeto Venta con solo el ID
        Venta venta = new Venta();
        venta.setId(rs.getInt("venta_id"));
        detalle.setVenta(venta);
        
        detalle.setCantidad(rs.getInt("cantidad"));
        detalle.setPrecioUnitario(rs.getDouble("precio_unitario"));
        
        return detalle;
    }
}
