package eu.siacs.p2.pushy;

import eu.siacs.p2.xmpp.extensions.push.MessageBody;

public class PushyMessage {
    public final String to;
    public final Data data;
    public final Notification notification;

    public PushyMessage(String to, String sender, MessageBody body) {
        this.to = to;
        this.data = new Data(body);
        this.notification = new Notification(body);
    }
}

class Data {
    String title = "New message";

    public Data(MessageBody body) {

    }
}

class Notification {
    String title = "New message";
    String body = "New message";
    String sound = "default";

    public Notification(MessageBody messagebody) {
        if (messagebody.type.equals("text")) {
            body = messagebody.content;
        }else if (messagebody.type.equals("image")) {
            body = "Receive image";
        }else if (messagebody.type.equals("voice")) {
            body = "Receive voice";
        }else if (messagebody.type.equals("video")) {
            body = "Receive video";
        }else if (messagebody.type.equals("file")) {
            body = "Receive file";
        }
    }
}
