package org.scrivo.imageio.netpbm;

import java.awt.color.ColorSpace;
import java.awt.image.BufferedImage;
import java.awt.image.ColorConvertOp;
import java.awt.image.DataBufferByte;
import java.io.IOException;
import java.util.Arrays;

import javax.imageio.ImageIO;

import junit.framework.TestCase;  

public class ImageTest extends TestCase {

	private static final class TestImage {
		public TestImage(int width, int height, byte[] data) {
			super();
			this.width = width;
			this.height = height;
			this.data = data;
		}

		private int width;
		private int height;
		private byte[] data;

		public int getWidth() {
			return width;
		}

		public int getHeight() {
			return height;
		}

		public byte[] getData() {
			return data;
		}

	}

	private void assertImage(TestImage ti, BufferedImage data1Ppm) {
		assertEquals(ti.getWidth(), data1Ppm.getWidth());
		assertEquals(ti.getHeight(), data1Ppm.getHeight());
		byte[] data = ((DataBufferByte) data1Ppm.getRaster().getDataBuffer()).getData();
		assertTrue(Arrays.equals(ti.getData(), data));
	}

	/**
	 * The Ppm Formatting test tests the same netpbm plain ppm image, but each with
	 * a different formatting.
	 */
	public void testPpmFormatting() throws IOException {

		TestImage ti = new TestImage(3, 2, new byte[] {
				-1, -0, -0, -0, -1, -0, -0, -0, -1,
				-1, -1, -0, -1, -1, -1, -0, -0, -0
		});

		assertImage(ti,ImageIO.read(getClass().getResourceAsStream("/wiki.sample1.ppm")));
		assertImage(ti,ImageIO.read(getClass().getResourceAsStream("/wiki.sample2.ppm")));
		assertImage(ti,ImageIO.read(getClass().getResourceAsStream("/wiki.sample3.ppm")));
		assertImage(ti,ImageIO.read(getClass().getResourceAsStream("/wiki.sample4.ppm")));
	}

	/**
	 * The Pbm Formatting test tests the same netppm plain ppm image, but 
	 * each with a different formatting.
	 */
	public void testPbmFormatting() throws IOException {

		TestImage ti = new TestImage(6, 10, new byte[] {
				-1, -1, -1, -1, -0, -1,
				-1, -1, -1, -1, -0, -1,
				-1, -1, -1, -1, -0, -1,
				-1, -1, -1, -1, -0, -1,
				-1, -1, -1, -1, -0, -1,
				-1, -1, -1, -1, -0, -1,
				-0, -1, -1, -1, -0, -1,
				-1, -0, -0, -0, -1, -1,
				-1, -1, -1, -1, -1, -1,
				-1, -1, -1, -1, -1, -1
		});

		assertImage(ti,ImageIO.read(getClass().getResourceAsStream("/wiki.test01.pbm")));
		assertImage(ti,ImageIO.read(getClass().getResourceAsStream("/wiki.test02.pbm")));
	}

	/**
	 * The Pbm Formatting test tests the same netppm plain ppm image, but 
	 * each with a different formatting.
	 */
	public void testGrayScale() throws IOException {

		TestImage ti = new TestImage(24, 7, new byte[] {
			-1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1,
			-1, -0, -0, -0, -0, -1, -1, -0, -0, -0, -0, -1, -1, -0, -0, -0, -0, -1, -1, -0, -0, -0, -0, -1,
			-1, -0, -1, -1, -1, -1, -1, -0, -1, -1, -1, -1, -1, -0, -1, -1, -1, -1, -1, -0, -1, -1, -0, -1,
			-1, -0, -0, -0, -1, -1, -1, -0, -0, -0, -1, -1, -1, -0, -0, -0, -1, -1, -1, -0, -0, -0, -0, -1,
			-1, -0, -1, -1, -1, -1, -1, -0, -1, -1, -1, -1, -1, -0, -1, -1, -1, -1, -1, -0, -1, -1, -1, -1,
			-1, -0, -1, -1, -1, -1, -1, -0, -0, -0, -0, -1, -1, -0, -0, -0, -0, -1, -1, -0, -1, -1, -1, -1,
			-1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1
		});

		assertImage(ti, ImageIO.read(getClass().getResourceAsStream("/wiki.feep.pbm")));

		ti = new TestImage(24, 7, new byte[] {
			-0, -0, -0, -0, -0, -0, -0, -0, -0, -0, -0, -0, -0, -0, -0, -0, -0, -0, -0, -0, -0, -0, -0, -0, 
			-0, 51, 51, 51, 51, -0, -0,119,119,119,119, -0, -0,-69,-69,-69,-69, -0, -0, -1, -1, -1, -1, -0, 
			-0, 51, -0, -0, -0, -0, -0,119, -0, -0, -0, -0, -0,-69, -0, -0, -0, -0, -0, -1, -0, -0, -1, -0, 
			-0, 51, 51, 51, -0, -0, -0,119,119,119, -0, -0, -0,-69,-69,-69, -0, -0, -0, -1, -1, -1, -1, -0, 
			-0, 51, -0, -0, -0, -0, -0,119, -0, -0, -0, -0, -0,-69, -0, -0, -0, -0, -0, -1, -0, -0, -0, -0, 
			-0, 51, -0, -0, -0, -0, -0,119,119,119,119, -0, -0,-69,-69,-69,-69, -0, -0, -1, -0, -0, -0, -0, 
			-0, -0, -0, -0, -0, -0, -0, -0, -0, -0, -0, -0, -0, -0, -0, -0, -0, -0, -0, -0, -0, -0, -0, -0
		});

		assertImage(ti, ImageIO.read(getClass().getResourceAsStream("/wiki.feep.pgm")));
	}

