package org.scrivo.imageio.netpbm;

import java.io.IOException;

import javax.imageio.stream.ImageInputStream;
import javax.imageio.stream.ImageInputStreamImpl;

/**
 * NetpbmImageInputStream is a buffered (Image)InputStream that supports
 * binary access to the image stream for reading the binary content data 
 * and text access for reading numbers and characters from the image stream.
 * 
 * For reading text data from the stream methods the readStringToken and 
 * readCharacterToken were added. One to read numbers (as string) from the
 * stream and the other to read bits (as chars).
 * 
 * Note: an earlier implementation used ImageInputStreamImpl.readLine to
 * parse character data from the stream, for whatever reason this proved
 * to be unacceptably slow.
 */
class NetpbmImageInputStream extends ImageInputStreamImpl {

	/** Size of the read buffer. */
	private static final int BUF_SIZE = 2048;
	/** Constant for indicate end-of-line. */
	private static final int EOF = -1;
	/** Constant for the stream comment character. */
	private static final int COMMENT = '#';
	
	/** The read buffer. */
	private byte[] buffer = new byte[BUF_SIZE];
	/** The current write position in the buffer. */
	private int pos = 0;
	/** The last byte read. */
	private int tokenChr = EOF;
	/** 
	 * Upper bound (exclusive) of the writeable part of the buffer 
	 * (=BUF_SIZE or smaller if less than BUF_SIZE bytes were read from the 
	 * stream). 
	 */
	private int maxPos = 0;
	
	/** The image input stream that supplies the data. */
	private ImageInputStream is;

	/**
	 * Test if a byte from the stream is a white space character or a
	 * comment character.
	 * @param c
	 * 		The character to test.
	 * @return
	 * 		True of given character is a white space/comment character.
	 */
	private boolean witeSpace(final int c) {
		return (c == ' ' || c == '\n' || c == '#' || c == '\r' || c == '\t');
	}

	/**
	 * Test if a byte from the stream is a line break character.
	 * @param c
	 * 		The character to test.
	 * @return
	 * 		True of given character is a line break character.
	 */
	private boolean lineBreak(final int c) {
		return (c == '\n' || c == '\r');
	}

	/**
	 * Construct a NetpbmImageInputStream using an ImageInputStream.
	 * @param is
	 * 		The image input stream to use.	
	 */
	public NetpbmImageInputStream(final ImageInputStream is) {
		this.is = is;
	}

	@Override
	public int read() throws IOException {
		// Position 0 indicates an empty input buffer, so fill it up.
		if (pos == 0) {
			// maxPos will be input buffer length unless there wasn't 
			// sufficient data in the stream (or eof).
			maxPos = is.read(buffer);
			// Bail out of were at the end of the file. 
			if (maxPos < 0 ) {
				return EOF;
			}
		}
		// Read the byte at position pos ...
		int res = buffer[pos] & 0xff;
		// ... increase the position ...
		pos++; 
		// ... and set it to zero if we went over the upper bound.
		if (pos >= maxPos || pos >= BUF_SIZE) {
			pos = 0;
		}
		// Return what we have read.
		return res;
	}

