package cz.clovekvtisni.coordinator.util;

import cz.clovekvtisni.coordinator.SecretInfo;
import cz.clovekvtisni.coordinator.domain.CanBeSigned;

import java.io.UnsupportedEncodingException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.text.DecimalFormat;
import java.text.DecimalFormatSymbols;
import java.util.*;
import java.util.logging.Logger;

/**
 * @author Tomas Zverina
 */
public class SignatureTool {

    private static Logger logger = Logger.getLogger(SignatureTool.class.getName());
	
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

	private static byte[] md5(String password) {
		MessageDigest md;
		try {
			md = MessageDigest.getInstance ("MD5");
		} catch (NoSuchAlgorithmException ex) {
			return new byte[0];
		}
		byte[] passwordBytes;
		try {
			passwordBytes = password.getBytes("UTF-8");
		} catch (UnsupportedEncodingException ex) {
			passwordBytes = new byte[0];
		}
		return md.digest(passwordBytes);
	}

	public static String md5Digest(String what) {
		if (what==null) return null;
		return asHex(md5(what));
	}
	

	public static String sign(Object what) {
		String whatToSign = ""+what+ SecretInfo.SECRET_SIGNATURE_KEY;
		String digest = md5Digest(whatToSign);
		String whatToSignForSecondTime = digest.substring(5,19);
		return md5Digest(whatToSignForSecondTime).substring(8,18).toUpperCase();
	}

    public static String signApi(String requestHash, String secret) {
        String whatToSign = requestHash + ";" + "api" + ";" + secret;
        String digest = md5Digest(whatToSign);
        String whatToSignForSecondTime = digest.substring(5,19);
        return md5Digest(whatToSignForSecondTime).substring(8,18).toUpperCase();
    }

    /**
     * pomocna funkcia pre vytvorenie hashu HTTP GET requestu
     *
     * @param requestParams parametre requestu
     * @return hash
     */
    public static String requestHash(Map<String, String> requestParams) {
        List<String> keys = new ArrayList<String>(requestParams.keySet());
        Collections.sort(keys);
        StringBuilder sb = new StringBuilder();
        for (String key : keys) {
            sb.append(key).append('=').append(requestParams.get(key)).append(';');
        }
        return md5Digest(sb.toString());
    }

    public static String computeHash(Object toHash) {
        logger.info("computing hash for " + toHash);
        StringBuilder b = new StringBuilder();
        computeHashBasis(toHash, b);
        return SignatureTool.md5Digest(b.toString());
    }

    @SuppressWarnings("unchecked")
    private static void computeHashBasis(Object toHash, StringBuilder b)  {
        if (toHash == null) {
            return;
        }
        b.append("#");
        if (toHash instanceof Number) {
//            logger.info("number");
            DecimalFormat df = new DecimalFormat("######################0.0#########");
            df.setDecimalFormatSymbols(new DecimalFormatSymbols(Locale.ENGLISH));
            b.append(df.format(toHash));

        } else if (toHash instanceof Boolean) {
//            logger.info("boolean");
            b.append(((Boolean)toHash) ? "1" : "0");

        } else if (toHash instanceof String) {
//            logger.info("string");
            b.append((String)toHash);

        } else if (toHash instanceof CanBeSigned) {
//            logger.info("CanBeSigned");
            b.append(((CanBeSigned) toHash).getSignature());
        } else if (toHash instanceof Map) {
//            logger.info("map");
            TreeSet keys = new TreeSet(((Map) toHash).keySet());
            for (Object key : keys) {
                b.append(key.toString());
                computeHashBasis(((Map) toHash).get(key), b);
            }
        } else if (toHash.getClass().isArray()) {
//            logger.info("array");
            Object[] arr = (Object[]) toHash;
            for (int a=0; a<arr.length; a++) {
                b.append(a);
                computeHashBasis(arr[a], b);
            }
        } else if (toHash instanceof List) {
//            logger.info("list");
            List arr = (List) toHash;
            for (int a=0; a<arr.size(); a++) {
                b.append(a);
                computeHashBasis(arr.get(a), b);
            }
        } else {
            throw new IllegalStateException("Dont know how to compute hash basis for: "+toHash+" ("+toHash.getClass()+")");
        }
//        logger.info("hash basis: " + b);
    }
}
