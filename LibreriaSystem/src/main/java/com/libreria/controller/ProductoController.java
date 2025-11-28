package com.libreria.controller;

import com.libreria.model.Producto;
import com.libreria.dao.ProductoDAO;

import java.util.List;

/**
 * ProductoController - Gestiona la lógica de negocio de productos
 * Compatible con testing mediante inyección de dependencias
 */
public class ProductoController extends BaseController<Producto> {
    private final ProductoDAO productoDAO;
    
    // ✅ Constructor SIN parámetros (para uso normal en la interfaz gráfica)
    // Tu aplicación usa ESTE constructor - no se toca nada
    public ProductoController() {
        this.productoDAO = new ProductoDAO();
    }
    
    // ✅ Constructor CON parámetros (NUEVO - solo para testing con mocks)
    // Los tests usan ESTE constructor para inyectar mocks
    public ProductoController(ProductoDAO productoDAO) {
        this.productoDAO = productoDAO;
    }
    
    @Override
    public void crear(Producto producto) throws Exception {
        validarDatos(producto);
        validarProducto(producto);
        
        // Si no se suministra código, lo generamos después de insertar (usamos ID generado)
        boolean codigoVacio = producto.getCodigo() == null || producto.getCodigo().trim().isEmpty();
        
        productoDAO.crear(producto);
        
        if (codigoVacio) {
            // Generar código basado en ID (P000001)
            String codigoGenerado = String.format("P%06d", producto.getId());
            producto.setCodigo(codigoGenerado);
            productoDAO.actualizar(producto);
        }
    }
    
    @Override
    public void actualizar(Producto producto) throws Exception {
        validarDatos(producto);
        validarProducto(producto);
        productoDAO.actualizar(producto);
    }
    
    @Override
    public void eliminar(int id) throws Exception {
        productoDAO.eliminar(id);
    }
    
    @Override
    public Producto obtenerPorId(int id) throws Exception {
        return productoDAO.obtenerPorId(id);
    }
    
    @Override
    public List<Producto> obtenerTodos() throws Exception {
        return productoDAO.obtenerTodos();
    }
    
    /**
     * Actualiza el stock de un producto
     * @param productoId ID del producto
     * @param cantidad Cantidad a sumar/restar (negativo para reducir)
     */
    public void actualizarStock(int productoId, int cantidad) throws Exception {
        Producto producto = obtenerPorId(productoId);
        if (producto == null) {
            throw new Exception("Producto no encontrado");
        }
        
        int nuevoStock = producto.getStock() + cantidad;
        if (nuevoStock < 0) {
            throw new Exception("Stock insuficiente");
        }
        
        producto.setStock(nuevoStock);
        actualizar(producto);
    }
    
    /**
     * Valida los datos del producto
     */
    private void validarProducto(Producto producto) throws Exception {
        if (producto.getNombre() == null || producto.getNombre().trim().isEmpty()) {
            throw new Exception("El nombre del producto es requerido");
        }
        if (producto.getCategoria() == null || producto.getCategoria().trim().isEmpty()) {
            throw new Exception("La categoría del producto es requerida");
        }
        if (producto.getPrecio() <= 0) {
            throw new Exception("El precio debe ser mayor a 0");
        }
        if (producto.getStock() < 0) {
            throw new Exception("El stock no puede ser negativo");
        }
    }
    
    /**
     * Busca productos por categoría
     */
    public List<Producto> buscarPorCategoria(String categoria) throws Exception {
        return productoDAO.obtenerTodos().stream()
                .filter(p -> p.getCategoria().equalsIgnoreCase(categoria))
                .toList();
    }
    
    /**
     * Verifica si hay stock disponible
     */
    public boolean verificarStockDisponible(int productoId, int cantidad) throws Exception {
        Producto producto = obtenerPorId(productoId);
        return producto != null && producto.getStock() >= cantidad;
    }
}