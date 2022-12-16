-- PARA PROBARLO
-- ALTER SESSION SET NLS_DATE_FORMAT = 'DD/MM/YYYY HH24:MI:SS';
-- select * from partidos_l_v_ta_tp_uwu;
-- set serveroutput on;
-- UPDATE partidos_l_v_ta_tp_uwu set Fecha = to_date('20/12/2021 19:31:00', 'DD/MM/YYYY HH24:MI:SS') where CodP = 3;
-- INSERT INTO partidos_l_v_ta_tp_uwu values(4, to_date('20/12/2021 22:30:00', 'DD/MM/YYYY HH24:MI:SS'),'11111111A', '11111111B', 1, '11111111C', '11111111D', 1, '33333333A', 2);

create or replace TRIGGER fechaPartido
    FOR INSERT OR UPDATE ON PARTIDOS_L_V_TA_TP_UWU
    COMPOUND TRIGGER

    -- Declarative part (optional)
    TYPE RecPartido IS RECORD(CodP Number(4), Fecha DATE, NumPista NUMBER(2));
    TYPE Lista IS TABLE OF RecPartido;
    -- Variables declared here have firing-statement duration.
    CURSOR curPartidos IS SELECT * FROM PARTIDOS_L_V_TA_TP_UWU;
    listaFechas Lista := Lista();
    fechaPartido DATE;
    diferenciaSeg NUMBER;
    diferenciaSegMinima NUMBER := 3*60*60;

    BEFORE STATEMENT IS
    BEGIN
        FOR registroPartido IN curPartidos LOOP
            listaFechas.extend();
            listaFechas(listaFechas.LAST).CodP:= registroPartido.CodP;
            listaFechas(listaFechas.LAST).Fecha := registroPartido.Fecha;
            listaFechas(listaFechas.LAST).NumPista := registroPartido.NumPista;
        END LOOP;
    END BEFORE STATEMENT;

    BEFORE EACH ROW IS
    BEGIN
        fechaPartido := :new.Fecha;

        FOR i IN listaFechas.FIRST .. listaFechas.LAST LOOP
            IF :NEW.NumPista = listaFechas(i).NumPista THEN
                diferenciaSeg := ROUND(ABS(fechaPartido - listaFechas(i).Fecha) * 24*60*60);
                --dbms_output.put_line('CodP ' || listaFechas(i).CodP || ' Fecha ' || listaFechas(i).Fecha);
                --dbms_output.put_line('DIFERENCIA ' || diferenciaSeg || ' vs ' || diferenciaSegMinima);

                IF diferenciaSeg < diferenciaSegMinima AND (INSERTING OR :OLD.CodP <> listaFechas(i).CodP) THEN
                    raise_application_error(-20750, 'Debe existir una diferencia de 3 horas entre las fechas de cada partido en la misma pista. ' ||
                        ' [Partido Conflico: ' || listaFechas(i).CodP || ']  [Fecha Conflicto: ' || listaFechas(i).Fecha || ']');
                END IF;
            END IF;
        END LOOP;
    END BEFORE EACH ROW;
END fechaPartido;
