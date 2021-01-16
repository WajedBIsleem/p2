package live.punkpanda.p2;


import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonSyntaxException;
import live.punkpanda.p2.apns.ApnsPushService;
import live.punkpanda.p2.apnsvoip.ApnsVoipPushService;
import live.punkpanda.p2.fcm.FcmPushService;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;

public class Configuration {

    private static File FILE = new File("config.json");
    private static Configuration INSTANCE;

    private String host = "localhost";
    private int port = 5348;
    private String jid;
    private boolean debug = false;
    private String sharedSecret;
    private FcmPushService.FcmConfiguration fcm;
    private ApnsPushService.ApnsConfiguration apns;
    private ApnsVoipPushService.ApnsVoipConfiguration apnsvoip;

    private String dbUrl;
    private String dbUsername;
    private String dbPassword;

    private String apiBaseURL;    

    public String getDbUrl() {
        return dbUrl;
    }

    public String getDbUsername() {
        return dbUsername;
    }

    public String getDbPassword() {
        return dbPassword;
    }

    public String getApiBaseURL() {
        return apiBaseURL;
    }

    private Configuration() {

    }

    public synchronized static void setFilename(String filename) throws FileNotFoundException {
        if (INSTANCE != null) {
            throw new IllegalStateException("Unable to set filename after instance has been created");
        }
        Configuration.FILE = new File(filename);
        if (!Configuration.FILE.exists()) {
            throw new FileNotFoundException();
        }
    }

    public synchronized static Configuration getInstance() {
        if (INSTANCE == null) {
            INSTANCE = load();
        }
        return INSTANCE;
    }

    private static Configuration load() {
        final GsonBuilder gsonBuilder = new GsonBuilder();
        final Gson gson = gsonBuilder.create();
        try {
            System.out.println("Reading configuration from " + FILE.getAbsolutePath());
            return gson.fromJson(new FileReader(FILE), Configuration.class);
        } catch (FileNotFoundException e) {
            throw new RuntimeException("Configuration file not found");
        } catch (JsonSyntaxException e) {
            throw new RuntimeException("Invalid syntax in config file");
        }
    }

    public boolean isDebug() {
        return debug;
    }

    public FcmPushService.FcmConfiguration getFcmConfiguration() {
        return this.fcm;
    }

    public ApnsPushService.ApnsConfiguration getApnsConfiguration() {
        return this.apns;
    }

    public ApnsVoipPushService.ApnsVoipConfiguration getApnsVoipConfiguration() {
        return this.apnsvoip;
    }

    public String getName() {
        return jid;
    }

    public String getHost() {
        return host;
    }

    public int getPort() {
        return port;
    }

    public String getSharedSecret() {
        return sharedSecret;
    }


    public String getJid() {
        return jid;
    }
}
