----creacion de base de datos---
CREATE DATABASE condominio;
GO

USE condominio;
GO

----Crear las tablas-------
/* =========================================================
   1. TABLA ROLES
   ========================================================= */
CREATE TABLE roles (
    id_rol INT IDENTITY(1,1) PRIMARY KEY,
    nombre VARCHAR(50) NOT NULL UNIQUE,
    descripcion VARCHAR(150),
    estado BIT NOT NULL DEFAULT 1
);
GO


/* =========================================================
   2. TABLA PERSONAS
   ========================================================= */
CREATE TABLE personas (
    id_persona INT IDENTITY(1,1) PRIMARY KEY,
    nombres VARCHAR(100) NOT NULL,
    apellidos VARCHAR(100) NOT NULL,
    dpi VARCHAR(20) NOT NULL UNIQUE,
    telefono VARCHAR(20),
    correo VARCHAR(150),
    fecha_nacimiento DATE,
    estado BIT NOT NULL DEFAULT 1
);
GO


/* =========================================================
   3. TABLA USUARIOS
   ========================================================= */
CREATE TABLE usuarios (
    id_usuario INT IDENTITY(1,1) PRIMARY KEY,
    id_persona INT NOT NULL,
    id_rol INT NOT NULL,
    username VARCHAR(50) NOT NULL UNIQUE,
    password VARCHAR(255) NOT NULL,
    estado BIT NOT NULL DEFAULT 1,
    fecha_creacion DATETIME NOT NULL DEFAULT GETDATE(),

    CONSTRAINT FK_Usuarios_Personas
        FOREIGN KEY (id_persona)
        REFERENCES personas(id_persona),

    CONSTRAINT FK_Usuarios_Roles
        FOREIGN KEY (id_rol)
        REFERENCES roles(id_rol)
);
GO


/* =========================================================
   4. TABLA VIVIENDAS
   ========================================================= */
CREATE TABLE viviendas (
    id_vivienda INT IDENTITY(1,1) PRIMARY KEY,
    numero VARCHAR(20) NOT NULL,
    bloque VARCHAR(20),
    direccion VARCHAR(200),
    estado VARCHAR(30) NOT NULL DEFAULT 'Ocupada',

    CONSTRAINT CK_Viviendas_Estado
        CHECK (estado IN ('Disponible', 'Ocupada', 'Mantenimiento', 'Inactiva'))
);
GO


/* =========================================================
   5. TABLA VIVIENDA_PERSONA
   ========================================================= */
CREATE TABLE vivienda_persona (
    id_vivienda_persona INT IDENTITY(1,1) PRIMARY KEY,
    id_vivienda INT NOT NULL,
    id_persona INT NOT NULL,
    tipo_relacion VARCHAR(30) NOT NULL,
    fecha_inicio DATE NOT NULL DEFAULT GETDATE(),
    fecha_fin DATE NULL,
    estado BIT NOT NULL DEFAULT 1,

    CONSTRAINT FK_ViviendaPersona_Viviendas
        FOREIGN KEY (id_vivienda)
        REFERENCES viviendas(id_vivienda),

    CONSTRAINT FK_ViviendaPersona_Personas
        FOREIGN KEY (id_persona)
        REFERENCES personas(id_persona),

    CONSTRAINT CK_ViviendaPersona_TipoRelacion
        CHECK (tipo_relacion IN (
            'Propietario',
            'Residente',
            'Inquilino',
            'Familiar'
        ))
);
GO


/* =========================================================
   6. TABLA VEHICULOS
   ========================================================= */
CREATE TABLE vehiculos (
    id_vehiculo INT IDENTITY(1,1) PRIMARY KEY,
    id_vivienda INT NOT NULL,
    placa VARCHAR(20) NOT NULL UNIQUE,
    marca VARCHAR(50),
    modelo VARCHAR(50),
    color VARCHAR(30),
    tipo VARCHAR(30),
    estado BIT NOT NULL DEFAULT 1,

    CONSTRAINT FK_Vehiculos_Viviendas
        FOREIGN KEY (id_vivienda)
        REFERENCES viviendas(id_vivienda)
);
GO


/* =========================================================
   7. TABLA CONCEPTOS_CUOTA
   ========================================================= */
CREATE TABLE conceptos_cuota (
    id_concepto INT IDENTITY(1,1) PRIMARY KEY,
    nombre VARCHAR(100) NOT NULL UNIQUE,
    descripcion VARCHAR(200),
    monto_base DECIMAL(10,2) NOT NULL,
    periodicidad VARCHAR(30) NOT NULL,
    estado BIT NOT NULL DEFAULT 1,

    CONSTRAINT CK_ConceptosCuota_Monto
        CHECK (monto_base >= 0),

    CONSTRAINT CK_ConceptosCuota_Periodicidad
        CHECK (periodicidad IN (
            'Mensual',
            'Trimestral',
            'Semestral',
            'Anual',
            'Unica'
        ))
);
GO


