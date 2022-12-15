/*
    Practica 3 - DDSI - 22/23
    Trigger correspondiente al requisito funcional 3.2 y requisito semantico 3.1
*/

-- Este trigger permite comprobar si un arbitro ya ha aceptado una oferta o no
-- En caso que si haya aceptado se permite realizar oferta, en otro caso, no
-- Para realizar esto, se cuenta cuantas ofertas aceptadas tiene el arbitro, si supera 0, ha aceptado

CREATE OR REPLACE TRIGGER YA_ACEPTO_OFERTA
	BEFORE
	INSERT ON OFERTAS_RECIBE_HECHA_UWU
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
