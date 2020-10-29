package eu.siacs.p2.pushy;


public class PushyMessage {
    public final String to;
    public final Data data;
    public final Notification notification;

    public PushyMessage(String to) {
        this.to = to;
        this.data = new Data();
        this.notification = new Notification();
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
