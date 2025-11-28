package com.libreria.controller;

import com.libreria.model.Venta;
import com.libreria.model.DetalleVenta;
import com.libreria.model.Producto;
import com.libreria.dao.VentaDAO;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.DisplayName;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

/**
 * Pruebas Unitarias para VentaController usando Mockito
 * Corresponde al informe: Informe de Testing Unitario - VentaController
 */
class VentaControllerTest {

    @Mock
    private VentaDAO ventaDAO;

    @Mock
    private ProductoController productoController;

    private VentaController ventaController;

    private Venta ventaValida;
    private Producto producto1;
    private Producto producto2;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        
        ventaController = new VentaController(ventaDAO, productoController);
        
        producto1 = new Producto();
        producto1.setId(1);
        producto1.setNombre("Cuaderno");
        producto1.setPrecio(15.50);
        producto1.setStock(200);
        
        producto2 = new Producto();
        producto2.setId(2);
        producto2.setNombre("Lapicero");
        producto2.setPrecio(2.50);
        producto2.setStock(200);
        
        ventaValida = new Venta();
        ventaValida.setMetodoPago("EFECTIVO");
        
        DetalleVenta detalle1 = new DetalleVenta(producto1, 5, producto1.getPrecio());
        DetalleVenta detalle2 = new DetalleVenta(producto2, 10, producto2.getPrecio());
        
