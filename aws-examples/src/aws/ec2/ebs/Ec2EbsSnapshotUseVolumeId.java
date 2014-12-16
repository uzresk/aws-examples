package aws.ec2.ebs;

import com.amazonaws.ClientConfiguration;
import com.amazonaws.auth.AWSCredentialsProvider;
import com.amazonaws.auth.profile.ProfileCredentialsProvider;
import com.amazonaws.regions.Region;
import com.amazonaws.regions.Regions;
import com.amazonaws.services.ec2.AmazonEC2;
import com.amazonaws.services.ec2.AmazonEC2Client;
import com.amazonaws.services.ec2.model.CreateSnapshotRequest;

public class Ec2EbsSnapshotUseVolumeId {

	/**
	 * 第一引数で指定されたvolume IDに紐づくEBSのsnapshotを作成します。
	 * 
	 * @param args
	 *            [0] ec2 instance id
	 */
	public static void main(String[] args) {

		if (args.length != 1 || args[0] == null || "".equals(args[0])) {
			throw new IllegalArgumentException("volumeId instance id is null.");
		}
		
		String volumeId = args[0];

		AWSCredentialsProvider provider = new ProfileCredentialsProvider("uzresk");

		AmazonEC2 ec2_tokyo = Region.getRegion(Regions.AP_NORTHEAST_1)
				.createClient(AmazonEC2Client.class, provider,
						new ClientConfiguration());

		ec2_tokyo.createSnapshot(new CreateSnapshotRequest(volumeId, volumeId
				+ " snapshot."));
		System.out.println("snapshotの作成を要求しました。");
	}
}
