package onion.cga;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.UnsupportedEncodingException;
import java.util.Arrays;

public class CgaVideoArea implements VideoBuffer {

	final private int width;
	final private int height;
	final private byte[] buf;

	public CgaVideoArea(int width, int height) {
		this.width = width;
		this.height = height;
		this.buf = new byte[width * height];
	}

	@Override
	public int getWidth() {
		return this.width;
	}

	@Override
	public int getHeight() {
		return this.height;
	}

	@Override
	public byte[] getBuf() {
		return this.buf;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = (prime * result) + Arrays.hashCode(this.buf);
		result = (prime * result) + this.height;
		result = (prime * result) + this.width;
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj) {
			return true;
		}
		if (obj == null) {
			return false;
		}
		if (this.getClass() != obj.getClass()) {
			return false;
		}
		final CgaVideoArea other = (CgaVideoArea) obj;
		if (!Arrays.equals(this.buf, other.buf)) {
			return false;
		}
		if (this.height != other.height) {
			return false;
		}
		if (this.width != other.width) {
			return false;
		}
		return true;
	}

	@Override
	public void clear() {
		this.fill((byte) 0);
	}

	@Override
	public void fill(byte color) {
		Arrays.fill(this.buf, color);
	}

	@Override
	public void drawPixel(int x, int y, byte color) {
		this.buf[x + (y * this.getWidth())] = color;
	}

	@Override
	public void drawHLine(int x, int y, int width, byte color) {
		final int ofs = x + (y * this.getWidth());
		Arrays.fill(this.buf, ofs, ofs + width, color);
	}

	@Override
	public void drawVLine(int x, int y, int height, byte color) {
		int ofs = x + (y * this.getWidth());
		while (height > 0) {
			this.buf[ofs] = color;
			ofs += this.getWidth();
			height--;
		}
	}

	@Override
	public void drawRectF(int x, int y, int width, int height, byte color) {
		int ofs = x + (y * this.getWidth());
		while (height > 0) {
			Arrays.fill(this.buf, ofs, ofs + width, color);
			ofs += this.getWidth();
			height--;
		}
	}

	@Override
	public void drawRectB(int x, int y, int width, int height, byte color) {
		int ofs = x + (y * this.getWidth());
		Arrays.fill(this.buf, ofs, ofs + width + 1, color);
		ofs += this.getWidth();
		height--;
		while (height > 1) {
			this.buf[ofs] = color;
			this.buf[ofs + width] = color;
			ofs += this.getWidth();
			height--;
		}
		Arrays.fill(this.buf, ofs, ofs + width + 1, color);
	}

	@Override
	public void drawRectBf(int x, int y, int width, int height, byte fgColor, byte bgColor) {
		this.drawRectF(x, y, width, height, bgColor);
		this.drawRectB(x, y, width, height, fgColor);
	}

	@Override
	public void drawStr(CgaFont font, int x, int y, String str, byte color) {
		try {
			final byte[] fnt = font.getBuf();
			final byte[] byteMsg = str.getBytes("866");
			int pos = (y * this.getWidth()) + x;
			for (int i = 0; i < byteMsg.length; i++) {
				final int b = byteMsg[i] & 0xFF;
				int cpos = pos;
				for (int yy = 0; yy < 8; yy++) {
					final byte row = fnt[(b * 8) + yy];
					for (int xx = 0; xx < 8; xx++) {
						if ((row & (0x80 >> xx)) != 0) {
							this.buf[cpos + xx] = color;
						}
					}
					cpos += this.getWidth();
				}

				pos += 8;
			}
		} catch (final UnsupportedEncodingException e) {
			System.err.println(e.getMessage());
			e.printStackTrace();
		}
	}

	@Override
	public void fullCopyFrom(VideoBuffer src) {
		System.arraycopy(src.getBuf(), 0, this.buf, 0, this.getWidth() * this.getHeight());
	}

	@Override
	public void copyFrom(VideoBuffer src, int dstX, int dstY) {
		int pos = (dstY * this.getWidth()) + dstX;
		int srcPos = 0;
		final byte[] srcBuf = src.getBuf();
		for (int y = 0; y < src.getHeight(); y++) {
			System.arraycopy(srcBuf, srcPos, this.buf, pos, src.getWidth());
			pos += this.getWidth();
			srcPos += src.getWidth();
		}
	}

	@Override
	public void copyPartFrom(VideoBuffer srcScreen, int srcX, int srcY, int dstX, int dstY, int width, int height) {
		int dstOfs = (dstY * this.getWidth()) + dstX;
		int srcOfs = (srcY * this.getWidth()) + srcX;
		final byte[] srcBuf = srcScreen.getBuf();
		for (int i = 0; (i < height) && ((i + dstY) < this.getHeight()); i++) {
			System.arraycopy(srcBuf, srcOfs, this.buf, dstOfs, width);
			dstOfs += this.getWidth();
			srcOfs += this.getWidth();
		}
	}

	@Override
	public void copyBitSpriteFrom(VideoBuffer srcScreen, byte srcColor, int dstX, int dstY, byte dstColor) {
		int dstOfs = (dstY * this.getWidth()) + dstX;
		int srcOfs = 0;
		final int width = srcScreen.getWidth();
		final int height = srcScreen.getHeight();
		final byte[] srcBuf = srcScreen.getBuf();
		for (int y = 0; (y < height) && ((y + dstY) < this.getHeight()); y++) {
			for (int x = 0; (x < width) && ((x + dstX) < this.getWidth()); x++) {
				final byte b = srcBuf[srcOfs + x];
				if (b == srcColor) {
					this.buf[dstOfs + x] = dstColor;
				}
			}
			dstOfs += this.getWidth();
			srcOfs += width;
		}
	}

	@Override
	public void copySpriteFrom(VideoBuffer srcScreen, int dstX, int dstY) {
		int dstOfs = (dstY * this.getWidth()) + dstX;
		int srcOfs = 0;
		final int width = srcScreen.getWidth();
		final int height = srcScreen.getHeight();
		final byte[] srcBuf = srcScreen.getBuf();
		for (int y = 0; (y < height) && ((y + dstY) < this.getHeight()); y++) {
			for (int x = 0; (x < width) && ((x + dstX) < this.getWidth()); x++) {
				final byte b = srcBuf[srcOfs + x];
				if (b != 0) {
					this.buf[dstOfs + x] = srcBuf[srcOfs + x];
				}
			}
			dstOfs += this.getWidth();
			srcOfs += width;
		}
	}

	@Override
	public void load(InputStream is) throws IOException {
		is.read(this.buf, 0, this.getWidth() * this.getHeight());
	}

	@Override
	public void save(OutputStream os) throws IOException {
		os.write(this.buf, 0, this.getWidth() * this.getHeight());
	}

	@Override
	public String toString() {
		return "CgaVideoArea [width=" + this.width + ", height=" + this.height + "]";
	}

}
