package aws.ec2.ebs;

import java.util.concurrent.Future;

import com.amazonaws.ClientConfiguration;
import com.amazonaws.auth.AWSCredentialsProvider;
import com.amazonaws.auth.profile.ProfileCredentialsProvider;
import com.amazonaws.regions.Region;
import com.amazonaws.regions.Regions;
import com.amazonaws.services.ec2.AmazonEC2AsyncClient;
import com.amazonaws.services.ec2.model.CreateSnapshotRequest;
import com.amazonaws.services.ec2.model.CreateSnapshotResult;

public class Ec2EbsSnapshotUseVolumeId {

	/**
	 * 第一引数で指定されたvolume IDに紐づくEBSのsnapshotを作成します。
	 * 
	 * @param args
	 *            [0] ec2 instance id
	 */
	public static void main(String[] args) throws Exception {

		if (args.length != 1 || args[0] == null || "".equals(args[0])) {
			throw new IllegalArgumentException("volumeId instance id is null.");
		}
		
		String volumeId = args[0];

		AWSCredentialsProvider provider = new ProfileCredentialsProvider("uzresk");

		AmazonEC2AsyncClient ec2 = Region.getRegion(Regions.AP_NORTHEAST_1)
				.createClient(AmazonEC2AsyncClient.class, provider,
						new ClientConfiguration());
		
		Future<CreateSnapshotResult> result =
				ec2.createSnapshotAsync(new CreateSnapshotRequest(volumeId, volumeId
				+ " snapshot."));
		
		System.out.println("create snapshot request end.");
		
		while (!result.isDone()) {
			Thread.sleep(1000);
			System.out.println("wait....");
		}
		System.out.println("snapshotの作成が完了しました。" + result.get().getSnapshot().getSnapshotId());
		
		ec2.shutdown();
	}
}
