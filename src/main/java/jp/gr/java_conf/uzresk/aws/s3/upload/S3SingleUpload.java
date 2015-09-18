package jp.gr.java_conf.uzresk.aws.s3.upload;

import java.io.File;

import com.amazonaws.auth.AWSCredentialsProvider;
import com.amazonaws.auth.profile.ProfileCredentialsProvider;
import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.AmazonS3Client;
import com.amazonaws.services.s3.model.PutObjectRequest;

public class S3SingleUpload {

	public static void main(String[] args) {

		AWSCredentialsProvider provider = new ProfileCredentialsProvider("uzr");

		// s3clientインスタンスを作成
		AmazonS3 s3client = new AmazonS3Client(provider.getCredentials());

		File file = new File("/Users/yuzuru/Downloads/10mfile");
		long start = System.currentTimeMillis();

		// アップロードします。
		// PutObjectRequestの引数はバケット名、アップロード後の名前、Fileオブジェクトです。
		s3client.putObject(new PutObjectRequest("data.uzr", "10mfile",file));

		// 処理時間を取得
		long end = System.currentTimeMillis();
		long interval = end - start;
		System.out.println("処理時間：" + interval + "ミリ秒");
		System.out.println("upload finish.");
	}

}
