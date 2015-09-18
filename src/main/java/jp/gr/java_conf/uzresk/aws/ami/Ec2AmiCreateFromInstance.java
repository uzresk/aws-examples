package jp.gr.java_conf.uzresk.aws.ami;

import com.amazonaws.ClientConfiguration;
import com.amazonaws.auth.AWSCredentialsProvider;
import com.amazonaws.auth.profile.ProfileCredentialsProvider;
import com.amazonaws.regions.Region;
import com.amazonaws.regions.Regions;
import com.amazonaws.services.ec2.AmazonEC2;
import com.amazonaws.services.ec2.AmazonEC2Client;
import com.amazonaws.services.ec2.model.CreateImageRequest;
import com.amazonaws.services.ec2.model.CreateImageResult;

public class Ec2AmiCreateFromInstance {

	/**
	 * 第一引数で指定されたec2 instance idから、AMIを作成します。
	 *
	 * @param args
	 */
	public static void main(String[] args) {

		String instanceId = args[0];

		if (instanceId == null || "".equals(instanceId)) {
			throw new IllegalArgumentException("instance id is null or empty.");
		}

		AWSCredentialsProvider provider = new ProfileCredentialsProvider("uzr");

		AmazonEC2 ec2_tokyo = Region.getRegion(Regions.AP_NORTHEAST_1)
				.createClient(AmazonEC2Client.class, provider,
						new ClientConfiguration());

		CreateImageRequest createImageRequest = new CreateImageRequest(
				instanceId, "create-image-test-2")
				.withDescription("description test 2");

		CreateImageResult image = ec2_tokyo.createImage(createImageRequest);
		System.out.println(instanceId + "から" + image.getImageId() + "を作成しました。");
	}
}
