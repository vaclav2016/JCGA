package onion.cga;

public class CgaScreen extends CgaVideoArea implements VideoBuffer {

	final static public int WIDTH = 320;
	final static public int HEIGHT = 200;

	final static private CgaScreen screen = new CgaScreen();

	public static CgaScreen getInstance() {
		return screen;
	}

	private CgaScreen() {
		super(WIDTH, HEIGHT);
	}
}
