package jp.gr.java_conf.uzresk.aws.kms.step;

import java.nio.ByteBuffer;

public class GenerateKeySaveS3 extends KeyManagementService {


	public static void main(String[] args) {

		new GenerateKeySaveS3().run();

	}

	public void run() {

		// generate encrypt key
		ByteBuffer encryptedDataKey = generateEncryptKey();

		saveEncryptKey(encryptedDataKey);

		System.out.println("upload encrypted key.");
	}
}
