-- Mejoras para bases de datos ya creadas con el script original.
-- Evita cobrar dos veces el mismo concepto en el mismo periodo a una vivienda.
USE condominio;
GO

ALTER TABLE cuotas
    ADD CONSTRAINT UK_Cuotas_Vivienda_Concepto_Periodo
    UNIQUE (id_vivienda, id_concepto, periodo);
GO
