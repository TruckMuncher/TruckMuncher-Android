package com.truckmuncher.truckmuncher.authentication;

import android.app.Activity;
import android.app.Fragment;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.androidsocialnetworks.lib.AccessToken;
import com.androidsocialnetworks.lib.SocialNetworkManager;
import com.androidsocialnetworks.lib.SocialPerson;
import com.androidsocialnetworks.lib.listener.OnLoginCompleteListener;
import com.androidsocialnetworks.lib.listener.OnRequestSocialPersonCompleteListener;
import com.truckmuncher.truckmuncher.R;

import butterknife.ButterKnife;
import butterknife.OnClick;
import timber.log.Timber;

public class LoginFragment extends Fragment {

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
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
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
        if (view.getId() == R.id.twitter_button) {
            logOutOfTwitter();
            loginWithTwitter();
        } else if (view.getId() == R.id.facebook_button) {
            logOutOfFacebook();
            loginWithFacebook();
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
                        String result = String.format(getString(R.string.twitter_token_format), accessToken.token, accessToken.secret);

                        loginSuccessCallback.onLoginSuccess(socialPerson.name, result);
                    }

                    @Override
                    public void onError(int socialNetworkID, String requestID, String errorMessage, Object data) {
                        // Something went wrong trying to retrieve the person's Twitter info. We
                        // can still log them in, but we don't have their name or account info.
                        Timber.w("Retreival of Twitter account information failed with the following" +
                                "error %s", errorMessage);

                        String token = socialNetworkManager.getFacebookSocialNetwork().getAccessToken().token;
                        String result = String.format(getString(R.string.facebook_token_format), token);

                        loginSuccessCallback.onLoginSuccess(getString(R.string.twitter), result);
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
                        String result = String.format(getString(R.string.facebook_token_format), token);

                        loginSuccessCallback.onLoginSuccess(socialPerson.name, result);
                    }

                    @Override
                    public void onError(int socialNetworkID, String requestID, String errorMessage, Object data) {
                        // Something went wrong trying to retrieve the person's Twitter info. We
                        // can still log them in, but we don't have their name or account info.
                        Timber.w("Retrieval of Facebook account information failed with the following" +
                                "error %s", errorMessage);

                        String token = socialNetworkManager.getFacebookSocialNetwork().getAccessToken().token;
                        String result = String.format(getString(R.string.facebook_token_format), token);

                        loginSuccessCallback.onLoginSuccess(getString(R.string.facebook), result);
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

    interface LoginSuccessCallback {
        void onLoginSuccess(String userName, String authToken);
    }
}