	@Override
    public int read(final byte[] b, final int off, final int len) 
    		throws IOException {
		// Position 0 indicates an empty input buffer, so fill it up.
		if (pos == 0) {
			// maxPos will be input buffer length unless there wasn't 
			// sufficient data in the stream (or eof).
			maxPos = is.read(buffer);
			// Bail out of were at the end of the file (ends recursion). 
			if (maxPos < 0) {
				return EOF; 
			}
		}
		// The number of bytes available in the input buffer.
		int available = maxPos-pos;
		// The number of bytes needed too fill the output buffer.
		int needed = len-off;
		// If we need less bytes than there are available ...
		if (needed <= available) {
			// ... (*) copy what we need, ...
			System.arraycopy(buffer, pos, b, off, needed);
			// ... set new current pos (note: will not go over upper bound) ...
			pos += needed;  
			// ... and return the no of bytes added to buffer (ends recursion).
			return needed;
		}
		// Else we need more, so copy what is available ... 
		System.arraycopy(buffer, pos, b, off, available);
		// ... set new current pos to 0 to indicate that we need data again ...
		pos = 0;
		// ... and recurse into read again, which will read data from the
		// stream again. So we'll be adding chunks of BUF_SIZE data to 
		// the output buffer until the input buffer holds more then we need
		// (*) and recursion is stopped.
		int numRead = read(b, off+available, len);
		// We're done, return the sum of what we've read here (available) and
		// what was read in the recursive calls (numBytes). EOF is a special 
		// case here: it returns a marker (-1) while actually 0 bytes were
		// read, so we need to fix this.
		return available + (numRead == EOF ? 0 : numRead);
	}
	
	/**
	 * Gets the next string token form the stream. A string token is just
	 * a sequence of characters that does not contain white space (or a
	 * comment), i.e. the string '0010 1111' holds 2 tokens.
	 * @return
	 * 		The next string token found in the stream (never an empty string),
	 * 		or null if we are at the end of the file.
	 * @throws IOException
	 */
	public String readStringToken() throws IOException {
		// Skip all whitespace first so that tokenChr holds the first 
		// character of the token.
        if (EOF != skipWhiteSpaceAndComments()) {
        	// A string builder to build the token. 
            StringBuilder input = new StringBuilder();
            // As long as we're not encountering white space ...
            while (!witeSpace(tokenChr)) {
            	// add read tokens to builder (filter out EOF (initial value!).
            	if (tokenChr >= 0) {
            		input.append((char)tokenChr);
            	}
            	// Read a new token to check.
            	tokenChr = read();
            	// Short circuit EOF, if we've found anything return this, if
            	// not return null.
            	if (EOF == tokenChr ) {
            		return 0 == input.length() ? null : input.toString();
            	}
            }
            // End of the token, there is white space in tokenChar.
            return input.toString();
        }
        // Whitespace trailing in the file.
    	return null;
    }

	/**
	 * Read a single character token from the stream. This method differs
	 * from readStringToken in that the tokens do not necessarily need 
	 * a whitespace seperator, i.e. the string '0010 1111' holds 8 tokens.
	 * @return
	 * 		The next character token from the stream, or EOF if end of file.
	 * @throws IOException
	 */
	public int readCharacterToken() throws IOException {
		// Skip all whitespace first so that tokenChr holds the first 
		// character of the token.
        int input = skipWhiteSpaceAndComments();
        if (EOF != input) {
        	// Return the character token ... 
    		input = tokenChr;
    		// ... and read the next one if we are not EOF.
        	if (EOF != tokenChr) {
            	tokenChr = read();
        	}
        }
        // Return the character token (of EOF).
        return input;
    }

	/**
	 * Skip white space and comments in the stream. If the method exits
	 * normally (returns 0) then tokenChr will hold the first byte after 
	 * the white space. 
	 * @return
	 * 		Return EOF (-1) if we reached the end of the stream, 0 if not. 
	 * @throws IOException
	 */
	private int skipWhiteSpaceAndComments() throws IOException {
		// As long as we're encountering white space or a comment keep
		// reading from the input. 
		while (witeSpace(tokenChr)) {
			// In the case of a comment, skip to end of line.
        	if (COMMENT == tokenChr) {
        		// Read characters until line break (or eof).
        		while (!lineBreak(tokenChr)) {
            		tokenChr = read();
                	if (EOF == tokenChr) {
                		return EOF;
                	}
        		}
        	} else {
        		// Read characters while white space, comment or or eof.
        		tokenChr = read();
            	if (EOF == tokenChr) {
            		return EOF;
            	}
        	}
        }
		// We're done, tokenChr now hold the first character after the 
		// white space.
		return 0;
	}

}