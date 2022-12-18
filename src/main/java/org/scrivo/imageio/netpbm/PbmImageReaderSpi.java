package org.scrivo.imageio.netpbm;

import java.io.IOException;
import java.util.Locale;

import javax.imageio.ImageReader;
import javax.imageio.spi.ImageReaderSpi;
import javax.imageio.stream.ImageInputStream;

/**
 * The PbmImageReaderSpi class provides information about the plug-in, including
 * the vendor name, plug-in version string and description, format name, file
 * suffixes associated with the format, MIME types associated with the format,
 * input source classes that the plug-in can handle, and the ImageWriterSpis of
 * plug-ins that are able to interoperate specially with the reader. It also
 * must provide an implementation of the canDecodeInput method, which is used to
 * locate plug-ins based on the contents of a source image file.
 * 
 * @See https://docs.oracle.com/javase/8/docs/technotes/guides/imageio/spec/extending.fm1.html
 */
public class PbmImageReaderSpi extends ImageReaderSpi {

	private static final String VENDOR_NAME = "scrivo.org";
	private static final String VERSION = "1.0";
	private static final String[] NAMES = {
			"pbm", "PBM", "pgm", "PGM", "ppm", "PPM" };
	private static final String[] SUFFIXES = { "pbm", "PBM", "pgm",
			"PGM", "ppm", "PPM", "pbm.plain", "pgm.plain", "ppm.plain" };
	private static final String[] MIME_TYPES = { "image/x-portable-bitmap",
			"image/x-portable-graymap", "image/x-portable-pixmap" };
	private static final Class<?>[] INPUT_TYPES = { ImageInputStream.class };
	private static final String DESCRIPTION = "ImageIO plugin for Netpbm (pbm, pgm, ppm) files.";

	public PbmImageReaderSpi() {
		super(
				/* String vendorName */ VENDOR_NAME,
				/* String version */ VERSION,
				/* String[] names */ NAMES,
				/* String[] suffixes */ SUFFIXES,
				/* String[] MIMETypes */ MIME_TYPES,
				/* String readerClassName */ PbmImageReader.class.getName(),
				/* Class<?>[] inputTypes */ INPUT_TYPES,
				/* String[] writerSpiNames */ null,
				/* boolean supportsStandardStreamMetadataFormat */ false,
				/* String nativeStreamMetadataFormatName */ null,
				/* String nativeStreamMetadataFormatClassName */ null,
				/* String[] extraStreamMetadataFormatNames */ null,
				/* String[] extraStreamMetadataFormatClassNames */ null,
				/* boolean supportsStandardImageMetadataFormat */ false,
				/* String nativeImageMetadataFormatName */ null,
				/* String nativeImageMetadataFormatClassName */ null,
				/* String[] extraImageMetadataFormatNames */ null,
				/* String[] extraImageMetadataFormatClassNames */ null);
	}

	@Override
	public String getDescription(Locale locale) {
		return DESCRIPTION;
	}

	/**
	 * The canDecodeInput method is responsible for determining two things: 
	 * first, whether the input parameter is an instance of a class that the 
	 * plug-in can understand, and second, whether the file contents appear to 
	 * be in the format handled by the plug-in. It must leave its input in the 
	 * same state as it was when it was passed in.
	 */
	@Override
	public boolean canDecodeInput(Object input) {
		if (!(input instanceof ImageInputStream)) {
			return false;
		}
		ImageInputStream stream = (ImageInputStream) input;
		byte[] b = new byte[2];
		try {
			stream.mark();
			stream.readFully(b);
			stream.reset();
		} catch (IOException e) {
			return false;
		}
		return (b[0] == (byte) 'P' && b[1] >= (byte) '1' && b[1] <= (byte) '6');
	}

	@Override
	public ImageReader createReaderInstance(Object extension) {
		return new PbmImageReader(this);
	}

}
