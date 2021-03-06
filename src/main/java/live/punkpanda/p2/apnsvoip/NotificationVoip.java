package live.punkpanda.p2.apnsvoip;

import com.google.gson.annotations.SerializedName;
import java.util.ArrayList;
import live.punkpanda.p2.xmpp.extensions.push.MessageBody;

public class NotificationVoip {

  public Aps aps;
  public Data data;

  public NotificationVoip(MessageBody body) {
    aps = new Aps();
    data = new Data(body);
  }

  @Override
  public String toString() {
    return (
      data.sid +
      " - " +
      data.sender +
      " - " +
      data.media.size() +
      " - " +
      (data.media.size() > 0 ? data.media.get(0) : "") +
      " - " +
      (data.media.size() > 1 ? data.media.get(1) : "")
    );
  }
}

class Aps {

  public Alert alert = new Alert();
  public int badge = 0;
  public String sound = "default";

  @SerializedName("content-available")
  public int contentavailable = 1;
}

class Alert {

  public String title = "Incomming call";
  public String subtitle;
  public String body;
}

class Data {

  public String sid;
  public String sender;
  public ArrayList<String> media;

  public Data(MessageBody body) {
    this.sid = body.sid;
    this.sender = body.sender;
    this.media = body.media;
  }
}
