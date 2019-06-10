package KafkaProducer;

import java.util.*;

import org.apache.kafka.clients.consumer.KafkaConsumer;
import org.apache.kafka.clients.consumer.ConsumerRecords;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.apache.kafka.common.PartitionInfo;

/*
Consumer will ask user for a topic to get subscriptions from.
If "--list" keyword used, a list of available topics will display
and then ask for a topic name again.
Otherwise, the consumer subscribes to the topic name given.
*/

public class GeneralConsumer {
    private static final String BOOTSTRAP_SERVERS = 
    		"localhost:9092";

   public static void main(String[] args) throws Exception {
      if(args.length == 0){
         System.out.println("Enter topic name");
         return;
      }
      //Kafka consumer configuration settings
      String topicName = args[0].toString();
      Properties props = new Properties();
      
      props.put("bootstrap.servers", BOOTSTRAP_SERVERS);
      props.put("group.id", "test");
      props.put("enable.auto.commit", "true");
      props.put("auto.commit.interval.ms", "1000");
      props.put("session.timeout.ms", "30000");
      props.put("key.deserializer", 
         "org.apache.kafka.common.serializa-tion.StringDeserializer");
      props.put("value.deserializer", 
         "org.apache.kafka.common.serializa-tion.StringDeserializer");
      KafkaConsumer<String, String> consumer = new KafkaConsumer
         <String, String>(props);
      
      //Return list of available topics to choose from 
	  if (topicName == "--list") {
	     Map<String, List<PartitionInfo>> topics = consumer.listTopics();
	     Iterator it = topics.entrySet().iterator();
	     System.out.println("Available topics:");
	     while (it.hasNext()) {
             Map.Entry entry = (Map.Entry)it.next();
             System.out.println("> " + entry.getKey());
	     }
	     System.out.println("\nEnter topic name");
	     return;
	  }
      
      //Kafka Consumer subscribes list of topics here.
      consumer.subscribe(Arrays.asList(topicName));
      
      //print the topic name
      System.out.println("Subscribed to topic " + topicName);
      int i = 0;
      
      while (true) {
         ConsumerRecords<String, String> records = consumer.poll(100);
         for (ConsumerRecord<String, String> record : records) {
         
         	// print the offset,key and value for the consumer records.
         	System.out.printf("offset = %d, key = %s, value = %s\n", 
            		record.offset(), record.key(), record.value());
         }
      }
   }
}
