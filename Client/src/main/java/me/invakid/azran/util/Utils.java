package me.invakid.azran.util;

import javax.crypto.Cipher;
import javax.crypto.spec.SecretKeySpec;
import java.io.File;
import java.io.UnsupportedEncodingException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Arrays;

public class Utils {
	private Utils() {
	}

	public static String formatTime(int secs) {
		if (secs < 60)
			return secs + " second(s)";

		double mins = (double) secs / 60d;
		if (mins < 60)
			return (int) mins + " minute(s)";

		double hrs = mins / 60d;
		if (hrs < 24)
			return (int) hrs + " hour(s)";

		double days = hrs / 24d;
		return (int) days + " day(s)";
	}

	public static boolean isInt(String s) {
		try {
			Integer.parseInt(s);
			return true;
		} catch (Exception e) {
		}

		return false;
	}

	public static boolean isInt(char c) {
		int i = (int) c - 48;
		return i >= 0 && i <= 9;
	}

	public static int getInt(char c) {
		return (int) c - 48;
	}

	private static SecretKeySpec secretKey;
	private static byte[] key;

	public static void setKey(String myKey) {
		MessageDigest sha = null;
		try {
			key = myKey.getBytes("UTF-8");
			sha = MessageDigest.getInstance("SHA-1");
			key = sha.digest(key);
			key = Arrays.copyOf(key, 16);
			secretKey = new SecretKeySpec(key, "AES");
		} catch (NoSuchAlgorithmException e) {
			e.printStackTrace();
		} catch (UnsupportedEncodingException e) {
			e.printStackTrace();
		}
	}

	public static byte[] encrypt(byte[] b, String secret) {
		try {
			setKey(secret);
			Cipher cipher = Cipher.getInstance("AES/ECB/PKCS5Padding");
			cipher.init(Cipher.ENCRYPT_MODE, secretKey);

			secretKey = null;
			key = null;

			return cipher.doFinal(b);
		} catch (Exception e) {
			System.out.println("Error while encrypting: " + e.toString());
		}

		secretKey = null;
		key = null;

		return null;
	}

	public static byte[] decrypt(byte[] b, String secret) {
		try {
			setKey(secret);
			Cipher cipher = Cipher.getInstance("AES/ECB/PKCS5PADDING");
			cipher.init(Cipher.DECRYPT_MODE, secretKey);

			secretKey = null;
			key = null;

			return cipher.doFinal(b);
		} catch (Exception e) {
			System.out.println("Error while decrypting: " + e.toString());
		}

		secretKey = null;
		key = null;

		return null;
	}

	public static int[] toIntArray(byte[] ba) {
		int[] ia = new int[ba.length];
		for (int i = 0; i < ba.length; i++) {
			ia[i] = ba[i];
		}

		return ia;
	}

	public static long getLastModified(File directory) {
		File[] files = directory.listFiles();
		if (files.length == 0) {
			return directory.lastModified();
		}

		Arrays.sort(files, (o1, o2) -> Long.compare(o2.lastModified(), o1.lastModified()));
		return files[0].lastModified();
	}
}