	/**
	 * Test the same 2 color bitmap image stored in different formats. 
	 * @throws IOException 
	 */
	public void testDifferentFormats() throws IOException {
		BufferedImage data1PbmPlain = ImageIO.read(
				getClass().getResourceAsStream("/potrace.data1.pbm.plain"));
		assertEquals(230, data1PbmPlain.getWidth());
		assertEquals(197, data1PbmPlain.getHeight());
		
		TestImage ti = new TestImage(230, 197, 
				((DataBufferByte) data1PbmPlain.getRaster().getDataBuffer()).getData());
		
		assertImage(ti, ImageIO.read(getClass().getResourceAsStream("/potrace.data1.pbm")));
		assertImage(ti, ImageIO.read(getClass().getResourceAsStream("/potrace.data1.pgm.plain")));
		assertImage(ti, ImageIO.read(getClass().getResourceAsStream("/potrace.data1.pgm")));

		// The color space of the two RGB versions need to converted.
		ColorSpace cs = ColorSpace.getInstance(ColorSpace.CS_GRAY);  
		ColorConvertOp op = new ColorConvertOp(cs, null);  
		
		assertImage(ti, op.filter(ImageIO.read(getClass().getResourceAsStream("/potrace.data1.ppm.plain")), null));
		assertImage(ti, op.filter(ImageIO.read(getClass().getResourceAsStream("/potrace.data1.ppm")), null));
	}

	/**
	 * Test the start of the raster when the first bytes of the raster
	 * contains data are valid whitespace text characters as well. 
	 * @throws IOException 
	 */
	public void testRasterStart() throws IOException {
		TestImage ti = new TestImage(5, 5, new byte[] {
				'\n', '\n', '\n', '\r', ' ', ' ', '\t', '\r', ' ', '\n', '\t', '\r', ' ', '\n', '\r',
				' ', '\n', '\r', ' ', '\n', '\t', '\r', ' ', 0, 0, -1, 0,	0, -1, 0,
				0, -1, 0, 0, -1, 0, 0, -1, 0, 0, -1, 0, 0, -1, 0,
				0, -1, 0, 0, -1, 0, 0, -1, 0, 0, -1, 0, 0, -1, 0,
				0, -1, 0, 0, -1, 0, 0, -1, 0, 0, -1, 0, 0, -1, 0
			});
		assertImage(ti, ImageIO.read(getClass().getResourceAsStream("/test.rasterstart.ppm")));
	}

	/**
	 * Test the case that here are not sufficient bytes defined in the buffer.
	 * @throws IOException 
	 */
	public void testDataError() throws IOException {
		// The test image only has 23 bytes int the file where there syoud 
		// have been 50. It is expected that the remaining lines/data repeats 
		// what was in the buffer at that point.
		TestImage ti = new TestImage(10, 5, new byte[] {
				0, 1, 2, 3, 4, 5, 6, 7, 8, 9,
				10, 11, 12, 13, 14, 15, 16, 17, 18, 19,
				20, 21, 22, 13, 14, 15, 16, 17, 18, 19,
				20, 21, 22, 13, 14, 15, 16, 17, 18, 19,
				20, 21, 22, 13, 14, 15, 16, 17, 18, 19
			});
		assertImage(ti, ImageIO.read(getClass().getResourceAsStream("/test.error.pgm")));
	}

	/**
	 * Test two images that were created by an external tool (Gimp). 
	 * @throws IOException 
	 */
	public void testCreatedByExternalTool() throws IOException {
		BufferedImage gimpBricks = ImageIO.read(
				getClass().getResourceAsStream("/gimp.bricks.pgm"));
		assertEquals(96, gimpBricks.getWidth());
		assertEquals(95, gimpBricks.getHeight());
		assertEquals(ColorSpace.TYPE_GRAY, 
				gimpBricks.getColorModel().getColorSpace().getType());
		BufferedImage gimpFabi = ImageIO.read(
				getClass().getResourceAsStream("/gimp.fabi.ppm"));
		assertEquals(300, gimpFabi.getWidth());
		assertEquals(400, gimpFabi.getHeight());
		assertEquals(ColorSpace.TYPE_RGB, 
				gimpFabi.getColorModel().getColorSpace().getType());
		BufferedImage potrace = ImageIO.read(
				getClass().getResourceAsStream("/potrace.data2.ppm"));
		assertEquals(64, potrace.getWidth());
		assertEquals(64, potrace.getHeight());
		assertEquals(ColorSpace.TYPE_RGB, 
				potrace.getColorModel().getColorSpace().getType());
	}
	
}
