package org.lucerna.customServerTeleport;

import net.kyori.adventure.text.Component;

import net.kyori.adventure.text.logger.slf4j.ComponentLogger;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;


public class VersionChecker {
    private final ComponentLogger logger;
    private final MessageManager msg;
    private static final String GITHUB_API_URL = BuildConstants.REPO_API + "/repos/" + BuildConstants.REPO_NAME + "/releases/latest";

    public boolean is_old;
    public String latestVersion;

    public VersionChecker(ComponentLogger logger, MessageManager msg) {
        this.logger = logger;
        this.msg = msg;
        // True if this is an old version, false if it's the newest or if it couldn't check
        this.is_old = checkVersion();
    }

    private boolean checkVersion() {
        this.latestVersion = getLatestVersion();
        String currentVersion = BuildConstants.VERSION;

        if (latestVersion != null) {
            if (!latestVersion.equals(currentVersion)) {
                logger.warn(
                        msg.deserialize(msg.format(
                                "A new version ({}) is available! Please update your plugin at {}/{}/releases", latestVersion, BuildConstants.REPO_URL, BuildConstants.REPO_NAME)));

                return true;
            } else {
                logger.info(Component.text("You are using the latest version"));
                return false;
            }
        } else {
            logger.error(Component.text("Could not check for the latest version."));
            return false;
        }
    }

    private String getLatestVersion() {
        try {
            URL url = new URL(GITHUB_API_URL);
            HttpURLConnection conn = (HttpURLConnection) url.openConnection();
            conn.setRequestMethod("GET");
            conn.setRequestProperty("Accept", "application/vnd.github.v3+json");
            int responseCode = conn.getResponseCode();

            if (responseCode == HttpURLConnection.HTTP_OK) {
                BufferedReader in = new BufferedReader(new InputStreamReader(conn.getInputStream()));
                String inputLine;
                StringBuilder response = new StringBuilder();

                while ((inputLine = in.readLine()) != null) {
                    response.append(inputLine);
                }
                in.close();

                // Parse the JSON response to extract the tag name
                return response.toString().split("\"tag_name\":\"")[1].split("\"")[0];
            }
        } catch (Exception e) {
            logger.error(Component.text("Error while checking the latest version: " + e.getMessage()));
        }
        return null;
    }
}
