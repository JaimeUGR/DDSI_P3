package SI;

import java.sql.*;

import ConsoleColors.ConsoleColors;
import ConsoleErrors.ConsoleError;
import Secret.SecretDB;
import oracle.jdbc.OracleTypes;

public class SistemaTorneo
{
	private Connection con;



	public SistemaTorneo()
	{
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

		// Iniciar los SubSistemas

	}

	private void CrearTablas()
	{
		try
		{

			Statement stm = con.createStatement();

			for (Tablas t : Tablas.values())
				if (ExisteTabla(t.toString()))
					stm.executeUpdate("DROP TABLE " + t);


			// Ediciones
			stm.executeUpdate("CREATE TABLE " + Tablas.EDICIONES + "( " +
					"CodEdicion NUMBER(4) CONSTRAINT EDICIONESPK PRIMARY KEY, " +
					"FechaInicio DATE" +
					")");

			// Jugadores
			stm.executeUpdate("CREATE TABLE " + Tablas.JUGADOR + "( " +
					"DNI CHAR(9) CONSTRAINT JUGADORPK PRIMARY KEY, " +
					"Fecha DATE," +
					"Nombre VARCHAR(50)," +
					"Apellidos VARCHAR(100)," +
					"Sexo VARCHAR(15) CONSTRAINT NOSEX NOT NULL CONSTRAINT BADSEX CHECK(Sexo='M' OR Sexo='F')" +
					")");

			// Comprobar que no se repite cambiar pdf TODO
			stm.executeUpdate("CREATE TABLE " + Tablas.PAREJA + "( " +
					"DNI_J1 CONSTRAINT PAREJA_FK_JUGADOR REFERENCES " + Tablas.JUGADOR + "(DNI)," +
					"DNI_J2 CONSTRAINT PAREJA_FK_JUGADOR REFERENCES " + Tablas.JUGADOR + "(DNI)," +
					"PRIMARY KEY(DNI_J1,DNI_J2)" +
					")");

			// Puntuacion no se comprueba rs TODO
			stm.executeUpdate("CREATE TABLE " + Tablas.PARTICIPA + "( " +
					"DNI_J1 CHAR(9)," +
					"DNI_J2 CHAR(9)," +
					"CodEdicion CONSTRAINT PARTICIPA_FK_EDICIONES REFERENCES " + Tablas.EDICIONES +  "(CodEdicion), " +
					"Puntuacion NUMBER(5) CONSTRAINT BADSCORE CHECK(Puntuacion>=0) DEFAULT 0," +
					"FOREIGN KEY (DNI_J1,DNI_J2) CONSTRAINT PARTICIPA_FK_PAREJA REFERENCES " + Tablas.PAREJA + "(DNI_J1, DNI_J2)," +
					"PRIMARY KEY(DNI_J1,DNI_J2,CodEdicion)" +
					")");

			stm.executeUpdate("CREATE TABLE " + Tablas.ENTRENADOR + "( " +
					"DNI CHAR(9) CONSTRAINT ENTRENADORPK PRIMARY KEY, " +
					"Fecha DATE," +
					"Nombre VARCHAR(50)," +
					"Apellidos VARCHAR(100)," +
					"Sexo VARCHAR(15) CONSTRAINT NOSEX_ENTRENADOR NOT NULL CONSTRAINT BADSEX_ENTRENADOR CHECK(Sexo='M' OR Sexo='F')" +
					")");

			stm.executeUpdate("CREATE TABLE " + Tablas.PAREJA_ENTRENADA + "( " +
					"DNI_J1 CHAR(9)," +
					"DNI_J2 CHAR(9)," +
					"CodEdicion NUMBER(4), " +
					"DNI_E CONSTRAINT PAREJA_ENTRENADA_FK_ENTRENADOR REFERENCES " + Tablas.ENTRENADOR + "(DNI)," +
					"FOREIGN KEY (DNI_J1,DNI_J2,CodEdicion) CONSTRAINT PAREJA_ENTRENADA_FK_PARTICIPA REFERENCES " + Tablas.PARTICIPA + "(DNI_J1,DNI_J2,CodEdicion)," +
					"PRIMARY KEY(DNI_J1,DNI_J2,CodEdicion)" +
					")");



			// Espectadores y Entradas Check el @ TODO
			stm.executeUpdate("CREATE TABLE " + Tablas.ESPECTADOR + "( " +
					"DNI CHAR(9) CONSTRAINT ESPECTADORPK PRIMARY KEY, " +
					"Nombre VARCHAR(50)," +
					"Apellidos VARCHAR(100)," +
					"CorreoElectronico VARCHAR(80) CONSTRAINT USEDMAIL UNIQUE CONSTRAINT NOMAIL NOT NULL ," +
					"Contrasenia VARCHAR(30) CONSTRAINT NOPASSWD NOT NULL" +
					")");

			stm.executeUpdate("CREATE TABLE " + Tablas.COMPRA_REALIZA_ENEDICION + "( " +
					"CodCompra NUMBER(9) CONSTRAINT COMPRAPK PRIMARY KEY, " +
					"FechaInicio DATE," +
					"DNI CONSTRAINT COMPRA_FK_ESPECTADOR REFERENCES " + Tablas.ESPECTADOR + "(DNI), " +
					"CodEdicion CONSTRAINT COMPRA_FK_EDICIONES REFERENCES " + Tablas.EDICIONES + "(CodEdicion)" +
					")");

			stm.executeUpdate("CREATE TABLE " + Tablas.COMPRAFINALIZADA + "( " +
					"CodCompra NUMBER(9) CONSTRAINT COMPRAFINALIZADA_FK_COMPRA REFERENCES " + Tablas.COMPRA_REALIZA_ENEDICION + "(CodCompra) PRIMARY KEY, " +
					"CodCompraFinalizada NUMBER(9) UNIQUE NOT NULL, " +
					"FechaFinalizacion DATE" +
					")");

			stm.executeUpdate("CREATE TABLE " + Tablas.COMPRAPAGADA + "( " +
					"CodCompra NUMBER(9)," +
					"CodCompraFinalizada NUMBER(9)," +
					"CodCompraPagada NUMBER(9) UNIQUE NOT NULL, " +
					"FechaPagos DATE," +
					"FOREIGN KEY (CodCompra,CodCompraFinalizada) REFERENCES " + Tablas.COMPRAFINALIZADA + "(CodCompra, CodCompraFinalizada)," +
					"PRIMARY KEY (CodCompra,CodCompraFinalizada)" +
					")");


			stm.executeUpdate("CREATE TABLE " + Tablas.ENTRADA_EMITIDAEN + "( " +
					"CodEntrada NUMBER(2) PRIMARY KEY," +
					"Tipo VARCHAR(50)," +
					"Precio NUMBER(12,2) CONSTRAINT BADPRICE CHECK(Precio>=0), " +
					"CantidadEmitida NUMBER(4) CONSTRAINT BADQUANTITY_ENTRADA CHECK(CantidadEmitida>=0)," +
					"CodEdicion CONSTRAINT ENTRADA_FK_EDICIONES REFERENCES " + Tablas.EDICIONES + "(CodEdicion)" +
					")");


			stm.executeUpdate("CREATE TABLE " + Tablas.TIENEENTRADAS + "( " +
					"CodCompra CONSTRAINT TIENEENTRADAS_FK_COMPRA REFERENCES " + Tablas.COMPRA_REALIZA_ENEDICION + "(CodCompra)," +
					"CodEntrada CONSTRAINT TIENEENTRADAS_FK_ENTRADA_EMITIDAEN REFERENCES " + Tablas.ENTRADA_EMITIDAEN + "(CodEntrada)," +
					"Cantidad NUMBER(4) CONSTRAINT BADQUANTITY CHECK(Cantidad>=0)" +
					"PRIMARY KEY (CodCompra,CodEnrtrada)" +
					")");


			// Arbitros y Ofertas
			stm.executeUpdate("CREATE TABLE " + Tablas.ARBITRO + "( " +
					"DNI CHAR(9) CONSTRAINT ARBITROPK PRIMARY KEY, " +
					"Nombre VARCHAR(50)," +
					"Apellidos VARCHAR(100)," +
					"FechaNac DATE," +
					"Sexo VARCHAR(15) CONSTRAINT NOSEX_ARBITRO NOT NULL CONSTRAINT BADSEX_ARBITRO CHECK(Sexo='M' OR Sexo='F')" +
					")");

			stm.executeUpdate("CREATE TABLE " + Tablas.OFERTAS_RECIBE_HECHA + "( " +
					"CodOferta NUMBER(6) CONSTRAINT OFERTAPK PRIMARY KEY, " +
					"CantidadDinero NUMBER(9,2)," +
					"EstadoOferta VARCHAR(20)," +
					"FechaOferta DATE," +
					"FechaAcep_Rech DATE," +
					"DNIArb REFERENCES " + Tablas.ARBITRO + "(DNI)," +
					"CodEdicion REFERENCES " + Tablas.EDICIONES + "(CodEdicion)" +
					")");

			stm.executeUpdate("CREATE TABLE " + Tablas.CONTRAOFERTAS + "( " +
					"CodContraoferta NUMBER(6) CONSTRAINT CONTRAOFERTASPK PRIMARY KEY, " +
					"CantidadDinero NUMBER(9,2)," +
					"EstadoContraoferta VARCHAR(20)," +
					"FechaContraoferta DATE," +
					"FechaAcep_Rech DATE" +
					")");

			stm.executeUpdate("CREATE TABLE " + Tablas.TIENE + "( " +
					"CodOferta CONSTRAINT TIENE_FK_OFERTA REFERENCES " + Tablas.OFERTAS_RECIBE_HECHA + "(CodOferta) CONSTRAINT OFERTAPK PRIMARY KEY, " +
					"CodContraoferta CONSTRAINT TIENE_FK_CONTRAOFERTA REFERENCES " + Tablas.CONTRAOFERTAS + "(CodContraoferta) UNIQUE NOT NULL" +
					")");

			// Pistas
			stm.executeUpdate("CREATE TABLE " + Tablas.PISTAS + "( " +
					"NumPista NUMBER(2) CONSTRAINT PISTASPK PRIMARY KEY, " +
					"Nombre VARCHAR(30)," +
					"Capacidad NUMBER(4) CONSTRAINT BADCAPACITY CHECK(Capacidad>=0)" +
					")");

			// Partidos
			stm.executeUpdate("CREATE TABLE " + Tablas.PARTIDOS_L_V_TA_TP + "( " +
					"CodP NUMBER(8) CONSTRAINT PARTIDOPK PRIMARY KEY, " +
					"Fecha DATE," +
					"DNI_J1 CHAR(9)," +
					"DNI_J2 CHAR(9)," +
					"CodEdicion1 NUMBER(4), " +
					"FOREIGN KEY (DNI_J1,DNI_J2,CodEdicion1) REFERENCES " + Tablas.PARTICIPA + "(DNI_J1,DNI_J2,CodEdicion)," +
					"DNI_J3 CHAR(9)," +
					"DNI_J4 CHAR(9)," +
					"CodEdicion2 NUMBER(4), " +
					"FOREIGN KEY (DNI_J3,DNI_J4,CodEdicion2) REFERENCES " + Tablas.PARTICIPA + "(DNI_J1,DNI_J2,CodEdicion)," +
					"DNI_Arb CONSTRAINT PARTIDO_FK_ARBITRO REFERENCES " + Tablas.ARBITRO + "(DNI)," +
					"NumPista CONSTRAINT PARTIDO_FK_PISTAS REFERENCES " + Tablas.PISTAS + "(NumPista)," +
					")");

			// Empresas
			stm.executeUpdate("CREATE TABLE " + Tablas.EMPRESA + "( " +
					"CIF CHAR(9) CONSTRAINT EMPRESAPK PRIMARY KEY, " +
					"Nombre VARCHAR(50)," +
					"PersonaContacto VARCHAR(100)," +
					"Email VARCHAR(80)," +
					"Telefono VARCHAR(15)" +
					")");

			stm.executeUpdate("CREATE TABLE " + Tablas.PATROCINA_COLABORA + "( " +
					"CIF CONSTRAINT PATROCINA_FK_EMPRESA REFERENCES " + Tablas.EMPRESA + "(CIF), " +
					"CodEdicion CONSTRAINT PATROCINA_FK_EDICIONES REFERENCES " + Tablas.EDICIONES + "(CodEdicion)," +
					"EsPatrocinador NUMBER(1) CONSTRAINT BADESPATROCINADOR CHECK (EsPAtrocinador IN (0,1))," +
					"Dinero NUMBER(9,2) CONSTRAINT BADMONEY CHECK (Dinero>=0)," +
					"PRIMARY KEY (CIF,CodEdicion)" +
					")");

			con.commit();
		}
		catch (Exception e)
		{
			ConsoleError.MostrarError(e.toString());
		}
	}

