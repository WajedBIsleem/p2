package live.punkpanda.p2.pushy;

import com.google.gson.FieldNamingPolicy;
import com.google.gson.GsonBuilder;
import live.punkpanda.p2.PushService;
import live.punkpanda.p2.persistance.TargetStore;
import live.punkpanda.p2.pojo.Target;
import live.punkpanda.p2.xmpp.extensions.push.MessageBody;
import okhttp3.ResponseBody;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class PushyPushService implements PushService {

    private static final Logger LOGGER = LoggerFactory.getLogger(PushyPushService.class);

    private static final String BASE_URL = "https://api.pushy.me";

    private final PushyHttpInterface httpInterface;

    public PushyPushService() {
        final GsonBuilder gsonBuilder = new GsonBuilder();
        gsonBuilder.setFieldNamingPolicy(FieldNamingPolicy.LOWER_CASE_WITH_UNDERSCORES);

        final Retrofit.Builder retrofitBuilder = new Retrofit.Builder();
        retrofitBuilder.baseUrl(BASE_URL);
        retrofitBuilder.addConverterFactory(GsonConverterFactory.create(gsonBuilder.create()));

        final Retrofit retrofit = retrofitBuilder.build();

        this.httpInterface = retrofit.create(PushyHttpInterface.class);
    }

    @Override
    public boolean push(Target target, String sender, MessageBody body) {
        final PushyMessage message = new PushyMessage(target.getToken(), sender, target.getAccount(), body);
        if (body == null || !body.type.equals("update")) {
            return push(message);
        } else
            return true;
    }

    private boolean push(PushyMessage message) {
        try {
            final Response<PushyResult> response = this.httpInterface.send(message).execute();
            if (response.isSuccessful()) {
                final PushyResult result = response.body();
                return result != null && result.success;
            } else {
                final ResponseBody errorBody = response.errorBody();
                final String errorBodyString = errorBody == null ? null : errorBody.string();
                LOGGER.warn("push to Pushy failed with response code=" + response.code() + ", body=" + errorBodyString);
                return false;
            }
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }
}
