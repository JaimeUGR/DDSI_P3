package SI;

import ConsoleColors.ConsoleColors;
import ConsoleErrors.ConsoleError;

import java.io.Console;
import java.util.Scanner;
import java.text.SimpleDateFormat;

public class MenuSistema
{
	private final SistemaTorneo st;
	private final Scanner menuScanner;

	public MenuSistema(SistemaTorneo st)
	{
		this.st = st;
		menuScanner = new Scanner(System.in);
	}

	private int LeerEntero()
	{
		String cadena = menuScanner.nextLine();
		int opcion = 0;

		try
		{
			opcion = Integer.parseInt(cadena);
		}
		catch (NumberFormatException e)
		{
			ConsoleError.MostrarError(e.toString());
			opcion = -1;
		}

		return opcion;
	}

	private float LeerDecimal()
	{
		String cadena = menuScanner.nextLine();
		float decimal = 0;

		try
		{
			decimal = Float.parseFloat(cadena);
		}
		catch (NumberFormatException e)
		{
			ConsoleError.MostrarError(e.toString());
			decimal = -1;
		}

		return decimal;
	}

	private String LeerCadena()
	{
		return menuScanner.nextLine();
	}

	private java.util.Date LeerFecha()
	{
		java.util.Date fecha = null;

		try
		{
			String fechaS = menuScanner.nextLine();
			SimpleDateFormat formatFecha = new SimpleDateFormat("dd/MM/yyyy HH:mm:ss");

			fecha = formatFecha.parse(fechaS);
		}
		catch (Exception e)
		{
			ConsoleError.MostrarError(e.toString());
			fecha = new java.util.Date(System.currentTimeMillis());
		}

		return fecha;
	}

	////////////////////////////////////////////////////////
	/**************** Métodos Principales *****************/
	////////////////////////////////////////////////////////

	private void MostrarTabla()
	{
		Tablas[] tablas = Tablas.values();
		int tablaSeleccionada;

		System.out.print(ConsoleColors.GREEN_BOLD_BRIGHT + "Lista de tablas:" + ConsoleColors.GREEN_BRIGHT);

		for (int i = 0; i < tablas.length; i++)
		{
			if (i % 4 == 0)
			{
				System.out.println();
				System.out.print("\t");
			}

			System.out.format("%-35s", "[" + Integer.toString(i) + "] " + tablas[i].toString());
		}

		System.out.println();
		System.out.println();
		System.out.print(ConsoleColors.YELLOW_BOLD + "Tabla A Mostrar >> ");
		tablaSeleccionada = LeerEntero();
		System.out.println(ConsoleColors.RESET);

		if (tablaSeleccionada < 0 || tablaSeleccionada >= tablas.length)
			return;

		st.MostrarTabla(tablas[tablaSeleccionada].toString());
	}

	private void AsignarParejaEntrenador()
	{
		String DNIJ1, DNIJ2, DNIEnt;
		int codEd;

		// Leer los datos
		System.out.print(ConsoleColors.YELLOW_BOLD + "DNI Jugador 1 >> " + ConsoleColors.RESET);
		DNIJ1 = LeerCadena();

		System.out.print(ConsoleColors.YELLOW_BOLD + "DNI Jugador 2 >> " + ConsoleColors.RESET);
		DNIJ2 = LeerCadena();

		System.out.print(ConsoleColors.YELLOW_BOLD + "DNI Entrenador >> " + ConsoleColors.RESET);
		DNIEnt = LeerCadena();

		System.out.print(ConsoleColors.YELLOW_BOLD + "Código Edición >> " + ConsoleColors.RESET);
		codEd = LeerEntero();

		// Hacer la llamada
		st.AsignarParejaEntrenador(DNIJ1, DNIJ2, DNIEnt, codEd);
	}

	private void PagarCompra()
	{
		int codCompraFinalizada;

		// Leer los datos
		System.out.print(ConsoleColors.YELLOW_BOLD + "Código Compra Finalizada >> " + ConsoleColors.RESET);
		codCompraFinalizada = LeerEntero();

		// Hacer la llamada
		st.PagarCompra(codCompraFinalizada);
	}

