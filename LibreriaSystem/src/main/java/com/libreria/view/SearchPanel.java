package com.libreria.view;

import com.libreria.util.UIConstants;
import javax.swing.*;
import java.awt.*;

public class SearchPanel extends JPanel {
    private JTextField txtBusqueda;
    private JComboBox<String> cmbFiltro;
    private JButton btnBuscar;
    
    public SearchPanel(String[] filtros) {
        initComponents(filtros);
    }
    
    private void initComponents(String[] filtros) {
        setLayout(new FlowLayout(FlowLayout.LEFT, 10, 10));
        setBackground(Color.WHITE);
        setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createMatteBorder(0, 0, 1, 0, new Color(220, 220, 220)),
            BorderFactory.createEmptyBorder(10, 10, 10, 10)
        ));
        
        JLabel lblBuscar = new JLabel("Buscar por:");
        lblBuscar.setFont(UIConstants.NORMAL_FONT.deriveFont(Font.BOLD));
        lblBuscar.setForeground(Color.BLACK);
        add(lblBuscar);
        
        cmbFiltro = new JComboBox<>(filtros);
        cmbFiltro.setFont(UIConstants.NORMAL_FONT);
        cmbFiltro.setPreferredSize(new Dimension(150, 35));
        cmbFiltro.setBackground(Color.WHITE);
        cmbFiltro.setForeground(Color.BLACK);
        add(cmbFiltro);
        
        txtBusqueda = new JTextField(25);
        txtBusqueda.setFont(UIConstants.NORMAL_FONT);
        txtBusqueda.setPreferredSize(new Dimension(300, 35));
        txtBusqueda.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(new Color(200, 200, 200), 1),
            BorderFactory.createEmptyBorder(5, 10, 5, 10)
        ));
        add(txtBusqueda);
        
        btnBuscar = new JButton("🔍 Buscar");
        btnBuscar.setFont(UIConstants.NORMAL_FONT.deriveFont(Font.BOLD));
        btnBuscar.setBackground(UIConstants.PRIMARY_COLOR);
        btnBuscar.setForeground(Color.WHITE);
        btnBuscar.setFocusPainted(false);
        btnBuscar.setBorderPainted(false);
        btnBuscar.setPreferredSize(new Dimension(120, 35));
        btnBuscar.setCursor(new Cursor(Cursor.HAND_CURSOR));
        add(btnBuscar);
    }
    
    public String getSearchText() {
        return txtBusqueda.getText();
    }
    
    public String getSelectedFilter() {
        return (String) cmbFiltro.getSelectedItem();
    }
    
    public void addSearchListener(java.awt.event.ActionListener listener) {
        btnBuscar.addActionListener(listener);
        txtBusqueda.addActionListener(listener);
    }
    
    public void clear() {
        txtBusqueda.setText("");
    }
}