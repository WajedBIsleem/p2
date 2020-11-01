package eu.siacs.p2.fcm;

import eu.siacs.p2.pojo.Target;

public class Message {

    public final String to;
    public final Data data;
    public final Notification notification;

    public Message(String to, Data data, Notification notification) {
        this.to = to;
        this.data = data;
        this.notification = notification;
    }

    public static Message create(Target target) {
        final Data data = new Data();
        final Notification notification = new Notification();
        return new Message(target.getToken(), data, notification);
    }

    public static class Data {
        String title = "New message";
    }

    public static class Notification {
        String title = "New message";
        String sound = "default";
    }
}