        ventaValida.agregarDetalle(detalle1);
        ventaValida.agregarDetalle(detalle2);
    }

    // ==================== CASO V-01 ====================
    @Test
    @DisplayName("V-01: Crear venta con productos válidos")
    void testCrearVentaValida() throws Exception {
        // Arrange
        when(ventaDAO.generarNumeroVenta()).thenReturn("V20231126-0001");
        when(productoController.verificarStockDisponible(anyInt(), anyInt())).thenReturn(true);
        doNothing().when(ventaDAO).crear(any(Venta.class));
        doNothing().when(productoController).actualizarStock(anyInt(), anyInt());

        // Act
        ventaController.crear(ventaValida);

        // Assert
        assertEquals("V20231126-0001", ventaValida.getNumeroVenta());
        verify(ventaDAO, times(1)).generarNumeroVenta();
        verify(ventaDAO, times(1)).crear(ventaValida);
        verify(productoController, times(1)).actualizarStock(1, -5);
        verify(productoController, times(1)).actualizarStock(2, -10);
    }

    // ==================== CASO V-02 ====================
    @Test
    @DisplayName("V-02: Validar venta sin productos")
    void testValidarVentaSinProductos() {
        Venta ventaVacia = new Venta();
        ventaVacia.setMetodoPago("EFECTIVO");

        Exception exception = assertThrows(Exception.class, () -> {
            ventaController.crear(ventaVacia);
        });
        
        assertEquals("La venta debe tener al menos un producto", exception.getMessage());
    }

    // ==================== CASO V-03 ====================
    @Test
    @DisplayName("V-03: Validar método de pago requerido")
    void testValidarMetodoPagoRequerido() {
        Venta venta = new Venta();
        DetalleVenta detalle = new DetalleVenta(producto1, 2, producto1.getPrecio());
        venta.agregarDetalle(detalle);
        venta.setMetodoPago("");

        Exception exception = assertThrows(Exception.class, () -> {
            ventaController.crear(venta);
        });
        
        assertEquals("El método de pago es requerido", exception.getMessage());
    }

    // ==================== CASO V-04 ====================
    @Test
    @DisplayName("V-04: Validar stock insuficiente")
    void testValidarStockInsuficiente() throws Exception {
        when(productoController.verificarStockDisponible(1, 5)).thenReturn(false);

        Exception exception = assertThrows(Exception.class, () -> {
            ventaController.crear(ventaValida);
        });
        
        assertTrue(exception.getMessage().startsWith("Stock insuficiente para el producto:"));
        verify(productoController, times(1)).verificarStockDisponible(1, 5);
    }


    // ==================== CASO V-05 ====================
    @Test
    @DisplayName("V-06: Buscar venta existente")
    void testBuscarVentaExistente() throws Exception {
        ventaValida.setId(1);
        when(ventaDAO.obtenerPorId(1)).thenReturn(ventaValida);

        Venta resultado = ventaController.obtenerPorId(1);

        assertNotNull(resultado);
        assertEquals(1, resultado.getId());
        assertEquals(2, resultado.getDetalles().size());
        verify(ventaDAO, times(1)).obtenerPorId(1);
    }

    // ==================== CASO V-06 ====================
    @Test
    @DisplayName("V-07: Filtrar ventas por rango de fechas")
    void testFiltrarVentasPorFecha() throws Exception {
        LocalDateTime inicio = LocalDateTime.of(2023, 11, 1, 0, 0);
        LocalDateTime fin = LocalDateTime.of(2023, 11, 30, 23, 59);
        
        List<Venta> ventas = Arrays.asList(ventaValida, new Venta());
        when(ventaDAO.obtenerPorFecha(inicio, fin)).thenReturn(ventas);

        List<Venta> resultado = ventaController.obtenerVentasPorFecha(inicio, fin);

        assertEquals(2, resultado.size());
        verify(ventaDAO, times(1)).obtenerPorFecha(inicio, fin);
    }

    // ==================== CASO V-07 ====================
    @Test
    @DisplayName("V-08: Calcular total de ventas del día")
    void testCalcularTotalVentasDia() throws Exception {
        Venta venta1 = new Venta();
        venta1.setTotal(100.00);
        Venta venta2 = new Venta();
        venta2.setTotal(150.50);
        
        List<Venta> ventas = Arrays.asList(venta1, venta2);
        when(ventaDAO.obtenerPorFecha(any(LocalDateTime.class), any(LocalDateTime.class)))
            .thenReturn(ventas);

        double total = ventaController.calcularTotalVentasDia();

        assertEquals(250.50, total, 0.01);
    }

    // ==================== CASO V-08 ====================
    @Test
    @DisplayName("V-09: Intentar actualizar venta")
    void testIntentarActualizarVenta() {
        UnsupportedOperationException exception = assertThrows(
            UnsupportedOperationException.class, 
            () -> ventaController.actualizar(ventaValida)
        );
        
        assertEquals("No se permite actualizar ventas", exception.getMessage());
    }

    // ==================== CASO V-9 ====================
    @Test
    @DisplayName("V-10: Intentar eliminar venta")
    void testIntentarEliminarVenta() {
        UnsupportedOperationException exception = assertThrows(
            UnsupportedOperationException.class,
            () -> ventaController.eliminar(1)
        );
        
        assertEquals("No se permite eliminar ventas", exception.getMessage());
    }

    // ==================== CASO V-10 ====================
    @Test
    @DisplayName("V-11: Generar reporte Excel")
    void testGenerarReporteExcel() throws Exception {
        LocalDateTime inicio = LocalDateTime.of(2023, 11, 1, 0, 0);
        LocalDateTime fin = LocalDateTime.of(2023, 11, 30, 23, 59);
        List<Venta> ventas = Arrays.asList(ventaValida);
        
        when(ventaDAO.obtenerPorFecha(inicio, fin)).thenReturn(ventas);

        ventaController.generarReporteVentasExcel("test_reporte.xlsx", inicio, fin);

        verify(ventaDAO, times(1)).obtenerPorFecha(inicio, fin);
    }

    // ==================== CASO V-11 ====================
    @Test
    @DisplayName("V-12: Listar todas las ventas")
    void testListarTodasVentas() throws Exception {
        List<Venta> ventas = Arrays.asList(ventaValida, new Venta());
        when(ventaDAO.obtenerTodos()).thenReturn(ventas);

        List<Venta> resultado = ventaController.obtenerTodos();

        assertEquals(2, resultado.size());
        verify(ventaDAO, times(1)).obtenerTodos();
    }
}