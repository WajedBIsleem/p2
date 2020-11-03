package eu.siacs.p2.fcm;

import eu.siacs.p2.vcard.VCardService;
import eu.siacs.p2.xmpp.extensions.push.MessageBody;

public class FcmMessage {

    public final String to;
    public final Data data;
    public final Notification notification;

    public FcmMessage(String to, String sender, MessageBody body) {
        this.to = to;

        VCardService vCardService = new VCardService();
        String account = vCardService.vcard(sender);

        this.data = new Data(account, body);
        this.notification = new Notification(account, body);
    }
}


class Data {
    String title = "New message";

    public Data(String sender, MessageBody body) {

    }
}

class Notification {
    String title = "New message";
    String body = "New message";
    String sound = "default";

    public Notification(String sender, MessageBody messagebody) {
        title = sender;
        if (messagebody.type.equals("text")) {
            body = messagebody.content;
        } else if (messagebody.type.equals("image")) {
            body = "Receive image";
        } else if (messagebody.type.equals("voice")) {
            body = "Receive voice";
        } else if (messagebody.type.equals("video")) {
            body = "Receive video";
        } else if (messagebody.type.equals("file")) {
            body = "Receive file";
        }
    }
}
