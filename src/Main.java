import SI.MenuSistema;
import SI.SistemaTorneo;

public class Main
{
	public static void main(String[] args)
	{
		SistemaTorneo st = new SistemaTorneo();

		MenuSistema menu = st.MenuSistema();
		menu.MenuPrincipal();
	}
}