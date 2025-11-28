-- =============================================
-- SCHEMA H2 PARA TESTING
-- Sistema de Librería
-- 
-- Ubicación: test/resources/h2-schema.sql
-- 
-- NOTA: Este archivo es OPCIONAL y solo para referencia
-- El schema se crea automáticamente en TestDatabaseConfig.java
-- =============================================

-- Tabla de Productos
CREATE TABLE IF NOT EXISTS productos (
    id INT AUTO_INCREMENT PRIMARY KEY,
    codigo VARCHAR(50) UNIQUE NOT NULL,
    nombre VARCHAR(200) NOT NULL,
    categoria VARCHAR(100) NOT NULL,
    precio DECIMAL(10,2) NOT NULL,
    stock INT DEFAULT 0,
    descripcion TEXT,
    fecha_registro TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

-- Tabla de Usuarios
CREATE TABLE IF NOT EXISTS usuarios (
    id INT AUTO_INCREMENT PRIMARY KEY,
    username VARCHAR(50) UNIQUE NOT NULL,
    password VARCHAR(255) NOT NULL,
    nombre VARCHAR(100) NOT NULL,
    rol VARCHAR(20) NOT NULL,
    activo BOOLEAN DEFAULT TRUE,
    fecha_registro TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

-- Tabla de Ventas
CREATE TABLE IF NOT EXISTS ventas (
    id INT AUTO_INCREMENT PRIMARY KEY,
    numero_venta VARCHAR(50) UNIQUE NOT NULL,
    fecha TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    total DECIMAL(10,2) NOT NULL,
    metodo_pago VARCHAR(20) NOT NULL
);

-- Tabla de Detalles de Venta
CREATE TABLE IF NOT EXISTS detalles_venta (
    id INT AUTO_INCREMENT PRIMARY KEY,
    venta_id INT NOT NULL,
    producto_id INT NOT NULL,
    cantidad INT NOT NULL,
    precio_unitario DECIMAL(10,2) NOT NULL,
    FOREIGN KEY (venta_id) REFERENCES ventas(id) ON DELETE CASCADE,
    FOREIGN KEY (producto_id) REFERENCES productos(id)
);

-- Índices para mejorar rendimiento
CREATE INDEX IF NOT EXISTS idx_productos_codigo ON productos(codigo);
CREATE INDEX IF NOT EXISTS idx_productos_categoria ON productos(categoria);
CREATE INDEX IF NOT EXISTS idx_ventas_fecha ON ventas(fecha);
CREATE INDEX IF NOT EXISTS idx_ventas_numero ON ventas(numero_venta);
CREATE INDEX IF NOT EXISTS idx_detalles_venta_id ON detalles_venta(venta_id);
CREATE INDEX IF NOT EXISTS idx_detalles_producto_id ON detalles_venta(producto_id);

-- =============================================
-- DATOS DE PRUEBA
-- =============================================

-- Productos de prueba
INSERT INTO productos (codigo, nombre, categoria, precio, stock, descripcion) VALUES
('P000001', 'Cuaderno A4 Universitario', 'Papelería', 15.50, 100, 'Cuaderno de 100 hojas'),
('P000002', 'Lapicero Azul Faber-Castell', 'Papelería', 2.50, 200, 'Lapicero tinta gel'),
('P000003', 'Resma Papel A4', 'Papelería', 25.00, 50, 'Resma 500 hojas'),
('P000004', 'Corrector Líquido', 'Papelería', 3.50, 150, 'Corrector 20ml'),
('P000005', 'Calculadora Científica', 'Electrónica', 45.00, 30, 'Calculadora Casio FX-82');

-- Usuarios de prueba
INSERT INTO usuarios (username, password, nombre, rol, activo) VALUES
('admin', 'admin123', 'Administrador Test', 'ADMINISTRADOR', true),
('vendedor', 'vendedor123', 'Vendedor Test', 'VENDEDOR', true);