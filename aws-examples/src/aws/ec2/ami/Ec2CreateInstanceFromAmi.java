package aws.ec2.ami;

import com.amazonaws.ClientConfiguration;
import com.amazonaws.auth.AWSCredentialsProvider;
import com.amazonaws.auth.profile.ProfileCredentialsProvider;
import com.amazonaws.regions.Region;
import com.amazonaws.regions.Regions;
import com.amazonaws.services.ec2.AmazonEC2;
import com.amazonaws.services.ec2.AmazonEC2Client;
import com.amazonaws.services.ec2.model.InstanceType;
import com.amazonaws.services.ec2.model.RunInstancesRequest;
import com.amazonaws.services.ec2.model.RunInstancesResult;

public class Ec2CreateInstanceFromAmi {

	public static void main(String[] args) {

		AWSCredentialsProvider provider = new ProfileCredentialsProvider("uzr");

		AmazonEC2 ec2_tokyo = Region.getRegion(Regions.AP_NORTHEAST_1)
				.createClient(AmazonEC2Client.class, provider,
						new ClientConfiguration());

		RunInstancesRequest instanceRequest = new RunInstancesRequest()
				.withImageId("ami-61a29d60")
				.withInstanceType(InstanceType.T2Micro)
				.withMinCount(1)
				.withMaxCount(1)
				.withSecurityGroupIds("launch-wizard-1")
				.withKeyName("uzr_key");

		RunInstancesResult result = ec2_tokyo.runInstances(instanceRequest);
		
		System.out.println(result.getReservation());
	}
}
