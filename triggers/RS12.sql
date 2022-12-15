CREATE OR REPLACE PROCEDURE RS12 AS
	CURSOR parejasAsignadas IS SELECT * FROM PAREJA_ENTRENADA_UWU;
    registro_parejas parejasAsignadas%ROWTYPE;
    cuantos INTEGER;

BEGIN
    FOR registro_parejas IN parejasAsignadas LOOP
        /* Tengo que ver si en una edición esa pareja ya tiene entrenador */
        SELECT COUNT(*) INTO cuantos FROM PAREJA_ENTRENADA_UWU WHERE CodEdicion = registro_parejas.CodEdicion, DNI_J1 = registro_parejas.DNI_J1, DNI_J2 = registro_parejas.DNI_J2;
        IF (cuantos > 0) THEN
            /* ¿Qué hacer cuando se encuentra que ya hay un entrenador asignado a la pareja? */
            raise_application_error(-20500, ' La pareja ya tiene un entrenador ');
        END IF;
    END LOOP;

EXCEPTION
	WHEN OTHERS THEN
		IF (parejasAsignadas%ISOPEN) THEN
			CLOSE parejasAsignadas;
        END IF;
END;