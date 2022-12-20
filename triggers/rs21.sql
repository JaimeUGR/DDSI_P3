--Trigger correspondiente al requisito funcional 2.4 y requisito semantico 2.1
-- Este trigger permite comprobar si un espectador ya ha pagado una compra en el dia
-- En caso que no haya pagado se permite realizar una compra, en otro caso, no.

CREATE OR REPLACE TRIGGER rs21
	BEFORE
	INSERT ON COMPRAPAGADA_UWU;
FOR EACH ROW
DECLARE
fechaPago DATE;
    fechaActual DATE;
BEGIN
    fechaActual := CURRENT_DATE();
SELECT fechaPago INTO FECHAPAGOS FROM COMPRAPAGADA_UWU
                                          IF (fechaPago=fechaActual) THEN
        raise_application_error(-20800, 'No se puede pagar una compra si ya ha pagado una hoy');
END IF;
END;