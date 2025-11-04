package com.libreria.util;

import com.libreria.model.Usuario;

public class SessionManager {
    private static SessionManager instance;
    private Usuario usuarioActual;
    
    private SessionManager() {
    }
    
    public static SessionManager getInstance() {
        if (instance == null) {
            instance = new SessionManager();
        }
        return instance;
    }
    
    public void iniciarSesion(Usuario usuario) {
        this.usuarioActual = usuario;
    }
    
    public void cerrarSesion() {
        this.usuarioActual = null;
    }
    
    public Usuario getUsuarioActual() {
        return usuarioActual;
    }
    
    public boolean isLoggedIn() {
        return usuarioActual != null;
    }
    
    public boolean isAdministrador() {
        return usuarioActual != null && usuarioActual.isAdministrador();
    }
    
    public boolean isVendedor() {
        return usuarioActual != null && usuarioActual.isVendedor();
    }
    
    public String getNombreUsuario() {
        return usuarioActual != null ? usuarioActual.getNombre() : "";
    }
    
    public String getRol() {
        return usuarioActual != null ? usuarioActual.getRol() : "";
    }
}