	private void HacerOfertaArbitro()
	{
		String DNIArb;
		int codEd;
		float dineroOfrecido;

		// Leer los datos
		System.out.print(ConsoleColors.YELLOW_BOLD + "DNI Árbitro >> " + ConsoleColors.RESET);
		DNIArb = LeerCadena();

		System.out.print(ConsoleColors.YELLOW_BOLD + "Código Edición >> " + ConsoleColors.RESET);
		codEd = LeerEntero();

		System.out.print(ConsoleColors.YELLOW_BOLD + "Dinero Ofrecido >> " + ConsoleColors.RESET);
		dineroOfrecido = LeerDecimal();

		// Hacer la llamada
		st.HacerOfertaArbitro(DNIArb, codEd, dineroOfrecido);
	}

	private void AniadirPartido()
	{
		String DNIJ1L, DNIJ2L, DNIJ1V, DNIJ2V, DNIArb;
		java.sql.Date fecha;
		int numPista, codEd;

		// Leer los datos
		System.out.print(ConsoleColors.YELLOW_BOLD + "DNI Jugador 1 Local >> " + ConsoleColors.RESET);
		DNIJ1L = LeerCadena();

		System.out.print(ConsoleColors.YELLOW_BOLD + "DNI Jugador 2 Local >> " + ConsoleColors.RESET);
		DNIJ2L = LeerCadena();

		System.out.print(ConsoleColors.YELLOW_BOLD + "DNI Jugador 1 Visitante >> " + ConsoleColors.RESET);
		DNIJ1V = LeerCadena();

		System.out.print(ConsoleColors.YELLOW_BOLD + "DNI Jugador 2 Visitante >> " + ConsoleColors.RESET);
		DNIJ2V = LeerCadena();

		System.out.print(ConsoleColors.YELLOW_BOLD + "DNI Árbitro >> " + ConsoleColors.RESET);
		DNIArb = LeerCadena();

		System.out.print(ConsoleColors.YELLOW_BOLD + "Fecha (dd/MM/yyyy HH:mm:ss) >> " + ConsoleColors.RESET);
		fecha = new java.sql.Date(LeerFecha().getTime());

		System.out.print(ConsoleColors.YELLOW_BOLD + "Número de Pista >> " + ConsoleColors.RESET);
		numPista = LeerEntero();

		System.out.print(ConsoleColors.YELLOW_BOLD + "Código Edición >> " + ConsoleColors.RESET);
		codEd = LeerEntero();

		// Hacer la llamada
		st.AniadirPartido(DNIJ1L, DNIJ2L, DNIJ1V, DNIJ2V, DNIArb, fecha, numPista, codEd);
	}

	private void EliminarColaborador()
	{
		String CIF;
		int codEd;

		// Leer los datos
		System.out.print(ConsoleColors.YELLOW_BOLD + "CIF >> " + ConsoleColors.RESET);
		CIF = LeerCadena();

		System.out.print(ConsoleColors.YELLOW_BOLD + "Código Edición >> " + ConsoleColors.RESET);
		codEd = LeerEntero();

		// Hacer la llamada
		st.EliminarColaborador(CIF, codEd);
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
			System.out.println("Selecciona la opción, 1-7: ");
			System.out.println("\t[1] Mostrar Tabla");
			System.out.println("\t[2] Asignar un entrenador a una pareja (RF1.2)");
			System.out.println("\t[3] Pagar una compra (RF2.4)");
			System.out.println("\t[4] Hacer oferta a árbitro (RF3.2)");
			System.out.println("\t[5] Añadir un partido (RF4.2)");
			System.out.println("\t[6] Eliminar un colaborador (RF5.4)");
			System.out.println("\t[7] Cerrar sesión y salir");
			System.out.println();
			System.out.print(ConsoleColors.YELLOW_BOLD + "Opción a seleccionar >> " + ConsoleColors.RESET);

			opcion = LeerEntero();

			System.out.println();

			switch (opcion)
			{
				case 1:
				{
					MostrarTabla();
					break;
				}
				case 2:
				{
					AsignarParejaEntrenador();
					break;
				}
				case 3:
				{
					PagarCompra();
					break;
				}
				case 4:
				{
					HacerOfertaArbitro();
					break;
				}
				case 5:
				{
					AniadirPartido();
					break;
				}
				case 6:
				{
					EliminarColaborador();
					break;
				}
				case 7:
				{
					st.FinalizarConexion();
					break;
				}
			}
		} while (opcion != 7 && st.Conectado());

		menuScanner.close();
	}
}
