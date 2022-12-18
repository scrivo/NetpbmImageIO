package org.scrivo.imageio.netpbm;

/**
 * Signatures of Netpbm image formats supported by this software.
 */
enum NetpbmSignature {
	/** Bitmap image (2 color) file with plain text image data */
	P1,
	/** Grayscale image file with plain text image data */
	P2,
	/** Color image file with plain text image data */
	P3,
	/** Bitmap image (2 color) file with binary image data */
	P4,
	/** Grayscale image with binary image data */
	P5,
	/** Color image with binary image data */
	P6
}