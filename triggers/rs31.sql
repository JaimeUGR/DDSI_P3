/*
    Practica 3 - DDSI - 22/23
    Trigger correspondiente al requisito funcional 3.2 y requisito semantico 3.1
    TODO Basarse en los triggers de abajo para realizar mi trigger, BORRAR cuando este terminado
*/

-- Este trigger permite comprobar si un arbitro ya ha aceptado una oferta o no
-- En caso que si haya aceptado se permite realizar oferta, en otro caso, no
-- Para realizar esto, se cuenta cuantas ofertas aceptadas tiene el arbitro, si supera 0, ha aceptado

CREATE OR REPLACE TRIGGER YA_ACEPTO_OFERTA
	BEFORE
	INSERT ON OFERTAS_RECIBE_HECHA_UWU
	FOR EACH ROW
DECLARE
	dni CHAR(9);
	aceptadas INT;
BEGIN
	-- TODO Importante, revisar como se llama la variable de entrada del dni del arbitro
	-- TODO Hace falta incluir el CodEd actual? Tiene sentido, pero de donde lo saco
	SELECT count(*) INTO aceptadas FROM OFERTAS_RECIBE_HECHA_UWU
	WHERE DNIArb = dni AND EstadoOferta = "ACEPTADA";
	IF (aceptadas > 0) THEN
		raise_application_error(-20520, "Error al realizar oferta, el arbitro con DNI " || dni || " ya ha aceptado una")
	END IF;
	-- NOTE Nota para el futuro, sumar strings parece ser con || en PLSQL
END;



-- Disparador para que no se permita que un empleado sea jefe de más de cinco empleados

CREATE OR REPLACE TRIGGER jefes
	BEFORE
	INSERT ON empleados
	FOR EACH ROW
DECLARE
	supervisa INTEGER;
BEGIN
	SELECT count(*) INTO supervisa FROM empleados WHERE dni_jefe = :new.dni_jefe;
	IF (supervisa > 4) THEN
		raise_application_error(-20600, :new.dni_jefe || ' no se puede supervisar más de 5');
	END IF;
END;
/

--  Disparador para impedir que se aumente el sueldo de un empleado en más de un 30%

CREATE OR REPLACE TRIGGER aumentoSueldo
	BEFORE
	UPDATE OF sueldo ON empleados
	FOR EACH ROW
BEGIN
	IF :NEW.sueldo > :OLD.sueldo*1.30 THEN
		raise_application_error(-20600, :new.sueldo || ' no se puede aumentar el sueldo más de un 30%');
	END IF;
END;
/

-- Disparador que inserte una fila en la tabla empleados_baja cuando se borra un empleado

CREATE OR REPLACE TRIGGER bajas
	AFTER
	DELETE ON empleados
	FOR EACH ROW
BEGIN
	INSERT INTO empleados_baja VALUES (:old.dni, :old.nombre, :old.dni_jefe, :old.departamento, :old.sueldo, USER, SYSDATE);
END;
/

-- Disparador para impedir que, al insertar un empleado, el empleado y su jefe puedan pertenecer a departamentos distintos

CREATE OR REPLACE TRIGGER mismoDepartamento
	BEFORE
	INSERT ON EMPLEADOS
	FOR EACH ROW
DECLARE
	dept_jefe INTEGER;
BEGIN
	IF (:new.dni_jefe IS NOT NULL) THEN
		SELECT departamento INTO dept_jefe FROM empleados WHERE dni = :new.dni_jefe;
		IF (dept_jefe <> :new.departamento) THEN
			raise_application_error(-20600, :new.departamento || ' Un empleado y su jefe no pueden pertenecer a distintos departamentos');
		END IF;
	END IF;
END;
/

-- Disparador para impedir que, al insertar un empleado, la suma de los sueldos de los empleados pertenecientes
-- al departamento del empleado insertado supere los 15 000 euros

CREATE OR REPLACE TRIGGER sumaDepartamento
	BEFORE
	INSERT ON empleados
	FOR EACH ROW
DECLARE
	suma INTEGER;
BEGIN
	SELECT SUM(sueldo) INTO suma FROM empleados WHERE departamento = :new.departamento;
	suma := suma + :new.sueldo;
	IF (suma > 15000) THEN
		raise_application_error(-20600, :new.departamento || ' La suma de salarios no puede sersuperior a 15 000');
	END IF;
END;
/
