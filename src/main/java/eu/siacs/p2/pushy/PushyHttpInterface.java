package eu.siacs.p2.pushy;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.POST;

public interface PushyHttpInterface {

    @POST("/push?api_key=61d546f4365dffa2a49d0bfa32f342a75d72b947da18eca7bca1ee27c628dced")
    Call<PushyResult> send(@Body PushyMessage message);

}
