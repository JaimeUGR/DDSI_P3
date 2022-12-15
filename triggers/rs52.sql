CREATE OR REPLACE TRIGGER eliminarColaborador
    BEFORE
        DELETE ON PATROCINA_COLABORA_UWU
    FOR EACH ROW
DECLARE
    fechainicioe DATE;
    fechaactual DATE;
BEGIN
    fechaactual := CURRENT_DATE();
    SELECT FechaInicio INTO fechainicioe FROM EDICIONES_UWU WHERE CodEdicion=:old.CodEdicion;
    IF fechainicioe < fechaactual THEN
        raise_application_error(-20510, :old.CIF || ' no se puede eliminar un colaborador si el torneo ya ha empezado');
    END IF;
END;
/