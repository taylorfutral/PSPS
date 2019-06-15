package app.core;

import app.logger.ServiceLogger;
import org.apache.kafka.clients.consumer.ConsumerConfig;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.apache.kafka.clients.consumer.ConsumerRecords;
import org.apache.kafka.clients.consumer.KafkaConsumer;
import org.apache.kafka.common.PartitionInfo;
import org.apache.zookeeper.KeeperException;
import org.apache.zookeeper.ZooKeeper;

import java.io.IOException;
import java.util.*;

public class Consumer {
    private static final String BOOTSTRAP_SERVERS = "169.234.16.58";

    private static final int POLL_TIME_OUT = 2000;

    private static KafkaConsumer<String, byte[]> consumer = getConsumer();

    private static KafkaConsumer<String, byte[]> getConsumer() {
        Properties props = new Properties();
        props.put(ConsumerConfig.MAX_POLL_RECORDS_CONFIG, 1);
        props.put(ConsumerConfig.ENABLE_AUTO_COMMIT_CONFIG, "false");
        props.put(ConsumerConfig.AUTO_OFFSET_RESET_CONFIG, "earliest");
        props.put("bootstrap.servers", BOOTSTRAP_SERVERS + ":9092");
        props.put("group.id", "unique_group");

        props.put("key.deserializer",
                "org.apache.kafka.common.serialization.StringDeserializer");

        props.put("value.deserializer",
                "org.apache.kafka.common.serialization.ByteArrayDeserializer");


        return new KafkaConsumer<String, byte[]>(props);
    }

    public static String[] getTopics() {

        try {
            ZooKeeper zk = new ZooKeeper(BOOTSTRAP_SERVERS + ":" +2181, 10000, null);
            List<String> topics = zk.getChildren("/brokers/topics", false);
            for (Iterator<String> iter = topics.listIterator(); iter.hasNext(); ) {
                String topic = iter.next();
                if (topic.equals("__consumer_offsets")) {
                    iter.remove();
                }
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


    public static String[] subscribedTo() {
        Set<String> topics = consumer.subscription();

        return topics.toArray(new String[0]);
    }

    public static void unsubscribeTo(String topicName) {
        Set<String> topics = consumer.subscription();

        List<String> topicsList = new ArrayList<String>();

        for(String topic: topics) {
            System.out.println("Adding existing topic: " + topic);
            topicsList.add(topic);
        }

        try {
            topicsList.remove(topicName);

            if (topicsList.isEmpty()) {
                ServiceLogger.LOGGER.info("No more topics");
                consumer.unsubscribe();
            } else if (topicName.equals("*")) {
                // Unsubcribes to all topics
                consumer.unsubscribe();
            } else {
                ServiceLogger.LOGGER.info("readding topics");
                // Unsubcribes to the specific topic
                consumer.subscribe(topicsList);
            }
        }catch (Exception e) {
            e.printStackTrace();
        }
    }


    public static byte[] pullImage() {
        System.out.println("accessed pullData");
        ConsumerRecords<String, byte[]> records = consumer.poll(POLL_TIME_OUT);
        System.out.println(records.toString());

        byte[] returnImage = null;
        if(records.isEmpty()) {
            return null;
        }

        for(ConsumerRecord<String, byte[]> record: records) {
            System.out.println("Record Key "+ record.key());
            System.out.println("Record partition "+record.partition());
            System.out.println("Record offset "+ record.offset());
            returnImage = record.value();
        }
        return returnImage;
    }
}
