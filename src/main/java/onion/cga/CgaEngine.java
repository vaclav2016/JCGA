package onion.cga;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Toolkit;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JDialog;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.WindowConstants;

public class CgaEngine {

	final private static char UI_SEPARATOR = ':';

	final private static String CGA_FONT = "/CGA1.FNT";

	final private static String ZERO_RESOLUTION = "0" + UI_SEPARATOR + " Exit";
	final private static String LABEL_SCREEN_RESOLUTION = "Screen:";
	final private static String TITLE_CONFIGURATION = "Configuration";
	final private static String TITLE_OK = "Ok";
	final private static String TITLE_CANCEL = "Cancel";

	final private static WindowAdapter WINDOW_ADAPTER = new WindowAdapter() {
		@Override
		public void windowClosing(WindowEvent e) {
			System.exit(0);
		}
	};

	private static void centerDialog(JDialog dlg) {
		final Toolkit tk = Toolkit.getDefaultToolkit();
		final Dimension screenSize = tk.getScreenSize();
		final int screenHeight = screenSize.height;
		final int screenWidth = screenSize.width;
		if ((screenWidth < dlg.getWidth()) || (screenHeight < dlg.getHeight())) {
			dlg.setLocation(0, 32);
		} else {
			dlg.setLocation((screenWidth / 2) - (dlg.getWidth() / 2), (screenHeight / 2) - (dlg.getHeight() / 2));
		}
	}

	public static void executeGame(final CgaGame game) throws IOException {

		final Config cnf = CgaEngine.getScaleFromDialog();
		final int scale = cnf.getScale();

		if (scale < 1) {
			return;
		}
		final Color[] palette;
		if (cnf.getPalette() == 0) {
			palette = CgaEngine.getCga1Palette();
		} else if (cnf.getPalette() == 1) {
			palette = CgaEngine.getCga2Palette();
		} else {
			palette = null;
		}

		final CgaScreen screen = CgaScreen.getInstance();
		final Dimension size = new Dimension(CgaScreen.WIDTH * scale, CgaScreen.HEIGHT * scale);
		final CgaComponent cgaComponent = new CgaComponent(size);
		cgaComponent.addKeyListener(new KeyListener() {
			@Override
			public void keyTyped(KeyEvent e) {
			}

			@Override
			public void keyPressed(KeyEvent e) {
				game.onKeyEvent(new CgaKey(e.getKeyCode(), e.getKeyChar()));
			}

			@Override
			public void keyReleased(KeyEvent e) {
			}
		});
		cgaComponent.addMouseListener(new MouseListener() {
			@Override
			public void mouseClicked(MouseEvent e) {
			}

			@Override
			public void mousePressed(MouseEvent e) {
				game.onMouseEvent(new CgaMouseClick(CgaMouseClick.Type.CLICK, e.getX() / scale, e.getY() / scale));
			}

			@Override
			public void mouseReleased(MouseEvent e) {
				game.onMouseEvent(new CgaMouseClick(CgaMouseClick.Type.RELEASE, e.getX() / scale, e.getY() / scale));
			}

			@Override
			public void mouseEntered(MouseEvent e) {
			}

			@Override
			public void mouseExited(MouseEvent e) {
			}
		});
		final JDialog dlg = new JDialog();
		dlg.setDefaultCloseOperation(WindowConstants.DISPOSE_ON_CLOSE);
		dlg.setModal(true);
		dlg.setResizable(false);
		dlg.add(cgaComponent);
		dlg.pack();

		final ScreenUpdateThread screenUpdateThread = new ScreenUpdateThread(screen, cgaComponent, palette);
		screenUpdateThread.start();

		final Thread tg = new Thread(game);
		game.setCgaScreen(screen);
		// game.setCgaFont(CgaFont.getIntanceFromFile(new File(CGA_FONT)));
		game.setCgaFont(CgaFont.getIntanceFromResource(CGA_FONT));
		game.setScreenUpdateSignal(screenUpdateThread.getSignal());
		tg.start();

		dlg.setTitle(game.getTitle());
		centerDialog(dlg);
		dlg.addWindowListener(WINDOW_ADAPTER);
		dlg.setVisible(true);
	}

	private static int detectResolution() {
		int scale = 1;
		final Toolkit tk = Toolkit.getDefaultToolkit();
		final Dimension screenSize = tk.getScreenSize();
		final int screenHeight = screenSize.height;
		final int screenWidth = screenSize.width;
		while ((screenWidth > (CgaScreen.WIDTH * scale)) && (screenHeight > (CgaScreen.HEIGHT * scale))) {
			scale++;
		}
		scale--;
		return scale > 0 ? scale : 1;
	}

