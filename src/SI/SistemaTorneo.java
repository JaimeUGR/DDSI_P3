package SI;

import java.nio.file.Files;
import java.nio.file.Path;
import java.sql.*;
import java.text.SimpleDateFormat;

import ConsoleColors.ConsoleColors;
import ConsoleErrors.ConsoleError;
import Secret.SecretDB;
import oracle.jdbc.OracleTypes;

public class SistemaTorneo
{
	private Connection con;
	private boolean tableCreation = true;
	private boolean tableFill = true;


	public SistemaTorneo()
	{
		System.out.println(ConsoleColors.PURPLE + "<> Iniciando Sistema <>" + ConsoleColors.RESET);

		try
		{
			Class.forName("oracle.jdbc.driver.OracleDriver");
			con = DriverManager.getConnection(
					"jdbc:oracle:thin:@oracle0.ugr.es:1521/practbd.oracle0.ugr.es",
					SecretDB.user,
					SecretDB.pass
			);

			con.setAutoCommit(false);

			System.out.println(ConsoleColors.CYAN + "Conexión establecida con la BD" + ConsoleColors.RESET);

			Inicializar();
		}
		catch (Exception e)
		{
			ConsoleError.MostrarError(e.toString());
			con = null;
		}
	}

	private void Inicializar()
	{
		// Iniciar las tablas
		CrearTablas();

		// Rellenado por defecto
		PreRellenarTablas();

		// Cargar los triggers
		CargarTriggers();
	}

