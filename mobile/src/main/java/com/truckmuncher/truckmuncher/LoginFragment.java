package com.truckmuncher.truckmuncher;

import android.app.Fragment;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.TextView;

import com.androidsocialnetworks.lib.SocialNetworkManager;
import com.androidsocialnetworks.lib.listener.OnLoginCompleteListener;

import butterknife.ButterKnife;
import butterknife.InjectView;
import timber.log.Timber;

public class LoginFragment extends Fragment
        implements SocialNetworkManager.OnInitializationCompleteListener, View.OnClickListener {

    public static final String SOCIAL_NETWORK_TAG = "MainActivity.SOCIAL_NETWORK_TAG";
    @InjectView(R.id.twitter_button)
    ImageButton twitterButton;
    @InjectView(R.id.facebook_button)
    ImageButton facebookButton;
    @InjectView(R.id.twitter_status)
    TextView twitterStatusView;
    @InjectView(R.id.facebook_status)
    TextView facebookStatusView;
    private SocialNetworkManager socialNetworkManager;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        socialNetworkManager = (SocialNetworkManager) getFragmentManager().findFragmentByTag(SOCIAL_NETWORK_TAG);

        if (socialNetworkManager == null) {
            socialNetworkManager = SocialNetworkManager.Builder.from(getActivity())
                    .twitter(BuildConfig.TWITTER_API_KEY, BuildConfig.TWITTER_API_SECRET)
                    .facebook()
                    .build();
            socialNetworkManager.setOnInitializationCompleteListener(this);
            getFragmentManager().beginTransaction().add(socialNetworkManager, SOCIAL_NETWORK_TAG).commit();
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_login, container, false);
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        ButterKnife.inject(this, view);

        twitterButton.setOnClickListener(this);
        facebookButton.setOnClickListener(this);
    }

    @Override
    public void onClick(View view) {
        if (view == twitterButton) {
            if (isLoggedInToTwitter()) {
                logOutOfTwitter();
            } else {
                loginWithTwitter();
            }
        } else if (view == facebookButton) {
            if (isLoggedInToFacebook()) {
                logOutOfFacebook();
            } else {
                loginWithFacebook();
            }
        }
    }

    @Override
    public void onSocialNetworkManagerInitialized() {
        updateTwitterStatus();
        updateFacebookStatus();
    }

    private void loginWithTwitter() {
        socialNetworkManager.getTwitterSocialNetwork().requestLogin(new OnLoginCompleteListener() {
            @Override
            public void onLoginSuccess(int i) {
                updateTwitterStatus();
            }

            @Override
            public void onError(int i, String s, String s2, Object o) {
                Timber.d("Login Fragment", "Login Failed");
            }
        });
    }

    private void loginWithFacebook() {
        socialNetworkManager.getFacebookSocialNetwork().requestLogin(new OnLoginCompleteListener() {
            @Override
            public void onLoginSuccess(int socialNetworkID) {
                updateFacebookStatus();
            }

            @Override
            public void onError(int socialNetworkID, String requestID, String errorMessage, Object data) {
                Timber.d("Login Fragment", "Facebook login failed: " + errorMessage);
            }
        });
    }

    private void logOutOfTwitter() {
        socialNetworkManager.getTwitterSocialNetwork().logout();
        updateTwitterStatus();
    }

    private void logOutOfFacebook() {
        socialNetworkManager.getFacebookSocialNetwork().logout();
        updateFacebookStatus();
    }

    private void updateTwitterStatus() {
        String message;

        if (isLoggedInToTwitter()) {
            message = "Signed in";
        } else {
            message = "Signed out";
        }

        twitterStatusView.setText(message);
    }

    private void updateFacebookStatus() {
        String message;

        if (isLoggedInToFacebook()) {
            message = "Signed in";
        } else {
            message = "Signed out";
        }

        facebookStatusView.setText(message);
    }

    private boolean isLoggedInToTwitter() {
        return socialNetworkManager.getTwitterSocialNetwork().isConnected();
    }

    private boolean isLoggedInToFacebook() {
        return socialNetworkManager.getFacebookSocialNetwork().isConnected();
    }

}
