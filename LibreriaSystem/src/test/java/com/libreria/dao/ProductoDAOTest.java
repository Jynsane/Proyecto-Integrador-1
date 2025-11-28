package com.libreria.dao;

import com.libreria.integration.BaseIntegrationTest;
import com.libreria.model.Producto;
import org.junit.jupiter.api.*;

import java.sql.SQLException;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
class ProductoDAOTest extends BaseIntegrationTest {

    private ProductoDAO productoDAO;

@BeforeEach
@Override  
protected void setUp() throws SQLException { 
    super.setUp();
    productoDAO = new ProductoDAO(); 
}

    // ==================== TESTS DE CREACIÓN ====================
    
    @Test
    @Order(1)
    @DisplayName("DAO-P-01: Crear producto válido")
    void testCrearProductoValido() throws Exception {
        // Arrange
        Producto producto = new Producto();
        producto.setCodigo("PDAO001");
        producto.setNombre("Producto DAO Test");
        producto.setCategoria("Test");
        producto.setPrecio(50.00);
        producto.setStock(20);
        producto.setDescripcion("Producto para test DAO");

        // Act
        productoDAO.crear(producto);

        // Assert
        assertNotNull(producto.getId(), "El ID debe ser generado automáticamente");
        assertTrue(producto.getId() > 0, "El ID debe ser mayor a 0");
        
        System.out.println("✓ Producto creado con ID: " + producto.getId());
    }

    @Test
    @Order(2)
    @DisplayName("DAO-P-02: Crear producto con código duplicado debe fallar")
    void testCrearProductoCodigoDuplicado() throws Exception {
        // Arrange - Crear primer producto
        Producto producto1 = new Producto();
        producto1.setCodigo("PDUP001");
        producto1.setNombre("Producto 1");
        producto1.setCategoria("Test");
        producto1.setPrecio(10.00);
        producto1.setStock(10);
        
        productoDAO.crear(producto1);

        // Act & Assert - Intentar crear otro con el mismo código
        Producto producto2 = new Producto();
        producto2.setCodigo("PDUP001"); // Mismo código
        producto2.setNombre("Producto 2");
        producto2.setCategoria("Test");
        producto2.setPrecio(20.00);
        producto2.setStock(20);

        assertThrows(Exception.class, () -> {
            productoDAO.crear(producto2);
        }, "Debe lanzar excepción por código duplicado");
        
        System.out.println("✓ Validación de código único funcionando");
    }

    // ==================== TESTS DE LECTURA ====================
    
    @Test
    @Order(3)
    @DisplayName("DAO-P-03: Obtener producto por ID existente")
    void testObtenerProductoPorIdExistente() throws Exception {
        // Act - Obtener producto de test data (ID 1)
        Producto producto = productoDAO.obtenerPorId(1);

        // Assert
        assertNotNull(producto);
        assertEquals(1, producto.getId());
        assertEquals("P000001", producto.getCodigo());
        assertEquals("Cuaderno A4 Universitario", producto.getNombre());
        assertEquals("Papelería", producto.getCategoria());
        assertEquals(15.50, producto.getPrecio(), 0.01);
        assertEquals(100, producto.getStock());
        
        System.out.println("✓ Producto recuperado: " + producto.getNombre());
    }

    @Test
    @Order(4)
    @DisplayName("DAO-P-04: Obtener producto por ID inexistente")
    void testObtenerProductoPorIdInexistente() throws Exception {
        // Act
        Producto producto = productoDAO.obtenerPorId(9999);

        // Assert
        assertNull(producto, "Debe retornar null para ID inexistente");
        
        System.out.println("✓ Manejo correcto de ID inexistente");
    }

@Test
@Order(5)
@DisplayName("DAO-P-05: Obtener todos los productos")
void testObtenerTodosLosProductos() throws Exception {
    List<Producto> productos = productoDAO.obtenerTodos();
    assertNotNull(productos);
    assertTrue(productos.size() >= 5, "Debe haber al menos 5 productos de test data"); 
    
    // Verificar que todos tienen datos completos
    for (Producto p : productos) {
        assertNotNull(p.getCodigo());
        assertNotNull(p.getNombre());
        assertNotNull(p.getCategoria());
        assertTrue(p.getPrecio() > 0);
        assertTrue(p.getStock() >= 0);
    }
    
    System.out.println("✓ " + productos.size() + " productos recuperados");
}
    // ==================== TESTS DE ACTUALIZACIÓN ====================
    
