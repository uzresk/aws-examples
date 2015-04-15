package kms.simple;

import java.nio.ByteBuffer;
import java.util.Base64;

import com.amazonaws.ClientConfiguration;
import com.amazonaws.auth.ClasspathPropertiesFileCredentialsProvider;
import com.amazonaws.services.kms.AWSKMSClient;
import com.amazonaws.services.kms.model.EncryptRequest;

public class Encrypt {

	private static final String KEYID = "arn:aws:kms:ap-northeast-1:xxxxxxxxxxx:key/xxxxxxxxxxxxxxx";

	public static void main(String[] args) {

		ClientConfiguration configuration = new ClientConfiguration();
		configuration.setProxyHost("xxxxxx");
		configuration.setProxyPort(8080);

		// get data key
		AWSKMSClient kmsClient = new AWSKMSClient(
				new ClasspathPropertiesFileCredentialsProvider(), configuration);
		kmsClient.setEndpoint("https://kms.ap-northeast-1.amazonaws.com");

		final String dataStr = "1234567890-sdhjj";

		byte[] data = dataStr.getBytes();

		ByteBuffer buffer = ByteBuffer.allocate(data.length);
		buffer.put(data);
		buffer.flip();

		EncryptRequest encryptRequest = new EncryptRequest().withKeyId(KEYID)
				.withPlaintext(buffer);
		ByteBuffer ciphertext = kmsClient.encrypt(encryptRequest)
				.getCiphertextBlob();

		String cipherText = getString(Base64.getEncoder().encode(ciphertext));
		System.out.println(cipherText);
	}

	public static String getString(ByteBuffer b) {
		byte[] byteArray = new byte[b.remaining()];
		b.get(byteArray);
		return new String(byteArray);
	}
}
