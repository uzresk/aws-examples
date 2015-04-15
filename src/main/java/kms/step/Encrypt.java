package kms.step;

public class Encrypt extends KeyManagementService {

	public static void main(String... args) throws Exception {

		new Encrypt().run();
	}

	public void run() {

		final String str = "1234567890!#$%&()";

		System.out.println("Original [" + str + "]");

		System.out.println("Encrypted["
				+ CryptUtils.encrypt(str, super.provideDataKey()) + "]");
	}
}
