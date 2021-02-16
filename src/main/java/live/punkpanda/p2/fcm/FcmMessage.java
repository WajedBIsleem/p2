package live.punkpanda.p2.fcm;

import com.google.gson.annotations.SerializedName;
import live.punkpanda.p2.offline.OfflineService;
import live.punkpanda.p2.vcard.VCardService;
import live.punkpanda.p2.xmpp.extensions.push.MessageBody;

public class FcmMessage {

  public final String to;
  public final Data data;

  public FcmMessage(
    String to,
    String sender,
    String recevier,
    MessageBody body
  ) {
    this.to = to;

    String senderName = "";
    if (!sender.equals("")) {
      VCardService vCardService = new VCardService();
      senderName = vCardService.vcard(sender);
    }

    OfflineService offlineService = new OfflineService();
    int offlineCount = offlineService.offline(recevier);

    this.data = new Data(sender, senderName, body, offlineCount);
  }
}

class Data {

  String title;
  String body;
  String sender;
  String priority = "high";

  @SerializedName("content-available")
  int contentavailable = 1;

  public Data(
    String sender,
    String senderName,
    MessageBody messagebody,
    int offlineCount
  ) {
    this.sender = sender;
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