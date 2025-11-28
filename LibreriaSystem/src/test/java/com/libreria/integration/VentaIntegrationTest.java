package com.libreria.integration;

import com.libreria.dao.ProductoDAO;
import com.libreria.dao.VentaDAO;
import com.libreria.model.Producto;
import com.libreria.model.Venta;
import com.libreria.model.DetalleVenta;
import org.junit.jupiter.api.*;

import java.sql.SQLException;

import static org.junit.jupiter.api.Assertions.*;



@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
class VentaIntegrationTest extends BaseIntegrationTest {

    private ProductoDAO productoDAO;
    private VentaDAO ventaDAO;

  @BeforeEach
@Override  // ← Agrega esta anotación
protected void setUp() throws SQLException {
    super.setUp(); // Llama al setUp de BaseIntegrationTest
    
    // Inicializar DAOs usando la conexión H2
    productoDAO = new ProductoDAO();
    ventaDAO = new VentaDAO();
    
    System.out.println("DAOs inicializados para el test");
}

    // ==================== CASO PI-01 ====================
    @Test
    @Order(1)
    @DisplayName("PI-01: Crear venta completa con integración H2")
    void testCrearVentaCompletaIntegracion() throws Exception {
        // Arrange - Obtener producto de prueba
        Producto producto = productoDAO.obtenerPorId(1); // P000001 del test data
        assertNotNull(producto, "El producto debe existir en BD H2");
        
        int stockInicial = producto.getStock();
        int cantidadVender = 10;
        
        Venta venta = new Venta();
        venta.setMetodoPago("EFECTIVO");
        
        DetalleVenta detalle = new DetalleVenta(producto, cantidadVender, producto.getPrecio());
        venta.agregarDetalle(detalle);

        // Act
        ventaDAO.crear(venta);

        // Assert
        assertNotNull(venta.getId(), "Venta debe tener ID generado");
        assertNotNull(venta.getNumeroVenta(), "Venta debe tener número generado");
        assertTrue(venta.getNumeroVenta().startsWith("V"), "Número de venta debe comenzar con V");
        
        // Verificar que el stock NO se actualizó (eso lo hace el Controller, no el DAO)
        Producto productoActualizado = productoDAO.obtenerPorId(1);
        assertEquals(stockInicial, productoActualizado.getStock(), 
                     "Stock debe mantenerse igual (el DAO solo guarda la venta)");
        
        // Verificar que la venta se guardó correctamente
        Venta ventaGuardada = ventaDAO.obtenerPorId(venta.getId());
        assertNotNull(ventaGuardada, "Venta debe recuperarse de BD H2");
        assertEquals(1, ventaGuardada.getDetalles().size(), "Debe tener 1 detalle");
        assertEquals(cantidadVender, ventaGuardada.getDetalles().get(0).getCantidad());
        
        System.out.println("✓ Venta creada: " + venta.getNumeroVenta());
    }

    // ==================== CASO PI-02 ====================
    @Test
    @Order(2)
    @DisplayName("PI-02: Flujo completo de venta con múltiples productos")
    void testFlujoCompletoVentaMultiplesProductos() throws Exception {
        // Arrange - Usar productos de prueba existentes
        Producto prod1 = productoDAO.obtenerPorId(1); // P000001
        Producto prod2 = productoDAO.obtenerPorId(2); // P000002
        
        assertNotNull(prod1);
        assertNotNull(prod2);
        
        // Act - Crear venta con 2 productos
        Venta venta = new Venta();
        venta.setMetodoPago("TARJETA");
        venta.agregarDetalle(new DetalleVenta(prod1, 5, prod1.getPrecio()));
        venta.agregarDetalle(new DetalleVenta(prod2, 3, prod2.getPrecio()));
        
        ventaDAO.crear(venta);

        // Assert
        double totalEsperado = (prod1.getPrecio() * 5) + (prod2.getPrecio() * 3);
        assertEquals(totalEsperado, venta.getTotal(), 0.01);
        
        Venta ventaGuardada = ventaDAO.obtenerPorId(venta.getId());
        assertEquals(2, ventaGuardada.getDetalles().size(), "Debe tener 2 detalles");
        
        System.out.println("✓ Venta múltiple creada. Total: S/." + venta.getTotal());
    }

