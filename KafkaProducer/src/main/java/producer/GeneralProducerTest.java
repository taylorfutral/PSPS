package producer;

import java.util.Arrays;
import java.util.Map;
import java.util.HashMap;
import java.util.Properties;
import org.apache.kafka.clients.admin.AdminClient;
import org.apache.kafka.clients.admin.AdminClientConfig;
import org.apache.kafka.clients.admin.Config;
import org.apache.kafka.clients.admin.ConfigEntry;
import org.apache.kafka.clients.admin.NewTopic;
import org.apache.kafka.common.config.ConfigResource;

public class GeneralProducerTest {

//    public GeneralProducerTest() {
//    }

    // Creates a topic and increases partitions
    public static long timeTest_createTopic() {

        long startTime = System.nanoTime();

        Properties config = new Properties();
        config.put(AdminClientConfig.BOOTSTRAP_SERVERS_CONFIG, "localhost:9092");
        AdminClient admin = AdminClient.create(config);
        Map<String, String> configs = new HashMap<>();
        int partitions = 1;
        short replication = 1;
        configs.put("partitions", Integer.toString(partitions+1));
        String partition_name = "partition";
        String partition_value = Integer.toString(partitions+1);


        Map<ConfigResource,Config> m = new HashMap<ConfigResource,Config>();
        Config c = new Config(Arrays.asList(new ConfigEntry(partition_name,partition_value)));
        ConfigResource cr = new ConfigResource(ConfigResource.Type.TOPIC, partition_name);  // not sure what second param should be
        admin.createTopics(Arrays.asList(new NewTopic("tester", partitions, replication).configs(configs)));

        m.put(cr, c);
        admin.alterConfigs(m);

        long endTime = System.nanoTime();

        long duration = (endTime - startTime);  //divide by 1000000 to get milliseconds.
        return duration;


        /*
        Script equivalent:

            bin/kafka-topics.sh --create --bootstrap-server localhost:9092 --replication-factor 1 --partitions 1 --topic bird

            bin/kafka-topics.sh --list --bootstrap-server localhost:9092

            bin/kafka-topics.sh --describe --bootstrap-server localhost:9092 --topic bird

            bin/kafka-topics.sh --alter --bootstrap-server localhost:9092 --topic bird --partitions 2

            bin/kafka-topics.sh --describe --bootstrap-server localhost:9092 --topic bird

            bin/kafka-topics.sh --delete --bootstrap-server localhost:9092 --topic bird

            bin/kafka-topics.sh --list --bootstrap-server localhost:9092
         */

    }

    // Returns how long it takes to push data for the situation below
    public static long timeTest_pushData() {
        GeneralProducer gp = new GeneralProducer();

        long startTime = System.nanoTime();
        gp.pushData("dogs", "./");
        long endTime = System.nanoTime();
        gp.getProducer().close();

        long duration = (endTime - startTime);  //divide by 1000000 to get milliseconds.
        return duration;
    }

    public static void main(String[] args) {
//        GeneralProducerTest.timeTest_pushData();
        System.out.println("in test...");
        long dur = GeneralProducerTest.timeTest_createTopic();
        System.out.println("Duration of test: "+dur);
    }


}