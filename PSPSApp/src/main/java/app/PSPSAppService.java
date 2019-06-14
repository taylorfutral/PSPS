package app;

import app.core.Consumer;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.dataformat.yaml.YAMLFactory;
import app.configs.Configs;
import app.logger.ServiceLogger;
import app.models.ConfigsModel;
import org.glassfish.grizzly.http.server.HttpServer;
import org.glassfish.jersey.grizzly2.httpserver.GrizzlyHttpServerFactory;
import org.glassfish.jersey.jackson.JacksonFeature;
import org.glassfish.jersey.server.ResourceConfig;

import javax.ws.rs.core.UriBuilder;
import java.io.File;
import java.io.IOException;
import java.net.*;

public class PSPSAppService {
    public static PSPSAppService PSPSAppService;

    private static Configs configs;

    public static void main(String[] args) {
        PSPSAppService = new PSPSAppService();
        PSPSAppService.initService(args);
    }

    private void initService(String[] args) {
        // Validate arguments
        validateArguments(args);
        // Exec the arguments
        execArguments(args);
        // Initialize logging
        initLogging();
        ServiceLogger.LOGGER.config("Starting service...");
        configs.currentConfigs();

        //Consumer.subscribeTo("dogs");
        // Initialize HTTP server
        initHTTPServer();

    }

    private void validateArguments(String[] args) {
        boolean isConfigOptionSet = false;
        for (int i = 0; i < args.length; ++i) {
            switch (args[i]) {
                case "--default":
                case "-d":
                    if (i + 1 < args.length) {
                        exitAppFailure("Invalid arg after " + args[i] + " option: " + args[i + 1]);
                    }
                case "--config":
                case "-c":
                    if (!isConfigOptionSet) {
                        isConfigOptionSet = true;
                        ++i;
                    } else {
                        exitAppFailure("Conflicting configuration file arguments.");
                    }
                    break;

                default:
                    exitAppFailure("Unrecognized argument: " + args[i]);
            }
        }
    }

    private void execArguments(String[] args) {
        if (args.length > 0) {
            for (int i = 0; i < args.length; ++i) {
                switch (args[i]) {
                    case "--config":
                    case "-c":
                        // Config file specified. Load it.
                        getConfigFile(args[i + 1]);
                        ++i;
                        break;
                    default:
                        exitAppFailure("Unrecognized argument: " + args[i]);
                }
            }
        } else {
            exitAppFailure("Please specify a config file");
        }
    }

    private void getConfigFile(String configFile) {
        try {
            System.err.println("Config file name: " + configFile);
            configs = new Configs(loadConfigs(configFile));
            System.err.println("Configuration file successfully loaded.");
        } catch (NullPointerException e) {
            exitAppFailure("Config file not found.");
        }
    }

    private ConfigsModel loadConfigs(String file) {
        System.err.println("Loading configuration file...");
        ObjectMapper mapper = new ObjectMapper(new YAMLFactory());
        ConfigsModel configs = null;

        try {
            configs = mapper.readValue(new File(file), ConfigsModel.class);
        } catch (IOException e) {
            exitAppFailure("Unable to load configuration file.");
        }
        return configs;
    }

    private void initLogging() {
        try {
            ServiceLogger.initLogger(configs.getOutputDir(), configs.getOutputFile());
        } catch (IOException e) {
            exitAppFailure("Unable to initialize logging.");
        }
    }

    private void initHTTPServer() {
        ServiceLogger.LOGGER.config("Initializing HTTP server...");
        String scheme = configs.getScheme();
        String hostName = configs.getHostName();
        int port = configs.getPort();
        String path = configs.getPath();

        try {
            ServiceLogger.LOGGER.config("Building URI from configs...");
            URI uri = UriBuilder.fromUri(scheme + hostName + path).port(port).build();
            ServiceLogger.LOGGER.config("Final URI: " + uri.toString());
            ResourceConfig rc = new ResourceConfig().packages("app.resources");
            ServiceLogger.LOGGER.config("Set Jersey resources.");
            rc.register(JacksonFeature.class);
            ServiceLogger.LOGGER.config("Set Jackson as serializer.");
            ServiceLogger.LOGGER.config("Starting HTTP server...");
            HttpServer server = GrizzlyHttpServerFactory.createHttpServer(uri, rc, false);
            server.start();
            ServiceLogger.LOGGER.config("HTTP server started.");
        } catch (IOException e) {
            e.printStackTrace();
            throw new RuntimeException();
        }
    }


    private void exitAppFailure(String message) {
        System.err.println("ERROR: " + message);
        System.err.println("Usage options: ");
        System.err.println("\tSpecify configuration file:");
        System.err.println("\t\t--config [file]");
        System.err.println("\t\t-c");
        System.err.println("\tUse default configuration:");
        System.err.println("\t\t--default");
        System.err.println("\t\t-d");
        System.exit(-1);
    }


    public static Configs getConfigs() {
        return configs;
    }
}