package producer.configs;

import producer.models.ConfigsModel;

public class Configs {
    private final int MIN_SERVICE_PORT = 1024;
    private final int MAX_SERVICE_PORT = 65535;
    // Default producer configs
    private final String DEFAULT_KAFKA_SERVER_HOSTNAME = "0.0.0.0";
    private final int    DEFAULT_KAFKA_SERVER_PORT = 9092;
    private final int    DEFAULT_RETRIES = 1;
    private final int    DEFAULT_BATCH_SIZE = 16384;
    private final int    DEFAULT_LINGER = 1;
    private final int    DEFAULT_BUFFER_MEMORY = 33554432;

    // Default logger configs
    private final String DEFAULT_OUTPUTDIR = "./logs/";
    private final String DEFAULT_OUTPUTFILE = "idm.log";

    // Service configs
    private String kafkaServerURL;
    private int    kafkaServerPort;
    private int    retries;
    private int    batchSize;
    private int    linger;
    private int    bufferMemory;

    // Logger configs
    private String outputDir;
    private String outputFile;

    public Configs(ConfigsModel cm) throws NullPointerException {
        if (cm == null) {
            throw new NullPointerException("Unable to create Configs from ConfigsModel.");
        } else {
            // Set producer configs

            kafkaServerURL = cm.getProducerConfig().get("kafkaServerURL");
            if (kafkaServerURL == null) {
                kafkaServerURL = DEFAULT_KAFKA_SERVER_HOSTNAME;
                System.err.println("kafkaServerURL not found in configuration file. Using default.");
            } else {
                System.err.println("kafkaServerURL: " + kafkaServerURL);
            }

            kafkaServerPort = Integer.parseInt(cm.getProducerConfig().get("kafkaServerPort"));
            if (kafkaServerPort == 0) {
                kafkaServerPort = DEFAULT_KAFKA_SERVER_PORT;
                System.err.println("kafkaServerPort not found in configuration file. Using default.");
            } else if (kafkaServerPort < MIN_SERVICE_PORT || kafkaServerPort > MAX_SERVICE_PORT) {
                kafkaServerPort = DEFAULT_KAFKA_SERVER_PORT;
                System.err.println("kafkaServerPort is not within valid range. Using default.");
            } else {
                System.err.println("kafkaServerPort: " + kafkaServerPort);
            }

            retries = Integer.parseInt(cm.getProducerConfig().get("retries"));
            if (retries == 0) {
                retries = DEFAULT_RETRIES;
                System.err.println("retries not found in configuration file. Using default.");
            } else {
                System.err.println("retries: " + retries);
            }

            batchSize = Integer.parseInt(cm.getProducerConfig().get("batchSize"));
            if (batchSize == 0) {
                batchSize = DEFAULT_BATCH_SIZE;
                System.err.println("batchSize not found in configuration file. Using default.");
            } else {
                System.err.println("batchSize: " + batchSize);
            }

            linger = Integer.parseInt(cm.getProducerConfig().get("linger"));
            if (linger == 0) {
                linger = DEFAULT_LINGER;
                System.err.println("linger not found in configuration file. Using default.");
            } else {
                System.err.println("linger: " + linger);
            }

            bufferMemory = Integer.parseInt(cm.getProducerConfig().get("bufferMemory"));
            if (bufferMemory == 0) {
                bufferMemory = DEFAULT_BUFFER_MEMORY;
                System.err.println("bufferMemory not found in configuration file. Using default.");
            } else {
                System.err.println("bufferMemory: " + bufferMemory);
            }

            // Set logger configs
            outputDir = cm.getProducerConfig().get("outputDir");
            if (outputDir == null) {
                outputDir = DEFAULT_OUTPUTDIR;
                System.err.println("Logging output directory not found in configuration file. Using default.");
            } else {
                System.err.println("Logging output directory: " + outputDir);
            }

            outputFile = cm.getProducerConfig().get("outputFile");
            if (outputFile == null) {
                outputFile = DEFAULT_OUTPUTFILE;
                System.err.println("Logging output file not found in configuration file. Using default.");
            } else {
                System.err.println("Logging output file: " + outputFile);
            }
        }
    }

    public void currentConfigs() {
        System.out.println("kafkaServerHostname: " + kafkaServerURL);
        System.out.println("kafkaServerPort: "+kafkaServerPort);
        System.out.println("retries: "+retries);
        System.out.println("batchSize: "+batchSize);
        System.out.println("linger: "+linger);
        System.out.println("bufferMemory: "+bufferMemory);
    }


    public String getKafkaURL() {
        return kafkaServerURL + ":" + kafkaServerPort;
    }

    public String getKafkaServerHostName() {
        return kafkaServerURL;
    }

    public int getRetries() {
        return retries;
    }

    public int getBatchSize() {
        return batchSize;
    }

    public int getLinger() {
        return linger;
    }

    public int getBufferMemory() {
        return bufferMemory;
    }

    public int getKafkaServerPort() {
        return kafkaServerPort;
    }

    public String getOutputDir() {
        return outputDir;
    }

    public String getOutputFile() {
        return outputFile;
    }
}
