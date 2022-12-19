CREATE OR REPLACE TRIGGER puntuacionParejas
FOR INSERT OR UPDATE OF Puntuacion ON PARTICIPA_UWU
COMPOUND TRIGGER

   -- Declarative Section (optional)
    TYPE Puntuaciones IS RECORD(Puntuacion Number(4), CodEdicion Number(4));
    TYPE Lista IS TABLE OF Puntuaciones;
    -- Variables declared here have firing-statement duration.
    listaPuntuaciones Lista := Lista();
    CURSOR cursor_puntuacion IS SELECT Puntuacion, CodEdicion FROM PARTICIPA_UWU;
    registroPuntuacion cursor_puntuacion%ROWTYPE;

     --Executed before DML statement
    BEFORE STATEMENT IS
    BEGIN
        FOR registroPuntuacion IN cursor_puntuacion LOOP
            listaPuntuaciones.extend();
            listaPuntuaciones(listaPuntuaciones.LAST).Puntuacion:= registroPuntuacion.Puntuacion;
            listaPuntuaciones(listaPuntuaciones.LAST).CodEdicion:= registroPuntuacion.CodEdicion;
        END LOOP;
    END BEFORE STATEMENT;

     --Executed before each row change- :NEW, :OLD are available
    BEFORE EACH ROW IS
    BEGIN
        FOR i IN listaPuntuaciones.FIRST .. listaPuntuaciones.LAST LOOP
                IF (:NEW.Puntuacion <> 0 and :NEW.Puntuacion = listaPuntuaciones(i).Puntuacion and :NEW.CodEdicion = listaPuntuaciones(i).CodEdicion) THEN
                        raise_application_error(-20500, ' La puntuación está repetida ');
                END IF;
        END LOOP;
    NULL;
    END BEFORE EACH ROW;

END puntuacionParejas;