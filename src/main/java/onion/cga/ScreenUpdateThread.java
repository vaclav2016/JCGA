package onion.cga;

import java.awt.Color;
import java.awt.image.BufferedImage;

public class ScreenUpdateThread extends Thread {

	final private Signal signal;
	final private CgaScreen cgaScreen;

	final private int[] palette;
	final private CgaComponent jc;

	public ScreenUpdateThread(CgaScreen cgaScreen, CgaComponent jc, Color[] srcPalette) {
		super();
		this.cgaScreen = cgaScreen;
		this.jc = jc;
		this.signal = new Signal();
		this.palette = new int[srcPalette.length];
		for (int i = 0; i < srcPalette.length; i++) {
			this.palette[i] = srcPalette[i].getRGB();
		}
	}

	@Override
	public void run() {
		try {
			while (true) {
				this.signal.clear();
				this.signal.waitForSignal();
				this.update();
			}
		} catch (final InterruptedException e) {
		}
	}

	private void update() {

		final byte[] buf = this.cgaScreen.getBuf();
		final BufferedImage image = this.jc.getImage();

		for (int y = 0; y < CgaScreen.HEIGHT; y++) {
			for (int x = 0; x < CgaScreen.WIDTH; x++) {
				image.setRGB(x, y, this.palette[buf[(y * CgaScreen.WIDTH) + x]]);
			}
		}

		this.jc.repaint();
	}

	public Signal getSignal() {
		return this.signal;
	}
}
