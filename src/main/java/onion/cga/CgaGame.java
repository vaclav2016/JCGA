package onion.cga;

public interface CgaGame extends Runnable {

	String getTitle();

	void setCgaScreen(CgaScreen screen);

	void setScreenUpdateSignal(Signal screenUpdateSignal);

	void setCgaFont(CgaFont font);

	void onKeyEvent(CgaKey cgaKey);

	void onMouseEvent(CgaMouseClick cgaMouseClick);

}
