package org.scrivo.imageio.netpbm;

import java.awt.Image;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Map.Entry;

import javax.imageio.ImageIO;
import javax.swing.ImageIcon;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.SwingUtilities;

/**
 * A simple test application to show test images used in the unit test.
 */
public class ShowTestImages {

	/**
	 * A map with the test images and their scaling factors.
	 */
	private static final Map<String, Integer> TEST_IMAGES = new LinkedHashMap<>();
	{
		TEST_IMAGES.put("/wiki.test01.pbm", 10);
		TEST_IMAGES.put("/wiki.test02.pbm", 10);
		TEST_IMAGES.put("/wiki.feep.pbm", 10);
		TEST_IMAGES.put("/wiki.feep.pgm", 10);

		TEST_IMAGES.put("/wiki.sample1.ppm", 100);
		TEST_IMAGES.put("/wiki.sample2.ppm", 100);
		TEST_IMAGES.put("/wiki.sample3.ppm", 100);
		TEST_IMAGES.put("/wiki.sample4.ppm", 100);

		TEST_IMAGES.put("/potrace.data1.pbm", 4);
		TEST_IMAGES.put("/potrace.data1.pbm.plain", 4);
		TEST_IMAGES.put("/potrace.data1.pgm", 4);
		TEST_IMAGES.put("/potrace.data1.pgm.plain", 4);
		TEST_IMAGES.put("/potrace.data1.ppm", 4);
		TEST_IMAGES.put("/potrace.data1.ppm.plain", 4);

		TEST_IMAGES.put("/potrace.data2.ppm", 10);

		TEST_IMAGES.put("/test.error.pgm", 40);
		TEST_IMAGES.put("/test.rasterstart.ppm", 40);

		TEST_IMAGES.put("/gimp.bricks.pgm", 4);
		TEST_IMAGES.put("/gimp.fabi.ppm", 2);
	}

	/**
	 * Class to load and show an image in a JFrame with a specified scale factor.
	 */
	private static class ImageJFrame {
		ImageJFrame(String image, int scaleFactor) throws IOException {
			JFrame f = new JFrame(image);
			f.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
			BufferedImage img = ImageIO.read(getClass().getResourceAsStream(image));
			ImageIcon icon = new ImageIcon(img.getScaledInstance(
					img.getWidth() * scaleFactor, img.getHeight() * scaleFactor,
					Image.SCALE_DEFAULT));
			f.add(new JLabel(icon));
			f.pack();
			f.setVisible(true);
		}
	}

	/**
	 * Loop through the list of images, load them one by one and show the load time
	 * for each image.
	 * 
	 * @throws IOException
	 */
	private void loadTimes() throws IOException {
		for (Entry<String, Integer> image : TEST_IMAGES.entrySet()) {
			long startTime = System.currentTimeMillis();
			ImageIO.read(getClass().getResourceAsStream(image.getKey()));
			System.out.format("%s: %d ms\n", image.getKey(),
					System.currentTimeMillis() - startTime);
		}
	}

	/**
	 * Loop through the list of images and show each one in a JFrame.
	 */
	private void showAllImages() {
		for (Entry<String, Integer> image : TEST_IMAGES.entrySet()) {
			SwingUtilities.invokeLater(() -> {
				try {
					new ImageJFrame(image.getKey(), image.getValue());
				} catch (IOException e) {
					throw new RuntimeException(e);
				}
			});
		}
	}

	/**
	 * Trivial test app, it (1) shows a list of image types supported by ImageIO
	 * (now including Netpbm files), (2) loads test images from the resources folder
	 * and displays their load times and (3) loads test images from the resources
	 * folder and displays them.
	 * 
	 * @param args Not used.
	 * @throws IOException
	 */
	public static void main(String[] args) throws IOException {

		for (String format : ImageIO.getReaderFormatNames()) {
			System.out.println(format);
		}

		new ShowTestImages().loadTimes();

		new ShowTestImages().showAllImages();
	}

}
