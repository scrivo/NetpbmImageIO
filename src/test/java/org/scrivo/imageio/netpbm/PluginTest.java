package org.scrivo.imageio.netpbm;

import java.io.IOException;
import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;

import javax.imageio.ImageIO;

import junit.framework.TestCase;

public class PluginTest extends TestCase {

	/**
	 * Test if the ImageIO plugin is found an initialized.
	 */
	public void testPlugin() throws IOException {
		Set<String> formatNames = new HashSet<>(Arrays.asList(
				ImageIO.getReaderFormatNames()));
		assertTrue(formatNames.contains("pbm"));
		assertTrue(formatNames.contains("pgm"));
		assertTrue(formatNames.contains("ppm"));

		Set<String> mimeTypes = new HashSet<>(Arrays.asList(
				ImageIO.getReaderMIMETypes()));
		assertTrue(mimeTypes.contains("image/x-portable-pixmap"));
		assertTrue(mimeTypes.contains("image/x-portable-bitmap"));
		assertTrue(mimeTypes.contains("image/x-portable-graymap"));

		Set<String> suffixes = new HashSet<>(Arrays.asList(
				ImageIO.getReaderFileSuffixes()));
		assertTrue(suffixes.contains("pbm"));
		assertTrue(suffixes.contains("PGM"));
		assertTrue(suffixes.contains("ppm.plain"));
	}

}
