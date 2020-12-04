package live.punkpanda.p2.vcard;

import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Query;

public interface VCardHttpInterface {

    @GET("/xmpp/vcard")
    Call<VCardResult> vcard(@Query("jid") String jid);

}
