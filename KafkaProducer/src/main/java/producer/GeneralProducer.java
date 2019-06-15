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
            String filename = "catfish.jpg";
            BufferedImage bImage = null;
            try {
                bImage = ImageIO.read(new File(path_to_dir+"/"+filename));
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
        String[] a = execArguments(args);
        String picDir = "./";
        if(a == null) {
            a[0] = "dummy";
            a[1] = "./";
        }

        GeneralProducer gp = new GeneralProducer();

        gp.pushData(a[0], a[1]);

        gp.getProducer().close();
    }

    private static String[] execArguments(String[] args) {
        String[] a = new String[2];
        a[0] = null;
        a[1] = null;
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
                        a[0] = args[i];
                        if (a[1] != null) return a;
                        break;
                    case "--dir":
                    case "-d":
                        ++i;
                        a[1] = args[i];
                        if (a[0] != null) return a;
                        break;
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
            System.err.println("Config file name: " + configFile);
            configs = new Configs(loadConfigs(configFile));
            System.err.println("Configuration file successfully loaded.");
        } catch (NullPointerException e) {
            System.out.println("Config file not found.");
        }
    }

    private static ConfigsModel loadConfigs(String file) {
        System.err.println("Loading configuration file...");
        ObjectMapper mapper = new ObjectMapper(new YAMLFactory());
        ConfigsModel configs = null;

        try {
            configs = mapper.readValue(new File(file), ConfigsModel.class);
        } catch (IOException e) {
            System.out.println("Unable to load configuration file.");
        }
        return configs;
    }
}
