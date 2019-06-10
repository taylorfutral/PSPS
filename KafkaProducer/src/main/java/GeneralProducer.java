import java.io.IOException;
import java.util.Properties;
import org.apache.kafka.clients.producer.KafkaProducer;
import org.apache.kafka.clients.producer.Producer;
import org.apache.kafka.clients.producer.ProducerRecord;
import org.apache.kafka.common.serialization.StringSerializer;

import java.io.ByteArrayOutputStream;
import java.awt.image.BufferedImage;
import java.io.File;
import javax.imageio.ImageIO;

public class GeneralProducer {

    public static final String KAFKA_SERVER_URL = "169.234.24.114";
    public static final int KAFKA_SERVER_PORT = 9092;

    public static void main(String[] args) {
        // Check arguments length value
        if(args.length == 0){
            System.out.println("Enter topic name");
            return;
        }

        //Assign topicName to string variable
        String topicName = args[0].toString();

        // create instance for properties to access producer configs
        Properties props = new Properties();

        //Assign localhost id
        props.put("bootstrap.servers", KAFKA_SERVER_URL+":"+KAFKA_SERVER_PORT);

        //Set acknowledgements for producer requests.
        props.put("acks", "all");

        //If the request fails, the producer can automatically retry,
        props.put("retries", 1);

        //Specify buffer size in config
        props.put("batch.size", 16384);

        //Reduce the no of requests less than 0
        props.put("linger.ms", 1);

        //The buffer.memory controls the total amount of memory available to the producer for buffering.
        props.put("buffer.memory", 33554432);

        props.put("key.serializer",
                "org.apache.kafka.common.serialization.StringSerializer");

        // OLD: send string message
        // props.put("value.serializer",
        //         "org.apache.kafka.common.serialization.StringSerializer");
        // Producer<String, String> producer = new KafkaProducer<String, String>(props);

        props.put("value.serializer", 
                "org.apache.kafka.common.serialization.ByteArraySerializer");
        Producer<String, byte[]> producer = new KafkaProducer<String, byte[]>(props);

        for(int i = 0; i < 10; i++) {
            String filename = "dog1.jpg";
            BufferedImage bImage = null;
            try {
                bImage = ImageIO.read(new File(filename));
                ByteArrayOutputStream bos = new ByteArrayOutputStream();
                ImageIO.write(bImage, "jpg", bos );
                byte [] data = bos.toByteArray();

                // Each record for `topicName stores key:`filename with value=image_data
                producer.send(new ProducerRecord<String, byte[]>(topicName,
                        filename, data));
            } catch (IOException e) {
                e.printStackTrace();
            }


            // OLD: send string message
            // producer.send(new ProducerRecord<String, String>(topicName,
                    // Integer.toString(i), "cats are awesome! Message #" + i));
        }

        System.out.println("Message sent successfully");
        producer.close();
    }
}
