package live.punkpanda.p2.offline;

import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Query;

public interface OfflineHttpInterface {
    @GET("/xmpp/offline")
    Call<OfflineResult> offline(@Query("username") String username);
}