	public static Config getScaleFromDialog() {

		final JDialog dlg = new JDialog();
		dlg.setDefaultCloseOperation(WindowConstants.DISPOSE_ON_CLOSE);
		dlg.setModal(true);
		dlg.setLayout(new BorderLayout());
		JPanel panel;
		JButton btn;

		panel = new JPanel();
		panel.add(new JLabel(LABEL_SCREEN_RESOLUTION));
		final List<String> resolutionList = new ArrayList<String>();
		resolutionList.add(ZERO_RESOLUTION);
		final int detectedResolution = detectResolution();
		for (int i = 1; i <= detectedResolution; i++) {
			resolutionList.add("" + i + "" + UI_SEPARATOR + " " + (320 * i) + " x " + (200 * i));
		}

		final JComboBox combo = new JComboBox(resolutionList.toArray());
		panel.add(combo);
		combo.setSelectedIndex(detectedResolution);
		// combo.setSelectedIndex(readFromFile(detectedResolution));

		final JComboBox pal = new JComboBox(new String[] { "CGA1", "CGA2" });
		panel.add(pal);

		dlg.add(BorderLayout.NORTH, panel);

		panel = new JPanel();
		btn = new JButton();
		btn.setText(TITLE_OK);
		btn.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				dlg.setVisible(false);
			}
		});
		panel.add(btn);
		dlg.getRootPane().setDefaultButton(btn);

		btn = new JButton();
		btn.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				combo.setSelectedIndex(0);
				dlg.setVisible(false);
			}
		});
		btn.setText(TITLE_CANCEL);
		panel.add(btn);
		dlg.add(BorderLayout.SOUTH, panel);

		dlg.setTitle(TITLE_CONFIGURATION);
		dlg.pack();
		centerDialog(dlg);
		dlg.addWindowListener(WINDOW_ADAPTER);
		dlg.setVisible(true);

		final String res = (String) combo.getSelectedItem();

		final Config result = new Config();
		result.setScale(Integer.parseInt(res.substring(0, res.indexOf(UI_SEPARATOR))));
		result.setPalette(pal.getSelectedIndex());
		return result;
	}

	public final static byte CGA1_BLACK = 0;
	public final static byte CGA1_CYAN = 1;
	public final static byte CGA1_MAGENTA = 2;
	public final static byte CGA1_WHITE = 3;

	public static Color[] getCga1Palette() {
		final Color[] palette = new Color[4];
		palette[0] = new Color(0, 0, 0);
		palette[1] = new Color(0, 0xaa, 0xaa);
		palette[2] = new Color(0xaa, 0, 0xaa);
		palette[3] = new Color(255, 255, 255);
		return palette;
	}

	public final static byte CGA2_BLACK = 0;
	public final static byte CGA2_RED = 1;
	public final static byte CGA2_GREEN = 2;
	public final static byte CGA2_BROWN = 3;

	public static Color[] getCga2Palette() {
		final Color[] palette = new Color[4];
		palette[0] = new Color(0, 0, 0);
		palette[1] = new Color(0xaa, 0, 0);
		palette[2] = new Color(0, 0xaa, 0);
		palette[3] = new Color(0xaa, 0x55, 0);
		return palette;
	}

	private static class Config {
		private int scale;
		private int palette;

		public int getScale() {
			return this.scale;
		}

		public void setScale(int scale) {
			this.scale = scale;
		}

		public int getPalette() {
			return this.palette;
		}

		public void setPalette(int palette) {
			this.palette = palette;
		}
	}

	private static int getInt(byte[] b, int ofs) {
		return (b[ofs] & 0xff) | ((b[ofs + 1] & 0xff) << 8);
	}

	public static VideoBuffer loadPcx(InputStream is) throws IOException {
		final byte[] hdr = new byte[128];
		final byte[] buf = new byte[8192];
		int bpos = 0;
		is.read(hdr);
		is.read(buf);
		if ((hdr[0] != 0x0a) || (hdr[3] != 8) || (hdr[0x41] != 1)) {
			throw new IOException("Invalid pcx-file format!");
		}
		final int pcxWidth = (getInt(hdr, 8) - getInt(hdr, 4)) + 1;
		final int pcxHeight = (getInt(hdr, 10) - getInt(hdr, 6)) + 1;
		final VideoBuffer result = new CgaVideoArea(pcxWidth, pcxHeight);
		result.clear();

		byte data = (byte) buf[bpos];
		bpos++;
		byte count = 1;

		if ((data & 0xC0) == 0xC0) {
			count = (byte) (data & 0x3f);
			data = (byte) buf[bpos];
			bpos++;
		}
		for (int y = 0; y < pcxHeight; y++) {
			for (int x = 0; x < pcxWidth; x++) {
				final byte clr = data;
				result.drawPixel(x, y, clr);
				count--;
				if (count == 0) {
					data = (byte) buf[bpos];
					bpos++;
					if(bpos>=buf.length) {
						is.read(buf);
						bpos = 0;
					}
					if ((data & 0xC0) == 0xC0) {
						count = (byte) (data & 0x3f);
						data = (byte) buf[bpos];
						bpos++;
						if(bpos>=buf.length) {
							is.read(buf);
							bpos = 0;
						}
					} else {
						count = 1;
					}
				}
			}
		}
		is.close();

		return result;
	}

	public static void savePcx(VideoBuffer src, OutputStream os) throws IOException {
		final byte[] hdr = new byte[128];
		Arrays.fill(hdr, (byte) 0);
		hdr[0] = 0x0a;
		hdr[3] = 8;
		hdr[0x41] = 1;
		final int width = src.getWidth();
		hdr[4] = (byte) (width & 0xff);
		hdr[5] = (byte) (width & (0xff00 >> 8));
		final int height = src.getHeight();
		hdr[6] = (byte) (height & 0xff);
		hdr[7] = (byte) (height & (0xff00 >> 8));
		os.write(hdr);
		final byte[] b = src.getBuf();
		byte data = 0;
		byte count = 0;
		for (int i = 0; i < b.length; i++) {
			final byte d = b[i];
			if (count == 0) {
				data = d;
				count = 1;
			} else if (data == d) {
				if (count < 0x3f) {
					count++;
				} else {
					os.write((byte) (count | 0xc0));
					os.write(data);
					count = 1;
					data = d;
				}
			} else if ((count > 0) && (data != d)) {
				if (((data & 0xc0) == 0xc0) || (count > 1)) {
					os.write((byte) (count | 0xc0));
					os.write(data);
					count = 1;
					data = d;
				} else {
					os.write(data);
					count = 1;
					data = d;
				}
			}
		}
		if (count > 0) {
			if (((data & 0xc0) == 0xc0) || (count > 1)) {
				os.write((byte) (count | 0xc0));
				os.write(data);
			} else {
				os.write(data);
			}

		}
		os.close();
	}
}
