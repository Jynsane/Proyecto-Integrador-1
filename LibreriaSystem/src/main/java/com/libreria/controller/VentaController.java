package com.libreria.controller;

import com.libreria.model.Venta;
import com.libreria.model.DetalleVenta;
import com.libreria.dao.VentaDAO;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;

public class VentaController extends BaseController<Venta> {
    private final ProductoController productoController;
    private final VentaDAO ventaDAO;
    private static int numeroVentaSecuencial = 1;
    
    public VentaController() {
        this.productoController = new ProductoController();
        this.ventaDAO = new VentaDAO();
    }

    @Override
    public void crear(Venta venta) throws Exception {
        validarDatos(venta);
        validarVenta(venta);
        
        // Generar número de venta
        // Generar número de venta consultando la base de datos para evitar duplicados
        venta.setNumeroVenta(ventaDAO.generarNumeroVenta());
        
        // Persistir la venta
        ventaDAO.crear(venta);
        
        // Actualizar stock de productos (hacerlo solo después de persistir la venta)
        for (DetalleVenta detalle : venta.getDetalles()) {
            productoController.actualizarStock(detalle.getProducto().getId(), -detalle.getCantidad());
        }
    }
    
    private String generarNumeroVenta() {
        // Este método queda obsoleto; la generación ahora se hace en VentaDAO
        // Este método queda obsoleto; la generación ahora se hace en VentaDAO
        LocalDateTime ahora = LocalDateTime.now();
        String fecha = ahora.format(DateTimeFormatter.ofPattern("yyyyMMdd"));
        String numero = String.format("%04d", numeroVentaSecuencial++);
        return "V" + fecha + "-" + numero;
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
}