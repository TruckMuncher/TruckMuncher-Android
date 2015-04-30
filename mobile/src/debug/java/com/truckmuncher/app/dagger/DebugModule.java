package com.truckmuncher.app.dagger;

import android.app.Application;
import android.content.Context;
import android.support.annotation.NonNull;

import com.facebook.stetho.Stetho;
import com.facebook.stetho.okhttp.StethoInterceptor;
import com.squareup.okhttp.OkHttpClient;
import com.truckmuncher.debug.RiseAndShine;

import org.apache.http.conn.ssl.AllowAllHostnameVerifier;

import java.security.SecureRandom;
import java.security.cert.X509Certificate;

import javax.net.ssl.SSLContext;
import javax.net.ssl.TrustManager;
import javax.net.ssl.X509TrustManager;

import dagger.Module;
import dagger.Provides;
import timber.log.Timber;

@Module(overrides = true, library = true)
public class DebugModule {

    private final Context context;

    public DebugModule(Application context) {
        this.context = context;
        Stetho.initialize(
                Stetho.newInitializerBuilder(context)
                        .enableDumpapp(Stetho.defaultDumperPluginsProvider(context))
                        .enableWebKitInspector(Stetho.defaultInspectorModulesProvider(context))
                        .build());

        context.registerActivityLifecycleCallbacks(new RiseAndShine());
    }

    private static void configureSsl(OkHttpClient client) {
        try {
            TrustManager[] trustAllCerts = new TrustManager[]{
                    new X509TrustManager() {
                        public X509Certificate[] getAcceptedIssuers() {
                            return new X509Certificate[0];
                        }

                        @Override
                        public void checkClientTrusted(@NonNull X509Certificate[] certs, @NonNull String authType) {
                        }

                        @Override
                        public void checkServerTrusted(@NonNull X509Certificate[] certs, @NonNull String authType) {
                        }
                    }
            };

            SSLContext sc = SSLContext.getInstance("TLS");
            sc.init(null, trustAllCerts, new SecureRandom());
            client.setSslSocketFactory(sc.getSocketFactory());
            client.setHostnameVerifier(new AllowAllHostnameVerifier());
        } catch (Exception e) {
            Timber.e(e, "Couldn't configure SSL");
        }
    }

    @Provides
    public OkHttpClient provideOkHttpClient() {
        OkHttpClient client = new OkHttpClient();
        client.networkInterceptors().add(new StethoInterceptor());
        GlobalModule.configureHttpCache(context, client);
        configureSsl(client);
        return client;
    }
}
