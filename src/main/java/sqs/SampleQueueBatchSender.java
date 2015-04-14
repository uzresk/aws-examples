package sqs;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import com.amazonaws.auth.AWSCredentialsProvider;
import com.amazonaws.auth.profile.ProfileCredentialsProvider;
import com.amazonaws.services.sqs.AmazonSQS;
import com.amazonaws.services.sqs.AmazonSQSClient;
import com.amazonaws.services.sqs.model.CreateQueueRequest;
import com.amazonaws.services.sqs.model.SendMessageBatchRequest;
import com.amazonaws.services.sqs.model.SendMessageBatchRequestEntry;

public class SampleQueueBatchSender {

	private static final String SQS_ENDPOINT = "http://sqs.ap-northeast-1.amazonaws.com";

	private static final String QUEUE = "SampleBatchQueue";

	public static void main(String[] args) {

		AWSCredentialsProvider provider = new ProfileCredentialsProvider(
				"uzresk");
		AmazonSQS sqs = new AmazonSQSClient(provider);
		sqs.setEndpoint(SQS_ENDPOINT);

		CreateQueueRequest request = new CreateQueueRequest(QUEUE);
		String queueUrl = sqs.createQueue(request).getQueueUrl();

		SendMessageBatchRequest batchRequest = new SendMessageBatchRequest(queueUrl);
		batchRequest.setQueueUrl(queueUrl);

		List<SendMessageBatchRequestEntry> messages = new ArrayList<SendMessageBatchRequestEntry>();
		for (int i=0; i<10 ; i++) {
			messages.add(new SendMessageBatchRequestEntry(Integer.toString(i),new Date().toString()));
		}
		batchRequest.setEntries(messages);

		sqs.sendMessageBatch(batchRequest);

	}
}
