package com.truckmuncher.truckmuncher.dagger;

import android.content.Context;

import com.squareup.okhttp.OkHttpClient;

import org.apache.http.conn.ssl.AllowAllHostnameVerifier;

import java.security.SecureRandom;
import java.security.cert.X509Certificate;

import javax.inject.Singleton;
import javax.net.ssl.SSLContext;
import javax.net.ssl.TrustManager;
import javax.net.ssl.X509TrustManager;

import dagger.Module;
import dagger.Provides;
import retrofit.RestAdapter;

@Module(overrides = true, library = true, complete = false)
public class DebugNetworkModule {

    private final Context appContext;

    public DebugNetworkModule(Context context) {
        appContext = context.getApplicationContext();
    }

    private static void configureSsl(OkHttpClient client) {
        try {
            TrustManager[] trustAllCerts = new TrustManager[]{
                    new X509TrustManager() {
                        public X509Certificate[] getAcceptedIssuers() {
                            X509Certificate[] myTrustedAnchors = new X509Certificate[0];
                            return myTrustedAnchors;
                        }

                        @Override
                        public void checkClientTrusted(X509Certificate[] certs, String authType) {
                        }

                        @Override
                        public void checkServerTrusted(X509Certificate[] certs, String authType) {
                        }
                    }
            };

            SSLContext sc = SSLContext.getInstance("TLS");
            sc.init(null, trustAllCerts, new SecureRandom());
            client.setSslSocketFactory(sc.getSocketFactory());
            client.setHostnameVerifier(new AllowAllHostnameVerifier());
        } catch (Exception e) {
        }
    }

    @Provides
    public OkHttpClient provideOkHttpClient() {
        OkHttpClient client = new OkHttpClient();
        NetworkModule.configureHttpCache(appContext, client);
        configureSsl(client);
        return client;
    }

    @Singleton
    @Provides
    public RestAdapter provideRestAdapter(RestAdapter.Builder builder) {
        return builder
                .setLogLevel(RestAdapter.LogLevel.FULL)
                .build();
    }
}
