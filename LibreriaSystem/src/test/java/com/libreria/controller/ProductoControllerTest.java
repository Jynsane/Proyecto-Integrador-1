package com.libreria.controller;

import com.libreria.model.Producto;
import com.libreria.dao.ProductoDAO;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.DisplayName;
//import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import java.util.Arrays;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;


//Pruebas Unitarias para ProductoController usando Mockito

class ProductoControllerTest {

    @Mock
    private ProductoDAO productoDAO;

    private ProductoController productoController;

    private Producto productoValido;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        
        // Usar el constructor con el mock inyectado
        productoController = new ProductoController(productoDAO);
        
        productoValido = new Producto();
        productoValido.setId(1);
        productoValido.setCodigo("P000001");
        productoValido.setNombre("Cuaderno A4");
        productoValido.setCategoria("Papelería");
        productoValido.setPrecio(15.50);
        productoValido.setStock(100);
        productoValido.setDescripcion("Cuaderno de 100 hojas");
    }

    // ==================== CASO P-01 ====================
    @Test
    @DisplayName("P-01: Crear producto con código automático")
    void testCrearProductoConCodigoAutomatico() throws Exception {
       
        Producto producto = new Producto();
        producto.setNombre("Cuaderno A4");
        producto.setCategoria("Papelería");
        producto.setPrecio(15.50);
        producto.setStock(100);
        producto.setCodigo(""); // Código vacío para generación automática
        
        doAnswer(invocation -> {
            Producto p = invocation.getArgument(0);
            p.setId(1); // Simular ID generado por BD
            return null;
        }).when(productoDAO).crear(any(Producto.class));
        
        doNothing().when(productoDAO).actualizar(any(Producto.class));
      
        productoController.crear(producto);

        assertNotNull(producto.getCodigo());
        assertEquals("P000001", producto.getCodigo());
        verify(productoDAO, times(1)).crear(any(Producto.class));
        verify(productoDAO, times(1)).actualizar(any(Producto.class));
    }

    // ==================== CASO P-02 ====================
    @Test
    @DisplayName("P-02: Validar nombre requerido")
    void testValidarNombreRequerido() {
        // Arrange
        Producto producto = new Producto();
        producto.setNombre(""); // Nombre vacío
        producto.setCategoria("Papelería");
        producto.setPrecio(15.50);
        producto.setStock(100);
        producto.setCodigo("P000001");

        Exception exception = assertThrows(Exception.class, () -> {
            productoController.crear(producto);
        });
        
        assertEquals("El nombre del producto es requerido", exception.getMessage());
    }

    // ==================== CASO P-03 ====================
    @Test
    @DisplayName("P-03: Validar categoría requerida")
    void testValidarCategoriaRequerida() {
        // Arrange
        Producto producto = new Producto();
        producto.setNombre("Cuaderno");
        producto.setCategoria(""); // Categoría vacía
        producto.setPrecio(15.50);
        producto.setStock(100);
        producto.setCodigo("P000001");

        Exception exception = assertThrows(Exception.class, () -> {
            productoController.crear(producto);
        });
        
        assertEquals("La categoría del producto es requerida", exception.getMessage());
    }

    // ==================== CASO P-04 ====================
    @Test
    @DisplayName("P-04: Validar precio mayor a cero")
    void testValidarPrecioMayorCero() {
        // Arrange
        Producto producto = new Producto();
        producto.setNombre("Cuaderno");
        producto.setCategoria("Papelería");
        producto.setPrecio(0); // Precio inválido
        producto.setStock(100);
        producto.setCodigo("P000001");

      
        Exception exception = assertThrows(Exception.class, () -> {
            productoController.crear(producto);
        });
        
        assertEquals("El precio debe ser mayor a 0", exception.getMessage());
    }

    // ==================== CASO P-05 ====================
    @Test
    @DisplayName("P-05: Validar stock no negativo")
    void testValidarStockNoNegativo() {
        // Arrange
        Producto producto = new Producto();
        producto.setNombre("Cuaderno");
        producto.setCategoria("Papelería");
        producto.setPrecio(15.50);
        producto.setStock(-5); // Stock negativo
        producto.setCodigo("P000001");

        Exception exception = assertThrows(Exception.class, () -> {
            productoController.crear(producto);
        });
        
        assertEquals("El stock no puede ser negativo", exception.getMessage());
    }
    
    // ==================== CASO P-06 ====================
    @Test
    @DisplayName("P-06: Actualizar producto existente")
    void testActualizarProductoExistente() throws Exception {
        // Arrange
        productoValido.setPrecio(20.00);
        doNothing().when(productoDAO).actualizar(any(Producto.class));

        // Act
        productoController.actualizar(productoValido);

        // Assert
        verify(productoDAO, times(1)).actualizar(productoValido);
        assertEquals(20.00, productoValido.getPrecio());
    }

    // ==================== CASO P-07 ====================
    @Test
    @DisplayName("P-07: Incrementar stock")
    void testIncrementarStock() throws Exception {
        // Arrange
        when(productoDAO.obtenerPorId(1)).thenReturn(productoValido);
        doNothing().when(productoDAO).actualizar(any(Producto.class));

        // Act
        productoController.actualizarStock(1, 50);

        // Assert
        verify(productoDAO, times(1)).obtenerPorId(1);
        verify(productoDAO, times(1)).actualizar(any(Producto.class));
        assertEquals(150, productoValido.getStock());
    }

    // ==================== CASO P-08 ====================
    @Test
    @DisplayName("P-08: Stock insuficiente al reducir")
    void testStockInsuficiente() throws Exception {
        // Arrange
        when(productoDAO.obtenerPorId(1)).thenReturn(productoValido);

        // Act & Assert
        Exception exception = assertThrows(Exception.class, () -> {
            productoController.actualizarStock(1, -200);
        });
        
        assertEquals("Stock insuficiente", exception.getMessage());
        verify(productoDAO, times(1)).obtenerPorId(1);
    }

    // ==================== CASO P-09 ====================
    @Test
    @DisplayName("P-09: Buscar producto existente")
    void testBuscarProductoExistente() throws Exception {
        // Arrange
        when(productoDAO.obtenerPorId(1)).thenReturn(productoValido);

        // Act
        Producto resultado = productoController.obtenerPorId(1);

        // Assert
        assertNotNull(resultado);
        assertEquals("Cuaderno A4", resultado.getNombre());
        verify(productoDAO, times(1)).obtenerPorId(1);
    }

    // ==================== CASO P-10 ====================
    @Test
    @DisplayName("P-10: Buscar producto inexistente")
    void testBuscarProductoInexistente() throws Exception {
        // Arrange
        when(productoDAO.obtenerPorId(999)).thenReturn(null);

        // Act
        Producto resultado = productoController.obtenerPorId(999);

        // Assert
        assertNull(resultado);
        verify(productoDAO, times(1)).obtenerPorId(999);
    }

    // ==================== CASO P-11 ====================
    @Test
    @DisplayName("P-11: Filtrar por categoría")
    void testBuscarPorCategoria() throws Exception {
        // Arrange
        Producto producto2 = new Producto();
        producto2.setNombre("Lapicero");
        producto2.setCategoria("Papelería");
        
        List<Producto> todosProductos = Arrays.asList(productoValido, producto2);
        when(productoDAO.obtenerTodos()).thenReturn(todosProductos);

        // Act
        List<Producto> resultado = productoController.buscarPorCategoria("Papelería");

        // Assert
        assertEquals(2, resultado.size());
        assertTrue(resultado.stream().allMatch(p -> p.getCategoria().equals("Papelería")));
    }

    // ==================== CASO P-12 ====================
    @Test
    @DisplayName("P-12: Verificar stock suficiente")
    void testVerificarStockSuficiente() throws Exception {
        // Arrange
        when(productoDAO.obtenerPorId(1)).thenReturn(productoValido);

        // Act
        boolean resultado = productoController.verificarStockDisponible(1, 50);

        // Assert
        assertTrue(resultado);
        verify(productoDAO, times(1)).obtenerPorId(1);
    }

    // ==================== CASO P-13 ====================
    @Test
    @DisplayName("P-13: Verificar stock insuficiente")
    void testVerificarStockInsuficiente() throws Exception {
        // Arrange
        when(productoDAO.obtenerPorId(1)).thenReturn(productoValido);

        // Act
        boolean resultado = productoController.verificarStockDisponible(1, 500);

        // Assert
        assertFalse(resultado);
    }

    // ==================== CASO P-14 ====================
    @Test
    @DisplayName("P-14: Eliminar producto")
    void testEliminarProducto() throws Exception {
        // Arrange
        doNothing().when(productoDAO).eliminar(1);

        // Act
        productoController.eliminar(1);

        // Assert
        verify(productoDAO, times(1)).eliminar(1);
    }

    // ==================== CASO P-15 ====================
    @Test
    @DisplayName("P-15: Listar todos los productos")
    void testListarTodosProductos() throws Exception {
        // Arrange
        List<Producto> productos = Arrays.asList(productoValido, new Producto());
        when(productoDAO.obtenerTodos()).thenReturn(productos);

        // Act
        List<Producto> resultado = productoController.obtenerTodos();

        // Assert
        assertEquals(2, resultado.size());
        verify(productoDAO, times(1)).obtenerTodos();
    }
}