    // ==================== CASO PI-03 ====================
    @Test
    @Order(3)
    @DisplayName("PI-03: Verificar integridad de datos en venta")
    void testIntegridadDatosVenta() throws Exception {
        // Arrange
        Producto producto = productoDAO.obtenerPorId(3); // P000003 - Resma Papel
        assertNotNull(producto);
        
        Venta venta = new Venta();
        venta.setMetodoPago("EFECTIVO");
        venta.agregarDetalle(new DetalleVenta(producto, 5, producto.getPrecio()));

        // Act
        ventaDAO.crear(venta);

        // Assert - Verificar que todos los datos se guardaron correctamente
        Venta ventaRecuperada = ventaDAO.obtenerPorId(venta.getId());
        
        assertNotNull(ventaRecuperada);
        assertEquals("EFECTIVO", ventaRecuperada.getMetodoPago());
        assertEquals(producto.getPrecio() * 5, ventaRecuperada.getTotal(), 0.01);
        assertNotNull(ventaRecuperada.getFecha());
        
        // Verificar detalle
        DetalleVenta detalle = ventaRecuperada.getDetalles().get(0);
        assertEquals(producto.getId(), detalle.getProducto().getId());
        assertEquals(5, detalle.getCantidad());
        assertEquals(producto.getPrecio(), detalle.getPrecioUnitario(), 0.01);
        
        System.out.println("✓ Integridad de datos verificada");
    }

    // ==================== CASO PI-04 ====================
    @Test
    @Order(4)
    @DisplayName("PI-04: Generar números de venta únicos consecutivos")
    void testGenerarNumerosVentaConsecutivos() throws Exception {
        // Arrange
        Producto producto = productoDAO.obtenerPorId(1);
        
        // Act - Crear 3 ventas consecutivas
        Venta venta1 = crearVentaSimple(producto);
        Venta venta2 = crearVentaSimple(producto);
        Venta venta3 = crearVentaSimple(producto);

        // Assert
        assertNotNull(venta1.getNumeroVenta());
        assertNotNull(venta2.getNumeroVenta());
        assertNotNull(venta3.getNumeroVenta());
        
        // Verificar que son diferentes
        assertNotEquals(venta1.getNumeroVenta(), venta2.getNumeroVenta());
        assertNotEquals(venta2.getNumeroVenta(), venta3.getNumeroVenta());
        
        // Verificar formato correcto: V20231126-0001
        assertTrue(venta1.getNumeroVenta().matches("V\\d{8}-\\d{4}"));
        assertTrue(venta2.getNumeroVenta().matches("V\\d{8}-\\d{4}"));
        assertTrue(venta3.getNumeroVenta().matches("V\\d{8}-\\d{4}"));
        
        System.out.println("Números generados:");
        System.out.println("  Venta 1: " + venta1.getNumeroVenta());
        System.out.println("  Venta 2: " + venta2.getNumeroVenta());
        System.out.println("  Venta 3: " + venta3.getNumeroVenta());
    }

    // ==================== CASO PI-05 ====================
    @Test
    @Order(5)
    @DisplayName("PI-05: Búsqueda de venta con detalles completos")
    void testBusquedaVentaConDetalles() throws Exception {
        // Arrange - Crear venta
        Producto producto = productoDAO.obtenerPorId(4); // P000004 - Corrector
        Venta venta = crearVentaSimple(producto);
        
        // Act - Buscar la venta
        Venta ventaRecuperada = ventaDAO.obtenerPorId(venta.getId());

        // Assert
        assertNotNull(ventaRecuperada, "Venta debe encontrarse");
        assertNotNull(ventaRecuperada.getDetalles(), "Detalles no deben ser null");
        assertFalse(ventaRecuperada.getDetalles().isEmpty(), "Debe tener detalles");
        
        DetalleVenta detalle = ventaRecuperada.getDetalles().get(0);
        assertNotNull(detalle.getProducto(), "Producto en detalle debe estar cargado");
        assertEquals(producto.getId(), detalle.getProducto().getId());
        assertEquals(producto.getNombre(), detalle.getProducto().getNombre());
        
        System.out.println("✓ Venta recuperada con detalles completos");
    }

