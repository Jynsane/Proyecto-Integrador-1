package com.libreria.controller;

import java.util.List;

public abstract class BaseController<T> {
    public abstract void crear(T entidad) throws Exception;
    public abstract void actualizar(T entidad) throws Exception;
    public abstract void eliminar(int id) throws Exception;
    public abstract T obtenerPorId(int id) throws Exception;
    public abstract List<T> obtenerTodos() throws Exception;
    
    protected void validarDatos(T entidad) throws Exception {
        if (entidad == null) {
            throw new Exception("La entidad no puede ser nula");
        }
    }
}