	private void CrearTablas()
	{
		if (!tableCreation)
			return;

		try
		{

			Statement stm = con.createStatement();
			Tablas[] tablas = Tablas.values();

			System.out.println(ConsoleColors.WHITE_BOLD);

			for (int i = tablas.length-1; i>= 0; i--)
			{
				Tablas t = tablas[i];

				if (ExisteTabla(t.toString()))
				{
					System.out.println("La tabla " + t.toString() + " existía");
					stm.executeUpdate("DROP TABLE " + t);
				}
			}

			System.out.println(ConsoleColors.RESET);

			// Ediciones
			stm.executeUpdate("CREATE TABLE " + Tablas.EDICIONES_UWU + "( " +
					"CodEdicion NUMBER(4) CONSTRAINT EDICIONESPK PRIMARY KEY, " +
					"FechaInicio DATE" +
					")");

			// Jugadores
			stm.executeUpdate("CREATE TABLE " + Tablas.JUGADOR_UWU + "( " +
					"DNI CHAR(9) CONSTRAINT JUGADORPK PRIMARY KEY, " +
					"Fecha DATE," +
					"Nombre VARCHAR(50)," +
					"Apellidos VARCHAR(100)," +
					"Sexo VARCHAR(15) CONSTRAINT NOSEX NOT NULL CONSTRAINT BADSEX CHECK(Sexo='M' OR Sexo='F')," +
					"CONSTRAINT DNI_JUG_FORMATO CHECK(REGEXP_LIKE(DNI, '^[0-9][0-9]{7}[A-Z]$'))" +
					")");

			// Comprobar que no se repite cambiar pdf TODO
			stm.executeUpdate("CREATE TABLE " + Tablas.PAREJA_UWU + "( " +
					"DNI_J1 CONSTRAINT PAREJA_FK_JUGADOR1 REFERENCES " + Tablas.JUGADOR_UWU + "(DNI)," +
					"DNI_J2 CONSTRAINT PAREJA_FK_JUGADOR2 REFERENCES " + Tablas.JUGADOR_UWU + "(DNI)," +
					"CONSTRAINT PAREJA_PK PRIMARY KEY(DNI_J1,DNI_J2)" +
					")");

			// Puntuacion no se comprueba rs TODO
			stm.executeUpdate("CREATE TABLE " + Tablas.PARTICIPA_UWU + "( " +
					"DNI_J1 CHAR(9)," +
					"DNI_J2 CHAR(9)," +
					"CodEdicion CONSTRAINT PARTICIPA_FK_EDICIONES REFERENCES " + Tablas.EDICIONES_UWU +  "(CodEdicion), " +
					"Puntuacion NUMBER(5) DEFAULT 0 CONSTRAINT BADSCORE CHECK(Puntuacion>=0)," +
					"CONSTRAINT PARTICIPA_FK_PAREJA FOREIGN KEY (DNI_J1,DNI_J2) REFERENCES " + Tablas.PAREJA_UWU + "(DNI_J1, DNI_J2)," +
					"CONSTRAINT PARTICIPA_PK PRIMARY KEY(DNI_J1,DNI_J2,CodEdicion)" +
					")");

			stm.executeUpdate("CREATE TABLE " + Tablas.ENTRENADOR_UWU + "( " +
					"DNI CHAR(9) CONSTRAINT ENTRENADORPK PRIMARY KEY, " +
					"Fecha DATE," +
					"Nombre VARCHAR(50)," +
					"Apellidos VARCHAR(100)," +
					"Sexo VARCHAR(15) CONSTRAINT NOSEX_ENTRENADOR NOT NULL CONSTRAINT BADSEX_ENTRENADOR CHECK(Sexo='M' OR Sexo='F')," +
					"CONSTRAINT DNI_ENT_FORMATO CHECK(REGEXP_LIKE(DNI, '^[0-9][0-9]{7}[A-Z]$'))" +
					")");

			stm.executeUpdate("CREATE TABLE " + Tablas.PAREJA_ENTRENADA_UWU + "( " +
					"DNI_J1 CHAR(9)," +
					"DNI_J2 CHAR(9)," +
					"CodEdicion NUMBER(4), " +
					"DNI_E CONSTRAINT PAREJA_ENTRENADA_FK_ENTRENADOR REFERENCES " + Tablas.ENTRENADOR_UWU + "(DNI)," +
					"CONSTRAINT PAREJA_ENTRENADA_FK_PARTICIPA FOREIGN KEY (DNI_J1,DNI_J2,CodEdicion) REFERENCES " + Tablas.PARTICIPA_UWU + "(DNI_J1,DNI_J2,CodEdicion)," +
					"CONSTRAINT PAREJA_ENTRANADA_PK PRIMARY KEY(DNI_J1,DNI_J2,CodEdicion)" +
					")");



			// Espectadores y Entradas Check el @ TODO
			stm.executeUpdate("CREATE TABLE " + Tablas.ESPECTADOR_UWU + "( " +
					"DNI CHAR(9) CONSTRAINT ESPECTADORPK PRIMARY KEY, " +
					"Nombre VARCHAR(50)," +
					"Apellidos VARCHAR(100)," +
					"CorreoElectronico VARCHAR(80) CONSTRAINT USEDMAIL UNIQUE CONSTRAINT NOMAIL NOT NULL ," +
					"Contrasenia VARCHAR(30) CONSTRAINT NOPASSWD NOT NULL," +
					"CONSTRAINT DNI_ESP_FORMATO CHECK(REGEXP_LIKE(DNI, '^[0-9][0-9]{7}[A-Z]$'))," +
					"CONSTRAINT EMAIL_ESP_FORMATO CHECK(REGEXP_LIKE(CorreoElectronico, '^[a-zA-Z][a-zA-Z0-9\\.]*@[a-zA-Z0-9]+\\.[a-zA-Z]{2,4}$'))" +
					")");

			stm.executeUpdate("CREATE TABLE " + Tablas.COMPRA_REALIZA_ENEDICION_UWU + "( " +
					"CodCompra NUMBER(9) CONSTRAINT COMPRA_PK PRIMARY KEY, " +
					"FechaInicio DATE," +
					"DNI CONSTRAINT NOTNULL_COMPRA_ESPECTADOR NOT NULL CONSTRAINT COMPRA_FK_ESPECTADOR REFERENCES " + Tablas.ESPECTADOR_UWU + "(DNI), " +
					"CodEdicion CONSTRAINT NOTNULL_COMPRA_EDI NOT NULL CONSTRAINT COMPRA_FK_EDICIONES REFERENCES " + Tablas.EDICIONES_UWU + "(CodEdicion)" +
					")");

			stm.executeUpdate("CREATE TABLE " + Tablas.COMPRAFINALIZADA_UWU + "( " +
					"CodCompra NUMBER(9) CONSTRAINT COMPRAFINALIZADA_FK_COMPRA REFERENCES " + Tablas.COMPRA_REALIZA_ENEDICION_UWU + "(CodCompra) CONSTRAINT COMPRAFINALIZADA_PK PRIMARY KEY, " +
					"CodCompraFinalizada NUMBER(9) CONSTRAINT COMPRAFINALIZADA_CCF_NN NOT NULL, " +
					"FechaFinalizacion DATE," +
					"CONSTRAINT COMPRA_FINALIZADA_UNIQUE UNIQUE (CodCompra, CodCompraFinalizada)" +
					")");

			stm.executeUpdate("CREATE TABLE " + Tablas.COMPRAPAGADA_UWU + "( " +
					"CodCompra NUMBER(9)," +
					"CodCompraFinalizada NUMBER(9)," +
					"CodCompraPagada NUMBER(9) NOT NULL, " +
					"FechaPagos DATE," +
					"CONSTRAINT COMPRA_PAGADA_UNIQUE UNIQUE (CodCompra, CodCompraFinalizada, CodCompraPagada), " +
					"CONSTRAINT COMPRAPAG_COMPRAF_FK FOREIGN KEY (CodCompra,CodCompraFinalizada) REFERENCES " + Tablas.COMPRAFINALIZADA_UWU + "(CodCompra,CodCompraFinalizada)," +
					"CONSTRAINT COMPRAPAGADA_PK PRIMARY KEY (CodCompra,CodCompraFinalizada)" +
					")");

			stm.executeUpdate("CREATE TABLE " + Tablas.ENTRADA_UWU + "( " +
					"CodEntrada NUMBER(2) PRIMARY KEY," +
					"Tipo VARCHAR(50)" +
					")");

			stm.executeUpdate("CREATE TABLE " + Tablas.EMITIDAEN_UWU + "( " +
					"CodEntrada NUMBER(2) CONSTRAINT EMITIDAEN_FK_ENTRADA REFERENCES " + Tablas.ENTRADA_UWU + "(CodEntrada)," +
					"CodEdicion CONSTRAINT NN_ENTRADA_EDI NOT NULL CONSTRAINT EMITIDAEN_FK_EDICIONES REFERENCES " + Tablas.EDICIONES_UWU + "(CodEdicion)," +
					"Precio NUMBER(12,2) CONSTRAINT BADPRICE CHECK(Precio>=0), " +
					"CantidadEmitida NUMBER(4) CONSTRAINT BADQUANTITY_ENTRADA CHECK(CantidadEmitida>=0)," +
					"CONSTRAINT EMITIDAEN_PK PRIMARY KEY (CodEntrada, CodEdicion)" +
					")");

			stm.executeUpdate("CREATE TABLE " + Tablas.TIENEENTRADAS_UWU + "( " +
					"CodCompra CONSTRAINT TIENEENTRADAS_FK_COMPRA REFERENCES " + Tablas.COMPRA_REALIZA_ENEDICION_UWU + "(CodCompra)," +
					"CodEntrada CONSTRAINT TIENEENTRADAS_FK_ENTRADA REFERENCES " + Tablas.ENTRADA_UWU + "(CodEntrada)," +
					"Cantidad NUMBER(4) CONSTRAINT BADQUANTITY CHECK(Cantidad>=0)," +
					"CONSTRAINT TIENEENTRADAS_PK PRIMARY KEY (CodCompra,CodEntrada)" +
					")");


			// Arbitros y Ofertas
			stm.executeUpdate("CREATE TABLE " + Tablas.ARBITRO_UWU + "( " +
					"DNI CHAR(9) CONSTRAINT ARBITROPK PRIMARY KEY, " +
					"Nombre VARCHAR(50)," +
					"Apellidos VARCHAR(100)," +
					"FechaNac DATE," +
					"Sexo VARCHAR(15) CONSTRAINT NOSEX_ARBITRO NOT NULL CONSTRAINT BADSEX_ARBITRO CHECK(Sexo='M' OR Sexo='F')," +
					"CONSTRAINT DNI_ARB_FORMATO CHECK(REGEXP_LIKE(DNI, '^[0-9][0-9]{7}[A-Z]$'))" +
					")");

			stm.executeUpdate("CREATE TABLE " + Tablas.OFERTAS_RECIBE_HECHA_UWU + "( " +
					"CodOferta NUMBER(6) CONSTRAINT OFERTAPK PRIMARY KEY, " +
					"CantidadDinero NUMBER(9,2)," +
					"EstadoOferta VARCHAR(20) DEFAULT 'PENDIENTE' CHECK(EstadoOferta IN ('ACEPTADA', 'RECHAZADA', 'PENDIENTE'))," +
					"FechaOferta DATE," +
					"FechaAcep_Rech DATE," +
					"DNIArb CONSTRAINT NN_OFERTAS_ARB NOT NULL CONSTRAINT OFERTAS_ARBITRO_FK REFERENCES " + Tablas.ARBITRO_UWU + "(DNI)," +
					"CodEdicion CONSTRAINT NN_OFERTAS_EDI NOT NULL CONSTRAINT OFERTAS_EDICIONES_FK REFERENCES " + Tablas.EDICIONES_UWU + "(CodEdicion)" +
					")");

			stm.executeUpdate("CREATE TABLE " + Tablas.CONTRAOFERTAS_UWU + "( " +
					"CodContraoferta NUMBER(6) CONSTRAINT CONTRAOFERTASPK PRIMARY KEY, " +
					"CantidadDinero NUMBER(9,2)," +
					"EstadoContraoferta VARCHAR(20) DEFAULT 'PENDIENTE' CHECK(EstadoContraoferta IN ('ACEPTADA', 'RECHAZADA', 'PENDIENTE'))," +
					"FechaContraoferta DATE," +
					"FechaAcep_Rech DATE" +
					")");

			stm.executeUpdate("CREATE TABLE " + Tablas.TIENE_UWU + "( " +
					"CodOferta CONSTRAINT TIENE_FK_OFERTA REFERENCES " + Tablas.OFERTAS_RECIBE_HECHA_UWU + "(CodOferta) CONSTRAINT TIENE_PK PRIMARY KEY, " +
					"CodContraoferta CONSTRAINT TIENE_FK_CONTRAOFERTA REFERENCES " + Tablas.CONTRAOFERTAS_UWU + "(CodContraoferta) CONSTRAINT UNIQUE_CONTRAOFERTA UNIQUE CONSTRAINT NOTNULL_CONTRAOFERTA NOT NULL" +
					")");

			// Pistas
			stm.executeUpdate("CREATE TABLE " + Tablas.PISTAS_UWU + "( " +
					"NumPista NUMBER(2) CONSTRAINT PISTASPK PRIMARY KEY, " +
					"Nombre VARCHAR(30)," +
					"Capacidad NUMBER(4) CONSTRAINT BADCAPACITY CHECK(Capacidad>=0)" +
					")");

			// Partidos
			stm.executeUpdate("CREATE TABLE " + Tablas.PARTIDOS_L_V_TA_TP_UWU + "( " +
					"CodP NUMBER(8) CONSTRAINT PARTIDOPK PRIMARY KEY, " +
					"Fecha DATE," +
					"DNI_J1 CHAR(9) CONSTRAINT NN_PARTIDOS_J1 NOT NULL," +
					"DNI_J2 CHAR(9) CONSTRAINT NN_PARTIDOS_J2 NOT NULL," +
					"CodEdicion1 NUMBER(4), " +
					"CONSTRAINT PARTIDO_PARTICIPA_FK FOREIGN KEY (DNI_J1,DNI_J2,CodEdicion1) REFERENCES " + Tablas.PARTICIPA_UWU + "(DNI_J1,DNI_J2,CodEdicion)," +
					"DNI_J3 CHAR(9) CONSTRAINT NN_PARTIDOS_J3 NOT NULL," +
					"DNI_J4 CHAR(9) CONSTRAINT NN_PARTIDOS_J4 NOT NULL," +
					"CodEdicion2 NUMBER(4), " +
					"CONSTRAINT PARTIDO_PARTICIPA_FK2 FOREIGN KEY (DNI_J3,DNI_J4,CodEdicion2) REFERENCES " + Tablas.PARTICIPA_UWU + "(DNI_J1,DNI_J2,CodEdicion)," +
					"DNI_Arb CONSTRAINT NN_PARTIDOS_ARB NOT NULL CONSTRAINT PARTIDO_FK_ARBITRO REFERENCES " + Tablas.ARBITRO_UWU + "(DNI)," +
					"NumPista CONSTRAINT NN_PARTIDOS_PIS NOT NULL CONSTRAINT PARTIDO_FK_PISTAS REFERENCES " + Tablas.PISTAS_UWU + "(NumPista)," +
					"CONSTRAINT PARTIDO_EDPAREJ_IG CHECK(CodEdicion1 = CodEdicion2)" +
					")");

			// Empresas
			stm.executeUpdate("CREATE TABLE " + Tablas.EMPRESA_UWU + "( " +
					"CIF CHAR(9) CONSTRAINT EMPRESAPK PRIMARY KEY, " +
					"Nombre VARCHAR(50)," +
					"PersonaContacto VARCHAR(100)," +
					"Email VARCHAR(80)," +
					"Telefono VARCHAR(15)," +
					"CONSTRAINT CIF_EMP_FORMATO CHECK(REGEXP_LIKE(CIF, '^[A-Z][0-9]{7}[A-Z0-9]$'))," +
					"CONSTRAINT EMAIL_EMP_FORMATO CHECK(REGEXP_LIKE(Email, '^[a-zA-Z][a-zA-Z0-9\\.]*@[a-zA-Z0-9]+\\.[a-zA-Z]{2,4}$'))," +
					"CONSTRAINT TELEF_EMP_FORMATO CHECK(REGEXP_LIKE(Telefono, '^\\+?[0-9]{10}[0-9]$'))" +
					")");

			stm.executeUpdate("CREATE TABLE " + Tablas.PATROCINA_COLABORA_UWU + "( " +
					"CIF CONSTRAINT PATROCINA_FK_EMPRESA REFERENCES " + Tablas.EMPRESA_UWU + "(CIF), " +
					"CodEdicion CONSTRAINT PATROCINA_FK_EDICIONES REFERENCES " + Tablas.EDICIONES_UWU + "(CodEdicion)," +
					"EsPatrocinador NUMBER(1) CONSTRAINT BADESPATROCINADOR CHECK (EsPatrocinador IN (0,1))," +
					"Dinero NUMBER(9,2) CONSTRAINT BADMONEY CHECK (Dinero>=0)," +
					"CONSTRAINT PATROCINA_COLABORA_PK PRIMARY KEY (CIF,CodEdicion)" +
					")");

			con.commit();
		}
		catch (Exception e)
		{
			Rollback();
			System.out.println(e.getCause().toString());
			ConsoleError.MostrarError(e.toString());
		}
	}

