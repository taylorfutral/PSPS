package app.core;

import org.apache.kafka.clients.producer.KafkaProducer;
import org.apache.kafka.clients.producer.ProducerRecord;
import java.util.Properties;

public class Producer {
    private static final String BOOTSTRAP_SERVERS = "169.234.16.58";

    public static final int KAFKA_SERVER_PORT = 9092;

    private static KafkaProducer<String, byte[]> producer = getKafkaProducer();

    private static KafkaProducer<String, byte[]> getKafkaProducer() {
        Properties props = new Properties();
        props.put("bootstrap.servers", BOOTSTRAP_SERVERS+":"+KAFKA_SERVER_PORT);
        props.put("acks", "all");
        props.put("retries", 1);
        props.put("batch.size", 16384);
        props.put("linger.ms", 50);
        props.put("buffer.memory", 33554432);
        props.put("key.serializer",
                "org.apache.kafka.common.serialization.StringSerializer");

        props.put("value.serializer",
                "org.apache.kafka.common.serialization.ByteArraySerializer");

        return new KafkaProducer<String, byte[]>(props);
    }


    public static void pushData(String[] topics, byte[] data) {
        for(String topic: topics) {
            producer.send(new ProducerRecord<String, byte[]>(topic, System.currentTimeMillis() + "", data));
            try {
                Thread.sleep(5);
            } catch(InterruptedException ex) {}
        }
    }
}
