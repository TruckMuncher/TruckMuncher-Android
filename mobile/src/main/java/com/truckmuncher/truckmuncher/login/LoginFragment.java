package com.truckmuncher.truckmuncher.login;

import android.app.Activity;
import android.app.Fragment;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.androidsocialnetworks.lib.SocialNetworkManager;
import com.androidsocialnetworks.lib.listener.OnLoginCompleteListener;
import com.truckmuncher.truckmuncher.R;

import butterknife.ButterKnife;
import butterknife.InjectView;
import timber.log.Timber;

public class LoginFragment extends Fragment implements View.OnClickListener {

    @InjectView(R.id.twitter_button)
    ImageView twitterButton;
    @InjectView(R.id.facebook_button)
    ImageView facebookButton;
    private SocialNetworkManager socialNetworkManager;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        socialNetworkManager = SocialNetworkManager.getInstance(getActivity());
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

    private void loginWithTwitter() {
        socialNetworkManager.getTwitterSocialNetwork().requestLogin(new OnLoginCompleteListener() {
            @Override
            public void onLoginSuccess(int i) {
                returnSuccessfulLogin();
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
                returnSuccessfulLogin();
            }

            @Override
            public void onError(int socialNetworkID, String requestID, String errorMessage, Object data) {
                Timber.d("Login Fragment", "Facebook login failed: " + errorMessage);
            }
        });
    }

    private void logOutOfTwitter() {
        socialNetworkManager.getTwitterSocialNetwork().logout();
    }

    private void logOutOfFacebook() {
        socialNetworkManager.getFacebookSocialNetwork().logout();
    }

    private boolean isLoggedInToTwitter() {
        return socialNetworkManager.getTwitterSocialNetwork().isConnected();
    }

    private boolean isLoggedInToFacebook() {
        return socialNetworkManager.getFacebookSocialNetwork().isConnected();
    }

    private void returnSuccessfulLogin() {
        Activity activity = getActivity();
        Intent returnIntent = new Intent();

        activity.setResult(activity.RESULT_OK, returnIntent);
        activity.finish();
    }

}
