package jp.gr.java_conf.uzresk.aws.sqs;

import com.amazonaws.auth.AWSCredentialsProvider;
import com.amazonaws.auth.profile.ProfileCredentialsProvider;
import com.amazonaws.services.sqs.AmazonSQS;
import com.amazonaws.services.sqs.AmazonSQSClient;
import com.amazonaws.services.sqs.model.DeleteMessageRequest;
import com.amazonaws.services.sqs.model.ReceiveMessageRequest;
import com.amazonaws.services.sqs.model.ReceiveMessageResult;

public class SampleQueueReceiver {

	private static final String SQS_ENDPOINT = "http://sqs.ap-northeast-1.amazonaws.com";

	private static final String QUEUE = "QueueSample";

	public static void main(String[] args) {

		AWSCredentialsProvider provider = new ProfileCredentialsProvider(
				"uzresk");
		AmazonSQS sqs = new AmazonSQSClient(provider);
		sqs.setEndpoint(SQS_ENDPOINT);

		String queueUrl = sqs.createQueue(QUEUE).getQueueUrl();
		ReceiveMessageRequest request = new ReceiveMessageRequest(QUEUE)
				.withQueueUrl(queueUrl);

		// 他のReceiverが受信できなくなる時間
		request.setVisibilityTimeout(5);

		ReceiveMessageResult result = sqs.receiveMessage(request);

		System.out.println(result.getMessages());

		// maxNumberOfMessagesのデフォルト値は１なのでget(0)としています。
		DeleteMessageRequest deleteRequest = new DeleteMessageRequest();
		deleteRequest.setQueueUrl(sqs.createQueue(QUEUE).getQueueUrl());
		deleteRequest.setReceiptHandle(result.getMessages().get(0)
				.getReceiptHandle());
		sqs.deleteMessage(deleteRequest);
	}
}
