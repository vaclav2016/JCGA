package onion.cga;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;

public class CgaFont {

	final static private int LENGTH = 256 * 8;

	private final byte[] buf;

	public CgaFont() {
		this.buf = new byte[LENGTH];
	}

	private CgaFont(byte[] buf) {
		this.buf = buf;
	}

	public static CgaFont getIntanceFromFile(File file) throws IOException {
		return getIntanceFromInputStream(new FileInputStream(file));
	}

	public static CgaFont getIntanceFromResource(String resource) throws IOException {
		return getIntanceFromInputStream(CgaFont.class.getResourceAsStream(resource));
	}

	public static CgaFont getIntanceFromInputStream(InputStream is) throws IOException {
		final byte[] buf = new byte[LENGTH];
		is.read(buf, 0, LENGTH);
		is.close();
		return new CgaFont(buf);
	}

	public byte[] getBuf() {
		return this.buf;
	}

}
