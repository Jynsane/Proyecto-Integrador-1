package com.libreria.config;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.sql.Statement;


//Configuración de Base de Datos H2 para Testing
//Esta clase gestiona la creación, población y limpieza de la BD de pruebas

public class TestDatabaseConfig {
    
    // Conexión H2 en memoria
    private static final String H2_URL = "jdbc:h2:mem:testdb;MODE=MySQL;DB_CLOSE_DELAY=-1;DATABASE_TO_UPPER=false";
    private static final String H2_USER = "sa";
    private static final String H2_PASSWORD = "";
    
    private static Connection connection;
    
  
  // Obtiene la conexión H2 para tests
  //Si no existe, la crea
    
    public static Connection getTestConnection() throws SQLException {
        if (connection == null || connection.isClosed()) {
            connection = DriverManager.getConnection(H2_URL, H2_USER, H2_PASSWORD);
            System.out.println("✅ Conexión H2 establecida: " + H2_URL);
        }
        return connection;
    }
    

     //Inicializa el esquema de la base de datos H2
     // Crea todas las tablas necesarias
     
    public static void initializeSchema() throws SQLException {
        System.out.println("📋 Creando esquema de base de datos...");
        
        Connection conn = getTestConnection();
        try (Statement stmt = conn.createStatement()) {
            
            // Desactivar foreign keys temporalmente
            stmt.execute("SET FOREIGN_KEY_CHECKS = 0");
            
            // Eliminar tablas si existen (para reiniciar limpio)
            stmt.execute("DROP TABLE IF EXISTS detalles_venta");
            stmt.execute("DROP TABLE IF EXISTS ventas");
            stmt.execute("DROP TABLE IF EXISTS productos");
            stmt.execute("DROP TABLE IF EXISTS usuarios");
            
            // Crear tabla de Productos
            stmt.execute("""
                CREATE TABLE productos (
                    id INT AUTO_INCREMENT PRIMARY KEY,
                    codigo VARCHAR(50) UNIQUE NOT NULL,
                    nombre VARCHAR(200) NOT NULL,
                    categoria VARCHAR(100) NOT NULL,
                    precio DECIMAL(10,2) NOT NULL,
                    stock INT DEFAULT 0,
                    descripcion TEXT,
                    fecha_registro TIMESTAMP DEFAULT CURRENT_TIMESTAMP
                )
            """);
            
            // Crear tabla de Usuarios
            stmt.execute("""
                CREATE TABLE usuarios (
                    id INT AUTO_INCREMENT PRIMARY KEY,
                    username VARCHAR(50) UNIQUE NOT NULL,
                    password VARCHAR(255) NOT NULL,
                    nombre VARCHAR(100) NOT NULL,
                    rol VARCHAR(20) NOT NULL,
                    activo BOOLEAN DEFAULT TRUE,
                    fecha_registro TIMESTAMP DEFAULT CURRENT_TIMESTAMP
                )
            """);
            
            // Crear tabla de Ventas
            stmt.execute("""
                CREATE TABLE ventas (
                    id INT AUTO_INCREMENT PRIMARY KEY,
                    numero_venta VARCHAR(50) UNIQUE NOT NULL,
                    fecha TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
                    total DECIMAL(10,2) NOT NULL,
                    metodo_pago VARCHAR(20) NOT NULL
                )
            """);
            
            // Crear tabla de Detalles de Venta
            stmt.execute("""
                CREATE TABLE detalles_venta (
                    id INT AUTO_INCREMENT PRIMARY KEY,
                    venta_id INT NOT NULL,
                    producto_id INT NOT NULL,
                    cantidad INT NOT NULL,
                    precio_unitario DECIMAL(10,2) NOT NULL,
                    FOREIGN KEY (venta_id) REFERENCES ventas(id) ON DELETE CASCADE,
                    FOREIGN KEY (producto_id) REFERENCES productos(id)
                )
            """);
            
            // Crear índices
            stmt.execute("CREATE INDEX idx_productos_codigo ON productos(codigo)");
            stmt.execute("CREATE INDEX idx_productos_categoria ON productos(categoria)");
            stmt.execute("CREATE INDEX idx_ventas_fecha ON ventas(fecha)");
            stmt.execute("CREATE INDEX idx_ventas_numero ON ventas(numero_venta)");
            stmt.execute("CREATE INDEX idx_detalles_venta_id ON detalles_venta(venta_id)");
            stmt.execute("CREATE INDEX idx_detalles_producto_id ON detalles_venta(producto_id)");
            
            // Reactivar foreign keys
            stmt.execute("SET FOREIGN_KEY_CHECKS = 1");
            
            System.out.println("✅ Esquema creado exitosamente");
        }
    }
    
