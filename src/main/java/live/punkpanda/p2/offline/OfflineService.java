package live.punkpanda.p2.offline;

import com.google.gson.FieldNamingPolicy;
import com.google.gson.GsonBuilder;
import okhttp3.ResponseBody;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import live.punkpanda.p2.Configuration;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class OfflineService {

    private static final Logger LOGGER = LoggerFactory.getLogger(OfflineService.class);

    private final OfflineHttpInterface httpInterface;

    public OfflineService() {
        final GsonBuilder gsonBuilder = new GsonBuilder();
        gsonBuilder.setFieldNamingPolicy(FieldNamingPolicy.LOWER_CASE_WITH_UNDERSCORES);

        final Retrofit.Builder retrofitBuilder = new Retrofit.Builder();

        final String BASE_URL = Configuration.getInstance().getApiBaseURL();

        retrofitBuilder.baseUrl(BASE_URL);
        retrofitBuilder.addConverterFactory(GsonConverterFactory.create(gsonBuilder.create()));

        final Retrofit retrofit = retrofitBuilder.build();

        this.httpInterface = retrofit.create(OfflineHttpInterface.class);
    }

    public int offline(String account) {
        try {
            final Response<OfflineResult> response = this.httpInterface.offline(account).execute();
            if (response.isSuccessful()) {
                final OfflineResult result = response.body();
                return result.value;
            } else {
                final ResponseBody errorBody = response.errorBody();
                final String errorBodyString = errorBody == null ? null : errorBody.string();
                LOGGER.warn("offline failed with response code=" + response.code() + ", body=" + errorBodyString);
                return 1;
            }
        } catch (Exception e) {
            e.printStackTrace();
            return 1;
        }
    }
}
