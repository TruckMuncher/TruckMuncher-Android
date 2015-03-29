package com.truckmuncher.app.gcm;

import android.app.IntentService;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.preference.PreferenceManager;

import com.google.android.gms.gcm.GoogleCloudMessaging;
import com.truckmuncher.api.gcm.GcmRegistrationRequest;
import com.truckmuncher.api.gcm.GcmRegistrationResponse;
import com.truckmuncher.api.gcm.GcmService;
import com.truckmuncher.app.App;
import com.truckmuncher.app.BuildConfig;

import java.io.IOException;

import javax.inject.Inject;

import timber.log.Timber;

public class GcmRegistrationService extends IntentService {

    @Inject
    GcmService gcmService;
    private GoogleCloudMessaging gcm;
    private GcmRegistrationIdPreference gcmIdPref;
    private AppVersionPreference appVersionPref;
    private InstallationIdPreference installationIdPref;

    public GcmRegistrationService() {
        super(GcmRegistrationService.class.getSimpleName());
    }

    public static Intent newIntent(Context context) {
        return new Intent(context, GcmRegistrationService.class);
    }

    /**
     * @return Application's version code from the {@code PackageManager}.
     */
    private static int getAppVersion(Context context) {
        try {
            PackageInfo packageInfo = context.getPackageManager().getPackageInfo(context.getPackageName(), 0);
            return packageInfo.versionCode;
        } catch (PackageManager.NameNotFoundException e) {
            throw new AssertionError(e);
        }
    }

    @Override
    public void onCreate() {
        super.onCreate();
        App.get(this).networkComponent().inject(this);
        gcm = GoogleCloudMessaging.getInstance(this);
        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(this);
        gcmIdPref = new GcmRegistrationIdPreference(preferences);
        appVersionPref = new AppVersionPreference(preferences);
        installationIdPref = new InstallationIdPreference(preferences);
    }

    @Override
    protected void onHandleIntent(Intent intent) {
        String regId = getRegistrationId();

        if (regId.isEmpty()) {
            register();
        }
    }

    /**
     * Gets the current registration ID for application on GCM service.
     * <p/>
     * If result is empty, the app needs to register.
     *
     * @return registration ID, or empty string if there is no existing
     * registration ID.
     */
    private String getRegistrationId() {
        if (!gcmIdPref.isSet()) {
            return "";
        }

        // Check if app was updated; if so, it must clear the registration ID
        // since the existing registration ID is not guaranteed to work with
        // the new app version.
        int registeredVersion = appVersionPref.get();
        int currentVersion = getAppVersion(this);
        if (registeredVersion != currentVersion) {
            Timber.i("App version changed.");
            return "";
        }
        return gcmIdPref.get();
    }

    /**
     * Registers the application with GCM servers synchronously.
     * <p/>
     * Stores the registration ID and app versionCode in the application's
     * shared preferences.
     */
    private void register() {
        try {
            String regId = gcm.register(BuildConfig.GCM_SENDER_ID);

            // You should send the registration ID to your server over HTTP,
            // so it can use GCM/HTTP or CCS to send messages to your app.
            // The request to your server should be authenticated if your app
            // is using accounts.
            if (sendRegistrationIdToBackend(regId)) {

                // Persist the registration ID - no need to register again.
                storeRegistrationId(regId);
            }
        } catch (IOException e) {
            // TODO
            // If there is an error, don't just keep trying to register.
            // Require the user to click a button again, or perform
            // exponential back-off.
            Timber.e(e, "Error while registering with GCM");
        }
    }

    /**
     * Sends the registration ID to your server over HTTP, so it can use GCM/HTTP
     * or CCS to send messages to your app. Not needed for this demo since the
     * device sends upstream messages to a server that echoes back the message
     * using the 'from' address in the message.
     */
    private boolean sendRegistrationIdToBackend(String gcmRegistrationId) {
        GcmRegistrationResponse response = gcmService.registerForGcm(new GcmRegistrationRequest.Builder()
                .gcmRegistrationId(gcmRegistrationId)
                .installationId(installationIdPref.get())
                .build());

        return response != null;
    }

    /**
     * Stores the registration ID and app versionCode in the application's {@code SharedPreferences}.
     *
     * @param regId registration ID
     */
    private void storeRegistrationId(String regId) {
        int appVersion = getAppVersion(this);
        appVersionPref.set(appVersion);
        gcmIdPref.set(regId);
    }
}
