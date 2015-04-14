package ebs;

import com.amazonaws.ClientConfiguration;
import com.amazonaws.auth.AWSCredentialsProvider;
import com.amazonaws.auth.profile.ProfileCredentialsProvider;
import com.amazonaws.regions.Region;
import com.amazonaws.regions.Regions;
import com.amazonaws.services.ec2.AmazonEC2;
import com.amazonaws.services.ec2.AmazonEC2Client;
import com.amazonaws.services.ec2.model.CreateSnapshotRequest;
import com.amazonaws.services.ec2.model.DescribeVolumesResult;
import com.amazonaws.services.ec2.model.Volume;
import com.amazonaws.services.ec2.model.VolumeAttachment;

public class Ec2EbsSnapshotUseInstanceId {

	/**
	 * 第一引数で指定されたEC2のインスタンスIDに紐づくEBSのsnapshotを作成します。
	 *
	 * @param args[0] ec2 instance id
	 */
	public static void main(String[] args) {

		String ec2InstanceId = args[0];
		if (args.length != 1 || ec2InstanceId == null
				|| "".equals(ec2InstanceId)) {
			throw new IllegalArgumentException("EC2 instance id is null.");
		}

		AWSCredentialsProvider provider = new ProfileCredentialsProvider("uzr");

		AmazonEC2 ec2_tokyo = Region.getRegion(Regions.AP_NORTHEAST_1)
				.createClient(AmazonEC2Client.class, provider,
						new ClientConfiguration());

		DescribeVolumesResult volumes = ec2_tokyo.describeVolumes();
		String volumeId = null;
		for (Volume volume : volumes.getVolumes()) {
			for (VolumeAttachment volumeAttatchment : volume.getAttachments()) {
				if (ec2InstanceId.equals(volumeAttatchment.getInstanceId())) {
					volumeId = volumeAttatchment.getVolumeId();
					System.out.println(ec2InstanceId + " : " + volumeId);
					break;
				}
			}
		}

		if (volumeId == null || "".equals(volumeId)) {
			System.out.println(ec2InstanceId + "に対応するvolumeIdが見つかりません。");
		} else {
			ec2_tokyo.createSnapshot(new CreateSnapshotRequest(volumeId,
					volumeId + " snapshot."));
			System.out.println("snapshotの作成を要求しました。");
		}
	}
}
