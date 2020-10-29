package eu.siacs.p2.pushy;

import com.google.gson.annotations.SerializedName;
import eu.siacs.p2.pojo.Target;

public class PushyMessage {
    public final String to;
    public final Data data;
    public final Notification notification;

    private PushyMessage(String to, Data data, Notification notificatio) {
        this.to = to;
        this.data = data;
        this.notification = notification;
    }
    
    private static PushyMessage create(String token) {
        final Data data = new Data();
        final Notification notification = new Notification();
        return new PushyMessage(token, data, notification);
    }

    public static class Data {
        String title = "New message";
    }
    
    public static class Notification {
        String title = "New message";
        String body = "New message";
        String sound = "default";
    }
}
