package consumer.core;

import org.apache.kafka.clients.consumer.ConsumerConfig;
import org.apache.kafka.clients.consumer.ConsumerRecords;
import org.apache.kafka.clients.consumer.KafkaConsumer;
import org.apache.kafka.common.PartitionInfo;
import org.apache.zookeeper.KeeperException;
import org.apache.zookeeper.ZooKeeper;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.IOException;
import java.util.*;

//import org.apache.kafka.clients.admin.AdminClientConfig;
//import org.apache.kafka.clients.admin.ListTopicsOptions;



public class GeneralConsumer {

    private static final String BOOTSTRAP_SERVERS = 
    		"169.234.52.77:9092";

    private static final int POLL_TIME_OUT = 10000;

//    private AdminClient adminClient = null;
    private KafkaConsumer<String, byte[]> consumer = null;
//    private KafkaConsumer<String, String> consumer = null;

    public GeneralConsumer() {
        // Kafka consumer configuration settings
        Properties props = new Properties();

//        props.put(ConsumerConfig.BOOTSTRAP_SERVERS_CONFIG, IKafkaConstants.KAFKA_BROKERS);
//        props.put(ConsumerConfig.GROUP_ID_CONFIG, IKafkaConstants.GROUP_ID_CONFIG);
//        props.put(ConsumerConfig.KEY_DESERIALIZER_CLASS_CONFIG, LongDeserializer.class.getName());
//        props.put(ConsumerConfig.VALUE_DESERIALIZER_CLASS_CONFIG, StringDeserializer.class.getName());
        props.put(ConsumerConfig.MAX_POLL_RECORDS_CONFIG, 1000);
        props.put(ConsumerConfig.ENABLE_AUTO_COMMIT_CONFIG, "false");
        props.put(ConsumerConfig.AUTO_OFFSET_RESET_CONFIG, "earliest");
//        Consumer<Long, String> consumer = new KafkaConsumer<>(props);
//        consumer.subscribe(Collections.singletonList(IKafkaConstants.TOPIC_NAME));

//        props.put(AdminClientConfig.BOOTSTRAP_SERVERS_CONFIG, "localhost:9092");

        props.put("bootstrap.servers", BOOTSTRAP_SERVERS);
//        props.put("acks", "all");
//        props.put("retries", 1);
        props.put("group.id", "dd");
//        props.put("enable.auto.commit", "true");
//        props.put("auto.commit.interval.ms", "1000");
//        props.put("session.timeout.ms", "30000");

        props.put("key.deserializer",
         "org.apache.kafka.common.serialization.StringDeserializer");
//         props.put("value.deserializer",
//            "org.apache.kafka.common.serialization.StringDeserializer");
//         consumer = new KafkaConsumer
//            <String, String>(props);

        props.put("value.deserializer",
         "org.apache.kafka.common.serialization.ByteArrayDeserializer");
        consumer = new KafkaConsumer<String, byte[]>(props);

//        adminClient = AdminClient.create(props);
    }


    // Return list of available topics to choose from
    public String[] getTopics() {

        try {
            ZooKeeper zk = new ZooKeeper("localhost:2181", 10000, null);
            List<String> topics = zk.getChildren("/brokers/topics", false);
//            for (String topic : topics) {
//                System.out.println(topic);
//            }
            return topics.toArray(new String[0]);

        } catch (InterruptedException | KeeperException | IOException e) {
            e.printStackTrace();
        }
        return null;

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
            Set<String> listOfRemainingNames = topics.keySet();
            consumer.subscribe(listOfRemainingNames);
        }
    }

    public void pullData() {
        System.out.println("accessed pullData");
        ConsumerRecords<String, byte[]> records = consumer.poll(POLL_TIME_OUT);
        System.out.println(records.toString());

         records.forEach(record -> {
             System.out.println("Record Key "+ record.key());
             System.out.print("Record value: ");
             try {
                ByteArrayInputStream bis = new ByteArrayInputStream(record.value());
                BufferedImage bImage2 = ImageIO.read(bis);
                ImageIO.write(bImage2, "jpg", new File(record.key()));
                System.out.println("image saved");
            }catch (IOException e){
                e.printStackTrace();
            }
             System.out.println("Record partition "+record.partition());
             System.out.println("Record offset "+ record.offset());
         });
        
    }

    public byte[] pullImage() {
        System.out.println("accessed pullData");
        ConsumerRecords<String, byte[]> records = consumer.poll(POLL_TIME_OUT);
        System.out.println(records.toString());

        byte[] returnImage = null;
//        records.forEach(record -> {
//            System.out.println("Record Key "+ record.key());
//            System.out.println("Record partition "+record.partition());
//            System.out.println("Record offset "+ record.offset());
//            returnImage = record.value();
//        });

        for(ConsumerRecord<String, byte[]> record: records) {
            System.out.println("Record Key "+ record.key());
            System.out.println("Record partition "+record.partition());
            System.out.println("Record offset "+ record.offset());
            returnImage = record.value();
        }
        return returnImage;
    }
}
