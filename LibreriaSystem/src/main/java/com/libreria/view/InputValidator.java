package com.libreria.view;

import javax.swing.*;
import javax.swing.text.*;
import java.awt.*;
import java.text.NumberFormat;
//import java.text.ParseException;
import java.util.Locale;

public class InputValidator {
    private static final Color ERROR_COLOR = new Color(255, 200, 200);
    private static final Color NORMAL_COLOR = Color.WHITE;
    
    public static void setNumericOnly(JTextField textField) {
        ((AbstractDocument) textField.getDocument()).setDocumentFilter(new DocumentFilter() {
            @Override
            public void insertString(FilterBypass fb, int offset, String string, AttributeSet attr)
                    throws BadLocationException {
                if (string == null)
                    return;
                
                if (string.matches("\\d*")) {
                    super.insertString(fb, offset, string, attr);
                }
            }

            @Override
            public void replace(FilterBypass fb, int offset, int length, String text, AttributeSet attrs)
                    throws BadLocationException {
                if (text == null)
                    return;
                
                if (text.matches("\\d*")) {
                    super.replace(fb, offset, length, text, attrs);
                }
            }
        });
    }
    
    public static void setDecimalOnly(JTextField textField) {
        NumberFormat format = NumberFormat.getInstance(new Locale("es", "PE"));
        format.setMinimumFractionDigits(2);
        format.setMaximumFractionDigits(2);
        
        ((AbstractDocument) textField.getDocument()).setDocumentFilter(new DocumentFilter() {
            @Override
            public void insertString(FilterBypass fb, int offset, String string, AttributeSet attr)
                    throws BadLocationException {
                if (string == null)
                    return;
                
                String newText = fb.getDocument().getText(0, fb.getDocument().getLength()) + string;
                if (isValidDecimal(newText)) {
                    super.insertString(fb, offset, string, attr);
                }
            }

            @Override
            public void replace(FilterBypass fb, int offset, int length, String text, AttributeSet attrs)
                    throws BadLocationException {
                if (text == null)
                    return;
                
                String newText = fb.getDocument().getText(0, fb.getDocument().getLength());
                newText = newText.substring(0, offset) + text + newText.substring(offset + length);
                if (isValidDecimal(newText)) {
                    super.replace(fb, offset, length, text, attrs);
                }
            }
            
            private boolean isValidDecimal(String text) {
                if (text.isEmpty()) return true;
                try {
                    Double.parseDouble(text);
                    return true;
                } catch (NumberFormatException e) {
                    return false;
                }
            }
        });
    }
    
    public static void setMaxLength(JTextField textField, int maxLength) {
        ((AbstractDocument) textField.getDocument()).setDocumentFilter(new DocumentFilter() {
            @Override
            public void insertString(FilterBypass fb, int offset, String string, AttributeSet attr)
                    throws BadLocationException {
                if (string == null)
                    return;
                
                if ((fb.getDocument().getLength() + string.length()) <= maxLength) {
                    super.insertString(fb, offset, string, attr);
                }
            }

            @Override
            public void replace(FilterBypass fb, int offset, int length, String text, AttributeSet attrs)
                    throws BadLocationException {
                if (text == null)
                    return;
                
                if ((fb.getDocument().getLength() - length + text.length()) <= maxLength) {
                    super.replace(fb, offset, length, text, attrs);
                }
            }
        });
    }
    
    public static boolean validateRequired(JTextField field, String fieldName) {
        if (field.getText().trim().isEmpty()) {
            field.setBackground(ERROR_COLOR);
            JOptionPane.showMessageDialog(field, 
                fieldName + " es un campo requerido", 
                "Error de validación", 
                JOptionPane.ERROR_MESSAGE);
            return false;
        }
        field.setBackground(NORMAL_COLOR);
        return true;
    }
    
    public static boolean validatePositiveNumber(JTextField field, String fieldName) {
        try {
            double value = Double.parseDouble(field.getText().trim());
            if (value <= 0) {
                field.setBackground(ERROR_COLOR);
                JOptionPane.showMessageDialog(field, 
                    fieldName + " debe ser mayor que 0", 
                    "Error de validación", 
                    JOptionPane.ERROR_MESSAGE);
                return false;
            }
            field.setBackground(NORMAL_COLOR);
            return true;
        } catch (NumberFormatException e) {
            field.setBackground(ERROR_COLOR);
            JOptionPane.showMessageDialog(field, 
                fieldName + " debe ser un número válido", 
                "Error de validación", 
                JOptionPane.ERROR_MESSAGE);
            return false;
        }
    }
    
    public static boolean validateNonNegativeNumber(JTextField field, String fieldName) {
        try {
            double value = Double.parseDouble(field.getText().trim());
            if (value < 0) {
                field.setBackground(ERROR_COLOR);
                JOptionPane.showMessageDialog(field, 
                    fieldName + " no puede ser negativo", 
                    "Error de validación", 
                    JOptionPane.ERROR_MESSAGE);
                return false;
            }
            field.setBackground(NORMAL_COLOR);
            return true;
        } catch (NumberFormatException e) {
            field.setBackground(ERROR_COLOR);
            JOptionPane.showMessageDialog(field, 
                fieldName + " debe ser un número válido", 
                "Error de validación", 
                JOptionPane.ERROR_MESSAGE);
            return false;
        }
    }
}