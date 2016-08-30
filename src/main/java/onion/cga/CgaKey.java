package onion.cga;

public class CgaKey {

	private final int keyCode;
	private final char keyChar;

	public CgaKey(int keyCode, char keyChar) {
		this.keyCode = keyCode;
		this.keyChar = keyChar;
	}

	public int getKeyCode() {
		return this.keyCode;
	}

	public char getKeyChar() {
		return this.keyChar;
	}

}
