package com.libreria.util;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class DatabaseConnection {
    private static final String URL = "jdbc:mysql://localhost:3306/libreria_db";
    private static final String USER = "root";
    private static final String PASSWORD = "Callupe07.";
    
    /**
     * Devuelve una nueva conexión para cada llamada. Las clases llamantes deben cerrar
     * la conexión cuando terminen (p. ej. con try-with-resources). Esto evita el problema
     * de compartir y cerrar accidentalmente una conexión global.
     */
    public static Connection getConnection() throws SQLException {
        try {
            Class.forName("com.mysql.cj.jdbc.Driver");
        } catch (ClassNotFoundException e) {
            throw new SQLException("MySQL JDBC Driver not found.", e);
        }
        return DriverManager.getConnection(URL, USER, PASSWORD);
    }

    /**
     * Método de compatibilidad; actualmente no mantiene una conexión global.
     */
    public static void closeConnection() {
        // No hay conexión global que cerrar; las conexiones se cierran por los llamantes.
    }
}