	private void PreRellenarTablas()
	{
		if (!tableFill)
			return;

		SimpleDateFormat dateFormatBasic = new SimpleDateFormat("dd/MM/yyyy");
		SimpleDateFormat dateFormatExtended = new SimpleDateFormat("dd/MM/yyyy HH:mm:ss");

		try
		{
			PreparedStatement pstm;
			String query;

			// EDICIONES
			query = "INSERT INTO " + Tablas.EDICIONES_UWU + " VALUES(?, ?)";
			pstm = con.prepareStatement(query);

			pstm.setObject(1, 1, oracle.jdbc.OracleTypes.NUMBER);
			pstm.setDate(2, new java.sql.Date(dateFormatBasic.parse("15/12/2021").getTime()));
			pstm.addBatch();

			pstm.setObject(1, 2, oracle.jdbc.OracleTypes.NUMBER);
			pstm.setDate(2, new java.sql.Date(dateFormatBasic.parse("15/12/2022").getTime()));
			pstm.addBatch();

			pstm.executeBatch();

			//JUGADOR
			query = "INSERT INTO " + Tablas.JUGADOR_UWU + " VALUES(?, ?, ?, ?, ?)";
			pstm = con.prepareStatement(query);

			pstm.setString(1, "11111111A");
			pstm.setDate(2, new java.sql.Date(dateFormatBasic.parse("15/12/2020").getTime()));
			pstm.setString(3, "Paco");
			pstm.setString(4, "Reyna");
			pstm.setString(5, "M");
			pstm.addBatch();

			pstm.setString(1, "11111111B");
			pstm.setDate(2, new java.sql.Date(dateFormatBasic.parse("15/12/2020").getTime()));
			pstm.setString(3, "Manuela");
			pstm.setString(4, "Reyna");
			pstm.setString(5, "F");
			pstm.addBatch();

			pstm.setString(1, "11111111C");
			pstm.setDate(2, new java.sql.Date(dateFormatBasic.parse("15/12/2020").getTime()));
			pstm.setString(3, "Luis");
			pstm.setString(4, "Reyna");
			pstm.setString(5, "M");
			pstm.addBatch();

			pstm.setString(1, "11111111D");
			pstm.setDate(2, new java.sql.Date(dateFormatBasic.parse("15/12/2020").getTime()));
			pstm.setString(3, "Ivan");
			pstm.setString(4, "Reyna");
			pstm.setString(5, "F");
			pstm.addBatch();

			pstm.setString(1, "11111111E");
			pstm.setDate(2, new java.sql.Date(dateFormatBasic.parse("15/12/2020").getTime()));
			pstm.setString(3, "Angel");
			pstm.setString(4, "Reyna");
			pstm.setString(5, "F");
			pstm.addBatch();

			pstm.setString(1, "11111111F");
			pstm.setDate(2, new java.sql.Date(dateFormatBasic.parse("15/12/2020").getTime()));
			pstm.setString(3, "Giouseppe");
			pstm.setString(4, "Reyna");
			pstm.setString(5, "M");
			pstm.addBatch();

			pstm.setString(1, "11111111G");
			pstm.setDate(2, new java.sql.Date(dateFormatBasic.parse("15/12/2020").getTime()));
			pstm.setString(3, "HLIB_1");
			pstm.setString(4, "Reyna");
			pstm.setString(5, "F");
			pstm.addBatch();

			pstm.setString(1, "11111111H");
			pstm.setDate(2, new java.sql.Date(dateFormatBasic.parse("15/12/2020").getTime()));
			pstm.setString(3, "HLIB_2");
			pstm.setString(4, "Reyna");
			pstm.setString(5, "F");
			pstm.addBatch();

			pstm.executeBatch();

			//PAREJA
			query = "INSERT INTO " + Tablas.PAREJA_UWU + " VALUES(?, ?)";
			pstm = con.prepareStatement(query);

			pstm.setString(1, "11111111A");
			pstm.setString(2, "11111111B");
			pstm.addBatch();

			pstm.setString(1, "11111111C");
			pstm.setString(2, "11111111D");
			pstm.addBatch();

			pstm.setString(1, "11111111E");
			pstm.setString(2, "11111111F");
			pstm.addBatch();

			pstm.executeBatch();

			//PARTICIPA
			query = "INSERT INTO " + Tablas.PARTICIPA_UWU + " VALUES(?, ?, ?, ?)";
			pstm = con.prepareStatement(query);

			pstm.setString(1, "11111111A");
			pstm.setString(2, "11111111B");
			pstm.setObject(3, 1, OracleTypes.NUMBER);
			pstm.setObject(4, 0, OracleTypes.NUMBER);
			pstm.addBatch();

			pstm.setString(1, "11111111C");
			pstm.setString(2, "11111111D");
			pstm.setObject(3, 1, OracleTypes.NUMBER);
			pstm.setObject(4, 0, OracleTypes.NUMBER);
			pstm.addBatch();

			pstm.setString(1, "11111111E");
			pstm.setString(2, "11111111F");
			pstm.setObject(3, 1, OracleTypes.NUMBER);
			pstm.setObject(4, 0, OracleTypes.NUMBER);
			pstm.addBatch();

			pstm.setString(1, "11111111A");
			pstm.setString(2, "11111111B");
			pstm.setObject(3, 2, OracleTypes.NUMBER);
			pstm.setObject(4, 0, OracleTypes.NUMBER);
			pstm.addBatch();

			pstm.setString(1, "11111111C");
			pstm.setString(2, "11111111D");
			pstm.setObject(3, 2, OracleTypes.NUMBER);
			pstm.setObject(4, 0, OracleTypes.NUMBER);
			pstm.addBatch();

			pstm.executeBatch();

			//ENTRENADOR
			query = "INSERT INTO " + Tablas.ENTRENADOR_UWU + " VALUES(?, ?, ?, ?, ?)";
			pstm = con.prepareStatement(query);

			pstm.setString(1, "22222222A");
			pstm.setDate(2, new java.sql.Date(dateFormatBasic.parse("15/12/2020").getTime()));
			pstm.setString(3, "Manue");
			pstm.setString(4, "Reyna");
			pstm.setString(5, "M");
			pstm.addBatch();

			pstm.setString(1, "22222222B");
			pstm.setDate(2, new java.sql.Date(dateFormatBasic.parse("15/12/2020").getTime()));
			pstm.setString(3, "Ana");
			pstm.setString(4, "Reyna");
			pstm.setString(5, "F");
			pstm.addBatch();

			pstm.executeBatch();

			//PAREJA_ENTRENADA
			query = "INSERT INTO " + Tablas.PAREJA_ENTRENADA_UWU + " VALUES(?, ?, ?, ?)";
			pstm = con.prepareStatement(query);

			pstm.setString(1, "11111111A");
			pstm.setString(2, "11111111B");
			pstm.setObject(3, 1, OracleTypes.NUMBER);
			pstm.setString(4, "22222222A");
			pstm.addBatch();

			pstm.setString(1, "11111111C");
			pstm.setString(2, "11111111D");
			pstm.setObject(3, 1, OracleTypes.NUMBER);
			pstm.setString(4, "22222222B");
			pstm.addBatch();

			pstm.executeBatch();

			//ESPECTADOR
			query = "INSERT INTO " + Tablas.ESPECTADOR_UWU + " VALUES(?, ?, ?, ?, ?)";
			pstm = con.prepareStatement(query);

			pstm.setString(1, "44444444A");
			pstm.setString(2, "Franchesco");
			pstm.setString(3, "Virgolinni");
			pstm.setString(4, "esp1@ugr.es");
			pstm.setString(5, "1234");
			pstm.addBatch();

			pstm.setString(1, "44444444B");
			pstm.setString(2, "Fran");
			pstm.setString(3, "Virgolinni");
			pstm.setString(4, "esp2@ugr.es");
			pstm.setString(5, "1234");
			pstm.addBatch();

			pstm.setString(1, "44444444C");
			pstm.setString(2, "Alverto");
			pstm.setString(3, "Virgolinni");
			pstm.setString(4, "esp3@ugr.es");
			pstm.setString(5, "1234");
			pstm.addBatch();

			pstm.setString(1, "44444444D");
			pstm.setString(2, "Jose");
			pstm.setString(3, "Virgolinni");
			pstm.setString(4, "esp4@ugr.es");
			pstm.setString(5, "1234");
			pstm.addBatch();

			pstm.executeBatch();

			//COMPRA_REALIZA_ENEDICION
			query = "INSERT INTO " + Tablas.COMPRA_REALIZA_ENEDICION_UWU + " VALUES(?, ?, ?, ?)";
			pstm = con.prepareStatement(query);

			pstm.setObject(1, 1, OracleTypes.NUMBER);
			pstm.setDate(2, new java.sql.Date(dateFormatBasic.parse("15/12/2021").getTime()));
			pstm.setString(3, "44444444A");
			pstm.setObject(4, 1, OracleTypes.NUMBER);
			pstm.addBatch();

			pstm.setObject(1, 2, OracleTypes.NUMBER);
			pstm.setDate(2, new java.sql.Date(dateFormatBasic.parse("15/12/2021").getTime()));
			pstm.setString(3, "44444444B");
			pstm.setObject(4, 1, OracleTypes.NUMBER);
			pstm.addBatch();

			pstm.setObject(1, 3, OracleTypes.NUMBER);
			pstm.setDate(2, new java.sql.Date(dateFormatBasic.parse("15/12/2021").getTime()));
			pstm.setString(3, "44444444C");
			pstm.setObject(4, 2, OracleTypes.NUMBER);
			pstm.addBatch();

			pstm.executeBatch();

			//COMPRAFINALIZADA
			query = "INSERT INTO " + Tablas.COMPRAFINALIZADA_UWU + " VALUES(?, ?, ?)";
			pstm = con.prepareStatement(query);

			pstm.setObject(1, 1, OracleTypes.NUMBER);
			pstm.setObject(2, 1, OracleTypes.NUMBER);
			pstm.setDate(3, new java.sql.Date(dateFormatBasic.parse("15/12/2021").getTime()));
			pstm.addBatch();

			pstm.setObject(1, 2, OracleTypes.NUMBER);
			pstm.setObject(2, 2, OracleTypes.NUMBER);
			pstm.setDate(3, new java.sql.Date(dateFormatBasic.parse("15/12/2021").getTime()));
			pstm.addBatch();

			pstm.executeBatch();

			// COMPRAPAGADA
			query = "INSERT INTO " + Tablas.COMPRAPAGADA_UWU + " VALUES(?, ?, ?, ?)";
			pstm = con.prepareStatement(query);

			pstm.setObject(1, 2, OracleTypes.NUMBER);
			pstm.setObject(2, 2, OracleTypes.NUMBER);
			pstm.setObject(3, 1, OracleTypes.NUMBER);
			pstm.setDate(4, new java.sql.Date(dateFormatBasic.parse("15/12/2021").getTime()));
			pstm.addBatch();

			pstm.executeBatch();

			// ENTRADA
			query = "INSERT INTO " + Tablas.ENTRADA_UWU + " VALUES(?, ?)";
			pstm = con.prepareStatement(query);

			pstm.setObject(1, 1, OracleTypes.NUMBER);
			pstm.setString(2, "Grada_1");
			pstm.addBatch();

			pstm.setObject(1, 2, OracleTypes.NUMBER);
			pstm.setString(2, "Grada_2");
			pstm.addBatch();

			pstm.executeBatch();

			// EMITIDAEN
			query = "INSERT INTO " + Tablas.EMITIDAEN_UWU + " VALUES(?, ?, ?, ?)";
			pstm = con.prepareStatement(query);

			pstm.setObject(1, 1, OracleTypes.NUMBER);
			pstm.setObject(2, 1, OracleTypes.NUMBER);
			pstm.setObject(3, 3.5, OracleTypes.NUMBER);
			pstm.setObject(4, 25, OracleTypes.NUMBER);
			pstm.addBatch();

			pstm.setObject(1, 2, OracleTypes.NUMBER);
			pstm.setObject(2, 1, OracleTypes.NUMBER);
			pstm.setObject(3, 3.5, OracleTypes.NUMBER);
			pstm.setObject(4, 25, OracleTypes.NUMBER);
			pstm.addBatch();

			pstm.setObject(1, 2, OracleTypes.NUMBER);
			pstm.setObject(2, 2, OracleTypes.NUMBER);
			pstm.setObject(3, 3.5, OracleTypes.NUMBER);
			pstm.setObject(4, 25, OracleTypes.NUMBER);
			pstm.addBatch();

			pstm.executeBatch();

			// TIENEENTRADAS
			query = "INSERT INTO " + Tablas.TIENEENTRADAS_UWU + " VALUES(?, ?, ?)";
			pstm = con.prepareStatement(query);

			pstm.setObject(1, 1, OracleTypes.NUMBER);
			pstm.setObject(2, 1, OracleTypes.NUMBER);
			pstm.setObject(3, 5, OracleTypes.NUMBER);
			pstm.addBatch();

			pstm.setObject(1, 1, OracleTypes.NUMBER);
			pstm.setObject(2, 2, OracleTypes.NUMBER);
			pstm.setObject(3, 5, OracleTypes.NUMBER);
			pstm.addBatch();

			pstm.setObject(1, 2, OracleTypes.NUMBER);
			pstm.setObject(2, 1, OracleTypes.NUMBER);
			pstm.setObject(3, 1, OracleTypes.NUMBER);
			pstm.addBatch();

			pstm.setObject(1, 3, OracleTypes.NUMBER);
			pstm.setObject(2, 2, OracleTypes.NUMBER);
			pstm.setObject(3, 4, OracleTypes.NUMBER);
			pstm.addBatch();

			pstm.executeBatch();

			// ARBITRO
			query = "INSERT INTO " + Tablas.ARBITRO_UWU + " VALUES(?, ?, ?, ?, ?)";
			pstm = con.prepareStatement(query);

			pstm.setString(1, "33333333A");
			pstm.setString(2, "Charles");
			pstm.setString(3, "Mein");
			pstm.setDate(4, new java.sql.Date(dateFormatBasic.parse("15/12/2002").getTime()));
			pstm.setString(5, "M");
			pstm.addBatch();

			pstm.setString(1, "33333333B");
			pstm.setString(2, "Julia");
			pstm.setString(3, "Mein");
			pstm.setDate(4, new java.sql.Date(dateFormatBasic.parse("15/12/2002").getTime()));
			pstm.setString(5, "F");
			pstm.addBatch();

			pstm.setString(1, "33333333C");
			pstm.setString(2, "Paz");
			pstm.setString(3, "Mein");
			pstm.setDate(4, new java.sql.Date(dateFormatBasic.parse("15/12/2002").getTime()));
			pstm.setString(5, "F");
			pstm.addBatch();

			pstm.executeBatch();

			// OFERTAS_RECIBE_HECHA
			query = "INSERT INTO " + Tablas.OFERTAS_RECIBE_HECHA_UWU + " VALUES(?, ?, ?, ?, ?, ?, ?)";
			pstm = con.prepareStatement(query);

			pstm.setObject(1, 1, OracleTypes.NUMBER);
			pstm.setObject(2, 500, OracleTypes.NUMBER);
			pstm.setString(3, "ACEPTADA");
			pstm.setDate(4, new java.sql.Date(dateFormatBasic.parse("10/12/2021").getTime()));
			pstm.setDate(5, new java.sql.Date(dateFormatBasic.parse("12/12/2021").getTime()));
			pstm.setString(6, "33333333A");
			pstm.setObject(7, 1, OracleTypes.NUMBER);
			pstm.addBatch();

			pstm.setObject(1, 2, OracleTypes.NUMBER);
			pstm.setObject(2, 250, OracleTypes.NUMBER);
			pstm.setString(3, "PENDIENTE");
			pstm.setDate(4, new java.sql.Date(dateFormatBasic.parse("10/12/2021").getTime()));
			pstm.setNull(5, OracleTypes.NULL);
			pstm.setString(6, "33333333B");
			pstm.setObject(7, 1, OracleTypes.NUMBER);
			pstm.addBatch();

			pstm.setObject(1, 3, OracleTypes.NUMBER);
			pstm.setObject(2, 250, OracleTypes.NUMBER);
			pstm.setString(3, "RECHAZADA");
			pstm.setDate(4, new java.sql.Date(dateFormatBasic.parse("10/12/2021").getTime()));
			pstm.setDate(5, new java.sql.Date(dateFormatBasic.parse("12/12/2021").getTime()));
			pstm.setString(6, "33333333C");
			pstm.setObject(7, 1, OracleTypes.NUMBER);
			pstm.addBatch();

			pstm.executeBatch();

			// CONTRAOFERTAS
			query = "INSERT INTO " + Tablas.CONTRAOFERTAS_UWU + " VALUES(?, ?, ?, ?, ?)";
			pstm = con.prepareStatement(query);

			pstm.setObject(1, 1, OracleTypes.NUMBER);
			pstm.setObject(2, 350, OracleTypes.NUMBER);
			pstm.setString(3, "ACEPTADA");
			pstm.setDate(4, new java.sql.Date(dateFormatBasic.parse("12/12/2021").getTime()));
			pstm.setDate(5, new java.sql.Date(dateFormatBasic.parse("14/12/2021").getTime()));
			pstm.addBatch();

			pstm.executeBatch();

			// TIENE
			query = "INSERT INTO " + Tablas.TIENE_UWU + " VALUES(?, ?)";
			pstm = con.prepareStatement(query);

			pstm.setObject(1, 3, OracleTypes.NUMBER);
			pstm.setObject(2, 1, OracleTypes.NUMBER);
			pstm.addBatch();

			pstm.executeBatch();

			// PISTAS
			query = "INSERT INTO " + Tablas.PISTAS_UWU + " VALUES(?, ?, ?)";
			pstm = con.prepareStatement(query);

			pstm.setObject(1, 1, OracleTypes.NUMBER);
			pstm.setString(2, "Pista_1");
			pstm.setObject(3, 50, OracleTypes.NUMBER);
			pstm.addBatch();

			pstm.setObject(1, 2, OracleTypes.NUMBER);
			pstm.setString(2, "Pista_2");
			pstm.setObject(3, 60, OracleTypes.NUMBER);
			pstm.addBatch();

			pstm.executeBatch();

			// PARTIDOS_L_V_TA_TP
			query = "INSERT INTO " + Tablas.PARTIDOS_L_V_TA_TP_UWU + " VALUES(?, ?, ?, ?, ?, ?, ?, ?, ?, ?)";
			pstm = con.prepareStatement(query);

			pstm.setObject(1, 1, OracleTypes.NUMBER);
			pstm.setDate(2, new java.sql.Date(dateFormatExtended.parse("20/12/2021 10:30:00").getTime()));
			pstm.setString(3, "11111111A");
			pstm.setString(4, "11111111B");
			pstm.setObject(5, 1, OracleTypes.NUMBER);
			pstm.setString(6, "11111111C");
			pstm.setString(7, "11111111D");
			pstm.setObject(8, 1, OracleTypes.NUMBER);
			pstm.setString(9, "33333333A");
			pstm.setObject(10, 1, OracleTypes.NUMBER);
			pstm.addBatch();

			pstm.setObject(1, 2, OracleTypes.NUMBER);
			pstm.setDate(2, new java.sql.Date(dateFormatExtended.parse("20/12/2021 15:00:00").getTime()));
			pstm.setString(3, "11111111C");
			pstm.setString(4, "11111111D");
			pstm.setObject(5, 1, OracleTypes.NUMBER);
			pstm.setString(6, "11111111E");
			pstm.setString(7, "11111111F");
			pstm.setObject(8, 1, OracleTypes.NUMBER);
			pstm.setString(9, "33333333C");
			pstm.setObject(10, 1, OracleTypes.NUMBER);
			pstm.addBatch();

			pstm.executeBatch();

			// EMPRESA
			query = "INSERT INTO " + Tablas.EMPRESA_UWU + " VALUES(?, ?, ?, ? ,?)";
			pstm = con.prepareStatement(query);

			pstm.setString(1, "A1111111A");
			pstm.setString(2, "Gulugulu");
			pstm.setString(3, "Paco");
			pstm.setString(4, "emp1@ugr.es");
			pstm.setString(5, "+34111111111");
			pstm.addBatch();

			pstm.setString(1, "B1111111A");
			pstm.setString(2, "Facebuk");
			pstm.setString(3, "Jeff");
			pstm.setString(4, "emp2@ugr.es");
			pstm.setString(5, "34111111112");
			pstm.addBatch();

			pstm.setString(1, "C1111111A");
			pstm.setString(2, "Aguazon");
			pstm.setString(3, "Martin");
			pstm.setString(4, "emp3@ugr.es");
			pstm.setString(5, "+34111111113");
			pstm.addBatch();

			pstm.executeBatch();

			// PATROCINA_COLABORA
			query = "INSERT INTO " + Tablas.PATROCINA_COLABORA_UWU + " VALUES(?, ?, ?, ?)";
			pstm = con.prepareStatement(query);

			pstm.setString(1, "A1111111A");
			pstm.setObject(2, 1, OracleTypes.NUMBER);
			pstm.setObject(3, 0, OracleTypes.NUMBER);
			pstm.setObject(4, 500, OracleTypes.NUMBER);
			pstm.addBatch();

			pstm.setString(1, "A1111111A");
			pstm.setObject(2, 2, OracleTypes.NUMBER);
			pstm.setObject(3, 0, OracleTypes.NUMBER);
			pstm.setObject(4, 1500, OracleTypes.NUMBER);
			pstm.addBatch();

			pstm.setString(1, "B1111111A");
			pstm.setObject(2, 2, OracleTypes.NUMBER);
			pstm.setObject(3, 1, OracleTypes.NUMBER);
			pstm.setObject(4, 100, OracleTypes.NUMBER);
			pstm.addBatch();

			pstm.executeBatch();

			con.commit();
		}
		catch (Exception e)
		{
			Rollback();
			ConsoleError.MostrarError(e.toString());
		}
	}

