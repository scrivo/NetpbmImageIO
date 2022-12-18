package org.scrivo.imageio.netpbm;

/**
 * Header for Netbpm files. A Netbpm header contains a signature, the width
 * and height of the image and a maxColor value. 
 */
class NetpbmHeader {

	private NetpbmSignature signature = null;
	private int width;
	private int height;
	private int maxColor;
	
	/**
	 * Get the signature of the image.
	 */
	public NetpbmSignature getSignature() {
		return signature;
	}
	/**
	 * Set the signature of the image.
	 */
	public void setSignature(NetpbmSignature signature) {
		this.signature = signature;
	}
	/**
	 * @return 
	 * 		The width of the image.
	 */
	public int getWidth() {
		return width;
	}
	/**
	 * @param width
	 * 		The width of the image.
	 */
	public void setWidth(int width) {
		this.width = width;
	}
	/**
	 * @return 
	 * 		The height of the image.
	 */
	public int getHeight() {
		return height;
	}
	/**
	 * @param height
	 * 		The height of the image.
	 */
	public void setHeight(int height) {
		this.height = height;
	}
	/**
	 * @return 
	 * 		The maximum color value for the colors in the bitmap.
	 */
	public int getMaxColor() {
		return maxColor;
	}
	/**
	 * @param maxColorValue
	 * 		The maximum color value for the colors in the bitmap.
	 */
	public void setMaxColor(int maxColorValue) {
		this.maxColor = maxColorValue;
	}
	
	/**
	 * @return
	 * 		True if the the image is a bitmap (i.e. 2 colors).
	 */
	public boolean isBitmap() {
		return NetpbmSignature.P1 == signature  
				|| NetpbmSignature.P4 == signature;
	}
	/**
	 * @return
	 * 		True if the image is a grayscale image.
	 */
	public boolean isGraymap() {
		return NetpbmSignature.P2 == signature  
				|| NetpbmSignature.P5 == signature;
	}
	/**
	 * @return
	 * 		True if the image is a color (RGB pixels) image.
	 */
	public boolean isPixmap() {
		return NetpbmSignature.P3 == signature  
				|| NetpbmSignature.P6 == signature;
	}
	
}
