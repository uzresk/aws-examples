package aws.sqs;

import java.util.Date;

import com.amazonaws.auth.AWSCredentialsProvider;
import com.amazonaws.auth.profile.ProfileCredentialsProvider;
import com.amazonaws.handlers.AsyncHandler;
import com.amazonaws.services.sqs.AmazonSQSAsync;
import com.amazonaws.services.sqs.AmazonSQSAsyncClient;
import com.amazonaws.services.sqs.model.CreateQueueRequest;
import com.amazonaws.services.sqs.model.SendMessageRequest;
import com.amazonaws.services.sqs.model.SendMessageResult;

public class SampleQueueSender {

	private static final String SQS_ENDPOINT = "http://sqs.ap-northeast-1.amazonaws.com";

	private static final String QUEUE = "SampleQueue";

	public static void main(String[] args) {

		AWSCredentialsProvider provider = new ProfileCredentialsProvider(
				"uzresk");

		AmazonSQSAsync sqs = new AmazonSQSAsyncClient(provider);
		sqs.setEndpoint(SQS_ENDPOINT);

		CreateQueueRequest request = new CreateQueueRequest(QUEUE);
		String queueUrl = sqs.createQueue(request).getQueueUrl();

		// 非同期リクエストにするとハンドラが使えます。
		sqs.sendMessageAsync(new SendMessageRequest(queueUrl, new Date().toString()),
				new AsyncHandler<SendMessageRequest, SendMessageResult>() {

					@Override
					public void onSuccess(SendMessageRequest request,
							SendMessageResult result) {
						System.out.println("成功しました。" + result.getMessageId());
					}

					@Override
					public void onError(Exception exception) {
						System.out.println("失敗しました。");
						exception.printStackTrace();
					}
				});
	}
}
