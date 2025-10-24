package com.libreria.view;

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
        setLayout(new FlowLayout(FlowLayout.LEFT));
        setBorder(BorderFactory.createEmptyBorder(5, 5, 5, 5));
        
        add(new JLabel("Buscar por:"));
        cmbFiltro = new JComboBox<>(filtros);
        add(cmbFiltro);
        
        txtBusqueda = new JTextField(20);
        add(txtBusqueda);
        
        btnBuscar = new JButton("Buscar");
        btnBuscar.setBackground(new Color(51, 122, 183));
        btnBuscar.setForeground(Color.WHITE);
        btnBuscar.setFocusPainted(false);
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