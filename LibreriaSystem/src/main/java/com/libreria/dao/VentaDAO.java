package com.libreria.dao;

import com.libreria.model.Venta;
import com.libreria.model.DetalleVenta;
import com.libreria.model.Producto;
import com.libreria.util.DatabaseConnection;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import java.time.LocalDateTime;

public class VentaDAO implements CrudDAO<Venta> {
    private final DetalleVentaDAO detalleVentaDAO;
    private final ProductoDAO productoDAO;
    
    public VentaDAO() {
        this.detalleVentaDAO = new DetalleVentaDAO();
        this.productoDAO = new ProductoDAO();
    }
    
    @Override
    public void crear(Venta venta) throws SQLException {
        String sql = "INSERT INTO ventas (numero_venta, fecha, total, metodo_pago) VALUES (?, ?, ?, ?)";
        
        try (Connection conn = DatabaseConnection.getConnection()) {
            conn.setAutoCommit(false);
            try {
                // Insertar venta
                try (PreparedStatement stmt = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
                    stmt.setString(1, venta.getNumeroVenta());
                    stmt.setTimestamp(2, Timestamp.valueOf(venta.getFecha()));
                    stmt.setDouble(3, venta.getTotal());
                    stmt.setString(4, venta.getMetodoPago());

                    stmt.executeUpdate();

                    // Obtener ID generado
                    try (ResultSet generatedKeys = stmt.getGeneratedKeys()) {
                        if (generatedKeys.next()) {
                            venta.setId(generatedKeys.getInt(1));

                            // Insertar detalles usando la misma conexión
                            for (DetalleVenta detalle : venta.getDetalles()) {
                                detalle.setVenta(venta);
                                detalleVentaDAO.crear(detalle, conn); // usar la misma conexión
                            }

                            conn.commit();
                        } else {
                            throw new SQLException("No se pudo obtener el ID de la venta");
                        }
                    }
                }
            } catch (Exception e) {
                conn.rollback();
                throw e;
            } finally {
                conn.setAutoCommit(true);
            }
        }
    }
    
    @Override
    public Venta obtenerPorId(int id) throws SQLException {
        String sql = "SELECT * FROM ventas WHERE id = ?";
        
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            stmt.setInt(1, id);
            
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    Venta venta = mapearVenta(rs);
                    venta.setDetalles(detalleVentaDAO.obtenerPorVenta(venta.getId()));
                    return venta;
                }
            }
        }
        return null;
    }
    
    @Override
    public List<Venta> obtenerTodos() throws SQLException {
        List<Venta> ventas = new ArrayList<>();
        String sql = "SELECT * FROM ventas ORDER BY fecha DESC";
        
        try (Connection conn = DatabaseConnection.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {
            
            while (rs.next()) {
                Venta venta = mapearVenta(rs);
                venta.setDetalles(detalleVentaDAO.obtenerPorVenta(venta.getId()));
                ventas.add(venta);
            }
        }
        return ventas;
    }
    
    @Override
    public void actualizar(Venta venta) throws SQLException {
        throw new UnsupportedOperationException("No se permite actualizar ventas");
    }
    
    @Override
    public void eliminar(int id) throws SQLException {
        throw new UnsupportedOperationException("No se permite eliminar ventas");
    }
    
    public List<Venta> obtenerPorFecha(LocalDateTime inicio, LocalDateTime fin) throws SQLException {
        List<Venta> ventas = new ArrayList<>();
        String sql = "SELECT * FROM ventas WHERE fecha BETWEEN ? AND ? ORDER BY fecha DESC";
        
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            stmt.setTimestamp(1, Timestamp.valueOf(inicio));
            stmt.setTimestamp(2, Timestamp.valueOf(fin));
            
            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    Venta venta = mapearVenta(rs);
                    venta.setDetalles(detalleVentaDAO.obtenerPorVenta(venta.getId()));
                    ventas.add(venta);
                }
            }
        }
        return ventas;
    }

    /**
     * Genera un número de venta único para el día actual basado en los registros existentes.
     * Usa una consulta a la base de datos para obtener el último número y lo incrementa.
     * El método está sincronizado para evitar colisiones dentro de la misma JVM.
     */
    public synchronized String generarNumeroVenta() throws SQLException {
        LocalDateTime ahora = LocalDateTime.now();
        String fecha = ahora.format(java.time.format.DateTimeFormatter.ofPattern("yyyyMMdd"));
        String like = "V" + fecha + "-%";

        String sql = "SELECT numero_venta FROM ventas WHERE numero_venta LIKE ? ORDER BY numero_venta DESC LIMIT 1";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setString(1, like);
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    String last = rs.getString(1);
                    String[] parts = last.split("-");
                    try {
                        int n = Integer.parseInt(parts[1]);
                        return "V" + fecha + "-" + String.format("%04d", n + 1);
                    } catch (Exception ex) {
                        // Si parsing falla, reiniciar a 0001
                        return "V" + fecha + "-0001";
                    }
                } else {
                    return "V" + fecha + "-0001";
                }
            }
        }
    }
    
    private Venta mapearVenta(ResultSet rs) throws SQLException {
        Venta venta = new Venta();
        venta.setId(rs.getInt("id"));
        venta.setNumeroVenta(rs.getString("numero_venta"));
        venta.setFecha(rs.getTimestamp("fecha").toLocalDateTime());
        venta.setTotal(rs.getDouble("total"));
        venta.setMetodoPago(rs.getString("metodo_pago"));
        return venta;
    }
}
