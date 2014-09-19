package com.truckmuncher.truckmuncher;

import android.app.Fragment;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.TextView;

import com.androidsocialnetworks.lib.SocialNetworkManager;
import com.androidsocialnetworks.lib.listener.OnLoginCompleteListener;

public class LoginFragment extends Fragment
        implements SocialNetworkManager.OnInitializationCompleteListener, View.OnClickListener {

    private SocialNetworkManager socialNetworkManager;
    private ImageButton twitterButton;
    private TextView twitterStatusView;

    public static final String SOCIAL_NETWORK_TAG = "MainActivity.SOCIAL_NETWORK_TAG";

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_login, container, false);
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setRetainInstance(true);

        socialNetworkManager = (SocialNetworkManager) getFragmentManager().findFragmentByTag(SOCIAL_NETWORK_TAG);

        if (socialNetworkManager == null) {
            socialNetworkManager = SocialNetworkManager.Builder.from(getActivity())
                    .twitter("KCWZ6nlCEykT9S7AjGqJwEsM8", "userPqxIrLjeKGDrURvZMWclgIWKsP5WslVEadMU7ii1cJtSqF")
                    .build();
            getFragmentManager().beginTransaction().add(socialNetworkManager, SOCIAL_NETWORK_TAG).commit();
        }
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        twitterButton = (ImageButton) view.findViewById(R.id.twitter_button);
        twitterStatusView = (TextView) view.findViewById(R.id.twitter_status);

        twitterButton.setOnClickListener(this);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();

        if (isLoggedInToTwitter()) {
            logOutOfTwitter();
        }
    }

    @Override
    public void onClick(View view) {
        if (isLoggedInToTwitter()) {
            logOutOfTwitter();
        } else {
            loginWithTwitter();
        }
    }

    @Override
    public void onSocialNetworkManagerInitialized() {

    }

    private void loginWithTwitter() {
        socialNetworkManager.getTwitterSocialNetwork().requestLogin(new OnLoginCompleteListener() {
            @Override
            public void onLoginSuccess(int i) {
                updateTwitterStatus();
            }

            @Override
            public void onError(int i, String s, String s2, Object o) {
                Log.d("Login Fragment", "Login Failed");
            }
        });
    }

    private void logOutOfTwitter() {
        socialNetworkManager.getTwitterSocialNetwork().logout();
        updateTwitterStatus();
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

    private boolean isLoggedInToTwitter() {
        return socialNetworkManager.getTwitterSocialNetwork().isConnected();
    }

}
