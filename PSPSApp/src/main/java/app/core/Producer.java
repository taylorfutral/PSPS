package app.core;

import org.apache.kafka.clients.producer.KafkaProducer;
import org.apache.kafka.clients.producer.ProducerRecord;
import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.util.Properties;

public class Producer {
    public static final String KAFKA_SERVER_URL = "localhost";
    public static final int KAFKA_SERVER_PORT = 9092;

    private static KafkaProducer<String, byte[]> producer = getKafkaProducer();
//    private Producer<String, String> producer = null;
    /**
    * Creates a Producer with the following properties
    */

    private static KafkaProducer<String, byte[]> getKafkaProducer() {
        Properties props = new Properties();
        props.put("bootstrap.servers", KAFKA_SERVER_URL+":"+KAFKA_SERVER_PORT);
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

    public void pushData(String topicName, byte[] data) {
        boolean DISK_UPLOAD = true;
        for(int i = 0; i < 10; i++) {
            String filename = "dog1.jpg";
            BufferedImage bImage = null;
            try {
                if(DISK_UPLOAD) {
                    bImage = ImageIO.read(new File(filename));
                    ByteArrayOutputStream bos = new ByteArrayOutputStream();
                    ImageIO.write(bImage, "jpg", bos);
                    data = bos.toByteArray();
                    producer.send(new ProducerRecord<String, byte[]>(topicName, i + filename, data));
                }
                else {
                    producer.send(new ProducerRecord<String, byte[]>(topicName, i+filename, data));
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        System.out.println("Messages sent successfully");
    }
}
