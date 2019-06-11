package KafkaProducer;

import java.util.*;

import org.apache.kafka.clients.consumer.KafkaConsumer;
import org.apache.kafka.clients.consumer.ConsumerRecords;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.apache.kafka.common.PartitionInfo;

import java.io.ByteArrayInputStream;
import java.awt.image.BufferedImage;
import java.io.File;
import javax.imageio.ImageIO;

import java.util.ArrayList;
import java.util.Set;


public class GeneralConsumer {

    private static final String BOOTSTRAP_SERVERS = 
    		"localhost:9092";

    private KafkaConsumer<String, byte[]> consumer = null;

    public GeneralConsumer() {
        // Kafka consumer configuration settings
        Properties props = new Properties();

        props.put("bootstrap.servers", BOOTSTRAP_SERVERS);
        props.put("group.id", "test");
        props.put("enable.auto.commit", "true");
        props.put("auto.commit.interval.ms", "1000");
        props.put("session.timeout.ms", "30000");
        props.put("key.deserializer", 
         "org.apache.kafka.common.serialization.StringDeserializer");
        // props.put("value.deserializer", 
        //    "org.apache.kafka.common.serializa-tion.StringDeserializer");
        // KafkaConsumer<String, String> consumer = new KafkaConsumer
        //    <String, String>(props);

        props.put("value.deserializer", 
         "org.apache.kafka.common.serialization.ByteArraySerializer");
        consumer = new KafkaConsumer<String, byte[]>(props);
    }

    // Return list of available topics to choose from
    public String[] getTopics() {
        ArrayList<String> listTopics = new ArrayList<String>();

        Map<String, List<PartitionInfo>> topics = consumer.listTopics();
        Set<String> topicNames = topics.keySet();
        return topicNames.toArray();

        // Prints the topics to stdout
        // Iterator it = topics.entrySet().iterator();
        // System.out.println("Available topics:");
        // while (it.hasNext()) {
        //  Map.Entry entry = (Map.Entry)it.next();
        //  System.out.println("> " + entry.getKey());
        // }
    }

    public void subscribeTo(String topicName) {
        // Kafka Consumer subscribes list of topics here.
        consumer.subscribe(Arrays.asList(topicName));
        System.out.println("Subscribed to topic " + topicName);
    }

    public void unsubscribeTo(String topicName) {
        Map<String, List<PartitionInfo>> topics = consumer.listTopics();
        topics.remove(topicName);
        if (topicName.equals("*")) {
            // Unsubcribes to all topics
            consumer.unsubscribe();
        } else {
            // Unsubcribes to the specific topic
            consumer.subscribe(topics);
        }
    }

    public void pullData() {

        ConsumerRecords<String, byte[]> records = consumer.poll(100);
        for (ConsumerRecord<String, byte[]> record : records) {

          // Unpack image data
          ByteArrayInputStream bis = new ByteArrayInputStream(record.value());
          BufferedImage bImage2 = ImageIO.read(bis);
          ImageIO.write(bImage2, "jpg", new File(record.key()));
          System.out.println("image saved");

          // print the offset,key and value for the consumer records.
          // System.out.printf("offset = %d, key = %s, value = %s\n", 
          //       record.offset(), record.key(), record.value());
        }

        // For unpacking string messages
        // ConsumerRecords<String, String> records = consumer.poll(100);
        // for (ConsumerRecord<String, String> record : records) {

        //   // print the offset,key and value for the consumer records.
        //   System.out.printf("offset = %d, key = %s, value = %s\n", 
        //       record.offset(), record.key(), record.value());
        // }
        
    }

    public static void main(String[] args) throws Exception {
        GeneralConsumer gc = new GeneralConsumer();

        String topicName = "cats";
        gc.subscribeTo(topicName);
        gc.pullData();
        
   }
}
