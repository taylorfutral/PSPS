package consumer.configs;

import consumer.logger.ServiceLogger;
import consumer.models.ConfigsModel;

public class Configs {
    private final int MIN_SERVICE_PORT = 1024;
    private final int MAX_SERVICE_PORT = 65535;
    // Default service configs
    private final String DEFAULT_SCHEME = "http://";
    private final String DEFAULT_HOSTNAME = "0.0.0.0";
    private final int    DEFAULT_PORT = 1234;
    private final String DEFAULT_PATH = "/api/billing";
    // Default logger configs
    private final String DEFAULT_OUTPUTDIR = "./logs/";
    private final String DEFAULT_OUTPUTFILE = "idm.log";

    private final String DEFAULT_KAFKA_HOSTNAME = "localhost";
    private final int DEFAULT_KAFKA_PORT = 9092;

    // Service configs
    private String scheme;
    private String hostName;
    private int    port;
    private String path;
    // Logger configs
    private String outputDir;
    private String outputFile;


    private String kafkaHostName;
    private int kafkaPort;

    public Configs(ConfigsModel cm) throws NullPointerException {
        if (cm == null) {
            throw new NullPointerException("Unable to create Configs from ConfigsModel.");
        } else {
            // Set service configs
            scheme = cm.getServiceConfig().get("scheme");
            if (scheme == null) {
                scheme = DEFAULT_SCHEME;
                System.err.println("Scheme not found in configuration file. Using default.");
            } else {
                System.err.println("Scheme: " + scheme);
            }

            hostName = cm.getServiceConfig().get("hostName");
            if (hostName == null) {
                hostName = DEFAULT_HOSTNAME;
                System.err.println("Hostname not found in configuration file. Using default.");
            } else {
                System.err.println("Hostname: " + hostName);
            }

            port = Integer.parseInt(cm.getServiceConfig().get("port"));
            if (port == 0) {
                port = DEFAULT_PORT;
                System.err.println("Port not found in configuration file. Using default.");
            } else if (port < MIN_SERVICE_PORT || port > MAX_SERVICE_PORT) {
                port = DEFAULT_PORT;
                System.err.println("Port is not within valid range. Using default.");
            } else {
                System.err.println("Port: " + port);
            }

            path = cm.getServiceConfig().get("path");
            if (path == null) {
                path = DEFAULT_PATH;
                System.err.println("Path not found in configuration file. Using default.");
            } else {
                System.err.println("Path: " + path);
            }

            // Set logger configs
            outputDir = cm.getLoggerConfig().get("outputDir");
            if (outputDir == null) {
                outputDir = DEFAULT_OUTPUTDIR;
                System.err.println("Logging output directory not found in configuration file. Using default.");
            } else {
                System.err.println("Logging output directory: " + outputDir);
            }

            outputFile = cm.getLoggerConfig().get("outputFile");
            if (outputFile == null) {
                outputFile = DEFAULT_OUTPUTFILE;
                System.err.println("Logging output file not found in configuration file. Using default.");
            } else {
                System.err.println("Logging output file: " + outputFile);
            }

            //set kafka configs
            kafkaHostName = cm.getKafkaConfig().get("hostName");
            if (kafkaHostName == null) {
                kafkaHostName = DEFAULT_KAFKA_HOSTNAME;
                System.err.println("Kafka Hostname not found in configuration file. Using default.");
            } else {
                System.err.println("Kafka hostname: " + hostName);
            }

            kafkaPort = Integer.parseInt(cm.getKafkaConfig().get("port"));
            if (kafkaPort == 0) {
                kafkaPort = DEFAULT_KAFKA_PORT;
                System.err.println("Port not found in configuration file. Using default.");
            } else if (kafkaPort < MIN_SERVICE_PORT || kafkaPort > MAX_SERVICE_PORT) {
                kafkaPort = DEFAULT_KAFKA_PORT;
                System.err.println("Port is not within valid range. Using default.");
            } else {
                System.err.println("Port: " + kafkaPort);
            }
        }
    }

    public void currentConfigs() {
        ServiceLogger.LOGGER.config("Scheme: " + scheme);
        ServiceLogger.LOGGER.config("Hostname: " + hostName);
        ServiceLogger.LOGGER.config("Port: " + port);
        ServiceLogger.LOGGER.config("Path: " + path);
        ServiceLogger.LOGGER.config("Logger output directory: " + outputDir);
        ServiceLogger.LOGGER.config("Logger output file: " + outputFile);
        ServiceLogger.LOGGER.config("kafkaHostName: " + kafkaHostName);
        ServiceLogger.LOGGER.config("kafkaPort: " + kafkaPort);
    }


    public String getBaseURL() {
        return scheme + hostName + ":" + port + path;
    }

    public String getHostURL() {
        return scheme + hostName + ":" + port;
    }

    public String getScheme() {
        return scheme;
    }

    public String getHostName() {
        return hostName;
    }

    public int getPort() {
        return port;
    }

    public String getPath() {
        return path;
    }

    public String getOutputDir() {
        return outputDir;
    }

    public String getOutputFile() {
        return outputFile;
    }

    public String getKafkaHostName() {
        return kafkaHostName;
    }

    public int getKafkaPort() {
        return kafkaPort;
    }
}
