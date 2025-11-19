-- === Roles ===
-- Solo inserta si el registro no existe
-- INSERT INTO role (id, nombre)
-- SELECT 1, 'ESTUDIANTE' WHERE NOT EXISTS (SELECT 1 FROM role WHERE id = 1);

-- INSERT INTO role (id, nombre)
-- SELECT 2, 'PROFESOR' WHERE NOT EXISTS (SELECT 1 FROM role WHERE id = 2);

INSERT INTO rol (id, nombre)
SELECT 3, 'DirectorCarrera' WHERE NOT EXISTS (SELECT 1 FROM rol WHERE id = 3);

-- INSERT INTO role (id, nombre)
-- SELECT 4, 'DIRECTOR_DEPARTAMENTO' WHERE NOT EXISTS (SELECT 1 FROM role WHERE id = 4);

-- === Usuarios ===
INSERT INTO usuario (id, nombre, rol_id)
SELECT 1, 'samegi', 3 WHERE NOT EXISTS (SELECT 1 FROM usuario WHERE id = 1); -- DIRECTOR_CARRERA

-- INSERT INTO usuario (id, nombre, role_id)
-- SELECT 2, 'Carlos Mendoza', 4 WHERE NOT EXISTS (SELECT 1 FROM usuario WHERE id = 2); -- DIRECTOR_DEPARTAMENTO

-- INSERT INTO usuario (id, nombre, role_id)
-- SELECT 3, 'Ana Gómez', 2 WHERE NOT EXISTS (SELECT 1 FROM usuario WHERE id = 3); -- PROFESOR

-- INSERT INTO usuario (id, nombre, role_id)
-- SELECT 4, 'Juan Pérez', 1 WHERE NOT EXISTS (SELECT 1 FROM usuario WHERE id = 4); -- ESTUDIANTE

-- === Departamento ===
INSERT INTO departamento (id, nombre)
SELECT 1, 'Departamento de Ingeniería de Sistemas' WHERE NOT EXISTS (SELECT 1 FROM departamento WHERE id = 1);

-- === Carrera ===
INSERT INTO carrera (id, nombre, departamento_id)
SELECT 1, 'Ingeniería de Sistemas', 1 WHERE NOT EXISTS (SELECT 1 FROM carrera WHERE id = 1);

-- === Semestres ===
INSERT INTO semestre (id, fecha_inicio, fecha_fin, nombre, año, periodo)
SELECT 1, '2025-01-15T00:00:00Z', '2025-06-15T00:00:00Z', 'Primer Semestre 2025', 2025, 1
    WHERE NOT EXISTS (SELECT 1 FROM semestre WHERE id = 1);

INSERT INTO semestre (id, fecha_inicio, fecha_fin, nombre, año, periodo)
SELECT 2, '2027-07-15T00:00:00Z', '2026-12-15T00:00:00Z', 'Segundo Semestre 2025', 2025, 2
    WHERE NOT EXISTS (SELECT 1 FROM semestre WHERE id = 2);

-- === Profesores ===
-- INSERT INTO profesor (nombre, correo, tipo_profesor, min_horas, max_horas, valor_hora, pago_extra_por_hora, empresa, categoria, departamento_id)
-- SELECT 'Laura Martínez', 'laura.martinez@universidad.edu.co', 'CATEDRA', 0, 0, 0, 0, 'Tech Solutions S.A.','B', 1
--    WHERE NOT EXISTS (SELECT 1 FROM profesor WHERE correo = 'laura.martinez@universidad.edu.co');

-- INSERT INTO profesor (nombre, correo, tipo_profesor, min_horas, max_horas, valor_hora, pago_extra_por_hora, empresa, categoria, departamento_id)
-- SELECT 'Edgar Munoz', 'edgar@universidad.edu.co', 'PLANTA', 10, 15, 20000, 50000, 'Puentes Edgar',null, 1
--    WHERE NOT EXISTS (SELECT 1 FROM profesor WHERE correo = 'edgar@universidad.edu.co');