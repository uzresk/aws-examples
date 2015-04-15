package kms.step;

import java.nio.ByteBuffer;
import java.security.InvalidKeyException;
import java.security.Key;
import java.security.NoSuchAlgorithmException;
import java.util.Base64;

import javax.crypto.BadPaddingException;
import javax.crypto.Cipher;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.NoSuchPaddingException;
import javax.crypto.spec.SecretKeySpec;

public class CryptUtils {

	public static String encrypt(String src, Key key) {

		Cipher cipher;
		byte[] enc;
		try {
			cipher = Cipher.getInstance("AES");

			cipher.init(Cipher.ENCRYPT_MODE, key);

			enc = cipher.doFinal(src.getBytes());
		} catch (NoSuchAlgorithmException | NoSuchPaddingException
				| IllegalBlockSizeException | BadPaddingException e) {
			throw new RuntimeException(
					"The exception which can't usually happen.");
		} catch (InvalidKeyException e) {
			throw new RuntimeException("Invalid Key. key[" + key.toString()
					+ "]");
		}

		return Base64.getEncoder().encodeToString(enc);
	}

	public static String decrypt(String src, Key key) {

		Cipher cipher;
		byte[] decrypted;

		try {
			byte[] decodeBase64src = Base64.getDecoder().decode(src);

			cipher = Cipher.getInstance("AES");

			cipher.init(Cipher.DECRYPT_MODE, key);

			decrypted = cipher.doFinal(decodeBase64src);
		} catch (NoSuchAlgorithmException | NoSuchPaddingException
				| IllegalBlockSizeException | BadPaddingException e) {
			throw new RuntimeException(
					"The exception which can't usually happen.");
		} catch (InvalidKeyException e) {
			throw new RuntimeException("Invalid Key. key[" + key.toString()
					+ "]");
		}

		return new String(decrypted);
	}

	public static Key makeKey(ByteBuffer key) {
		return new SecretKeySpec(getByteArray(key), "AES");
	}

	public static String getString(ByteBuffer b) {

		return new String(getByteArray(b));
	}

	public static byte[] getByteArray(ByteBuffer b) {
		byte[] byteArray = new byte[b.remaining()];
		b.get(byteArray);
		return byteArray;
	}
}