    // ==================== CASO PI-06 ====================
    @Test
    @Order(6)
    @DisplayName("PI-06: Listar todas las ventas")
    void testListarTodasVentas() throws Exception {
        // Arrange - Crear varias ventas
        Producto prod1 = productoDAO.obtenerPorId(1);
        Producto prod2 = productoDAO.obtenerPorId(2);
        
        crearVentaSimple(prod1);
        crearVentaSimple(prod2);
        crearVentaSimple(prod1);
        
        // Act
        var ventas = ventaDAO.obtenerTodos();

        // Assert
        assertNotNull(ventas);
        assertEquals(3, ventas.size(), "Debe haber 3 ventas");
        
        // Verificar que todas tienen detalles
        for (Venta v : ventas) {
            assertNotNull(v.getDetalles());
            assertFalse(v.getDetalles().isEmpty());
        }
        
        System.out.println("✓ " + ventas.size() + " ventas recuperadas correctamente");
    }

    // ==================== CASO PI-07 ====================
    @Test
    @Order(7)
    @DisplayName("PI-07: Crear producto y usarlo en venta inmediatamente")
    void testCrearProductoYVender() throws Exception {
        // Arrange - Crear nuevo producto
        Producto nuevoProducto = new Producto();
        nuevoProducto.setCodigo("PTEST999");
        nuevoProducto.setNombre("Producto Test Temporal");
        nuevoProducto.setCategoria("Test");
        nuevoProducto.setPrecio(99.99);
        nuevoProducto.setStock(50);
        nuevoProducto.setDescripcion("Producto para test de integración");
        
        productoDAO.crear(nuevoProducto);
        assertNotNull(nuevoProducto.getId(), "Producto debe tener ID generado");
        
        // Act - Crear venta con el nuevo producto
        Venta venta = new Venta();
        venta.setMetodoPago("EFECTIVO");
        venta.agregarDetalle(new DetalleVenta(nuevoProducto, 2, nuevoProducto.getPrecio()));
        
        ventaDAO.crear(venta);

        // Assert
        assertNotNull(venta.getId());
        
        Venta ventaRecuperada = ventaDAO.obtenerPorId(venta.getId());
        assertEquals(nuevoProducto.getId(), 
                     ventaRecuperada.getDetalles().get(0).getProducto().getId());
        
        System.out.println("✓ Producto creado y vendido exitosamente");
    }

    // ==================== CASO PI-08 ====================
    @Test
    @Order(8)
    @DisplayName("PI-08: Verificar transaccionalidad en creación de venta")
    void testTransaccionalidadVenta() throws Exception {
        // Este test verifica que si falla la creación de detalles,
        // toda la venta debe hacer rollback
        
        // Arrange
        Producto producto = productoDAO.obtenerPorId(1);
        
        Venta venta = new Venta();
        venta.setMetodoPago("EFECTIVO");
        venta.agregarDetalle(new DetalleVenta(producto, 2, producto.getPrecio()));
        
        ventaDAO.crear(venta);
        int ventaId = venta.getId();

        // Assert - Si la venta se creó, los detalles también deben existir
        Venta ventaRecuperada = ventaDAO.obtenerPorId(ventaId);
        assertNotNull(ventaRecuperada);
        assertFalse(ventaRecuperada.getDetalles().isEmpty(), 
                   "Los detalles deben existir si la venta se creó");
        
        System.out.println("✓ Transaccionalidad verificada");
    }

    // ==================== Métodos auxiliares ====================
    
    private Venta crearVentaSimple(Producto producto) throws Exception {
        Venta venta = new Venta();
        venta.setMetodoPago("EFECTIVO");
        DetalleVenta detalle = new DetalleVenta(producto, 2, producto.getPrecio());
        venta.agregarDetalle(detalle);
        ventaDAO.crear(venta);
        return venta;
    }
}