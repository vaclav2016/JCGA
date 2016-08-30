package onion.cga;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

public interface VideoBuffer {

	int getWidth();

	int getHeight();

	byte[] getBuf();

	void clear();

	void fill(byte color);

	void drawPixel(int x, int y, byte color);

	void drawHLine(int x, int y, int width, byte color);

	void drawVLine(int x, int y, int height, byte color);

	void drawRectF(int x, int y, int width, int height, byte color);

	void drawRectB(int x, int y, int width, int height, byte color);

	void drawRectBf(int x, int y, int width, int height, byte fgColor, byte bgColor);

	void drawStr(CgaFont font, int x, int y, String str, byte color);

	void fullCopyFrom(VideoBuffer src);

	void copyFrom(VideoBuffer src, int dstX, int dstY);

	void copyPartFrom(VideoBuffer srcScreen, int srcX, int srcY, int dstX, int dstY, int width, int height);

	void copySpriteFrom(VideoBuffer srcScreen, int dstX, int dstY);

	void copyBitSpriteFrom(VideoBuffer srcScreen, byte srcColor, int dstX, int dstY, byte dstColor);

	void load(InputStream is) throws IOException;

	void save(OutputStream os) throws IOException;

}
