package com.truckmuncher.truckmuncher.authentication;

import android.app.Activity;
import android.app.Fragment;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.androidsocialnetworks.lib.AccessToken;
import com.androidsocialnetworks.lib.SocialNetworkManager;
import com.androidsocialnetworks.lib.SocialPerson;
import com.androidsocialnetworks.lib.listener.OnLoginCompleteListener;
import com.androidsocialnetworks.lib.listener.OnRequestSocialPersonCompleteListener;
import com.truckmuncher.truckmuncher.R;

import butterknife.ButterKnife;
import butterknife.InjectView;
import butterknife.OnClick;
import timber.log.Timber;

public class LoginFragment extends Fragment {

    public static final String SOCIAL_NETWORK_TAG = SocialNetworkManager.class.getSimpleName();

    @InjectView(R.id.twitter_button)
    ImageView twitterButton;
    @InjectView(R.id.facebook_button)
    ImageView facebookButton;

    private SocialNetworkManager socialNetworkManager;
    private LoginSuccessCallback loginSuccessCallback;

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        try {
            loginSuccessCallback = (LoginSuccessCallback) activity;
        } catch (ClassCastException e) {
            throw new ClassCastException("Calling activity must implement " + LoginSuccessCallback.class.getName());
        }
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        socialNetworkManager = SocialNetworkManager.getInstance(getActivity());
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_login, container, false);
        ButterKnife.inject(this, view);
        return view;
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        ButterKnife.reset(this);
    }

    @Override
    public void onDetach() {
        super.onDetach();
        loginSuccessCallback = null;
    }


    @OnClick({R.id.twitter_button, R.id.facebook_button})
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
            public void onLoginSuccess(int socialNetworkID) {
                socialNetworkManager.getTwitterSocialNetwork().requestCurrentPerson(new OnRequestSocialPersonCompleteListener() {
                    @Override
                    public void onRequestSocialPersonSuccess(int socialNetworkID, SocialPerson socialPerson) {
                        AccessToken accessToken = socialNetworkManager.getTwitterSocialNetwork().getAccessToken();
                        // TODO extract the constant. Fragment is not a good place for it.
                        String result = String.format("oauth_token=%s,oauth_secret=%s", accessToken.token, accessToken.secret);

                        loginSuccessCallback.onLoginSuccess(socialPerson.name, result);
                    }

                    @Override
                    public void onError(int socialNetworkID, String requestID, String errorMessage, Object data) {
                        // TODO: handle
                    }
                });
            }

            @Override
            public void onError(int socialNetworkID, String requestID, String errorMessage, Object data) {
                Timber.e("Twitter login failed: %s", errorMessage);
            }
        });
    }

    private void loginWithFacebook() {
        socialNetworkManager.getFacebookSocialNetwork().requestLogin(new OnLoginCompleteListener() {
            @Override
            public void onLoginSuccess(int socialNetworkID) {
                socialNetworkManager.getFacebookSocialNetwork().requestCurrentPerson(new OnRequestSocialPersonCompleteListener() {
                    @Override
                    public void onRequestSocialPersonSuccess(int socialNetworkID, SocialPerson socialPerson) {
                        String token = socialNetworkManager.getFacebookSocialNetwork().getAccessToken().token;
                        // TODO extract the constant. Fragment is not a good place for it.
                        loginSuccessCallback.onLoginSuccess(socialPerson.name, "access_token=" + token);
                    }

                    @Override
                    public void onError(int socialNetworkID, String requestID, String errorMessage, Object data) {
                        // TODO: handle
                    }
                });
            }

            @Override
            public void onError(int socialNetworkID, String requestID, String errorMessage, Object data) {
                Timber.e("Facebook login failed: %s", errorMessage);
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

    interface LoginSuccessCallback {
        void onLoginSuccess(String userName, String authToken);
    }
}
