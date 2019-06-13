package producer;

import java.io.IOException;
import java.util.Properties;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.dataformat.yaml.YAMLFactory;
import org.apache.kafka.clients.producer.KafkaProducer;
import org.apache.kafka.clients.producer.Producer;
import org.apache.kafka.clients.producer.ProducerRecord;
import producer.configs.Configs;
import producer.models.ConfigsModel;

import java.io.ByteArrayOutputStream;
import java.awt.image.BufferedImage;
import java.io.File;
import javax.imageio.ImageIO;

public class GeneralProducer {

    private static Configs configs;

    public static final String KAFKA_SERVER_URL = "localhost";
    public static final int KAFKA_SERVER_PORT = 9092;

    private Producer<String, byte[]> producer = null;
//    private Producer<String, String> producer = null;
    /**
    * Creates a Producer with the following properties
    */
    public GeneralProducer() {
        // create instance for properties to access producer configs
        Properties props = new Properties();

        //Assign localhost id
        props.put("bootstrap.servers", KAFKA_SERVER_URL+":"+KAFKA_SERVER_PORT);

        //Set acknowledgements for producer requests.
        props.put("acks", "all");

        //If the request fails, the producer can automatically retry,
        System.out.println("retries: "+configs.getRetries());
        props.put("retries", 1);

        //Specify buffer size in config
        props.put("batch.size", 16384);

        //Reduce the no of requests less than 0
        props.put("linger.ms", 1);

        //The buffer.memory controls the total amount of memory available to the producer for buffering.
        props.put("buffer.memory", 33554432);

        props.put("key.serializer",
                "org.apache.kafka.common.serialization.StringSerializer");

//        // OLD: send string message
//         props.put("value.serializer",
//                 "org.apache.kafka.common.serialization.StringSerializer");
//         producer = new KafkaProducer<String, String>(props);

        props.put("value.serializer",
                "org.apache.kafka.common.serialization.ByteArraySerializer");
        producer = new KafkaProducer<String, byte[]>(props);
    }

    // Pushes image data from the given directory
    public void pushData(String topicName, String path_to_dir) {

        // Sends the same image 10 times
        for(int i = 0; i < 10; i++) {
            String filename = "dog1.jpg";
            BufferedImage bImage = null;
            try {
                bImage = ImageIO.read(new File(filename));
                ByteArrayOutputStream bos = new ByteArrayOutputStream();
                ImageIO.write(bImage, "jpg", bos );
                byte [] data = bos.toByteArray();
                System.out.println("data: " + data.toString());
                // Each record for `topicName stores key:`filename with value=image_data
                producer.send(new ProducerRecord<String, byte[]>(topicName,
                        i + filename, data));
            } catch (IOException e) {
                e.printStackTrace();
            }

//            // OLD: send string message
//            System.out.println(topicName);
//            try {
//                producer.send(new ProducerRecord<String, String>(topicName,
//                        Integer.toString(i), "cats are awesome! Message #" + i));
//            }
//            catch (Exception e) {
//                e.printStackTrace();
//            }
        }

        System.out.println("Messages sent successfully");
    }

    public Producer<String, byte[]> getProducer() {
        return producer;
    }

//    public Producer<String, String> getProducer() {
//        return producer;
//    }



    public static void main(String[] args) {
        // Check arguments length value
        // if(args.length == 0){
        //     System.out.println("Enter topic name");
        //     return;
        // }
        //Assign topicName to string variables
        // String topicName = args[0].toString();
        String topic = execArguments(args);
        if(topic == null) topic = "dogs";
        System.out.println("topic: "+topic);

        configs.currentConfigs();

        GeneralProducer gp = new GeneralProducer();

        gp.pushData(topic, "./");

        gp.getProducer().close();
    }

    private static String execArguments(String[] args) {
        if (args.length > 0) {
            for (int i = 0; i < args.length; ++i) {
                switch (args[i]) {
                    case "--config":
                    case "-c": {
                        // Config file specified. Load it.
                        getConfigFile(args[i + 1]);
                        ++i;
                        break;
                    }
                    case "--topic":
                    case "-t":
                        ++i;
                        return args[i];
                    default:
                        System.out.println("unrecognized argument: "+args[i]);
                }
            }
        } else {
            System.out.println("no config, using default values");
        }
        return null;
    }

    private static void getConfigFile(String configFile) {
        try {
            System.out.println("Config file name: " + configFile);
            configs = new Configs(loadConfigs(configFile));
            System.out.println("Configuration file successfully loaded.");
        } catch (NullPointerException e) {
            System.out.println("Config file not found.");
        }
    }

    private static ConfigsModel loadConfigs(String file) {
        System.out.println("Loading configuration file...");
        ObjectMapper mapper = new ObjectMapper(new YAMLFactory());
        ConfigsModel configs = null;
        System.out.println("Reading config values");
        try {
            configs = mapper.readValue(new File(file), ConfigsModel.class);
        } catch (IOException e) {
            System.out.println("Unable to load configuration file.");
        }
        return configs;
    }
}
