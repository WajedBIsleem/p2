package live.punkpanda.p2.fcm;

import com.google.gson.Gson;
import com.google.gson.FieldNamingPolicy;
import com.google.gson.GsonBuilder;
import live.punkpanda.p2.Configuration;
import live.punkpanda.p2.PushService;
import live.punkpanda.p2.pojo.Target;
import live.punkpanda.p2.xmpp.extensions.push.MessageBody;
import okhttp3.ResponseBody;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class FcmPushService implements PushService {

    private static final Logger LOGGER = LoggerFactory.getLogger(FcmPushService.class);

    private static final String BASE_URL = "https://fcm.googleapis.com";

    private final FcmHttpInterface httpInterface;

    public FcmPushService() {
        final GsonBuilder gsonBuilder = new GsonBuilder();
        gsonBuilder.setFieldNamingPolicy(FieldNamingPolicy.LOWER_CASE_WITH_UNDERSCORES);

        final Retrofit.Builder retrofitBuilder = new Retrofit.Builder();
        retrofitBuilder.baseUrl(BASE_URL);
        retrofitBuilder.addConverterFactory(GsonConverterFactory.create(gsonBuilder.create()));

        final Retrofit retrofit = retrofitBuilder.build();

        this.httpInterface = retrofit.create(FcmHttpInterface.class);
    }

    @Override
    public boolean push(Target target, String sender, MessageBody body) {
        final FcmMessage message = new FcmMessage(target.getToken(), sender, target.getAccount(), body);

        if (body == null || !body.type.equals("update")) {
            return push(message);
        } else
            return true;
    }

    private boolean push(FcmMessage message) {
        final FcmConfiguration config = Configuration.getInstance().getFcmConfiguration();
        final String authKey = config == null ? null : config.getAuthKey();
        if (authKey == null) {
            LOGGER.warn("No fcm auth key configured");
            return false;
        }
        try {
            final Response<FcmResult> response = this.httpInterface.send(message, "key=" + authKey).execute();
            if (response.isSuccessful()) {
                final FcmResult result = response.body();
                return result != null && result.getSuccess() > 0;
            } else {
                final ResponseBody errorBody = response.errorBody();
                final String errorBodyString = errorBody == null ? null : errorBody.string();
                LOGGER.warn("push to FCM failed with response code=" + response.code() + ", body=" + errorBodyString);
                return false;
            }
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }

    public static class FcmConfiguration {
        private String authKey;

        public String getAuthKey() {
            return authKey;
        }
    }
}
