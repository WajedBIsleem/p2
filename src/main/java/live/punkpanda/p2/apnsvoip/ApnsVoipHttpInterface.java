package live.punkpanda.p2.apnsvoip;

import retrofit2.Call;
import retrofit2.http.*;

public interface ApnsVoipHttpInterface {

    @Headers({
        "apns-push-type: voip",
        "apns-priority: 10"
    })
    @POST("/3/device/{token}")
    Call<Void> send(@Path("token") String token, @Body NotificationVoip notification);
}
