package aws.sqs;

import com.amazonaws.auth.AWSCredentialsProvider;
import com.amazonaws.auth.profile.ProfileCredentialsProvider;
import com.amazonaws.services.sqs.AmazonSQS;
import com.amazonaws.services.sqs.AmazonSQSClient;
import com.amazonaws.services.sqs.model.DeleteMessageRequest;
import com.amazonaws.services.sqs.model.Message;
import com.amazonaws.services.sqs.model.ReceiveMessageRequest;
import com.amazonaws.services.sqs.model.ReceiveMessageResult;

public class SampleLongPollQueueReceiver {

	private static final String SQS_ENDPOINT = "http://sqs.ap-northeast-1.amazonaws.com";

	private static final String QUEUE = "SampleLongPollQueue";
	
	public static void main(String[] args) {

		AWSCredentialsProvider provider = new ProfileCredentialsProvider(
				"uzresk");
		AmazonSQS sqs = new AmazonSQSClient(provider);
		sqs.setEndpoint(SQS_ENDPOINT);
		
		ReceiveMessageRequest request = new ReceiveMessageRequest(QUEUE);
		request.setQueueUrl(sqs.createQueue(QUEUE).getQueueUrl());
		// 他のReceiverが受信できなくなる時間
		request.setVisibilityTimeout(10);
		// Long pollの設定
		request.setWaitTimeSeconds(20);
		
		ReceiveMessageResult result = sqs.receiveMessage(request);
		
		for (Message message : result.getMessages()) {
			// 受信したメッセージを表示
			System.out.println(message.getMessageId() + ":" + message.getBody());
			// メッセージを削除
			DeleteMessageRequest deleteRequest = new DeleteMessageRequest();
			deleteRequest.setQueueUrl(sqs.createQueue(QUEUE).getQueueUrl());
			deleteRequest.setReceiptHandle(message.getReceiptHandle());
			sqs.deleteMessage(deleteRequest);				
		}
		
	}
}
