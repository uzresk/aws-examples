package aws.sqs;

import java.util.ArrayList;
import java.util.List;

import com.amazonaws.auth.AWSCredentialsProvider;
import com.amazonaws.auth.profile.ProfileCredentialsProvider;
import com.amazonaws.services.sqs.AmazonSQS;
import com.amazonaws.services.sqs.AmazonSQSClient;
import com.amazonaws.services.sqs.model.DeleteMessageRequest;
import com.amazonaws.services.sqs.model.Message;
import com.amazonaws.services.sqs.model.ReceiveMessageRequest;
import com.amazonaws.services.sqs.model.ReceiveMessageResult;

public class DeadLetterQueueReceiver {

	private static final String SQS_ENDPOINT = "http://sqs.ap-northeast-1.amazonaws.com";

	private static final String QUEUE = "SampleBatchQueue2";

	public static void main(String[] args) {

		AWSCredentialsProvider provider = new ProfileCredentialsProvider(
				"uzresk");
		AmazonSQS sqs = new AmazonSQSClient(provider);
		sqs.setEndpoint(SQS_ENDPOINT);
	
		ReceiveMessageRequest request = new ReceiveMessageRequest(QUEUE);
		List<String> attributeNames = new ArrayList<String>();
		attributeNames.add("ApproximateFirstReceiveTimestamp");
		attributeNames.add("ApproximateReceiveCount");
		attributeNames.add("SenderId");
		attributeNames.add("SentTimestamp");
		request.setAttributeNames(attributeNames);
		String queueUrl = sqs.createQueue(QUEUE).getQueueUrl();
		request.setQueueUrl(queueUrl);
		// 他のReceiverが受信できなくなる時間
		request.setVisibilityTimeout(10);
		// Long pollの設定
		request.setWaitTimeSeconds(10);

		ReceiveMessageResult result = sqs.receiveMessage(request);

		for (Message message : result.getMessages()) {
			// 受信したメッセージを表示
			System.out.println(message.getMessageId() + ":" + message.getBody());
			System.out.println("ApproximateFirstReceiveTimestamp:" + message.getAttributes().get("ApproximateFirstReceiveTimestamp"));
			System.out.println("ApproximateReceiveCount:" + message.getAttributes().get("ApproximateReceiveCount"));
			System.out.println("SenderId:" + message.getAttributes().get("SenderId"));
			System.out.println("SentTimestamp:" + message.getAttributes().get("SentTimestamp"));
			// メッセージを削除
//			DeleteMessageRequest deleteRequest = new DeleteMessageRequest();
//			deleteRequest.setQueueUrl(sqs.createQueue(QUEUE).getQueueUrl());
//			deleteRequest.setReceiptHandle(message.getReceiptHandle());
//			sqs.deleteMessage(deleteRequest);
		}

	}
}