	////////////////////////////////////////////////////////
	/**************** Métodos Principales *****************/
	////////////////////////////////////////////////////////

	void AsignarParejaEntrenador(String DNIJ1, String DNIJ2, String DNIEnt, int codEd)
	{
		try
		{
			//System.out.println("Insertando tuplas en " + Tablas.PAREJA_ENTRENADA);
			String query = "INSERT INTO " + Tablas.PAREJA_ENTRENADA + "(DNI_J1, DNI_J2, CodEdicion, DNI_E) VALUES(?, ?, ?, ?)";
			PreparedStatement pstm = con.prepareStatement(query);

			pstm.setObject(1, DNIJ1, OracleTypes.VARCHAR);
			pstm.setObject(2, DNIJ2, OracleTypes.VARCHAR);
			pstm.setObject(3, codEd, OracleTypes.NUMBER);
			pstm.setObject(4, DNIEnt, OracleTypes.VARCHAR);
			pstm.execute();

		}
		catch (Exception e)
		{
			ConsoleError.MostrarError(e.toString());
		}
	}

	void PagarCompra(int codCompraFinalizada)
	{
		try
		{

		}
		catch (Exception e)
		{
			ConsoleError.MostrarError(e.toString());
		}
	}

	void HacerOfertaArbitro(String DNIArb, int codEd, float dineroOfrecido)
	{
		try
		{

		}
		catch (Exception e)
		{
			ConsoleError.MostrarError(e.toString());
		}
	}

	void AniadirPartido(String DNIJ1L, String DNIJ2L, String DNIJ1V, String DNIJ2V, String DNIArb
		, Date fecha, int numPista, int codEd)
	{
		try
		{

		}
		catch (Exception e)
		{
			ConsoleError.MostrarError(e.toString());
		}
	}

	void EliminarColaborador(String CIF, int codEd)
	{
		try {
			Statement stm = con.createStatement();

			stm.executeUpdate("DELETE FROM " + Tablas.PATROCINA_COLABORA +
					" WHERE CIF=" + CIF + " AND CodEdicion=" + codEd + " AND EsPatrocinador=1;");
		}
		catch (Exception e)
		{
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
				con.rollback(); // Las transacciones en curso / cambios no guardados no se mantienen
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

	public MenuSistema MenuSistema()
	{
		return new MenuSistema(this);
	}

	public boolean Conectado()
	{
		return con != null;
	}
}
