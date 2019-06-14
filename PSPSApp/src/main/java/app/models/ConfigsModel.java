package app.models;

import java.util.Map;

public class ConfigsModel {
    private Map<String, String> serviceConfig;
    private Map<String, String> loggerConfig;
    private Map<String, String> kafkaConfig;

    public ConfigsModel() { }

    public Map<String, String> getServiceConfig() {
        return serviceConfig;
    }

    public void setServiceConfig(Map<String, String> service) {
        this.serviceConfig = service;
    }

    public Map<String, String> getLoggerConfig() {
        return loggerConfig;
    }

    public void setLoggerConfig(Map<String, String> loggerConfig) {
        this.loggerConfig = loggerConfig;
    }

    public Map<String, String> getKafkaConfig() {
        return kafkaConfig;
    }

    public void setKafkaConfig(Map<String, String> kafkaConfig) {
        this.kafkaConfig = kafkaConfig;
    }
}