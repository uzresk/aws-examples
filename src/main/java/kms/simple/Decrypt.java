package kms.simple;

import java.nio.ByteBuffer;
import java.util.Base64;

import com.amazonaws.ClientConfiguration;
import com.amazonaws.auth.ClasspathPropertiesFileCredentialsProvider;
import com.amazonaws.services.kms.AWSKMSClient;
import com.amazonaws.services.kms.model.DecryptRequest;

public class Decrypt {

	private static final String TEXT = "CiBHIy8/xx/xx==";

	public static void main(String[] args) {

		byte[] cipherText = Base64.getDecoder().decode(TEXT);

		ByteBuffer buffer = ByteBuffer.allocate(cipherText.length);
		buffer.put(cipherText);
		buffer.flip();

		ClientConfiguration configuration = new ClientConfiguration();
		configuration.setProxyHost("tisproxy");
		configuration.setProxyPort(8080);

		AWSKMSClient kmsClient = new AWSKMSClient(
				new ClasspathPropertiesFileCredentialsProvider(), configuration);
		kmsClient.setEndpoint("https://kms.ap-northeast-1.amazonaws.com");

		// decrypt data
		DecryptRequest decryptRequest = new DecryptRequest()
				.withCiphertextBlob(buffer);
		ByteBuffer plainText = kmsClient.decrypt(decryptRequest).getPlaintext();

		System.out.println(getString(plainText));

	}

	public static String getString(ByteBuffer b) {
		byte[] byteArray = new byte[b.remaining()];
		b.get(byteArray);
		return new String(byteArray);
	}
}