	private void CargarTriggers()
	{
		try
		{
			String basePath = System.getProperty("user.dir") + "/triggers/", query;
			Statement stm = con.createStatement();

			// RS1.2
			query = Files.readString(Path.of(basePath + "rs12.sql"));
			stm.executeUpdate(query);

			// RS2.1
			query = Files.readString(Path.of(basePath + "rs21.sql"));
			stm.executeUpdate(query);

			// RS3.1
			query = Files.readString(Path.of(basePath + "rs31.sql"));
			stm.executeUpdate(query);

			// RS4.1
			query = Files.readString(Path.of(basePath + "rs41.sql"));
			stm.executeUpdate(query);

			// RS5.2
			query = Files.readString(Path.of(basePath + "rs52.sql"));
			stm.executeUpdate(query);

			con.commit();
		}
		catch (Exception e)
		{
			Rollback();
			ConsoleError.MostrarError(e.toString());
		}
	}

	private void Rollback()
	{
		try
		{
			con.rollback();
		}
		catch (SQLException e)
		{
			ConsoleError.MostrarError(e.toString());
		}
	}

	private boolean ExisteTabla(String nombreTabla)
	{
		boolean existe = false;

		try
		{
			DatabaseMetaData dbmd = con.getMetaData();
			existe = dbmd.getTables(null, null, nombreTabla.toUpperCase(), new String[] {"TABLE"}).next();
		}
		catch (SQLException e)
		{
			ConsoleError.MostrarError(e.toString());
		}

		return existe;
	}

