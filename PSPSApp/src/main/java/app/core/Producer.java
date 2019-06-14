package app.core;

import org.apache.kafka.clients.producer.KafkaProducer;
import org.apache.kafka.clients.producer.ProducerRecord;
import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.*;
import java.util.Properties;

public class Producer {
//    public static final String KAFKA_SERVER_URL = "169.234.34.23";
//    public static final String KAFKA_SERVER_URL = "169.234.6.225";
    private static final String BOOTSTRAP_SERVERS = "169.234.16.58";

    public static final int KAFKA_SERVER_PORT = 9092;

    private static KafkaProducer<String, byte[]> producer = getKafkaProducer();
//    private Producer<String, String> producer = null;
    /**
    * Creates a Producer with the following properties
    */

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
//    public Producer() {
//        // create instance for properties to access producer configs
//        Properties props = new Properties();
//
//        //Assign localhost id
//        props.put("bootstrap.servers", KAFKA_SERVER_URL+":"+KAFKA_SERVER_PORT);
//
//        //Set acknowledgements for producer requests.
//        props.put("acks", "all");
//
//        //If the request fails, the producer can automatically retry,
//        System.out.println("retries: "+configs.getRetries());
//        props.put("retries", configs.getRetries());
//
//        //Specify buffer size in config
//        props.put("batch.size", configs.getBatchSize());
//
//        //Reduce the no of requests less than 0
//        props.put("linger.ms", configs.getLinger());
//
//        //The buffer.memory controls the total amount of memory available to the producer for buffering.
//        props.put("buffer.memory", configs.getBufferMemory());
//
//        props.put("key.serializer",
//                "org.apache.kafka.common.serialization.StringSerializer");
//
////        // OLD: send string message
////         props.put("value.serializer",
////                 "org.apache.kafka.common.serialization.StringSerializer");
////         producer = new KafkaProducer<String, String>(props);
//
//        props.put("value.serializer",
//                "org.apache.kafka.common.serialization.ByteArraySerializer");
//        producer = new KafkaProducer<String, byte[]>(props);
//    }

    public static void pushData(String[] topics, byte[] data) {
        boolean DISK_UPLOAD = false;
        BufferedImage bImage = null;
        try {
            if(DISK_UPLOAD) {
                for(String topic: topics) {
                    try (OutputStream stream = new FileOutputStream("/home/amcheng/Workspace/cs237/PSPS/PSPSApp/" + topic + ".jpg")) {
                        stream.write(data);
                    }
                }
            }
            else {
                for(String topic: topics) {
                    producer.send(new ProducerRecord<String, byte[]>(topic, System.currentTimeMillis() + "", data));
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }

        System.out.println("Messages sent successfully");
    }
}