/* =========================================================
   8. TABLA CUOTAS
   ========================================================= */
CREATE TABLE cuotas (
    id_cuota INT IDENTITY(1,1) PRIMARY KEY,
    id_vivienda INT NOT NULL,
    id_concepto INT NOT NULL,
    periodo VARCHAR(20) NOT NULL,
    monto DECIMAL(10,2) NOT NULL,
    fecha_emision DATE NOT NULL DEFAULT CAST(GETDATE() AS DATE),
    fecha_vencimiento DATE NOT NULL,
    estado VARCHAR(30) NOT NULL DEFAULT 'Pendiente',

    CONSTRAINT FK_Cuotas_Viviendas
        FOREIGN KEY (id_vivienda)
        REFERENCES viviendas(id_vivienda),

    CONSTRAINT FK_Cuotas_Conceptos
        FOREIGN KEY (id_concepto)
        REFERENCES conceptos_cuota(id_concepto),

    CONSTRAINT UK_Cuotas_Vivienda_Concepto_Periodo
        UNIQUE (id_vivienda, id_concepto, periodo),

    CONSTRAINT CK_Cuotas_Monto
        CHECK (monto >= 0),

    CONSTRAINT CK_Cuotas_Estado
        CHECK (estado IN (
            'Pendiente',
            'Pagada',
            'Vencida',
            'Cancelada'
        ))
);
GO


/* =========================================================
   9. TABLA PAGOS
   ========================================================= */
CREATE TABLE pagos (
    id_pago INT IDENTITY(1,1) PRIMARY KEY,
    id_cuota INT NOT NULL,
    id_usuario INT NOT NULL,
    monto DECIMAL(10,2) NOT NULL,
    fecha_pago DATETIME NOT NULL DEFAULT GETDATE(),
    metodo_pago VARCHAR(30) NOT NULL,
    referencia VARCHAR(100),
    estado VARCHAR(30) NOT NULL DEFAULT 'Aprobado',

    CONSTRAINT FK_Pagos_Cuotas
        FOREIGN KEY (id_cuota)
        REFERENCES cuotas(id_cuota),

    CONSTRAINT FK_Pagos_Usuarios
        FOREIGN KEY (id_usuario)
        REFERENCES usuarios(id_usuario),

    CONSTRAINT CK_Pagos_Monto
        CHECK (monto > 0),

    CONSTRAINT CK_Pagos_Metodo
        CHECK (metodo_pago IN (
            'Efectivo',
            'Transferencia',
            'Tarjeta',
            'Deposito'
        )),

    CONSTRAINT CK_Pagos_Estado
        CHECK (estado IN (
            'Pendiente',
            'Aprobado',
            'Rechazado',
            'Cancelado'
        ))
);
GO


/* =========================================================
   10. TABLA NOTIFICACIONES
   ========================================================= */
CREATE TABLE notificaciones (
    id_notificacion INT IDENTITY(1,1) PRIMARY KEY,
    id_usuario INT NOT NULL,
    titulo VARCHAR(150) NOT NULL,
    mensaje VARCHAR(MAX) NOT NULL,
    tipo VARCHAR(30) NOT NULL,
    fecha_publicacion DATETIME NOT NULL DEFAULT GETDATE(),
    fecha_expiracion DATETIME NULL,
    estado BIT NOT NULL DEFAULT 1,

    CONSTRAINT FK_Notificaciones_Usuarios
        FOREIGN KEY (id_usuario)
        REFERENCES usuarios(id_usuario),

    CONSTRAINT CK_Notificaciones_Tipo
        CHECK (tipo IN (
            'Pago',
            'Aviso',
            'Alerta',
            'Sistema'
        ))
);
GO


/* =========================================================
   11. TABLA AUDITORIA
   ========================================================= */
CREATE TABLE auditoria (
    id_auditoria INT IDENTITY(1,1) PRIMARY KEY,
    id_usuario INT NOT NULL,
    accion VARCHAR(50) NOT NULL,
    tabla_afectada VARCHAR(50) NOT NULL,
    registro_id INT NULL,
    descripcion VARCHAR(255),
    fecha DATETIME NOT NULL DEFAULT GETDATE(),

    CONSTRAINT FK_Auditoria_Usuarios
        FOREIGN KEY (id_usuario)
        REFERENCES usuarios(id_usuario)
);
GO
