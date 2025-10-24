package com.libreria.view;

import javax.swing.*;
import java.awt.*;

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
    
    protected void configureButton(JButton button) {
        button.setBackground(new Color(51, 122, 183));
        button.setForeground(Color.WHITE);
        button.setFocusPainted(false);
        button.setBorderPainted(false);
        button.setFont(new Font("Arial", Font.BOLD, 12));
    }
    
    protected JPanel createButtonPanel(JButton... buttons) {
        JPanel panel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        for (JButton button : buttons) {
            configureButton(button);
            panel.add(button);
        }
        return panel;
    }
    
    protected JTextField createTextField(int columns) {
        JTextField field = new JTextField(columns);
        field.setPreferredSize(new Dimension(field.getPreferredSize().width, 30));
        return field;
    }
    
    protected JPanel createFormPanel() {
        JPanel panel = new JPanel(new GridBagLayout());
        panel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
        return panel;
    }
    
    protected void addFormField(JPanel panel, String label, JComponent field, GridBagConstraints gbc, int row) {
        gbc.gridx = 0;
        gbc.gridy = row;
        gbc.anchor = GridBagConstraints.EAST;
        panel.add(new JLabel(label + ": "), gbc);
        
        gbc.gridx = 1;
        gbc.anchor = GridBagConstraints.WEST;
        panel.add(field, gbc);
    }
}