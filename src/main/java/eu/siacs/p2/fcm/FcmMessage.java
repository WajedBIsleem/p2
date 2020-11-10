package eu.siacs.p2.fcm;

import eu.siacs.p2.vcard.VCardService;
import eu.siacs.p2.xmpp.extensions.push.MessageBody;
import com.google.gson.annotations.SerializedName;

public class FcmMessage {

    public final String to;
    public final Data data;
    public final Notification notification;
    public final String wajed;

    public FcmMessage(String to, String sender, MessageBody body) {
        this.to = to;
        this.wajed = "wajed";
        VCardService vCardService = new VCardService();
        String senderName = vCardService.vcard(sender);

        this.data = new Data(sender, senderName, body);
        this.notification = new Notification(sender, senderName, body);
    }
}


class Data {
    String title = "New message";

    public Data(String sender, String senderName, MessageBody body) {

    }
}

class Notification {
    String title = "New message";
    String body = "New message";
    String sound = "default";
    int badge = 1;
    String senderId = "";
    @SerializedName("content-available")
    int contentavailable = 1;

    public Notification(String sender, String senderName, MessageBody messagebody) {
        senderId = sender;
        title = senderName;
        
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
