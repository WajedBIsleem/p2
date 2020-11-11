package eu.siacs.p2.fcm;

import eu.siacs.p2.vcard.VCardService;
import eu.siacs.p2.offline.OfflineService;
import eu.siacs.p2.xmpp.extensions.push.MessageBody;
import com.google.gson.annotations.SerializedName;

public class FcmMessage {

    public final String to;
    public final Data data;
    public final Notification notification;

    public FcmMessage(String to, String sender, MessageBody body) {
        this.to = to;
        
        VCardService vCardService = new VCardService();
        String senderName = vCardService.vcard(sender);

        OfflineService offlineService = new OfflineService();
        int offlineCount = offlineService.offline(sender);

        this.data = new Data(sender);
        this.notification = new Notification(senderName, body, offlineCount);
    }
}


class Data {
    String title = "New message";
    String sender = "";
    @SerializedName("content-available")
    int contentavailable = 1;

    public Data(String sender) {
        this.sender = sender;
    }
}

class Notification {
    String title;
    String body;
    String sound = "default";
    int badge= 1;

    public Notification(String senderName, MessageBody messagebody, int offlineCount) {
        title = senderName + offlineCount;
        badge = 10;
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
