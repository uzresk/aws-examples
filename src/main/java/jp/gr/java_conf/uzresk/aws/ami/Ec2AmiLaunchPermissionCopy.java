package jp.gr.java_conf.uzresk.aws.ami;

import com.amazonaws.ClientConfiguration;
import com.amazonaws.auth.AWSCredentialsProvider;
import com.amazonaws.auth.profile.ProfileCredentialsProvider;
import com.amazonaws.regions.Region;
import com.amazonaws.regions.Regions;
import com.amazonaws.services.ec2.AmazonEC2;
import com.amazonaws.services.ec2.AmazonEC2Client;
import com.amazonaws.services.ec2.model.DescribeImageAttributeRequest;
import com.amazonaws.services.ec2.model.DescribeImageAttributeResult;
import com.amazonaws.services.ec2.model.ImageAttributeName;
import com.amazonaws.services.ec2.model.LaunchPermission;
import com.amazonaws.services.ec2.model.LaunchPermissionModifications;
import com.amazonaws.services.ec2.model.ModifyImageAttributeRequest;

public class Ec2AmiLaunchPermissionCopy {

	/**
	 * 第一引数で指定されたAMI imageに紐づく起動許可設定を<br>
	 * 第二引数で指定されたAMI imageにコピーします。
	 */
	public static void main(String[] args) {

		String sourceImageId = args[0];
		String destImageId = args[1];

		if (sourceImageId == null || "".equals(sourceImageId)
				|| destImageId == null || "".equals(destImageId)) {
			throw new IllegalArgumentException("instance id is null or empty.");
		}

		AWSCredentialsProvider provider = new ProfileCredentialsProvider("uzr");

		AmazonEC2 ec2ClientTokyo = Region.getRegion(Regions.AP_NORTHEAST_1)
				.createClient(AmazonEC2Client.class, provider,
						new ClientConfiguration());

		// 起動許可（LaunchPermision）をコピーします。
		DescribeImageAttributeResult describeImageAttributeResult = ec2ClientTokyo
				.describeImageAttribute(new DescribeImageAttributeRequest(
						sourceImageId, ImageAttributeName.LaunchPermission));

		ModifyImageAttributeRequest modifyImageAttributeRequest = new ModifyImageAttributeRequest(
				destImageId, ImageAttributeName.LaunchPermission.toString());

		for (LaunchPermission permission : describeImageAttributeResult
				.getImageAttribute().getLaunchPermissions()) {
			modifyImageAttributeRequest
					.setLaunchPermission(new LaunchPermissionModifications()
							.withAdd(permission));
		}

		ec2ClientTokyo.modifyImageAttribute(modifyImageAttributeRequest);
	}
}
