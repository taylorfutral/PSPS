package app.core;

import app.PSPSAppService;
import org.apache.kafka.clients.consumer.ConsumerConfig;
import org.apache.kafka.clients.consumer.ConsumerRecord;
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

public class Consumer {
    private static final String BOOTSTRAP_SERVERS = 
    		"169.234.52.77:9092";

    private static final int POLL_TIME_OUT = 5000;

    private static KafkaConsumer<String, byte[]> consumer = getConsumer();

    private static KafkaConsumer<String, byte[]> getConsumer() {
        Properties props = new Properties();
        props.put(ConsumerConfig.MAX_POLL_RECORDS_CONFIG, 1);
        props.put(ConsumerConfig.ENABLE_AUTO_COMMIT_CONFIG, "false");
        props.put(ConsumerConfig.AUTO_OFFSET_RESET_CONFIG, "latest");
        props.put("bootstrap.servers", PSPSAppService.getConfigs().getKafkaURL());
        props.put("group.id", "dd");
        props.put("key.deserializer",
                "org.apache.kafka.common.serialization.StringDeserializer");

        props.put("value.deserializer",
                "org.apache.kafka.common.serialization.ByteArrayDeserializer");


        return new KafkaConsumer<String, byte[]>(props);
    }

//    public Consumer() {
//        Properties props = new Properties();
//
////        props.put(ConsumerConfig.BOOTSTRAP_SERVERS_CONFIG, IKafkaConstants.KAFKA_BROKERS);
////        props.put(ConsumerConfig.GROUP_ID_CONFIG, IKafkaConstants.GROUP_ID_CONFIG);
////        props.put(ConsumerConfig.KEY_DESERIALIZER_CLASS_CONFIG, LongDeserializer.class.getName());
////        props.put(ConsumerConfig.VALUE_DESERIALIZER_CLASS_CONFIG, StringDeserializer.class.getName());
//        props.put(ConsumerConfig.MAX_POLL_RECORDS_CONFIG, 1000);
//        props.put(ConsumerConfig.ENABLE_AUTO_COMMIT_CONFIG, "false");
//        props.put(ConsumerConfig.AUTO_OFFSET_RESET_CONFIG, "earliest");
////        Consumer<Long, String> app = new KafkaConsumer<>(props);
////        app.subscribe(Collections.singletonList(IKafkaConstants.TOPIC_NAME));
//
////        props.put(AdminClientConfig.BOOTSTRAP_SERVERS_CONFIG, "localhost:9092");
//
//        props.put("bootstrap.servers", BOOTSTRAP_SERVERS);
////        props.put("acks", "all");
////        props.put("retries", 1);
//        props.put("group.id", "dd");
////        props.put("enable.auto.commit", "true");
////        props.put("auto.commit.interval.ms", "1000");
////        props.put("session.timeout.ms", "30000");
//
//        props.put("key.deserializer",
//         "org.apache.kafka.common.serialization.StringDeserializer");
//
//        props.put("value.deserializer",
//         "org.apache.kafka.common.serialization.ByteArrayDeserializer");
//        consumer = new KafkaConsumer<String, byte[]>(props);
//    }


    // Return list of available topics to choose from
    public static String[] getTopics() {

        try {
            ZooKeeper zk = new ZooKeeper("169.234.34.23:2181", 10000, null);
            List<String> topics = zk.getChildren("/brokers/topics", false);
            for (String topic : topics) {
                System.out.println(topic);
            }
            return topics.toArray(new String[0]);

        } catch (InterruptedException | KeeperException | IOException e) {
            e.printStackTrace();
        }
        return null;
    }

    public static void subscribeTo(String topicName) {
        Set<String> topics = consumer.subscription();

        List<String> topicsList = new ArrayList<String>();
        topicsList.add(topicName);

        for(String topic: topics) {
            System.out.println("Adding existing topic: " + topic);
            topicsList.add(topic);
        }
        try {
            consumer.subscribe(topicsList);
        }
        catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static void unsubscribeTo(String topicName) {
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

    public static void pullData() {
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

    public static byte[] pullImage() {
        //unsubscribeTo("*");

        System.out.println("accessed pullData");
        ConsumerRecords<String, byte[]> records = consumer.poll(POLL_TIME_OUT);
        System.out.println(records.toString());

        byte[] returnImage = null;

        for(ConsumerRecord<String, byte[]> record: records) {
            System.out.println("Record Key "+ record.key());
            System.out.println("Record partition "+record.partition());
            System.out.println("Record offset "+ record.offset());
            returnImage = record.value();
        }
        return returnImage;
    }
}
