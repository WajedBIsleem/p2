package live.punkpanda.p2.apns;

import retrofit2.Call;
import retrofit2.http.*;

public interface ApnsHttpInterface {

    @Headers({
            "apns-push-type: alert",
            "apns-priority: 10"
    })
    @POST("/3/device/{token}")
    Call<Void> send(@Path("token") String token, @Header("apns-topic") String topic, @Body Notification notification);
}
