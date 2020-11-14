package eu.siacs.p2.vcard;

import com.google.gson.FieldNamingPolicy;
import com.google.gson.GsonBuilder;
import okhttp3.ResponseBody;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class VCardService {

    private static final Logger LOGGER = LoggerFactory.getLogger(VCardService.class);

    private static final String BASE_URL = "https://app.punkpanda.live";

    private final VCardHttpInterface httpInterface;

    public VCardService() {
        final GsonBuilder gsonBuilder = new GsonBuilder();
        gsonBuilder.setFieldNamingPolicy(FieldNamingPolicy.LOWER_CASE_WITH_UNDERSCORES);

        final Retrofit.Builder retrofitBuilder = new Retrofit.Builder();
        retrofitBuilder.baseUrl(BASE_URL);
        retrofitBuilder.addConverterFactory(GsonConverterFactory.create(gsonBuilder.create()));

        final Retrofit retrofit = retrofitBuilder.build();

        this.httpInterface = retrofit.create(VCardHttpInterface.class);
    }

    public String vcard(String account) {
        try {
            final Response<VCardResult> response = this.httpInterface.vcard(account).execute();
            if (response.isSuccessful()) {
                final VCardResult result = response.body();
                return result.given + " " + result.family;
            } else {
                final ResponseBody errorBody = response.errorBody();
                final String errorBodyString = errorBody == null ? null : errorBody.string();
                LOGGER.warn("vcard failed with response code=" + response.code() + ", body=" + errorBodyString);
                return account;
            }
        } catch (Exception e) {
            e.printStackTrace();
            return account;
        }
    }
}