	void MostrarTabla(String nombreTabla)
	{
		try
		{
			Statement stm = con.createStatement();
			ResultSet rs = stm.executeQuery("SELECT * FROM " + nombreTabla);
			ResultSetMetaData rsmd = rs.getMetaData();

			int numCols = rsmd.getColumnCount();

			int numEspacios = numCols*22;

			System.out.print(ConsoleColors.GREEN);
			// Cabecera
			System.out.println("<" + "-".repeat(numEspacios/2) + " " + nombreTabla + " " + "-".repeat(numEspacios/2) + ">");

			// Columnas de los atributos
			for (int i = 1; i <= numCols; i++)
			{
				System.out.format("%-24s", rsmd.getColumnName(i));
			}

			System.out.println();

			// Valores de las columnas
			while (rs.next())
			{
				for (int i = 1; i <= numCols; i++)
				{
					System.out.format("%-24s", "  " + rs.getString(i));
				}

				System.out.println();
			}

			// Parte inferior
			numEspacios += nombreTabla.length() + 2;

			System.out.println("<" + "-".repeat(numEspacios) + ">");
			System.out.print(ConsoleColors.RESET);
			System.out.println();
		}
		catch (SQLException e)
		{
			System.out.println(e.toString());
		}
	}

	////////////////////////////////////////////////////////
	/**************** Métodos Principales *****************/
	////////////////////////////////////////////////////////

