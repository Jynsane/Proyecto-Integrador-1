package com.libreria.controller;

import com.libreria.model.Usuario;
import com.libreria.dao.UsuarioDAO;

import java.util.List;

public class UsuarioController extends BaseController<Usuario> {
    private final UsuarioDAO usuarioDAO;
    
    public UsuarioController() {
        this.usuarioDAO = new UsuarioDAO();
    }
    
    @Override
    public void crear(Usuario usuario) throws Exception {
        validarDatos(usuario);
        validarUsuario(usuario);
        
        // Verificar que el username no exista
        if (usuarioDAO.existeUsername(usuario.getUsername())) {
            throw new Exception("El nombre de usuario ya existe");
        }
        
        usuarioDAO.crear(usuario);
    }
    
    @Override
    public void actualizar(Usuario usuario) throws Exception {
        validarDatos(usuario);
        validarUsuario(usuario);
        usuarioDAO.actualizar(usuario);
    }
    
    @Override
    public void eliminar(int id) throws Exception {
        usuarioDAO.eliminar(id);
    }
    
    @Override
    public Usuario obtenerPorId(int id) throws Exception {
        return usuarioDAO.obtenerPorId(id);
    }
    
    @Override
    public List<Usuario> obtenerTodos() throws Exception {
        return usuarioDAO.obtenerTodos();
    }
    
    public Usuario autenticar(String username, String password) throws Exception {
        if (username == null || username.trim().isEmpty()) {
            throw new Exception("El nombre de usuario es requerido");
        }
        if (password == null || password.trim().isEmpty()) {
            throw new Exception("La contraseña es requerida");
        }
        
        Usuario usuario = usuarioDAO.autenticar(username, password);
        if (usuario == null) {
            throw new Exception("Usuario o contraseña incorrectos");
        }
        
        return usuario;
    }
    
    private void validarUsuario(Usuario usuario) throws Exception {
        if (usuario.getUsername() == null || usuario.getUsername().trim().isEmpty()) {
            throw new Exception("El nombre de usuario es requerido");
        }
        if (usuario.getPassword() == null || usuario.getPassword().trim().isEmpty()) {
            throw new Exception("La contraseña es requerida");
        }
        if (usuario.getNombre() == null || usuario.getNombre().trim().isEmpty()) {
            throw new Exception("El nombre completo es requerido");
        }
        if (usuario.getRol() == null || usuario.getRol().trim().isEmpty()) {
            throw new Exception("El rol es requerido");
        }
        if (!usuario.getRol().equals("ADMINISTRADOR") && !usuario.getRol().equals("VENDEDOR")) {
            throw new Exception("El rol debe ser ADMINISTRADOR o VENDEDOR");
        }
    }
}