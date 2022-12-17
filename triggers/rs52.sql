CREATE OR REPLACE TRIGGER eliminarColab_Patro
    BEFORE
    DELETE ON PATROCINA_COLABORA_UWU
    FOR EACH ROW
DECLARE
    fechaInicioEd DATE;
    fechaActual DATE;
BEGIN
    fechaActual := CURRENT_DATE();
    SELECT FechaInicio INTO fechaInicioEd FROM EDICIONES_UWU WHERE CodEdicion=:old.CodEdicion;
    IF fechaInicioEd < fechaActual THEN
        raise_application_error(-20510, :old.CIF || ' no se puede eliminar un colaborador si la edición ya ha empezado');
    END IF;
END;
