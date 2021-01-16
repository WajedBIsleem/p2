package live.punkpanda.p2.apnsvoip;

import com.google.gson.annotations.SerializedName;

public class NotificationVoip {
    public Aps aps = new Aps();
}

class Aps {
    public Alert alert = new Alert();
    public int badge;
    public String sound= "default";
    @SerializedName("mutable-content")
    public int mutableContent = 1;
}

class Alert {
    public String title = "New message";
    public String subtitle;
    public String body;
}