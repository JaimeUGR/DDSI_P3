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
			//MostrarError(e.toString());
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
