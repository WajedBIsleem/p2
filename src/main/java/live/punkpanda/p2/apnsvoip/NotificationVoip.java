package live.punkpanda.p2.apnsvoip;

import com.google.gson.annotations.SerializedName;

public class NotificationVoip {
    public Aps aps = new Aps();
}

class Aps {
    public Alert alert = new Alert();
    public int badge = 0;
    public String sound= "default";
    @SerializedName("content-available")
    public int contentavailable = 1;
}

class Alert {
    public String title = "Incomming call";
    public String subtitle;
    public String body;
}