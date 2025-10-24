package com.libreria.dao;

import java.util.List;

public interface CrudDAO<T> {
    void crear(T entidad) throws Exception;
    T obtenerPorId(int id) throws Exception;
    List<T> obtenerTodos() throws Exception;
    void actualizar(T entidad) throws Exception;
    void eliminar(int id) throws Exception;
}