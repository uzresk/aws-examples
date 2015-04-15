package kms.step;

public class Decrypt extends KeyManagementService {

	public static void main(String... args) throws Exception {

		new Decrypt().run();

	}

	public void run() {

		final String encodeStr = "k8hzdBPeRojdC90yE6Bckeoxca+DZGU02s0oFNJrqoI=";

		System.out.println("Encrypted[" + encodeStr + "]");

		System.out.println("Decrypted["
				+ CryptUtils.decrypt(encodeStr, super.provideDataKey()) + "]");

	}

}
