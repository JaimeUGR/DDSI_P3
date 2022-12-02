package SI;

import ConsoleColors.ConsoleColors;
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
			System.out.println(e.toString());
			fecha = new java.util.Date(System.currentTimeMillis());
		}

		return fecha;
	}

	////////////////////////////////////////////////////////
	/**************** Métodos Principales *****************/
	////////////////////////////////////////////////////////

	private void AsignarParejaEntrenador()
	{
		String DNIJ1, DNIJ2, DNIEnt;
		int codEd;

		// Leer los datos
		System.out.print("DNI Jugador 1 >> ");
		DNIJ1 = LeerCadena();

		System.out.print("DNI Jugador 2 >> ");
		DNIJ2 = LeerCadena();

		System.out.print("DNI Entrenador >> ");
		DNIEnt = LeerCadena();

		System.out.print("Código Edición >> ");
		codEd = LeerEntero();

		// Hacer la llamada
		st.AsignarParejaEntrenador(DNIJ1, DNIJ2, DNIEnt, codEd);
	}

	private void PagarCompra()
	{
		int codCompraFinalizada;

		// Leer los datos
		System.out.print("Código Compra Finalizada >> ");
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
		System.out.print("DNI Árbitro >> ");
		DNIArb = LeerCadena();

		System.out.print("Código Edición >> ");
		codEd = LeerEntero();

		System.out.print("Dinero Ofrecido >> ");
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
		System.out.print("DNI Jugador 1 Local >> ");
		DNIJ1L = LeerCadena();

		System.out.print("DNI Jugador 2 Local >> ");
		DNIJ2L = LeerCadena();

		System.out.print("DNI Jugador 1 Visitante >> ");
		DNIJ1V = LeerCadena();

		System.out.print("DNI Jugador 2 Visitante >> ");
		DNIJ2V = LeerCadena();

		System.out.print("DNI Árbitro >> ");
		DNIArb = LeerCadena();

		System.out.print("Fecha >> ");
		fecha = new java.sql.Date(LeerFecha().getTime());

		System.out.print("Número de Pista >> ");
		numPista = LeerEntero();

		System.out.print("Código Edición >> ");
		codEd = LeerEntero();

		// Hacer la llamada
		st.AniadirPartido(DNIJ1L, DNIJ2L, DNIJ1V, DNIJ2V, DNIArb, fecha, numPista, codEd);
	}

	private void EliminarColaborador()
	{
		String CIF;
		int codEd;

		// Leer los datos
		System.out.print("CIF >> ");
		CIF = LeerCadena();

		System.out.print("Código Edición >> ");
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
