--select * from compra_realiza_enedicion_uwu;
--select * from comprafinalizada_uwu;
--select * from comprapagada_uwu;
--insert into compra_realiza_enedicion_uwu values(10, CURRENT_DATE, '44444444A', 1);
--insert into compra_realiza_enedicion_uwu values(11, CURRENT_DATE, '44444444A', 1);
--insert into comprafinalizada_uwu values(10, 10, CURRENT_DATE);
--insert into comprafinalizada_uwu values(11, 11, CURRENT_DATE);
--insert into comprapagada_uwu values(10, 10, 10, to_date('20/12/2022', 'DD/MM/YYYY'));
--insert into comprapagada_uwu values(11, 11, 11, to_date('20/12/2022', 'DD/MM/YYYY'));
--insert into comprapagada_uwu values(11, 11, 11, to_date('21/12/2022', 'DD/MM/YYYY'));

/*

FORMA 1
CREATE OR REPLACE TRIGGER compraPagadaFecha
	BEFORE
	INSERT ON COMPRAPAGADA_UWU
    FOR EACH ROW

DECLARE
    DNI_ESPECTADOR CHAR(9);
    DNI CHAR(9);
    fechaPagada CHAR(10);
    CURSOR curPagadas IS SELECT * FROM COMPRAPAGADA_UWU;
    registroPagada curPagadas%ROWTYPE;
BEGIN
    SELECT DNI INTO DNI_ESPECTADOR FROM COMPRA_REALIZA_ENEDICION_UWU WHERE CodCompra = :NEW.CodCompra;
    fechaPagada := to_char(:NEW.FechaPagos, 'DD/MM/YYYY');

    -- Este método es ineficiente si tenemos muchísimas compras pagadas. Es recomendada la siguiente alternativa para el futuro:
    --  <> Obtener el DNI del espectador
    --  <> Obtener los CodCompra iniciados por el espectador
    --  <> Buscar en COMPRAPAGADA_UWU las pagadas que coincidan con ese CodCompra
    --  <> Hacer la comprobación de la fecha solo con esas tuplas
    -- Por ahora dejamos esta alternativa que es más sencilla para no complicar el trigger
    FOR registroPagada IN curPagadas LOOP
        SELECT DNI INTO DNI FROM COMPRA_REALIZA_ENEDICION_UWU WHERE CodCompra = registroPagada.CodCompra;

        IF DNI = DNI_ESPECTADOR AND to_char(registroPagada.FechaPagos, 'DD/MM/YYYY') = fechaPagada THEN
            raise_application_error(-20575, 'El espectador ' || DNI_ESPECTADOR || ' ya ha pagado una compra hoy: ' || registroPagada.CodCompraPagada);
        END IF;
    END LOOP;
END compraPagadaFecha;
 */

CREATE OR REPLACE TRIGGER compraPagadaFecha
	BEFORE
	INSERT ON COMPRAPAGADA_UWU
    FOR EACH ROW
DECLARE
    DNI_ESPECTADOR CHAR(9);
    fechaPagada CHAR(10);
    numPagadas INTEGER;
BEGIN
    SELECT DNI INTO DNI_ESPECTADOR FROM COMPRA_REALIZA_ENEDICION_UWU WHERE CodCompra = :NEW.CodCompra;
    fechaPagada := to_char(:NEW.FechaPagos, 'DD/MM/YYYY');

    SELECT COUNT(*) INTO numPagadas FROM (SELECT CodCompra FROM COMPRA_REALIZA_ENEDICION_UWU where DNI = DNI_ESPECTADOR) CRE,
    COMPRAPAGADA_UWU CP WHERE CRE.CodCompra = CP.CodCompra AND to_char(CP.FechaPagos, 'DD/MM/YYYY') = fechaPagada;

    IF numPagadas > 0 THEN
        raise_application_error(-20575, 'El espectador ' || DNI_ESPECTADOR || ' ya ha pagado una compra hoy');
    END IF;
END compraPagadaFecha;