package SI;

import java.sql.*;

import ConsoleColors.ConsoleColors;
import Secret.SecretDB;

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
			//MostrarError(e.toString());
			System.out.println(e.toString());
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

		}
		catch (Exception e)
		{
			System.out.println(e.toString());
		}
	}

	////////////////////////////////////////////////////////
	/**************** Métodos Principales *****************/
	////////////////////////////////////////////////////////

	public void AsignarParejaEntrenador(String DNIJ1, String DNIJ2, String DNIEnt, int codEd)
	{
		try
		{

		}
		catch (Exception e)
		{
			System.out.println(e.toString());
		}
	}

	public void PagarCompra(int codCompraFinalizada)
	{
		try
		{

		}
		catch (Exception e)
		{
			System.out.println(e.toString());
		}
	}

	public void HacerOfertaArbitro(String DNIArb, int codEd, float dineroOfrecido)
	{
		try
		{

		}
		catch (Exception e)
		{
			System.out.println(e.toString());
		}
	}

	public void AniadirPartido(String DNIJ1L, String DNIJ2L, String DNIJ1V, String DNIJ2V, String DNIArb
		, Date fecha, int numPista, int codEd)
	{
		try
		{

		}
		catch (Exception e)
		{
			System.out.println(e.toString());
		}
	}

	public void EliminarColaborador(String CIF, int codEd)
	{

	}


	public void FinalizarConexion()
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
			//MostrarError(e.toString()); Hello
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
