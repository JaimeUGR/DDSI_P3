CREATE OR REPLACE TRIGGER rs12
-- FOR INSERT OR UPDATE [OF column] ON PARTICIPA
FOR INSERT OR UPDATE [OF column] ON PARTICIPA
COMPOUND TRIGGER
   -- Declarative Section (optional)
    CURSOR cursor_puntuacion IS SELECT Puntuacion FROM PARTICIPA_UWU;
    registro_puntuacion cursor_puntuacion%ROWTYPE;
   -- Variables declared here have firing-statement duration.
    hay_igual INTEGER;
     --Executed before DML statement
     BEFORE STATEMENT IS
BEGIN
    FOR registro_puntuacion IN puntuacion LOOP
        SELECT Puntuacion FROM PARTICIPA_UWU WHERE CodEdicion = registro_parejas.CodEdicion;
        IF (puntuacion <> 0 and Puntuacion = puntuacion) THEN
            raise_application_error(-20500, ' La puntuación está repetida ');
        END IF;
    END LOOP;
END BEFORE STATEMENT;

     --Executed before each row change- :NEW, :OLD are available
     BEFORE EACH ROW IS
BEGIN
NULL;
END BEFORE EACH ROW;

END compound_trigger_name;




























CREATE OR REPLACE TRIGGER puntuacion
	BEFORE
	INSERT ON PARTICIPA_UWU
	FOR EACH ROW
DECLARE
    punt INTEGER;
BEGIN
    SELECT COUNT(*) INTO punt FROM PARTICIPA_UWU WHERE Puntuacion = :NEW.Puntuacion AND CodEdicion = :NEW.CodEdicion;
    IF (:NEW.Puntuacion <> 0 and punt > 0) THEN
            raise_application_error(-20500, ' La puntuación está repetida ');
    END IF;
END;






















CREATE OR REPLACE TRIGGER RS12

	CURSOR puntuacion IS SELECT * FROM PARTICIPA_UWU WHERE;
    registro_puntuacion puntuacion%ROWTYPE;

BEGIN
    FOR registro_puntuacion IN puntuacion LOOP
        SELECT Puntuacion FROM PARTICIPA_UWU WHERE CodEdicion = registro_parejas.CodEdicion;
        IF (puntuacion <> 0 and Puntuacion = puntuacion) THEN
            raise_application_error(-20500, ' La puntuación está repetida ');
        END IF;
    END LOOP;

EXCEPTION
	WHEN OTHERS THEN
		IF (puntuacion%ISOPEN) THEN
			CLOSE puntuacion;
        END IF;
END;