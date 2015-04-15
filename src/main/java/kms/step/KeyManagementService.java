package kms.step;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.Writer;
import java.nio.ByteBuffer;
import java.security.Key;
import java.util.Base64;

import javax.crypto.spec.SecretKeySpec;

import com.amazonaws.ClientConfiguration;
import com.amazonaws.auth.ClasspathPropertiesFileCredentialsProvider;
import com.amazonaws.regions.Region;
import com.amazonaws.regions.Regions;
import com.amazonaws.services.kms.AWSKMSClient;
import com.amazonaws.services.kms.model.DecryptRequest;
import com.amazonaws.services.kms.model.GenerateDataKeyRequest;
import com.amazonaws.services.kms.model.GenerateDataKeyResult;
import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.AmazonS3Client;
import com.amazonaws.services.s3.model.GetObjectRequest;
import com.amazonaws.services.s3.model.PutObjectRequest;
import com.amazonaws.services.s3.model.S3ObjectInputStream;

public class KeyManagementService {

	private static final String KEYID = "arn:aws:kms:ap-northeast-1:xxxxxx:key/xxxxxxxxxxxxxxxxx";

	private static final String PROXY_HOST = "xxxxx";

	private static final int PROXY_PORT = 8080;

	private static final String KMS_BUCKET_NAME = "xxxxxxxxxx";

	private static final String KEY_NAME = "key";

	protected Key provideDataKey() {
		// get encrypted key
		AmazonS3 s3 = new AmazonS3Client(
				new ClasspathPropertiesFileCredentialsProvider(),
				new ClientConfiguration().withProxyHost(PROXY_HOST)
						.withProxyPort(PROXY_PORT));
		s3.setRegion(Region.getRegion(Regions.AP_NORTHEAST_1));

		S3ObjectInputStream s3ois = s3.getObject(
				new GetObjectRequest(KMS_BUCKET_NAME, KEY_NAME))
				.getObjectContent();
		String encryptedKey = new BufferedReader(new InputStreamReader(s3ois))
				.lines().findFirst().get();
		System.out.println("EncryptedKey[" + encryptedKey + "]");

		byte[] base64DecodedKey = Base64.getDecoder().decode(encryptedKey);
		ByteBuffer decodedKey = ByteBuffer.allocate(base64DecodedKey.length);
		decodedKey.put(base64DecodedKey);
		decodedKey.flip();

		AWSKMSClient kmsClient = new AWSKMSClient(
				new ClasspathPropertiesFileCredentialsProvider(),
				new ClientConfiguration().withProxyHost(PROXY_HOST)
						.withProxyPort(PROXY_PORT));
		kmsClient.setEndpoint("https://kms.ap-northeast-1.amazonaws.com");

		DecryptRequest decryptRequest = new DecryptRequest()
				.withCiphertextBlob(decodedKey);
		ByteBuffer plainText = kmsClient.decrypt(decryptRequest).getPlaintext();

		return makeKey(plainText);
	}

	protected void saveEncryptedDataKey() {

		ByteBuffer encryptedDataKey = generateEncryptKey();

		saveEncryptKey(encryptedDataKey);
	}

	protected ByteBuffer generateEncryptKey() {
		// get data key
		AWSKMSClient kmsClient = new AWSKMSClient(
				new ClasspathPropertiesFileCredentialsProvider(),
				new ClientConfiguration().withProxyHost(PROXY_HOST)
						.withProxyPort(PROXY_PORT));
		kmsClient.setEndpoint("https://kms.ap-northeast-1.amazonaws.com");
		GenerateDataKeyRequest dataKeyRequest = new GenerateDataKeyRequest();
		dataKeyRequest.setKeyId(KEYID);
		dataKeyRequest.setKeySpec("AES_128");
		GenerateDataKeyResult dataKeyResult = kmsClient
				.generateDataKey(dataKeyRequest);

		return dataKeyResult.getCiphertextBlob();
	}

	protected void saveEncryptKey(ByteBuffer encryptedDataKey) {
		AmazonS3 s3 = new AmazonS3Client(
				new ClasspathPropertiesFileCredentialsProvider(),
				new ClientConfiguration().withProxyHost(PROXY_HOST)
						.withProxyPort(PROXY_PORT));
		s3.setRegion(Region.getRegion(Regions.AP_NORTHEAST_1));

		String base64EncryptedDataKey = Base64.getEncoder().encodeToString(
				getByteArray(encryptedDataKey));

		s3.putObject(new PutObjectRequest(KMS_BUCKET_NAME, KEY_NAME,
				createFile(base64EncryptedDataKey)));

	}

	private static File createFile(String base64EncryptedDataKey) {

		File file = null;
		try {
			file = File.createTempFile("tmp", ".txt");
			file.deleteOnExit();

			Writer writer = new OutputStreamWriter(new FileOutputStream(file));
			writer.write(base64EncryptedDataKey);
			writer.close();
		} catch (IOException e) {
			throw new RuntimeException("can't create file.", e);
		}

		return file;
	}

	private Key makeKey(ByteBuffer key) {
		return new SecretKeySpec(getByteArray(key), "AES");
	}

	private byte[] getByteArray(ByteBuffer b) {
		byte[] byteArray = new byte[b.remaining()];
		b.get(byteArray);
		return byteArray;
	}
}
