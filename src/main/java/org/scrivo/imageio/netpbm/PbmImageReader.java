package org.scrivo.imageio.netpbm;

import java.awt.Point;
import java.awt.Rectangle;
import java.awt.color.ColorSpace;
import java.awt.image.BufferedImage;
import java.awt.image.DataBuffer;
import java.awt.image.DataBufferByte;
import java.awt.image.Raster;
import java.awt.image.WritableRaster;
import java.io.IOException;
import java.util.Collections;
import java.util.Iterator;

import javax.imageio.IIOException;
import javax.imageio.ImageReadParam;
import javax.imageio.ImageReader;
import javax.imageio.ImageTypeSpecifier;
import javax.imageio.metadata.IIOMetadata;
import javax.imageio.spi.ImageReaderSpi;
import javax.imageio.stream.ImageInputStream;

/**
 * This class is responsible for responding to queries about the images actually
 * stored in an input file or stream, as well as the actual reading of images,
 * thumbnails, and metadata.
 * 
 * Note: this implementation does not include thumbnails and metadata.
 * 
 * @See https://docs.oracle.com/javase/8/docs/technotes/guides/imageio/spec/extending.fm1.html
 */
public class PbmImageReader extends ImageReader {

	NetpbmImageInputStream stream = null;
	NetpbmHeader header = null;

	protected PbmImageReader(ImageReaderSpi originatingProvider) {
		super(originatingProvider);
	}

	private void checkIndex(int imageIndex) {
		if (imageIndex != 0) {
			throw new IndexOutOfBoundsException("Bad index");
		}
	}

	@Override
	public void setInput(Object input, boolean seekForwardOnly,
			boolean ignoreMetadata) {
		super.setInput(input, seekForwardOnly, ignoreMetadata);
		stream = new NetpbmImageInputStream((ImageInputStream) input);
		header = null;
	}

	@Override
	public int getNumImages(boolean allowSearch) throws IOException {
		// Netpbm images actually support more than one image but this
		// is not supported by this reader.
		return 1;
	}

	@Override
	public int getWidth(int imageIndex) throws IOException {
		checkIndex(imageIndex);
		readHeader();
		return header.getWidth();
	}

	@Override
	public int getHeight(int imageIndex) throws IOException {
		checkIndex(imageIndex);
		readHeader();
		return header.getHeight();
	}

	@Override
	public Iterator<ImageTypeSpecifier> getImageTypes(int imageIndex)
			throws IOException {
		checkIndex(imageIndex);
		readHeader();
		ImageTypeSpecifier imageTypeSpecifier;
		if (header.isPixmap()) {
			imageTypeSpecifier = ImageTypeSpecifier.createInterleaved(
					ColorSpace.getInstance(ColorSpace.CS_sRGB),
					new int[] { 0, 1, 2 }, DataBuffer.TYPE_BYTE, false, false);

		} else {
			imageTypeSpecifier = ImageTypeSpecifier.createGrayscale(
					8, DataBuffer.TYPE_BYTE, false);
		}
		return Collections.singletonList(imageTypeSpecifier).iterator();
	}

	@Override
	public IIOMetadata getStreamMetadata() throws IOException {
		return null;
	}

	@Override
	public IIOMetadata getImageMetadata(int imageIndex) throws IOException {
		return null;
	}

	private void readHeader() throws IIOException {
		if (null == header) {
			header = NetpbmUtil.readHeader(stream);
		}
	}

	@Override
	public BufferedImage read(int imageIndex, ImageReadParam param)
			throws IOException {

		if (stream == null) {
			throw new IllegalStateException("PbmImageReader");
		}

		checkIndex(imageIndex);
		readHeader();

		if (null == param) {
			param = new ImageReadParam();
		}

		// Compute initial source region, clip against destination later
		Rectangle sourceRegion = getSourceRegion(param, header.getWidth(),
				header.getHeight());

		// Get the specified detination image or create a new one
		BufferedImage dst = getDestination(param, getImageTypes(0),
				header.getWidth(), header.getHeight());
		// Ensure band settings from param are compatible with images
		int inputBands = header.isPixmap() ? 3 : 1;
		checkReadParamBandSettings(param, inputBands,
				dst.getSampleModel().getNumBands());

		int[] bandOffsets = new int[inputBands];
		for (int i = 0; i < inputBands; i++) {
			bandOffsets[i] = i;
		}
		int bytesPerRow = header.getWidth() * inputBands;
		DataBufferByte rowDB = new DataBufferByte(bytesPerRow);
		WritableRaster rowRas = Raster.createInterleavedRaster(rowDB,
				header.getWidth(), 1, bytesPerRow, inputBands, bandOffsets,
				new Point(0, 0));

		WritableRaster imRas = dst.getWritableTile(0, 0);

		// Create a child raster exposing only the desired source bands
		if (param.getSourceBands() != null) {
			rowRas = rowRas.createWritableChild(0, 0, header.getWidth(),
					1, 0, 0, param.getSourceBands());
		}

		// Create a child raster exposing only the desired dest bands
		if (param.getDestinationBands() != null) {
			imRas = imRas.createWritableChild(0, 0, imRas.getWidth(),
					imRas.getHeight(), 0, 0, param.getDestinationBands());
		}

		rasterData(param, sourceRegion, rowDB, rowRas, imRas);

		return dst;
	}

	private void rasterData(ImageReadParam param, Rectangle srcRegion,
			DataBufferByte rowDB, WritableRaster rowRas,
			WritableRaster imRas) throws IIOException {

		for (int srcY = 0; srcY < header.getHeight(); srcY++) {

			NetpbmUtil.readRow(stream, header, rowDB.getData(), srcY);

			// Reject rows that lie outside the source region,
			// or which aren't part of the subsampling
			if (srcY < srcRegion.y || srcY >= srcRegion.y + srcRegion.height ||
					(srcY - srcRegion.y) % param.getSourceYSubsampling() == 0) {
				// Determine where the row will go in the destination
				int dstY = param.getDestinationOffset().y +
						(srcY - srcRegion.y) / param.getSourceYSubsampling();
				if (dstY >= imRas.getMinY()
						&& dstY < imRas.getMinY() + imRas.getHeight()) {
					rasterRow(param, srcRegion, rowRas, imRas, dstY);
				}
			}
		}
	}

	private void rasterRow(ImageReadParam param, Rectangle srcRegion,
			WritableRaster rowRas, WritableRaster imRas, int dstY) {

		// Create an int[] that can a single pixel
		int[] pixel = rowRas.getPixel(0, 0, (int[]) null);

		// Copy each (subsampled) source pixel into imRas
		for (int srcX = srcRegion.x; srcX < srcRegion.x
				+ srcRegion.width; srcX++) {
			if ((srcX - srcRegion.x) % param.getSourceXSubsampling() == 0) {
				int dstX = param.getDestinationOffset().x +
						(srcX - srcRegion.x) / param.getSourceXSubsampling();
				if (dstX >= imRas.getMinX()
						&& dstX < imRas.getMinX() + imRas.getWidth()) {
					// Copy the pixel, sub-banding is done automatically
					rowRas.getPixel(srcX, 0, pixel);
					imRas.setPixel(dstX, dstY, pixel);
				}
			}
		}
	}

}
