package kms.simple;

import java.nio.ByteBuffer;
import java.security.InvalidAlgorithmParameterException;
import java.security.InvalidKeyException;
import java.security.Key;
import java.security.NoSuchAlgorithmException;
import java.util.Base64;

import javax.crypto.BadPaddingException;
import javax.crypto.Cipher;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.NoSuchPaddingException;
import javax.crypto.spec.SecretKeySpec;

import com.amazonaws.auth.ClasspathPropertiesFileCredentialsProvider;
import com.amazonaws.services.kms.AWSKMSClient;
import com.amazonaws.services.kms.model.DecryptRequest;
import com.amazonaws.services.kms.model.GenerateDataKeyRequest;
import com.amazonaws.services.kms.model.GenerateDataKeyResult;

public class KmsExample {

	private static final String KEYID = "arn:aws:kms:ap-northeast-1:xxxxx:key/xxxxxxxxxxxxxxxxxxx";

	public static void main(String... args) throws Exception {

		final String dataStr = "asdfas";

		// get data key
		AWSKMSClient kmsClient = new AWSKMSClient(
				new ClasspathPropertiesFileCredentialsProvider());
		kmsClient.setEndpoint("https://kms.ap-northeast-1.amazonaws.com");
		GenerateDataKeyRequest dataKeyRequest = new GenerateDataKeyRequest();
		dataKeyRequest.setKeyId(KEYID);
		dataKeyRequest.setKeySpec("AES_128");
		GenerateDataKeyResult dataKeyResult = kmsClient
				.generateDataKey(dataKeyRequest);

		// data key
		ByteBuffer plainTextKey = dataKeyResult.getPlaintext();

		// encrypt and base64
		String encryptStr = encrypt(dataStr, makeKey(plainTextKey));

		System.out.println("encrypt[" + encryptStr + "]" + encryptStr.length());

		plainTextKey.clear();

		// /////////////////////////////////////////////////////////////////////

		// encrypt data key
		ByteBuffer encryptedKey = dataKeyResult.getCiphertextBlob();

		DecryptRequest decryptRequest = new DecryptRequest()
				.withCiphertextBlob(encryptedKey);
		plainTextKey = kmsClient.decrypt(decryptRequest).getPlaintext();

		String decryptStr = decrypt(encryptStr, makeKey(plainTextKey));

		System.out.println("decrypt[" + decryptStr + "]");

	}

	public static String encrypt(String src, Key key)
			throws NoSuchAlgorithmException, NoSuchPaddingException,
			InvalidKeyException, IllegalBlockSizeException,
			BadPaddingException, InvalidAlgorithmParameterException {

		Cipher cipher = Cipher.getInstance("AES");
		cipher.init(Cipher.ENCRYPT_MODE, key);

		byte[] enc = cipher.doFinal(src.getBytes());

		return Base64.getEncoder().encodeToString(enc);
	}

	public static String decrypt(String src, Key key)
			throws NoSuchAlgorithmException, NoSuchPaddingException,
			InvalidKeyException, IllegalBlockSizeException,
			BadPaddingException, InvalidAlgorithmParameterException {

		byte[] decodeBase64src = Base64.getDecoder().decode(src);
		System.out.println(new String(decodeBase64src));

		Cipher cipher = Cipher.getInstance("AES");

		cipher.init(Cipher.DECRYPT_MODE, key);
		System.out.println(new String(cipher.doFinal(decodeBase64src)));
		return new String(cipher.doFinal(decodeBase64src));
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
