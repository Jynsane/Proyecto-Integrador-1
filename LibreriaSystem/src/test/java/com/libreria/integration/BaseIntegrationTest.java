package com.libreria.integration;

import com.libreria.config.TestDatabaseConfig;
import com.libreria.util.DatabaseConnection;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;

import java.sql.Connection;
import java.sql.SQLException;


//Clase base para todas las pruebas de integración
// Configura y limpia la base de datos H2 automáticamente
 
public abstract class BaseIntegrationTest {
    
    protected static Connection testConnection;
    
    // Inicializa el esquema de la base de datos H2
     
    @BeforeAll
    public static void setUpDatabase() throws SQLException {
        System.out.println("\n========================================");
        System.out.println("🚀 Iniciando Base de Datos H2 para Tests");
        System.out.println("========================================");
        
        // Obtener conexión H2
        testConnection = TestDatabaseConfig.getTestConnection();
        
        // Configurar DatabaseConnection para usar H2 en modo test
        DatabaseConnection.setTestMode(true);
        DatabaseConnection.setTestConnection(testConnection);
        
        // Inicializar esquema
        TestDatabaseConfig.initializeSchema();
        
        System.out.println("✅ Configuración completada");
        System.out.println("📊 Modo Test: " + DatabaseConnection.isTestMode());
        System.out.println("========================================\n");
    }
    
 
  
     //Carga datos de prueba frescos
     
    @BeforeEach
    protected void setUp() throws SQLException {
        System.out.println("→ Preparando test...");
        TestDatabaseConfig.insertTestData();
    }
    
 
     //Se ejecuta DESPUÉS de CADA test individual
     //Limpia la base de datos
    
    @AfterEach
    protected void tearDown() throws SQLException {
        System.out.println("→ Limpiando después del test...\n");
        TestDatabaseConfig.cleanDatabase();
    }
    
   
     // Se ejecuta UNA SOLA VEZ después de TODOS los tests de la clase
     //Cierra la base de datos H2 completamente
    
    @AfterAll
    public static void tearDownDatabase() throws SQLException {
        System.out.println("\n========================================");
        System.out.println("🔒 Cerrando Base de Datos H2");
        System.out.println("========================================");
        
        // Restaurar modo normal (MySQL)
        DatabaseConnection.setTestMode(false);
        DatabaseConnection.clearTestConnection();
        
        // Cerrar conexión H2
        if (testConnection != null && !testConnection.isClosed()) {
            testConnection.close();
        }
        
        // Shutdown H2
        TestDatabaseConfig.shutdownDatabase();
        
        System.out.println("✅ Limpieza completada");
        System.out.println("📊 Modo Test: " + DatabaseConnection.isTestMode());
        System.out.println("========================================\n");
    }
    
    //Obtiene la conexión de prueba
     //util si necesitas ejecutar SQL personalizado en un test

    protected Connection getConnection() {
        return testConnection;
    }
    
     //Método de utilidad para debugging
     //Muestra el estado actual de la BD
    
    protected void printDatabaseStatus() throws SQLException {
        TestDatabaseConfig.printDatabaseStatus();
    }
}