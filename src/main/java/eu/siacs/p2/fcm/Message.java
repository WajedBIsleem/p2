package eu.siacs.p2.fcm;

import com.google.gson.annotations.SerializedName;
import eu.siacs.p2.pojo.Target;

public class Message {

    public final Priority priority;
    public final Data data;
    public final Notification notification;
    public final String to;
    public final String collapseKey;

    private Message(Priority priority, Data data, Notification notification, String to, String collapseKey) {
        this.priority = priority;
        this.data = data;
        this.notification = notification;
        this.to = to;
        this.collapseKey = collapseKey;
    }

    public static Message createHighPriority(Target target, boolean collapse) {
        return createHighPriority(target.getDevice(), target.getChannel(), target.getToken(), collapse);
    }

    private static Message createHighPriority(String account, String channel, String token, boolean collapse) {
        final Data data = new Data();
        data.account = account;
        data.channel = channel == null || channel.isEmpty() ? null : channel;
        
        final Notification notification = new Notification();
                
        final String collapseKey;
        if (collapse) {
            if (data.channel == null) {
                collapseKey = account.substring(0, 6);
            } else {
                collapseKey = data.channel.substring(0,6);
            }
        } else {
            collapseKey = null;
        }
        return new Message(Priority.HIGH, data, notification, token, collapseKey);
    }

    public enum Priority {
        @SerializedName("high") HIGH,
        @SerializedName("normal") NORMAL
    }

    public static class Data {
        String title = "New message";
        String account;
        String channel;
    }
    
    public static class Notification {
        String title = "New message";
        String sound = "default";
    }
}
