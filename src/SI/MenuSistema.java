package SI;

import ConsoleColors.ConsoleColors;
import java.util.Scanner;

public class MenuSistema
{
	private final SistemaTorneo st;
	private final Scanner menuScanner;

	public MenuSistema(SistemaTorneo st)
	{
		this.st = st;
		menuScanner = new Scanner(System.in);
	}

	private int LeerOpcion()
	{
		String cadena = menuScanner.nextLine();
		int opcion = 0;

		try
		{
			opcion = Integer.parseInt(cadena);
		}
		catch (NumberFormatException e)
		{
			opcion = -1;
		}

		return opcion;
	}

	public void MenuPrincipal()
	{
		if (!st.Conectado())
		{
			return;
		}

		int opcion;

		do
		{
			System.out.println();
			System.out.print(ConsoleColors.BLUE_BRIGHT);
			System.out.println("<----------> Menú Principal <---------->");
			System.out.println("Selecciona la opción, 1-4: ");
			System.out.println("\t[1] Borrar y crear las tablas, con 10 productos en Stock");
			System.out.println("\t[2] Dar de alta un nuevo pedido");
			System.out.println("\t[3] Mostrar contenido de las tablas");
			System.out.println("\t[4] Cerrar sesión y salir");
			System.out.println();
			System.out.print(ConsoleColors.YELLOW_BOLD + "Opción a seleccionar >> " + ConsoleColors.RESET);

			opcion = LeerOpcion();

			System.out.println();

			switch (opcion)
			{
				case 1:
				{
					break;
				}
				case 2:
				{
					break;
				}
				case 3:
				{
					break;
				}
				case 4:
				{
					st.FinalizarConexion();
					break;
				}
			}
		} while (opcion != 4 && st.Conectado());

		menuScanner.close();
	}
}
