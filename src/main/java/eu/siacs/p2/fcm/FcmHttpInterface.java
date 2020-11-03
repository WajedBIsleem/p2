package eu.siacs.p2.fcm;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.Header;
import retrofit2.http.POST;

public interface FcmHttpInterface {

    @POST("/fcm/send")
    Call<FcmResult> send(@Body FcmMessage message, @Header("Authorization") String authorization);

}
