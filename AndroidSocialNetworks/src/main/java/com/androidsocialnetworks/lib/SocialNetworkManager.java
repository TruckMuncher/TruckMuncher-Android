package com.androidsocialnetworks.lib;

import android.app.Activity;
import android.app.Fragment;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;

import com.androidsocialnetworks.lib.impl.FacebookSocialNetwork;
import com.androidsocialnetworks.lib.impl.GooglePlusSocialNetwork;
import com.androidsocialnetworks.lib.impl.TwitterSocialNetwork;
import com.facebook.internal.Utility;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class SocialNetworkManager extends Fragment {

    private static final String TAG = SocialNetworkManager.class.getSimpleName();
    private static final String PARAM_TWITTER_KEY = "SocialNetworkManager.PARAM_TWITTER_KEY";
    private static final String PARAM_TWITTER_SECRET = "SocialNetworkManager.PARAM_TWITTER_SECRET";
    private static final String PARAM_FACEBOOK = "SocialNetworkManager.PARAM_FACEBOOK";
    private static final String PARAM_GOOGLE_PLUS = "SocialNetworkManager.PARAM_GOOGLE_PLUS";
    private static final String SOCIAL_NETWORK_TAG = "SocialNetworkManager.SOCIAL_NETWORK_TAG";

    private Map<Integer, SocialNetwork> mSocialNetworksMap = new HashMap<Integer, SocialNetwork>();
    private OnInitializationCompleteListener mOnInitializationCompleteListener;

    public static SocialNetworkManager getInstance(Activity context) {
        SocialNetworkManager instance = (SocialNetworkManager) context.getFragmentManager()
                .findFragmentByTag(SOCIAL_NETWORK_TAG);

        if (instance == null) {
            instance = SocialNetworkManager.Builder.from(context)
                    .twitter(BuildConfig.TWITTER_API_KEY, BuildConfig.TWITTER_API_SECRET)
                    .facebook()
                    .build();
            context.getFragmentManager().beginTransaction().add(instance, SOCIAL_NETWORK_TAG).commit();
        }

        return instance;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Log.d(TAG, "SocialNetworkManager.onCreate");

        setRetainInstance(true);

        Bundle args = getArguments();

        final String paramTwitterKey = args.getString(PARAM_TWITTER_KEY);
        final String paramTwitterSecret = args.getString(PARAM_TWITTER_SECRET);

        final boolean paramFacebook = args.getBoolean(PARAM_FACEBOOK, false);
        final boolean paramGooglePlus = args.getBoolean(PARAM_GOOGLE_PLUS, false);

        if (!TextUtils.isEmpty(paramTwitterKey) || !TextUtils.isEmpty(paramTwitterKey)) {
            mSocialNetworksMap.put(TwitterSocialNetwork.ID,
                    new TwitterSocialNetwork(this, paramTwitterKey, paramTwitterSecret));
        }

        if (paramFacebook) {
            mSocialNetworksMap.put(FacebookSocialNetwork.ID, new FacebookSocialNetwork(this));
        }

        if (paramGooglePlus) {
            mSocialNetworksMap.put(GooglePlusSocialNetwork.ID, new GooglePlusSocialNetwork(this));
        }

        for (SocialNetwork socialNetwork : mSocialNetworksMap.values()) {
            socialNetwork.onCreate(savedInstanceState);
        }
    }

    @Override
    public void onStart() {
        super.onStart();
        Log.d(TAG, "SocialNetworkManager.onStart");

        for (SocialNetwork socialNetwork : mSocialNetworksMap.values()) {
            socialNetwork.onStart();
        }
    }

    @Override
    public void onResume() {
        super.onResume();
        Log.d(TAG, "SocialNetworkManager.onResume");

        for (SocialNetwork socialNetwork : mSocialNetworksMap.values()) {
            socialNetwork.onResume();
        }

        if (mOnInitializationCompleteListener != null) {
            Log.d(TAG, "SocialNetworkManager.onResume: mOnInitializationCompleteListener != null");
            mOnInitializationCompleteListener.onSocialNetworkManagerInitialized();
        }
    }

    @Override
    public void onPause() {
        super.onPause();
        Log.d(TAG, "SocialNetworkManager.onPause");

        for (SocialNetwork socialNetwork : mSocialNetworksMap.values()) {
            socialNetwork.onPause();
        }
    }

    @Override
    public void onStop() {
        super.onStop();
        Log.d(TAG, "SocialNetworkManager.onStop");

        for (SocialNetwork socialNetwork : mSocialNetworksMap.values()) {
            socialNetwork.onStop();
        }
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        Log.d(TAG, "SocialNetworkManager.onDestroy");

        for (SocialNetwork socialNetwork : mSocialNetworksMap.values()) {
            socialNetwork.onDestroy();
        }
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        Log.d(TAG, "SocialNetworkManager.onSaveInstanceState");

        for (SocialNetwork socialNetwork : mSocialNetworksMap.values()) {
            socialNetwork.onSaveInstanceState(outState);
        }
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        Log.d(TAG, "SocialNetworkManager.onActivityResult: " + requestCode + " : " + resultCode);

        for (SocialNetwork socialNetwork : mSocialNetworksMap.values()) {
            socialNetwork.onActivityResult(requestCode, resultCode, data);
        }
    }

    public TwitterSocialNetwork getTwitterSocialNetwork() throws SocialNetworkException {
        if (!mSocialNetworksMap.containsKey(TwitterSocialNetwork.ID)) {
            throw new SocialNetworkException("Twitter wasn't initialized...");
        }

        return (TwitterSocialNetwork) mSocialNetworksMap.get(TwitterSocialNetwork.ID);
    }

    public FacebookSocialNetwork getFacebookSocialNetwork() throws SocialNetworkException {
        if (!mSocialNetworksMap.containsKey(FacebookSocialNetwork.ID)) {
            throw new IllegalStateException("Facebook wasn't initialized...");
        }

        return (FacebookSocialNetwork) mSocialNetworksMap.get(FacebookSocialNetwork.ID);
    }

    public GooglePlusSocialNetwork getGooglePlusSocialNetwork() {
        if (!mSocialNetworksMap.containsKey(GooglePlusSocialNetwork.ID)) {
            throw new IllegalStateException("Facebook wasn't initialized...");
        }

        return (GooglePlusSocialNetwork) mSocialNetworksMap.get(GooglePlusSocialNetwork.ID);
    }

    public SocialNetwork getSocialNetwork(int id) throws SocialNetworkException {
        if (!mSocialNetworksMap.containsKey(id)) {
            throw new SocialNetworkException("Social network with id = " + id + " not found");
        }

        return mSocialNetworksMap.get(id);
    }

    public void addSocialNetwork(SocialNetwork socialNetwork) {
        if (mSocialNetworksMap.get(socialNetwork.getID()) != null) {
            throw new SocialNetworkException("Social network with id = " + socialNetwork.getID() + " already exists");
        }

        mSocialNetworksMap.put(socialNetwork.getID(), socialNetwork);
    }

    public List<SocialNetwork> getInitializedSocialNetworks() {
        return Collections.unmodifiableList(new ArrayList<SocialNetwork>(mSocialNetworksMap.values()));
    }

    public void setOnInitializationCompleteListener(OnInitializationCompleteListener onInitializationCompleteListener) {
        mOnInitializationCompleteListener = onInitializationCompleteListener;
    }

    public boolean needsLogin() {
        boolean needsLogin = true;

        for (SocialNetwork socialNetwork : mSocialNetworksMap.values()) {
            if (socialNetwork.isConnected()) {
                needsLogin = false;
            }
        }

        return needsLogin;
    }

    public void logout() {
        for (SocialNetwork socialNetwork : mSocialNetworksMap.values()) {
            if (socialNetwork.isConnected()) {
                socialNetwork.logout();
            }
        }
    }

    public static interface OnInitializationCompleteListener {
        public void onSocialNetworkManagerInitialized();
    }

    private static class Builder {
        private String twitterConsumerKey, twitterConsumerSecret;
        private boolean facebook;
        private boolean googlePlus;

        private Context mContext;

        private Builder(Context context) {
            mContext = context;
        }

        private static Builder from(Context context) {
            return new Builder(context);
        }

        private Builder twitter(String consumerKey, String consumerSecret) {
            twitterConsumerKey = consumerKey;
            twitterConsumerSecret = consumerSecret;
            return this;
        }

        // https://developers.facebook.com/docs/android/getting-started/
        private Builder facebook() {
            String applicationID = Utility.getMetadataApplicationId(mContext);

            if (applicationID == null) {
                throw new IllegalStateException("applicationID can't be null\n" +
                        "Please check https://developers.facebook.com/docs/android/getting-started/");
            }

            facebook = true;

            return this;
        }

        private Builder googlePlus() {
            googlePlus = true;
            return this;
        }

        private SocialNetworkManager build() {
            Bundle args = new Bundle();

            if (!TextUtils.isEmpty(twitterConsumerKey) && !TextUtils.isEmpty(twitterConsumerSecret)) {
                args.putString(PARAM_TWITTER_KEY, twitterConsumerKey);
                args.putString(PARAM_TWITTER_SECRET, twitterConsumerSecret);
            }

            if (facebook) {
                args.putBoolean(PARAM_FACEBOOK, true);
            }

            if (googlePlus) {
                args.putBoolean(PARAM_GOOGLE_PLUS, true);
            }

            SocialNetworkManager socialNetworkManager = new SocialNetworkManager();
            socialNetworkManager.setArguments(args);
            return socialNetworkManager;
        }
    }
}
