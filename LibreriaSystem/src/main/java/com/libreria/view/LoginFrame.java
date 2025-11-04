package com.libreria.view;

import javax.swing.*;

//Clase Login_frame
public class LoginFrame extends JFrame {
    
    public LoginFrame() {
        initComponents();
    }
    
    private void initComponents() {
        setTitle("Login - LibreTools");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(1000, 700);
        setLocationRelativeTo(null);
        setResizable(false);
        
        LoginPanel loginPanel = new LoginPanel(this);
        add(loginPanel);
    }
    
    public static void main(String[] args) {
        try {
            UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
        } catch (Exception e) {
            e.printStackTrace();
        }
        
        SwingUtilities.invokeLater(() -> {
            LoginFrame frame = new LoginFrame();
            frame.setVisible(true);
        });
    }
}