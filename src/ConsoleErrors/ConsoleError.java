package ConsoleErrors;

import ConsoleColors.ConsoleColors;

public class ConsoleError
{
	public static void MostrarError(String error)
	{
		System.out.println(ConsoleColors.RED + error + ConsoleColors.RESET);
	}
}
