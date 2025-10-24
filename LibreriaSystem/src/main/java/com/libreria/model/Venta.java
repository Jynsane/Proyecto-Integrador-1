package com.libreria.model;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

public class Venta {
    private int id;
    private String numeroVenta;
    private LocalDateTime fecha;
    private double total;
    private String metodoPago;
    private List<DetalleVenta> detalles;

    public Venta() {
        this.detalles = new ArrayList<>();
        this.fecha = LocalDateTime.now();
    }

    public void agregarDetalle(DetalleVenta detalle) {
        detalles.add(detalle);
        calcularTotal();
    }

    private void calcularTotal() {
        this.total = detalles.stream()
                .mapToDouble(detalle -> detalle.getPrecioUnitario() * detalle.getCantidad())
                .sum();
    }

    // Getters y Setters
    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getNumeroVenta() {
        return numeroVenta;
    }

    public void setNumeroVenta(String numeroVenta) {
        this.numeroVenta = numeroVenta;
    }

    public LocalDateTime getFecha() {
        return fecha;
    }

    public void setFecha(LocalDateTime fecha) {
        this.fecha = fecha;
    }

    public double getTotal() {
        return total;
    }

    public void setTotal(double total) {
        this.total = total;
    }

    public String getMetodoPago() {
        return metodoPago;
    }

    public void setMetodoPago(String metodoPago) {
        this.metodoPago = metodoPago;
    }

    public List<DetalleVenta> getDetalles() {
        return detalles;
    }

    public void setDetalles(List<DetalleVenta> detalles) {
        this.detalles = detalles;
        calcularTotal();
    }
}