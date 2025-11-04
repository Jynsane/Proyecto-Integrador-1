package com.libreria.view;

import javax.swing.*;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.JTableHeader;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import com.libreria.util.UIConstants;

public abstract class BasePanel extends JPanel {
    
    protected void showError(String message) {
        JOptionPane.showMessageDialog(
            this,
            message,
            "Error",
            JOptionPane.ERROR_MESSAGE
        );
    }
    
    protected void showInfo(String message) {
        JOptionPane.showMessageDialog(
            this,
            message,
            "Información",
            JOptionPane.INFORMATION_MESSAGE
        );
    }
    
    protected boolean showConfirm(String message) {
        return JOptionPane.showConfirmDialog(
            this,
            message,
            "Confirmar",
            JOptionPane.YES_NO_OPTION
        ) == JOptionPane.YES_OPTION;
    }
    
    protected JButton createStyledButton(String text, Color bgColor) {
        JButton button = new JButton(text);
        button.setFont(UIConstants.BUTTON_FONT);
        button.setForeground(Color.WHITE);
        button.setBackground(bgColor);
        button.setBorderPainted(false);
        button.setFocusPainted(false);
        button.setPreferredSize(new Dimension(140, UIConstants.BUTTON_HEIGHT));
        button.setCursor(new Cursor(Cursor.HAND_CURSOR));
        
        // Efecto hover
        button.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseEntered(MouseEvent e) {
                button.setBackground(bgColor.darker());
            }
            
            @Override
            public void mouseExited(MouseEvent e) {
                button.setBackground(bgColor);
            }
        });
        
        return button;
    }
    
    protected void configureButton(JButton button) {
        button.setBackground(UIConstants.PRIMARY_COLOR);
        button.setForeground(Color.WHITE);
        button.setFocusPainted(false);
        button.setBorderPainted(false);
        button.setFont(UIConstants.BUTTON_FONT);
        button.setCursor(new Cursor(Cursor.HAND_CURSOR));
        
        Color originalColor = button.getBackground();
        button.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseEntered(MouseEvent e) {
                button.setBackground(originalColor.darker());
            }
            
            @Override
            public void mouseExited(MouseEvent e) {
                button.setBackground(originalColor);
            }
        });
    }
    
    protected JPanel createButtonPanel(JButton... buttons) {
        JPanel panel = new JPanel(new FlowLayout(FlowLayout.RIGHT, 10, 10));
        panel.setBackground(Color.WHITE);
        for (JButton button : buttons) {
            panel.add(button);
        }
        return panel;
    }
    
    protected JTextField createStyledTextField(int columns) {
        JTextField field = new JTextField(columns);
        field.setFont(UIConstants.NORMAL_FONT);
        field.setPreferredSize(new Dimension(field.getPreferredSize().width, UIConstants.INPUT_HEIGHT));
        field.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(UIConstants.BORDER_COLOR, 1),
            BorderFactory.createEmptyBorder(5, 10, 5, 10)
        ));
        return field;
    }
    
    protected JTextField createTextField(int columns) {
        return createStyledTextField(columns);
    }
    
    protected void stylePasswordField(JPasswordField field) {
        field.setFont(UIConstants.NORMAL_FONT);
        field.setPreferredSize(new Dimension(field.getPreferredSize().width, UIConstants.INPUT_HEIGHT));
        field.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(UIConstants.BORDER_COLOR, 1),
            BorderFactory.createEmptyBorder(5, 10, 5, 10)
        ));
    }
    
    protected void styleComboBox(JComboBox<?> comboBox) {
        comboBox.setFont(UIConstants.NORMAL_FONT);
        comboBox.setPreferredSize(new Dimension(comboBox.getPreferredSize().width, UIConstants.INPUT_HEIGHT));
        comboBox.setBackground(Color.WHITE);
    }
    
    protected JPanel createStyledFormPanel() {
        JPanel panel = new JPanel(new GridBagLayout());
        panel.setBackground(Color.WHITE);
        panel.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(UIConstants.BORDER_COLOR, 1),
            BorderFactory.createEmptyBorder(20, 20, 20, 20)
        ));
        return panel;
    }
    
    protected JPanel createFormPanel() {
        return createStyledFormPanel();
    }
    
    protected void addFormField(JPanel panel, String label, JComponent field, GridBagConstraints gbc, int row) {
        gbc.gridx = 0;
        gbc.gridy = row;
        gbc.anchor = GridBagConstraints.EAST;
        gbc.weightx = 0.0;
        
        JLabel lblField = new JLabel(label + (label.isEmpty() ? "" : ": "));
        lblField.setFont(UIConstants.NORMAL_FONT.deriveFont(Font.BOLD));
        lblField.setForeground(UIConstants.TEXT_COLOR);
        panel.add(lblField, gbc);
        
        gbc.gridx = 1;
        gbc.anchor = GridBagConstraints.WEST;
        gbc.weightx = 1.0;
        panel.add(field, gbc);
    }
    
    protected void styleTable(JTable table) {
        table.setFont(UIConstants.NORMAL_FONT);
        table.setRowHeight(40);
        table.setShowGrid(false);
        table.setIntercellSpacing(new Dimension(0, 0));
        table.setSelectionBackground(UIConstants.PRIMARY_LIGHT);
        table.setSelectionForeground(Color.WHITE);
        
        // Estilo del header
        JTableHeader header = table.getTableHeader();
        header.setFont(UIConstants.NORMAL_FONT.deriveFont(Font.BOLD));
        header.setBackground(UIConstants.PRIMARY_COLOR);
        header.setForeground(Color.WHITE);
        header.setPreferredSize(new Dimension(header.getPreferredSize().width, 45));
        
        // Centrar contenido de las celdas
        DefaultTableCellRenderer centerRenderer = new DefaultTableCellRenderer();
        centerRenderer.setHorizontalAlignment(JLabel.CENTER);
        for (int i = 0; i < table.getColumnCount(); i++) {
            table.getColumnModel().getColumn(i).setCellRenderer(centerRenderer);
        }
    }
}