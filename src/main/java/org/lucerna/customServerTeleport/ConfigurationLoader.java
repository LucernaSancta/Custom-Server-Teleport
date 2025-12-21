package org.lucerna.customServerTeleport;

import org.slf4j.Logger;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.FileInputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Map;

import org.yaml.snakeyaml.Yaml;


public class ConfigurationLoader {
    private static ConfigurationLoader instance;
    private final Logger logger;
    private final Path dataDirectory;
    private Map<String, Object> configuration;

    public ConfigurationLoader(Logger logger, Path dataDirectory) {
        // Ensure this is the only instance
        assert instance == null;
        instance = this;

        this.logger = logger;
        this.dataDirectory = dataDirectory;
    }

    public Map<String, Object> getConfiguration() {
        if (configuration == null) {
            this.loadConfiguration();
        }
        return configuration;
    }

    public boolean reload() {
        return loadConfiguration();
    }

    private boolean loadConfiguration() {
        // Create the dataDirectory if it does not exist
        if (!Files.exists(dataDirectory)) {
            try {
                Files.createDirectories(dataDirectory);
            } catch (IOException e) {
                logger.error("Error creating data directory", e);
                return false;
            }
        }

        // Load the config.yml file from the dataDirectory
        File configurationFile = dataDirectory.resolve("config.yml").toFile();

        // Create the config.yml file if it does not exist
        if (!configurationFile.exists()) {
            logger.warn("Configuration file not found, creating it: {}", configurationFile.getAbsolutePath());
            try (InputStream in = getClass().getResourceAsStream("/config.yml")) {
                if (in != null) {
                    Files.copy(in, configurationFile.toPath());
                } else {
                    // Fucking hell, resource not found
                    throw new FileNotFoundException("config.yml not found in resources");
                }
            } catch (IOException e) {
                logger.error("Error creating default configuration", e);
                return false;
            }
        }

        // Load the config.yml file
        try {
            Yaml yaml = new Yaml();
            try (InputStream is = new FileInputStream(configurationFile)) {
                this.configuration = yaml.load(is);
            }
            return true;
        } catch (IOException e) {
            logger.error("Error loading configuration", e);
            return false;
        }
    }

}