    @Test
    @Order(6)
    @DisplayName("DAO-P-06: Actualizar producto existente")
    void testActualizarProductoExistente() throws Exception {
        // Arrange
        Producto producto = productoDAO.obtenerPorId(2);
        assertNotNull(producto);
        
        String nombreOriginal = producto.getNombre();
        double precioOriginal = producto.getPrecio();
        
        // Modificar datos
        producto.setNombre("Lapicero Actualizado");
        producto.setPrecio(5.00);
        producto.setStock(250);

        // Act
        productoDAO.actualizar(producto);

        // Assert - Recuperar y verificar cambios
        Producto productoActualizado = productoDAO.obtenerPorId(2);
        assertEquals("Lapicero Actualizado", productoActualizado.getNombre());
        assertEquals(5.00, productoActualizado.getPrecio(), 0.01);
        assertEquals(250, productoActualizado.getStock());
        
        System.out.println("✓ Producto actualizado exitosamente");
        System.out.println("  Nombre: " + nombreOriginal + " → " + productoActualizado.getNombre());
        System.out.println("  Precio: S/." + precioOriginal + " → S/." + productoActualizado.getPrecio());
    }

    @Test
    @Order(7)
    @DisplayName("DAO-P-07: Actualizar stock de producto")
    void testActualizarStock() throws Exception {
        // Arrange
        Producto producto = productoDAO.obtenerPorId(3);
        int stockOriginal = producto.getStock();
        
        producto.setStock(stockOriginal - 10);

        // Act
        productoDAO.actualizar(producto);

        // Assert
        Producto productoActualizado = productoDAO.obtenerPorId(3);
        assertEquals(stockOriginal - 10, productoActualizado.getStock());
        
        System.out.println("✓ Stock actualizado: " + stockOriginal + " → " + productoActualizado.getStock());
    }

    // ==================== TESTS DE ELIMINACIÓN ====================
    
    @Test
    @Order(8)
    @DisplayName("DAO-P-08: Eliminar producto existente")
    void testEliminarProductoExistente() throws Exception {
        // Arrange - Crear producto temporal para eliminar
        Producto producto = new Producto();
        producto.setCodigo("PTEMP001");
        producto.setNombre("Producto Temporal");
        producto.setCategoria("Test");
        producto.setPrecio(10.00);
        producto.setStock(10);
        
        productoDAO.crear(producto);
        int productoId = producto.getId();
        
        // Verificar que existe
        assertNotNull(productoDAO.obtenerPorId(productoId));

        // Act
        productoDAO.eliminar(productoId);

        // Assert
        Producto productoEliminado = productoDAO.obtenerPorId(productoId);
        assertNull(productoEliminado, "El producto debe ser null después de eliminar");
        
        System.out.println("✓ Producto eliminado correctamente");
    }

    @Test
    @Order(9)
    @DisplayName("DAO-P-09: Eliminar producto inexistente no debe lanzar error")
    void testEliminarProductoInexistente() {
        // Act & Assert - No debe lanzar excepción
        assertDoesNotThrow(() -> {
            productoDAO.eliminar(9999);
        });
        
        System.out.println("✓ Eliminación de ID inexistente manejada correctamente");
    }

    // ==================== TESTS DE CASOS ESPECIALES ====================
   
@Test
@Order(10)
@DisplayName("DAO-P-10: Crear múltiples productos en secuencia")
void testCrearMultiplesProductos() throws Exception {
    // Contar productos antes
    int productosAntes = productoDAO.obtenerTodos().size();
    
    // Arrange & Act
    for (int i = 1; i <= 3; i++) {
        Producto producto = new Producto();
        producto.setCodigo("PMULT00" + i);
        producto.setNombre("Producto Múltiple " + i);
        producto.setCategoria("Test");
        producto.setPrecio(10.00 * i);
        producto.setStock(10 * i);
        
        productoDAO.crear(producto);
        assertNotNull(producto.getId());
    }

    // Assert
    List<Producto> productos = productoDAO.obtenerTodos();
    assertEquals(productosAntes + 3, productos.size(), 
                 "Deben haberse agregado exactamente 3 productos"); 
    
    System.out.println("✓ Múltiples productos creados correctamente");
}

    @Test
    @Order(11)
    @DisplayName("DAO-P-11: Verificar mapeo correcto de todos los campos")
    void testMapeoCompletoProducto() throws Exception {
        // Arrange
        Producto producto = new Producto();
        producto.setCodigo("PMAP001");
        producto.setNombre("Producto Mapeo Completo");
        producto.setCategoria("Categoría Test");
        producto.setPrecio(123.45);
        producto.setStock(999);
        producto.setDescripcion("Descripción detallada para verificar mapeo");

        // Act
        productoDAO.crear(producto);
        Producto productoRecuperado = productoDAO.obtenerPorId(producto.getId());

        // Assert - Verificar que todos los campos se mapearon correctamente
        assertNotNull(productoRecuperado);
        assertEquals(producto.getCodigo(), productoRecuperado.getCodigo());
        assertEquals(producto.getNombre(), productoRecuperado.getNombre());
        assertEquals(producto.getCategoria(), productoRecuperado.getCategoria());
        assertEquals(producto.getPrecio(), productoRecuperado.getPrecio(), 0.01);
        assertEquals(producto.getStock(), productoRecuperado.getStock());
        assertEquals(producto.getDescripcion(), productoRecuperado.getDescripcion());
        
        System.out.println("✓ Todos los campos mapeados correctamente");
    }
}