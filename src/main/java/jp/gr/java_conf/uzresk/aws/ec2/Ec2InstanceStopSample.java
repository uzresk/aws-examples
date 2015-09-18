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
import com.amazonaws.services.ec2.model.DescribeInstancesResult;
import com.amazonaws.services.ec2.model.Instance;
import com.amazonaws.services.ec2.model.InstanceStateName;
import com.amazonaws.services.ec2.model.Reservation;
import com.amazonaws.services.ec2.model.StopInstancesRequest;

public class Ec2InstanceStopSample {

	public static void main(String[] args) {

		AWSCredentialsProvider provider = new ProfileCredentialsProvider("uzr");

		AmazonEC2 ec2_tokyo = Region.getRegion(Regions.AP_NORTHEAST_1)
				.createClient(AmazonEC2Client.class, provider,
						new ClientConfiguration());
		DescribeInstancesResult instanceResult = ec2_tokyo.describeInstances();

		List<String> runningIncetanceIds = new ArrayList<String>();
		for (Reservation reservation : instanceResult.getReservations()) {
			for (Instance instance : reservation.getInstances()) {
				// インスタンスのステータスがRunningのインスタンスIDを取得する。
				if (InstanceStateName.Running.toString().equals(instance.getState().getName())) {
					String instanceId = instance.getInstanceId();
					System.out.println(instanceId + "is Running");
					runningIncetanceIds.add(instanceId);
				}
			}
		}
		// インスタンスを停止する。
		ec2_tokyo.stopInstances(new StopInstancesRequest(runningIncetanceIds));
	}
}
