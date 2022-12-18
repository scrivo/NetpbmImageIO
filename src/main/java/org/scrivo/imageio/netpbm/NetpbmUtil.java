package org.scrivo.imageio.netpbm;

import java.io.IOException;

import javax.imageio.IIOException;
import javax.imageio.stream.ImageInputStream;

/**
 * The NetpbmUtil class contains all methods to work with Netpbm image files.
 * It contains a method for reading the file header and a methods decoding
 * the image data contains in the files.
 */
class NetpbmUtil {
	
	private NetpbmUtil() {} 

	/**
	 * Read a Netpbm image header from a (NetpbmImage)InputStream. Supposedly 
	 * the stream has just been opened an its position is at the start of
	 * the file. 
	 * @param stream
	 * 		The stream from which to read the image.
	 * @return
	 * 		The header of the image.
	 * @throws IIOException
	 */
	static NetpbmHeader readHeader(NetpbmImageInputStream stream) 
			throws IIOException {
		NetpbmHeader header = new NetpbmHeader();
		try {
			// First check if we have an input stream.
			if (stream == null) {
				throw new IllegalStateException("No input stream");
			}
			// Read the image signature.
			String sigRead = stream.readStringToken();
			for (NetpbmSignature sig: NetpbmSignature.values()) {
				if (sigRead.startsWith(sig.toString())) {
					header.setSignature(sig);
					break;
				}
			}
			if (null == header.getSignature()) {
				throw new IIOException("Bad file signature!");
			}
			// Get the image width and height.
			header.setWidth(Integer.parseInt(stream.readStringToken()));
			header.setHeight(Integer.parseInt(stream.readStringToken()));
			if (header.isBitmap()) {
				// If the image is a bitmap the maxColor is limited to 1.
				header.setMaxColor(1);
			} else {
				// Get the maxColor value (Grayscale and bitmap).
				header.setMaxColor(Integer.parseInt(stream.readStringToken()));
			}
		} catch (Exception e) {
			throw new IIOException("Error reading header", e);
		}
		return header;
	}

	/**
	 * Read an image row of image data a (NetpbmImage)InputStream. Supposedly
	 * the stream is pointed at the start of the bitmap data in the file or
	 * any given line ih the bitmap. 
	 * @param stream
	 * 		The stream from which to read the image.
	 * @param header
	 * 		The header data of the image we are trying to read.
	 * @param rowBuf
	 * 		The buffer to store the output data (one bitmap line).
	 * @param lineNo
	 * 		The number of the line we are trying to read.
	 * @throws IIOException
	 */
	static void readRow(NetpbmImageInputStream stream, NetpbmHeader header, 
			byte[] rowBuf, int lineNo) throws IIOException {
		try {
			switch (header.getSignature()) {
			case P1:
				readBitDataLine(stream, rowBuf);
				break;
			case P2:
			case P3:
				readByteDataLine(stream, rowBuf, header.getMaxColor());
				break;
			case P4:
				readBitDataLineRaw(stream, rowBuf);
				break;
			case P5:
			case P6:
				readByteDataLineRaw(stream, rowBuf, header.getMaxColor());
				break;
			}
		} catch (IOException e) {
			throw new IIOException("Error reading line " + lineNo, e);
		}
	}

	/**
	 * Read plain text bits ("1011 1100") from the stream into an image
	 * row. Note that the colors are inverted: '0' becomes -1 (255, white)
	 * and '1' becomes 0 (black). 
	 * @param stream
	 * 		The stream from which to read the image.
	 * @param rowBuf
	 * 		The buffer to store the output data (one bitmap line).
	 * @throws IOException
	 */
	static void readBitDataLine(NetpbmImageInputStream stream, byte[] rowBuf) 
			throws IOException {
		for (int i=0; i<rowBuf.length; i++) {
			int wBit = stream.readCharacterToken();
			if (-1 != wBit) {
				rowBuf[i] = (byte)(wBit - '1');
			}
		}
	}

	/**
	 * Read binary bitmap image date into an image row. Note that the image
	 * data is stored in bits, not bytes, i.e. each byte holds the data for
	 * 8 pixels. 
	 * @param stream
	 * 		The stream from which to read the image.
	 * @param rowBuf
	 * 		The buffer to store the output data (one bitmap line).
	 * @throws IOException
	 */
	static void readBitDataLineRaw(ImageInputStream stream, byte[] rowBuf) 
			throws IOException {
		byte[] buff = new byte[(rowBuf.length+7)/8];
		int numRead = stream.read(buff);
		for (int i=0; i<rowBuf.length; i+=8) {
			for (int j=0; j<8 && j+i<rowBuf.length && i+j<numRead*8; j++) {
				rowBuf[j+i] = (buff[i/8] >> (7-j) & 0x01) == 1 ? 0 : (byte)255;
			}
		}
	}
	
	/**
	 * Read string tokens (grayscale values or RGB tokens) from the stream into an
	 * image row.
	 * 
	 * @param stream
	 * 		The stream from which to read the image.
	 * @param rowBuf
	 * 		The buffer to store the output data (one bitmap line).
	 * @param maxColorValue
	 * 		The maximum color value supported by the image.
	 * @throws IOException
	 */
	static void readByteDataLine(NetpbmImageInputStream stream, byte[] rowBuf, 
			int maxColorValue) throws IOException {
		for (int i=0; i<rowBuf.length; i++) {
			String wBit = stream.readStringToken();
			if (null == wBit) {
				return;
			}
			int col = Integer.parseInt(wBit);
			rowBuf[i] = (byte)(col * 255 / maxColorValue);
		}
	}
	
	/**
	 * Read binary image data (grayscale or RGB triplets) into an image row.
	 * @param stream
	 * 		The stream from which to read the image.
	 * @param rowBuf
	 * 		The buffer to store the output data (one bitmap line).
	 * @param maxColorValue
	 * 		The maximum color value supported by the image.
	 * @throws IOException
	 */
	static void readByteDataLineRaw(ImageInputStream stream, byte[] rowBuf, 
			int maxColorValue) throws IOException {
		int numRead = stream.read(rowBuf);
		for (int i=0; i<rowBuf.length && i < numRead; i++) {
			rowBuf[i] = (byte) (rowBuf[i] * 255 / maxColorValue);
		}
	}

}
