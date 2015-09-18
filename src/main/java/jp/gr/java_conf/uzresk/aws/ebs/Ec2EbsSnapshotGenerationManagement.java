package jp.gr.java_conf.uzresk.aws.ebs;

import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.List;

import com.amazonaws.ClientConfiguration;
import com.amazonaws.auth.AWSCredentialsProvider;
import com.amazonaws.auth.profile.ProfileCredentialsProvider;
import com.amazonaws.regions.Region;
import com.amazonaws.regions.Regions;
import com.amazonaws.services.ec2.AmazonEC2;
import com.amazonaws.services.ec2.AmazonEC2Client;
import com.amazonaws.services.ec2.model.DeleteSnapshotRequest;
import com.amazonaws.services.ec2.model.DescribeSnapshotsRequest;
import com.amazonaws.services.ec2.model.DescribeSnapshotsResult;
import com.amazonaws.services.ec2.model.Filter;
import com.amazonaws.services.ec2.model.Snapshot;
import com.amazonaws.services.ec2.model.SnapshotState;

/**
 * snapshotの世代管理を行います。
 * 引数：EBS VolumeId　世代管理数（残しておきたい数）
 *
 * @author yuzuru
 */
public class Ec2EbsSnapshotGenerationManagement {

	public static void main(String[] args) {

		if (args.length != 2) {
			throw new IllegalArgumentException("parameter error");
		}

		String volumeId = args[0];
		int generationCount = Integer.parseInt(args[1]);

		// error checkはちゃんとやる。

		AWSCredentialsProvider provider = new ProfileCredentialsProvider(
				"uzresk");

		AmazonEC2 ec2_tokyo = Region.getRegion(Regions.AP_NORTHEAST_1)
				.createClient(AmazonEC2Client.class, provider,
						new ClientConfiguration());

		// volume id をキーにsnapshotの一覧を取得します。
		Filter filter = new Filter().withName("volume-id").withValues(volumeId);
		DescribeSnapshotsRequest snapshotRequest = new DescribeSnapshotsRequest()
				.withFilters(filter);
		DescribeSnapshotsResult snapshotResult = ec2_tokyo
				.describeSnapshots(snapshotRequest);

		// snapshot作成開始日でソートします。（古い→新しい）
		List<Snapshot> snapshots = snapshotResult.getSnapshots();
		Collections.sort(snapshots, new SnapshotComparator());

		// snapshotの情報を出力
		for (Snapshot snapshot : snapshots) {
			System.out.println(snapshot);
		}

		// 世代管理保持数 < snapshotの数の場合、対象をpargeします。
		int snapshotSize = snapshots.size();
		if (generationCount < snapshotSize) {
			for (int i = 0; i < snapshotSize - generationCount; i++) {
				Snapshot snapshot = snapshots.get(i);
				// （念のため）snapshotのステータスが完了しているものだけをparge対象とする。
				if (SnapshotState.Completed.toString().equals(snapshot.getState())) {
					System.out.println("perge 対象 snapshot：" + snapshot);
					pargeSnapshot(ec2_tokyo, snapshot.getSnapshotId());
				}
			}
		}
	}

	/**
	 * snapshotの作成開始日の昇順で並び替えるためのcomparator
	 */
	private static class SnapshotComparator implements Comparator<Snapshot> {
		@Override
		public int compare(Snapshot o1, Snapshot o2) {
			Date startDateO1 = o1.getStartTime();
			Date startDateO2 = o2.getStartTime();
			return startDateO1.compareTo(startDateO2);
		}
	}

	/**
	 * snapshotIdをキーに対象をパージします。
	 *
	 * @param ec2 ec2
	 * @param snapshotId スナップショットID
	 */
	public static void pargeSnapshot(AmazonEC2 ec2, String snapshotId) {
		DeleteSnapshotRequest request = new DeleteSnapshotRequest(snapshotId);
		ec2.deleteSnapshot(request);
		System.out.println("snapshotのpargeを行います。snapshotId:" + snapshotId);
	}
}
