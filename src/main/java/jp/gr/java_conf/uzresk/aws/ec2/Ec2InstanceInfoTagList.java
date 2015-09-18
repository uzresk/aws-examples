package jp.gr.java_conf.uzresk.aws.ec2;

import java.util.ArrayList;
import java.util.List;

import com.amazonaws.ClientConfiguration;
import com.amazonaws.auth.AWSCredentialsProvider;
import com.amazonaws.auth.profile.ProfileCredentialsProvider;
import com.amazonaws.regions.Region;
import com.amazonaws.regions.Regions;
import com.amazonaws.services.ec2.AmazonEC2;
import com.amazonaws.services.ec2.AmazonEC2Client;
import com.amazonaws.services.ec2.model.DescribeTagsRequest;
import com.amazonaws.services.ec2.model.DescribeTagsResult;
import com.amazonaws.services.ec2.model.TagDescription;

public class Ec2InstanceInfoTagList {

	public static void main(String[] args) {

		AWSCredentialsProvider provider = new ProfileCredentialsProvider("uzr");

		AmazonEC2 ec2_tokyo = Region.getRegion(Regions.AP_NORTHEAST_1)
				.createClient(AmazonEC2Client.class, provider,
						new ClientConfiguration());

		List<String> systemAAAResourceIds = new ArrayList<String>();

		DescribeTagsResult tagsResult = ec2_tokyo
				.describeTags(new DescribeTagsRequest());
		for (TagDescription description : tagsResult.getTags()) {
			if ("System".equals(description.getKey()) && "BBB".equals(description.getValue())) {
				systemAAAResourceIds.add(description.getResourceId());
			}
		}
		System.out.println("System AAA ResourceIds : " + systemAAAResourceIds);
	}
}