	void AsignarParejaEntrenador(String DNIJ1, String DNIJ2, String DNIEnt, int codEd)
	{
		try
		{
			String query = "INSERT INTO " + Tablas.PAREJA_ENTRENADA_UWU + "(DNI_J1, DNI_J2, CodEdicion, DNI_E) VALUES(?, ?, ?, ?)";
			PreparedStatement pstm = con.prepareStatement(query);

			pstm.setObject(1, DNIJ1, OracleTypes.VARCHAR);
			pstm.setObject(2, DNIJ2, OracleTypes.VARCHAR);
			pstm.setObject(3, codEd, OracleTypes.NUMBER);
			pstm.setObject(4, DNIEnt, OracleTypes.VARCHAR);
			pstm.execute();

			con.commit();
		}
		catch (Exception e)
		{
			Rollback();
			ConsoleError.MostrarError(e.toString());
		}
	}

	void PagarCompra(int codCompraFinalizada, int codCompraPagada)
	{
		try
		{
			String query;
			PreparedStatement pstm;
			ResultSet rs;

			int codCompra;
			float cantidadPagada;

			// Obtener CodCompra
			query = "SELECT CodCompra FROM " + Tablas.COMPRAFINALIZADA_UWU + " WHERE CodCompraFinalizada = ?";
			pstm = con.prepareStatement(query);

			pstm.setObject(1, codCompraFinalizada, OracleTypes.NUMBER);
			rs = pstm.executeQuery();

			rs.next();
			codCompra = rs.getInt(1);

			// Obtener la cantidad total pagada
			query = "SELECT SUM(CANTIDAD*PRECIO) FROM (" +
					"SELECT * FROM " + Tablas.TIENEENTRADAS_UWU + " TE," + Tablas.EMITIDAEN_UWU + " EE, " +
					"(SELECT CodEdicion FROM " + Tablas.COMPRA_REALIZA_ENEDICION_UWU + " where CodCompra = ?) CRE" +
					" WHERE TE.CodEntrada = EE.CodEntrada AND EE.CodEdicion = CRE.CodEdicion AND CodCompra = ?" +
					")";
			pstm = con.prepareStatement(query);

			pstm.setObject(1, codCompra, OracleTypes.NUMBER);
			pstm.setObject(2, codCompra, OracleTypes.NUMBER);
			rs = pstm.executeQuery();

			rs.next();
			cantidadPagada = rs.getFloat(1);

			// Insertar en la tabla
			query = "INSERT INTO " + Tablas.COMPRAPAGADA_UWU + " VALUES(?, ?, ?, ?)";
			pstm = con.prepareStatement(query);

			pstm.setObject(1, codCompra, OracleTypes.NUMBER);
			pstm.setObject(2, codCompraFinalizada, OracleTypes.NUMBER);
			pstm.setObject(3, codCompraPagada, OracleTypes.NUMBER);
			pstm.setDate(4, new java.sql.Date(new java.util.Date(System.currentTimeMillis()).getTime()));
			pstm.execute();

			con.commit();

			// Mostrar el total pagado y el codCompra
			System.out.println();
			System.out.println(ConsoleColors.WHITE_BOLD + "CodCompra: " + codCompra);
			System.out.println("Total Pagado: " + cantidadPagada + ConsoleColors.RESET);
		}
		catch (Exception e)
		{
			Rollback();
			ConsoleError.MostrarError(e.toString());
		}
	}

