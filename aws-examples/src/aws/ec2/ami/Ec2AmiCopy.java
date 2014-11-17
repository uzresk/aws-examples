package aws.ec2.ami;

import java.util.List;

import com.amazonaws.ClientConfiguration;
import com.amazonaws.auth.AWSCredentialsProvider;
import com.amazonaws.auth.profile.ProfileCredentialsProvider;
import com.amazonaws.regions.Region;
import com.amazonaws.regions.Regions;
import com.amazonaws.services.ec2.AmazonEC2;
import com.amazonaws.services.ec2.AmazonEC2Client;
import com.amazonaws.services.ec2.model.CopyImageRequest;
import com.amazonaws.services.ec2.model.CopyImageResult;
import com.amazonaws.services.ec2.model.CreateTagsRequest;
import com.amazonaws.services.ec2.model.DescribeImagesRequest;
import com.amazonaws.services.ec2.model.DescribeImagesResult;
import com.amazonaws.services.ec2.model.Image;
import com.amazonaws.services.ec2.model.Tag;

public class Ec2AmiCopy {

	/**
	 * 第一引数で指定されたAMI imageをリージョン間コピーします。
	 * tagの情報についても合わせてコピーします。
	 * 
	 * @param args AMI image id
	 */
	public static void main(String[] args) {

		String sourceImageId = args[0];

		if (sourceImageId == null || "".equals(sourceImageId)) {
			throw new IllegalArgumentException("instance id is null or empty.");
		}

		AWSCredentialsProvider provider = new ProfileCredentialsProvider("uzr");

		AmazonEC2 ec2ClientTokyo2 = Region.getRegion(Regions.AP_NORTHEAST_1)
				.createClient(AmazonEC2Client.class, provider,
						new ClientConfiguration());

		// AMIをコピーするときのプロパティを設定します。コピー元のリージョンとAMIIDを指定しています。
		CopyImageRequest copyImageRequest = new CopyImageRequest();
		copyImageRequest.setDescription("copy sourceImageId:" + sourceImageId);
		copyImageRequest.setSourceRegion(Regions.AP_NORTHEAST_1.getName());
		copyImageRequest.setRequestCredentials(provider.getCredentials());
		copyImageRequest.setSourceImageId(sourceImageId);

		// AMIコピーの実行
		CopyImageResult copyImageResult = ec2ClientTokyo2
				.copyImage(copyImageRequest);
		String copyImageId = copyImageResult.getImageId();
		
		// この時点ではリクエストが受け付けられただけです。
		System.out.println(sourceImageId + "から" + copyImageId + "を作成しました。");

		// Tagの情報をコピー元から取得して、コピーした先のAMIに紐付けます。
		AmazonEC2 ec2ClientTokyo = Region.getRegion(Regions.AP_NORTHEAST_1)
				.createClient(AmazonEC2Client.class, provider,
						new ClientConfiguration());
		
		// Tagの情報を取得
		DescribeImagesResult describeImagesResult = ec2ClientTokyo
				.describeImages(new DescribeImagesRequest()
						.withImageIds(sourceImageId));

		List<Image> images = describeImagesResult.getImages();
		if (images.size() != 1) {
			throw new IllegalStateException("error:" + images.size());
		}

		Image image = images.get(0);
		
		// AMIコピーしてできたイメージにタグを紐付けます。
		for (Tag tag : image.getTags()) {
			System.out.println("tag key:" + tag.getKey() + " value : "
					+ tag.getValue());
			ec2ClientTokyo2.createTags(new CreateTagsRequest()
					.withResources(copyImageId).withTags(new Tag(tag.getKey(), tag.getValue())));
		}
	}
}
