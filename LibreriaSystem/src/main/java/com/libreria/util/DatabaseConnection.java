package com.libreria.util;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;


public class DatabaseConnection {
    
    // ========== CONFIGURACIÓN MYSQL ==========
    private static final String MYSQL_URL = "jdbc:mysql://localhost:3306/libreria_db";
    private static final String MYSQL_USER = "root";
    private static final String MYSQL_PASSWORD = "Callupe07.";
    
    // ========== MODO DE PRUEBA (SOLO PARA TESTS) ==========
    private static boolean isTestMode = false;
    private static Connection testConnection = null;
    
    /**
     * Obtiene una conexión a la base de datos
     * 
     * FUNCIONAMIENTO:
     * - Modo PRODUCCIÓN (normal): Crea nueva conexión MySQL cada vez (TU CÓDIGO ORIGINAL)
     * - Modo TEST: Usa conexión H2 compartida (solo durante tests)
     * 
     * @return Connection - Nueva conexión MySQL o conexión H2 de prueba
     * @throws SQLException si no puede conectarse
     */
    public static Connection getConnection() throws SQLException {
        // Si está en modo test, usar conexión H2
        if (isTestMode && testConnection != null) {
            return testConnection;
        }
        
        // Modo PRODUCCIÓN
        try {
            Class.forName("com.mysql.cj.jdbc.Driver");
            return DriverManager.getConnection(MYSQL_URL, MYSQL_USER, MYSQL_PASSWORD);
        } catch (ClassNotFoundException e) {
            throw new SQLException("MySQL JDBC Driver not found.", e);
        }
    }
    
   
    public static void closeConnection() {
        
    }
    
    // ========== MÉTODOS PARA TESTING ==========
    
    /**
     * Activa/desactiva el modo de prueba
     * SOLO es llamado por BaseIntegrationTest durante los tests
     * 
     * @param testMode true para activar modo test, false para producción
     */

public static void setTestMode(boolean testMode) {
    isTestMode = testMode;
}

public static void setTestConnection(Connection connection) {
    testConnection = connection;
}

public static void setTestMode(boolean testMode, Connection connection) {
    isTestMode = testMode;
    testConnection = connection;
}
    
    /**
     * Verifica si está en modo de prueba
     * Útil para DAOs que necesitan comportarse diferente en tests
     * 
     * @return true si está en modo test, false en producción
     */
    public static boolean isTestMode() {
        return isTestMode;
    }
    
    /**
     * Cierra una conexión de forma segura
     * En modo test NO cierra la conexión (se gestiona por BaseIntegrationTest)
     * En modo producción la cierra normalmente
     * 
     * @param connection Conexión a cerrar
     */
    public static void closeConnection(Connection connection) {
        if (connection != null && !isTestMode) {
            try {
                connection.close();
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
        // En modo test, NO cerramos la conexión aquí
        // La conexión se cierra en BaseIntegrationTest.tearDownDatabase()
    }
    
  
    public static void clearTestConnection() {
        testConnection = null;
    }
}