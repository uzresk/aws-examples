package ec2;

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
import com.amazonaws.services.ec2.model.StartInstancesRequest;

public class Ec2InstanceRestartSample {

	public static void main(String[] args) throws Exception {

		AWSCredentialsProvider provider = new ProfileCredentialsProvider("uzr");

		AmazonEC2 ec2_tokyo = Region.getRegion(Regions.AP_NORTHEAST_1)
				.createClient(AmazonEC2Client.class, provider,
						new ClientConfiguration());
		DescribeInstancesResult instanceResult = ec2_tokyo.describeInstances();

		List<String> stoppedIncetanceIds = new ArrayList<String>();
		for (Reservation reservation : instanceResult.getReservations()) {
			for (Instance instance : reservation.getInstances()) {

				if (InstanceStateName.Stopped.toString().equals(instance.getState().getName())) {
					String instanceId = instance.getInstanceId();
					System.out.println(instanceId + "is Stopped");
					stoppedIncetanceIds.add(instanceId);
				}
			}
		}
		// インスタンスを停止する
		ec2_tokyo.startInstances(new StartInstancesRequest(stoppedIncetanceIds));

		System.out.println("状況を確認します。");

		boolean pending = true;


		while(pending) {
			AmazonEC2 ec2 = Region.getRegion(Regions.AP_NORTHEAST_1)
					.createClient(AmazonEC2Client.class, provider,
							new ClientConfiguration());

			for (Reservation reservation : ec2.describeInstances().getReservations()) {

				int runningInstanceCnt = 0;
				for (Instance instance : reservation.getInstances()) {
					String instanceId = instance.getInstanceId();
					System.out.println(instanceId + " is " + instance.getState().getName());

					if (InstanceStateName.Running.toString().equals(instance.getState().getName())) {
						runningInstanceCnt++;
					}
				}

				if (runningInstanceCnt == reservation.getInstances().size()) {
					pending = false;
				}
			}
			Thread.sleep(5000);
		}
		System.out.println("EC2のインスタンスが起動しました。");
	}
}
