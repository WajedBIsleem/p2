package live.punkpanda.p2.apns;

import live.punkpanda.p2.vcard.VCardService;
import live.punkpanda.p2.offline.OfflineService;
import com.google.gson.annotations.SerializedName;
import live.punkpanda.p2.xmpp.extensions.push.MessageBody;

public class Notification { 
    public Aps aps;
    public Notification(String sender, String recevier, MessageBody body) {
        aps = new Aps(sender, recevier, body);
    }
}

class Aps {
    public Alert alert;
    public int badge;
    public String sound= "default";


     public Aps(String sender, String recevier, MessageBody body) {
        String senderName = "";
        if (!sender.equals("")) {
            VCardService vCardService = new VCardService();
            senderName = vCardService.vcard(sender);
        }
        OfflineService offlineService = new OfflineService();
        badge = offlineService.offline(recevier);

        alert = new Alert(senderName, body);
    }
}

class Alert {
    public String title;
    public String body;

    public Alert(String senderName, MessageBody messagebody) {
        title = senderName.equals("") ? "Group message" : senderName;
        if (messagebody != null) {
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
        } else {
            body = "";
        }
    }
}