	void HacerOfertaArbitro(int codOferta, String DNIArb, int codEd, float dineroOfrecido)
	{
		try
		{
			String query = "INSERT INTO " + Tablas.OFERTAS_RECIBE_HECHA_UWU.toString() + " VALUES(?, ?, ?, ?, ?, ?, ?)";
			PreparedStatement pstm = con.prepareStatement(query);

			pstm.setObject(1, codOferta, OracleTypes.NUMBER);
			pstm.setObject(2, dineroOfrecido, OracleTypes.NUMBER);
			pstm.setString(3, "PENDIENTE");
			pstm.setDate(4, new java.sql.Date(new java.util.Date(System.currentTimeMillis()).getTime()));
			pstm.setNull(5, OracleTypes.NULL);
			pstm.setString(6, DNIArb);
			pstm.setObject(7, codEd, OracleTypes.NUMBER);

			pstm.executeUpdate();

			con.commit();
		}
		catch (Exception e)
		{
			Rollback();
			ConsoleError.MostrarError(e.toString());
		}
	}

	void AniadirPartido(int codP, String DNIJ1L, String DNIJ2L, String DNIJ1V, String DNIJ2V, String DNIArb
		, Date fecha, int numPista, int codEd)
	{
		try
		{
			String query = "INSERT INTO " + Tablas.PARTIDOS_L_V_TA_TP_UWU.toString() + " VALUES(?, ?, ?, ?, ?, ?, ?, ?, ?, ?)";
			PreparedStatement pstm = con.prepareStatement(query);

			pstm.setObject(1, codP, OracleTypes.NUMBER);
			pstm.setDate(2, new java.sql.Date(fecha.getTime()));
			pstm.setString(3, DNIJ1L);
			pstm.setString(4, DNIJ2L);
			pstm.setObject(5, codEd, OracleTypes.NUMBER);
			pstm.setString(6, DNIJ1V);
			pstm.setString(7, DNIJ2V);
			pstm.setObject(8, codEd, OracleTypes.NUMBER);
			pstm.setString(9, DNIArb);
			pstm.setObject(10, numPista, OracleTypes.NUMBER);
			pstm.executeUpdate();

			con.commit();
		}
		catch (Exception e)
		{
			Rollback();
			ConsoleError.MostrarError(e.toString());
		}
	}