    //Inserta datos de prueba en la base de datos
      //Se ejecuta antes de cada test

    public static void insertTestData() throws SQLException {
        System.out.println("📝 Insertando datos de prueba...");
        
        Connection conn = getTestConnection();
        try (Statement stmt = conn.createStatement()) {
            
            // Insertar productos de prueba
            stmt.execute("""
                INSERT INTO productos (codigo, nombre, categoria, precio, stock, descripcion) VALUES
                ('P000001', 'Cuaderno A4 Universitario', 'Papelería', 15.50, 100, 'Cuaderno de 100 hojas'),
                ('P000002', 'Lapicero Azul Faber-Castell', 'Papelería', 2.50, 200, 'Lapicero tinta gel'),
                ('P000003', 'Resma Papel A4', 'Papelería', 25.00, 50, 'Resma 500 hojas'),
                ('P000004', 'Corrector Líquido', 'Papelería', 3.50, 150, 'Corrector 20ml'),
                ('P000005', 'Calculadora Científica', 'Electrónica', 45.00, 30, 'Calculadora Casio FX-82')
            """);
            
            // Insertar usuarios de prueba
            stmt.execute("""
                INSERT INTO usuarios (username, password, nombre, rol, activo) VALUES
                ('admin', 'admin123', 'Administrador Test', 'ADMINISTRADOR', true),
                ('vendedor', 'vendedor123', 'Vendedor Test', 'VENDEDOR', true)
            """);
            
            System.out.println("✅ Datos de prueba insertados: 5 productos, 2 usuarios");
        }
    }
    
    
    //Limpia todos los datos de las tablas
    //Se ejecuta después de cada test
   
    public static void cleanDatabase() throws SQLException {
        Connection conn = getTestConnection();
        try (Statement stmt = conn.createStatement()) {
            
            // Desactivar foreign keys temporalmente
            stmt.execute("SET FOREIGN_KEY_CHECKS = 0");
            
            // Limpiar tablas en orden correcto (respetando foreign keys)
            stmt.execute("DELETE FROM detalles_venta");
            stmt.execute("DELETE FROM ventas");
            stmt.execute("DELETE FROM productos");
            stmt.execute("DELETE FROM usuarios");
            
            // Reiniciar auto-increment
            stmt.execute("ALTER TABLE detalles_venta ALTER COLUMN id RESTART WITH 1");
            stmt.execute("ALTER TABLE ventas ALTER COLUMN id RESTART WITH 1");
            stmt.execute("ALTER TABLE productos ALTER COLUMN id RESTART WITH 1");
            stmt.execute("ALTER TABLE usuarios ALTER COLUMN id RESTART WITH 1");
            
            // Reactivar foreign keys
            stmt.execute("SET FOREIGN_KEY_CHECKS = 1");
            
            System.out.println("🧹 Base de datos limpiada");
        }
    }
    
    //
    public static void shutdownDatabase() throws SQLException {
        if (connection != null && !connection.isClosed()) {
            try (Statement stmt = connection.createStatement()) {
                stmt.execute("SHUTDOWN");
            }
            connection.close();
            connection = null;
            System.out.println("🔒 Base de datos H2 cerrada");
        }
    }
    
    
     //Método de utilidad para ejecutar cualquier SQL
     // Útil para preparar datos específicos en tests individuales
   
    public static void executeSQL(String sql) throws SQLException {
        Connection conn = getTestConnection();
        try (Statement stmt = conn.createStatement()) {
            stmt.execute(sql);
        }
    }
    
    
     //Método de utilidad para verificar el estado de la BD
    // Útil para debugging
     
    public static void printDatabaseStatus() throws SQLException {
        Connection conn = getTestConnection();
        try (Statement stmt = conn.createStatement()) {
            
            var rs = stmt.executeQuery("SELECT COUNT(*) FROM productos");
            if (rs.next()) {
                System.out.println("📊 Productos en BD: " + rs.getInt(1));
            }
            
            rs = stmt.executeQuery("SELECT COUNT(*) FROM ventas");
            if (rs.next()) {
                System.out.println("📊 Ventas en BD: " + rs.getInt(1));
            }
            
            rs = stmt.executeQuery("SELECT COUNT(*) FROM usuarios");
            if (rs.next()) {
                System.out.println("📊 Usuarios en BD: " + rs.getInt(1));
            }
        }
    }
}