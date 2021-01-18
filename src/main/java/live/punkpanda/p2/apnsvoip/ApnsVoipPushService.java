package live.punkpanda.p2.apnsvoip;

import com.google.gson.FieldNamingPolicy;
import com.google.gson.GsonBuilder;
import live.punkpanda.p2.Configuration;
import live.punkpanda.p2.PushService;
import live.punkpanda.p2.pojo.Target;
import live.punkpanda.p2.util.TrustManager;
import live.punkpanda.p2.xmpp.extensions.push.MessageBody;
import okhttp3.OkHttpClient;
import okhttp3.ResponseBody;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

import javax.net.ssl.KeyManager;
import javax.net.ssl.SSLContext;
import javax.net.ssl.X509TrustManager;
import java.security.KeyManagementException;
import java.security.NoSuchAlgorithmException;

public class ApnsVoipPushService implements PushService {

    private static final java.util.logging.Logger LOGGER = LoggerFactory.getLogger(ApnsVoipPushService.class);

    private static final String BASE_URL = "https://api.push.apple.com";

    private static final String SANDBOX_BASE_URL = "https://api.sandbox.push.apple.com";

    private final ApnsVoipHttpInterface httpInterface;


    public ApnsVoipPushService() {
        final GsonBuilder gsonBuilder = new GsonBuilder();
        gsonBuilder.setFieldNamingPolicy(FieldNamingPolicy.LOWER_CASE_WITH_DASHES);

        final SSLContext sslContext;
        try {
            sslContext = SSLContext.getInstance("TLSv1.2");

            sslContext.init(new KeyManager[]{ new ClientCertificateKeyManagerVoip() }, null, null);
        } catch (NoSuchAlgorithmException | KeyManagementException e) {
            throw new AssertionError(e);
        }

        final X509TrustManager trustManager = TrustManager.getDefault();
        if (trustManager == null) {
            throw new AssertionError("Unable to find default trust manager");
        }
        final OkHttpClient.Builder okHttpBuilder = new OkHttpClient.Builder();
        okHttpBuilder.sslSocketFactory(sslContext.getSocketFactory(), trustManager);

        ApnsVoipConfiguration configuration = Configuration.getInstance().getApnsVoipConfiguration();

        final Retrofit.Builder retrofitBuilder = new Retrofit.Builder();
        if (configuration != null && configuration.isSandbox()) {
            retrofitBuilder.baseUrl(SANDBOX_BASE_URL);
        } else {
            retrofitBuilder.baseUrl(BASE_URL);
        }
        retrofitBuilder.addConverterFactory(GsonConverterFactory.create(gsonBuilder.create()));
        retrofitBuilder.client(okHttpBuilder.build());

        final Retrofit retrofit = retrofitBuilder.build();

        this.httpInterface = retrofit.create(ApnsVoipHttpInterface.class);
    }

    @Override
    public boolean push(final Target target, final String sender, final MessageBody body) {
        LOGGER.info("attempt push to APNS (" + target.getToken2() + ")");
        final ApnsVoipConfiguration configuration = Configuration.getInstance().getApnsVoipConfiguration();
        final String bundleId = configuration.getBundleId();

        try {
            final NotificationVoip notificationvoip = new NotificationVoip();
            final Response<Void> response = this.httpInterface.send(target.getToken2(), bundleId, notificationvoip).execute();
            if (response.isSuccessful()) {
                LOGGER.info("push to APNS was successful");
                return true;
            } else {
                final ResponseBody errorBody = response.errorBody();
                final String errorBodyString = errorBody == null ? null : errorBody.string();
                LOGGER.warn("push to APNS failed with response code=" + response.code() + ", body=" + errorBodyString);
            }
        } catch (Exception e) {
            LOGGER.warn("push to APNS failed", e);
            return false;
        }

        return false;
    }

    public static class ApnsVoipConfiguration {
        private String privateKey;
        private String certificate;
        private String bundleId;
        private boolean sandbox = false;

        public String getPrivateKey() {
            return privateKey;
        }

        public String getCertificate() {
            return certificate;
        }

        public String getBundleId() {
            return bundleId;
        }

        public boolean isSandbox() {
            return sandbox;
        }
    }
}