	void EliminarColaboradorPatrocinador(String CIF, int codEd)
	{
		try
		{
			String query = "DELETE FROM " + Tablas.PATROCINA_COLABORA_UWU + " WHERE CIF=? AND CodEdicion=?";
			PreparedStatement pstm = con.prepareStatement(query);

			pstm.setString(1, CIF);
			pstm.setObject(2, codEd, OracleTypes.NUMBER);
			pstm.executeUpdate();

			con.commit();
		}
		catch (Exception e)
		{
			Rollback();
			ConsoleError.MostrarError(e.toString());
		}
	}


	void FinalizarConexion()
	{
		try
		{
			if (con != null)
			{
				System.out.println(ConsoleColors.CYAN + "Terminando conexión con BD" + ConsoleColors.RESET);
				con.rollback(); // Cambios no guardados no se mantienen (Recomendación de la documentación oficial)
				con.close();
			}
		}
		catch (SQLException e)
		{
			ConsoleError.MostrarError(e.toString());
		}

		System.out.println(ConsoleColors.PURPLE + "<> Finalizando Sistema <>" + ConsoleColors.RESET);
		con = null;
	}

	public MenuSistema MenuSistema()
	{
		return new MenuSistema(this);
	}

	public boolean Conectado()
	{
		return con != null;
	}
}
