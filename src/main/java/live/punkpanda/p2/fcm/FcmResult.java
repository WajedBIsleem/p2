package live.punkpanda.p2.fcm;

import com.google.gson.annotations.SerializedName;

public class FcmResult {

    private int success;
    private int failure;
    @SerializedName("multicast_id")
    public long multicastId;


    public int getSuccess() {
        return success;
    }

    public int getFailure() {
        return failure;
    }
}
