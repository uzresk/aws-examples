package s3.upload;

import java.io.File;

import com.amazonaws.auth.AWSCredentialsProvider;
import com.amazonaws.auth.profile.ProfileCredentialsProvider;
import com.amazonaws.event.ProgressEvent;
import com.amazonaws.event.ProgressEventType;
import com.amazonaws.event.ProgressListener;
import com.amazonaws.services.s3.model.PutObjectRequest;
import com.amazonaws.services.s3.transfer.TransferManager;
import com.amazonaws.services.s3.transfer.Upload;

public class S3MultiparatUpload {

	public static void main(String[] args) {

		AWSCredentialsProvider provider = new ProfileCredentialsProvider("uzr");

		TransferManager tm = new TransferManager(provider.getCredentials());

		File file = new File("/Users/yuzuru/Downloads/10mfile");
		long start = System.currentTimeMillis();

		Upload upload = tm.upload(new PutObjectRequest("data.uzr", "10mfile",
				file));

		// Listnerを仕込みます。
		upload.addProgressListener(new ProgressListener() {
			/**
			 * 進捗状況が変わったら呼ばれるメソッド
			 * ProgressEventからは、転送バイト数やパーセンテージなどが取得できます。
			 * ここでは転送完了後メッセージを出すようにしています。
			 */
			@Override
			public void progressChanged(ProgressEvent progressEvent) {
				if (ProgressEventType.TRANSFER_COMPLETED_EVENT == progressEvent.getEventType()) {
					long end = System.currentTimeMillis();
					long interval = end - start;
					System.out.println("処理時間：" + interval + "ミリ秒");
					System.out.println("upload finish.");
				}
			}
		});

	}

}
