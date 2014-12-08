package aws.ec2.sts;

import com.amazonaws.auth.AWSCredentialsProvider;
import com.amazonaws.auth.BasicSessionCredentials;
import com.amazonaws.auth.profile.ProfileCredentialsProvider;
import com.amazonaws.regions.Region;
import com.amazonaws.regions.Regions;
import com.amazonaws.services.ec2.AmazonEC2;
import com.amazonaws.services.ec2.AmazonEC2Client;
import com.amazonaws.services.ec2.model.DescribeTagsRequest;
import com.amazonaws.services.ec2.model.DescribeTagsResult;
import com.amazonaws.services.ec2.model.Filter;
import com.amazonaws.services.ec2.model.TagDescription;
import com.amazonaws.services.securitytoken.AWSSecurityTokenServiceClient;
import com.amazonaws.services.securitytoken.model.AssumeRoleRequest;
import com.amazonaws.services.securitytoken.model.AssumeRoleResult;

public class Ec2InstanceListUseSTS {

	/** Aのリソースにアクセスするためのロールの*/
	private static final String ARN = "arn:aws:iam::xxxxxxxxxxxx:role/EC2FullAccessRole";

	public static void main(String[] args) {

		// BアカウントのProviderを取得する。
		AWSCredentialsProvider provider = new ProfileCredentialsProvider(
				"uzr_api");
		
		// STSサービスを利用するためのClientを取得します。
		AWSSecurityTokenServiceClient client = new AWSSecurityTokenServiceClient(
				provider.getCredentials());
		AssumeRoleRequest request = new AssumeRoleRequest().withRoleArn(ARN)
				.withDurationSeconds(3600)
				.withRoleSessionName("EC2FullAccess_uzr_session");

		AssumeRoleResult result = client.assumeRole(request);

		// Aアカウントのリソースにアクセスするための一時的な認証情報を取得します。
		BasicSessionCredentials tempCredential = new BasicSessionCredentials(
				result.getCredentials().getAccessKeyId(), 
				result.getCredentials().getSecretAccessKey(),
				result.getCredentials().getSessionToken());

		// 一時的な認証情報を使って、EC2のインスタンス一覧のタグ名を取得します。
		AmazonEC2 ec2 = new AmazonEC2Client(tempCredential);
		ec2.setRegion(Region.getRegion(Regions.AP_NORTHEAST_1));

		DescribeTagsRequest describeTagRequest = new DescribeTagsRequest()
				.withFilters(new Filter().withName("resource-type").withValues(
						"instance"));

		DescribeTagsResult tagsResult = ec2.describeTags(describeTagRequest);

		for (TagDescription description : tagsResult.getTags()) {
			System.out.println(description.getKey() + " : "
					+ description.getValue());
		}

	}

}
