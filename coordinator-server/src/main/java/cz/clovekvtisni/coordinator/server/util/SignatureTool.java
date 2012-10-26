package cz.clovekvtisni.coordinator.server.util;

import java.io.UnsupportedEncodingException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

/**
 * @author Tomas Zverina
 */
public class SignatureTool {
	
	private String secretSeed;
	
	public SignatureTool(String secretSeed) {
		this.secretSeed = secretSeed;
	}

	/**
	 * Turns array of bytes into string
	 *
	 * @param buf    Array of bytes to convert to hex string
	 * 
	 */
	public static String asHex (byte[] buf) {
        StringBuilder strbuf = new StringBuilder(buf.length * 2);
		int i;
		for (i = 0; i < buf.length; i++) {
			if ((buf[i] & 0xff) < 0x10)
				strbuf.append("0");
			
			strbuf.append(Long.toString(buf[i] & 0xff, 16));
		}
		return strbuf.toString().toLowerCase();
	}

	/** From a password, generates a 128-bit key.
	 * The method is very simple (simply generating a MD5 hash)
	 * and is not advisable to use it; use some another
	 * method such as that defined in "Password-Based Encryption" schemes.
	 */
	private static byte[] md5(String password) {
		MessageDigest md = null;
		try {
			md = MessageDigest.getInstance("MD5");
		} catch (NoSuchAlgorithmException ex) {
			return new byte[0];
		}
		byte[] passwordBytes = null;
		try {
			passwordBytes = password.getBytes("UTF-8");
		} catch (UnsupportedEncodingException ex) {
			passwordBytes = new byte[0];
		}
		return md.digest(passwordBytes);
	}

	/**
	 * Creates md5digest of a specified string and returns it's hexadecimal interpretation as a string.
	 * @param what
	 * @return
	 */
	public static String md5Digest(String what) {
		if (what==null) return null;
		return asHex(md5(what));
	}

	
	/**
	 * Creates secret hash of a specified string. Uppercase, 10 chars long.
	 * 
	 * @param what
	 * @return
	 */
	public String sign(Object what) {
		String whatToSign = ""+what+secretSeed;
		String digest = md5Digest(whatToSign);
		String whatToSignForSecondTime = digest.substring(5,19);
		return md5Digest(whatToSignForSecondTime).substring(8,18).toUpperCase();
	}
	
}
