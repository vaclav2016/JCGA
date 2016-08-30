package onion.cga;

import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.image.BufferedImage;

import javax.swing.JPanel;

public class CgaComponent extends JPanel {

	private static final long serialVersionUID = 1L;

	private final BufferedImage image;

	public CgaComponent(final Dimension d) {
		super();
		this.image = new BufferedImage(CgaScreen.WIDTH, CgaScreen.HEIGHT, BufferedImage.TYPE_INT_ARGB);
		this.setPreferredSize(d);
		this.setSize(d);
		this.setFocusTraversalKeysEnabled(false);
		this.setFocusable(true);
	}

	@Override
	public void paintComponent(Graphics g) {
		g.drawImage(this.image, 0, 0, this.getWidth(), this.getHeight(), null);
	}

	public BufferedImage getImage() {
		return this.image;
	}
}
