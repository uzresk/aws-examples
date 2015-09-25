package jp.gr.java_conf.uzresk.aws.dynamodb;

import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;

import com.amazonaws.ClientConfiguration;
import com.amazonaws.auth.ClasspathPropertiesFileCredentialsProvider;
import com.amazonaws.regions.Region;
import com.amazonaws.regions.Regions;
import com.amazonaws.services.dynamodbv2.AmazonDynamoDBClient;
import com.amazonaws.services.dynamodbv2.document.DynamoDB;
import com.amazonaws.services.dynamodbv2.document.ItemCollection;
import com.amazonaws.services.dynamodbv2.document.QueryOutcome;
import com.amazonaws.services.dynamodbv2.document.Table;
import com.amazonaws.services.dynamodbv2.document.spec.QuerySpec;
import com.amazonaws.services.dynamodbv2.document.utils.ValueMap;

public class DynamoDBLoadSampleData {

	static DynamoDB dynamoDB = new DynamoDB(Region.getRegion(Regions.AP_NORTHEAST_1).createClient(
			AmazonDynamoDBClient.class, new ClasspathPropertiesFileCredentialsProvider(),
			new ClientConfiguration().withProxyHost("tisproxy.intra.tis.co.jp").withProxyPort(8080)));

	public static void main(String[] args) {
		findThread();
		
		findReplyWithRange();
	}

	static void findThread() {
		Table table = dynamoDB.getTable("Thread");
		QuerySpec querySpec = new QuerySpec().withKeyConditionExpression("ForumName = :v1")
				.withValueMap(new ValueMap().withString(":v1", "Amazon DynamoDB"))
				.withProjectionExpression("LastPostedBy");
		ItemCollection<QueryOutcome> items = table.query(querySpec);

		items.forEach(item -> System.out.println(item.toJSONPretty()));

	}
	
	static void findReplyWithRange() {
		
		ZonedDateTime past14Day = ZonedDateTime.now().minusDays(14);
		String day = past14Day.format(DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'"));
	
		Table table = dynamoDB.getTable("Reply");
		QuerySpec querySpec = new QuerySpec().withKeyConditionExpression("Id = :v1 and ReplyDateTime > :v2")
				.withValueMap(new ValueMap().withString(":v1", "Amazon DynamoDB#DynamoDB Thread 2").withString(":v2", day));
		ItemCollection<QueryOutcome> items = table.query(querySpec);

		items.forEach(item -> System.out.println(item.toJSONPretty()));

	}
}
