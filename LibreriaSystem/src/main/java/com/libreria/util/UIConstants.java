package com.libreria.util;

import javax.swing.*;
import javax.swing.border.Border;
import java.awt.*;

public class UIConstants {
    // Colores principales
    public static final Color PRIMARY_COLOR = new Color(87, 127, 101);    // Verde principal
    public static final Color SECONDARY_COLOR = new Color(196, 219, 204); // Verde claro
    public static final Color BACKGROUND_COLOR = new Color(240, 248, 242); // Fondo verdoso muy claro
    public static final Color TEXT_COLOR = new Color(33, 37, 41);         // Texto oscuro
    public static final Color ACCENT_COLOR = new Color(63, 100, 75);      // Verde oscuro para acentos
    
    // Fuentes
    public static final Font TITLE_FONT = new Font("Segoe UI", Font.BOLD, 24);
    public static final Font HEADER_FONT = new Font("Segoe UI", Font.BOLD, 18);
    public static final Font NORMAL_FONT = new Font("Segoe UI", Font.PLAIN, 14);
    public static final Font SMALL_FONT = new Font("Segoe UI", Font.PLAIN, 12);
    
    // Bordes
    public static final Border PANEL_BORDER = BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(SECONDARY_COLOR, 1),
            BorderFactory.createEmptyBorder(10, 10, 10, 10)
    );
    
    // Estilos de botones
    public static void setupButton(JButton button, Color background, Color foreground) {
        button.setBackground(background);
        button.setForeground(foreground);
        button.setFont(NORMAL_FONT);
        button.setFocusPainted(false);
        button.setBorderPainted(false);
        button.setOpaque(true);
        button.setCursor(new Cursor(Cursor.HAND_CURSOR));
    }
    
    public static void styleDefaultButton(JButton button) {
        setupButton(button, PRIMARY_COLOR, Color.WHITE);
    }
    
    public static void styleSecondaryButton(JButton button) {
        setupButton(button, SECONDARY_COLOR, TEXT_COLOR);
    }
    
    // Estilos de campos de texto
    public static void setupTextField(JTextField textField) {
        textField.setFont(NORMAL_FONT);
        textField.setBackground(Color.WHITE);
        textField.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(SECONDARY_COLOR),
                BorderFactory.createEmptyBorder(5, 5, 5, 5)
        ));
    }
    
    // Estilos de tablas
    public static void setupTable(JTable table) {
        table.setFont(NORMAL_FONT);
        table.setGridColor(SECONDARY_COLOR);
        table.setSelectionBackground(SECONDARY_COLOR);
        table.setSelectionForeground(TEXT_COLOR);
        table.setRowHeight(25);
        table.getTableHeader().setFont(NORMAL_FONT.deriveFont(Font.BOLD));
        table.getTableHeader().setBackground(PRIMARY_COLOR);
        table.getTableHeader().setForeground(Color.WHITE);
    }
    
    // Estilos de paneles
    public static void setupPanel(JPanel panel) {
        panel.setBackground(BACKGROUND_COLOR);
        panel.setBorder(PANEL_BORDER);
    }
}