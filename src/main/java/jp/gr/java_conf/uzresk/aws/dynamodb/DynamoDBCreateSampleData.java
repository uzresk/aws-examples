package jp.gr.java_conf.uzresk.aws.dynamodb;

import java.text.SimpleDateFormat;
import java.util.Arrays;
import java.util.Date;
import java.util.HashSet;
import java.util.TimeZone;

import com.amazonaws.AmazonServiceException;
import com.amazonaws.ClientConfiguration;
import com.amazonaws.auth.ClasspathPropertiesFileCredentialsProvider;
import com.amazonaws.regions.Region;
import com.amazonaws.regions.Regions;
import com.amazonaws.services.dynamodbv2.AmazonDynamoDBClient;
import com.amazonaws.services.dynamodbv2.document.DynamoDB;
import com.amazonaws.services.dynamodbv2.document.Item;
import com.amazonaws.services.dynamodbv2.document.Table;

public class DynamoDBCreateSampleData {

	static DynamoDB dynamoDB = new DynamoDB(Region.getRegion(Regions.AP_NORTHEAST_1).createClient(
			AmazonDynamoDBClient.class, new ClasspathPropertiesFileCredentialsProvider(),
			new ClientConfiguration().withProxyHost("tisproxy.intra.tis.co.jp").withProxyPort(8080)));

	static SimpleDateFormat dateFormatter = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'");
	static String forumTableName = "Forum";
	static String threadTableName = "Thread";
	static String replyTableName = "Reply";

	public static void main(String[] args) {

		try {
			loadSampleForums(forumTableName);
			loadSampleThreads(threadTableName);
			loadSampleReplies(replyTableName);

		} catch (AmazonServiceException ase) {
			System.err.println("Data load script failed.");
		}
	}

	private static void loadSampleForums(String tableName) {

		Table table = dynamoDB.getTable(tableName);

		try {

			System.out.println("Adding data to " + tableName);

			Item item = new Item().withPrimaryKey("Name", "Amazon DynamoDB")
					.withString("Category", "Amazon Web Services").withNumber("Threads", 2).withNumber("Messages", 4)
					.withNumber("Views", 1000);
			table.putItem(item);

			item = new Item().withPrimaryKey("Name", "Amazon S3").withString("Category", "Amazon Web Services")
					.withNumber("Threads", 0);
			table.putItem(item);

		} catch (Exception e) {
			System.err.println("Failed to create item in " + tableName);
			System.err.println(e.getMessage());
		}
	}

	private static void loadSampleThreads(String tableName) {
		try {
			long time1 = (new Date()).getTime() - (7 * 24 * 60 * 60 * 1000); // 7
			// days
			// ago
			long time2 = (new Date()).getTime() - (14 * 24 * 60 * 60 * 1000); // 14
			// days
			// ago
			long time3 = (new Date()).getTime() - (21 * 24 * 60 * 60 * 1000); // 21
			// days
			// ago

			Date date1 = new Date();
			date1.setTime(time1);

			Date date2 = new Date();
			date2.setTime(time2);

			Date date3 = new Date();
			date3.setTime(time3);

			dateFormatter.setTimeZone(TimeZone.getTimeZone("UTC"));

			Table table = dynamoDB.getTable(tableName);

			System.out.println("Adding data to " + tableName);

			Item item = new Item().withPrimaryKey("ForumName", "Amazon DynamoDB")
					.withString("Subject", "DynamoDB Thread 1").withString("Message", "DynamoDB thread 1 message")
					.withString("LastPostedBy", "User A").withString("LastPostedDateTime", dateFormatter.format(date2))
					.withNumber("Views", 0).withNumber("Replies", 0).withNumber("Answered", 0)
					.withStringSet("Tags", new HashSet<String>(Arrays.asList("index", "primarykey", "table")));
			table.putItem(item);

			item = new Item().withPrimaryKey("ForumName", "Amazon DynamoDB").withString("Subject", "DynamoDB Thread 2")
					.withString("Message", "DynamoDB thread 2 message").withString("LastPostedBy", "User A")
					.withString("LastPostedDateTime", dateFormatter.format(date3)).withNumber("Views", 0)
					.withNumber("Replies", 0).withNumber("Answered", 0)
					.withStringSet("Tags", new HashSet<String>(Arrays.asList("index", "primarykey", "rangekey")));
			table.putItem(item);

			item = new Item().withPrimaryKey("ForumName", "Amazon S3").withString("Subject", "S3 Thread 1")
					.withString("Message", "S3 Thread 3 message").withString("LastPostedBy", "User A")
					.withString("LastPostedDateTime", dateFormatter.format(date1)).withNumber("Views", 0)
					.withNumber("Replies", 0).withNumber("Answered", 0)
					.withStringSet("Tags", new HashSet<String>(Arrays.asList("largeobjects", "multipart upload")));
			table.putItem(item);

		} catch (Exception e) {
			System.err.println("Failed to create item in " + tableName);
			System.err.println(e.getMessage());
		}

	}

	private static void loadSampleReplies(String tableName) {
		try {
			// 1 day ago
			long time0 = (new Date()).getTime() - (1 * 24 * 60 * 60 * 1000);
			// 7 days ago
			long time1 = (new Date()).getTime() - (7 * 24 * 60 * 60 * 1000);
			// 14 days ago
			long time2 = (new Date()).getTime() - (14 * 24 * 60 * 60 * 1000);
			// 21 days ago
			long time3 = (new Date()).getTime() - (21 * 24 * 60 * 60 * 1000);

			Date date0 = new Date();
			date0.setTime(time0);

			Date date1 = new Date();
			date1.setTime(time1);

			Date date2 = new Date();
			date2.setTime(time2);

			Date date3 = new Date();
			date3.setTime(time3);

			dateFormatter.setTimeZone(TimeZone.getTimeZone("UTC"));

			Table table = dynamoDB.getTable(tableName);

			System.out.println("Adding data to " + tableName);

			// Add threads.

			Item item = new Item().withPrimaryKey("Id", "Amazon DynamoDB#DynamoDB Thread 1")
					.withString("ReplyDateTime", (dateFormatter.format(date3)))
					.withString("Message", "DynamoDB Thread 1 Reply 1 text").withString("PostedBy", "User A");
			table.putItem(item);

			item = new Item().withPrimaryKey("Id", "Amazon DynamoDB#DynamoDB Thread 1")
					.withString("ReplyDateTime", dateFormatter.format(date2))
					.withString("Message", "DynamoDB Thread 1 Reply 2 text").withString("PostedBy", "User B");
			table.putItem(item);

			item = new Item().withPrimaryKey("Id", "Amazon DynamoDB#DynamoDB Thread 2")
					.withString("ReplyDateTime", dateFormatter.format(date1))
					.withString("Message", "DynamoDB Thread 2 Reply 1 text").withString("PostedBy", "User A");
			table.putItem(item);

			item = new Item().withPrimaryKey("Id", "Amazon DynamoDB#DynamoDB Thread 2")
					.withString("ReplyDateTime", dateFormatter.format(date0))
					.withString("Message", "DynamoDB Thread 2 Reply 2 text").withString("PostedBy", "User A");
			table.putItem(item);

		} catch (Exception e) {
			System.err.println("Failed to create item in " + tableName);
			System.err.println(e.getMessage());

		}
	}

}
