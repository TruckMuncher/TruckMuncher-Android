package com.truckmuncher.app.authentication;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.facebook.CallbackManager;
import com.facebook.FacebookCallback;
import com.facebook.FacebookException;
import com.facebook.login.LoginResult;
import com.facebook.login.widget.LoginButton;
import com.truckmuncher.app.R;
import com.twitter.sdk.android.core.Callback;
import com.twitter.sdk.android.core.Result;
import com.twitter.sdk.android.core.TwitterAuthToken;
import com.twitter.sdk.android.core.TwitterException;
import com.twitter.sdk.android.core.TwitterSession;
import com.twitter.sdk.android.core.identity.TwitterLoginButton;

import butterknife.ButterKnife;
import butterknife.InjectView;
import timber.log.Timber;

public class LoginFragment extends Fragment {

    @InjectView(R.id.twitter_login_button)
    TwitterLoginButton twitterLoginButton;
    @InjectView(R.id.facebook_login_button)
    LoginButton facebookLoginButton;
    private LoginSuccessCallback loginSuccessCallback;
    private CallbackManager callbackManager;

    public static LoginFragment newInstance() {
        return new LoginFragment();
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_login, container, false);
        ButterKnife.inject(this, view);

        loginSuccessCallback = (LoginSuccessCallback) getActivity();

        facebookLoginButton.setReadPermissions("public_profile");
        facebookLoginButton.setFragment(this);

        callbackManager = CallbackManager.Factory.create();
        facebookLoginButton.registerCallback(callbackManager, new FacebookCallback<LoginResult>() {
            @Override
            public void onSuccess(LoginResult loginResult) {
                String tokenString = String.format(getString(R.string.facebook_token_format), loginResult.getAccessToken().getToken());

                loginSuccessCallback.onLoginSuccess(loginResult.getAccessToken().getUserId(), tokenString);
            }

            @Override
            public void onCancel() {
                // No-op
            }

            @Override
            public void onError(FacebookException e) {
                Timber.e(e, "Facebook login failed");
                Toast.makeText(getActivity(), R.string.error_facebook_auth, Toast.LENGTH_LONG).show();
            }
        });

        twitterLoginButton.setCallback(new Callback<TwitterSession>() {
            @Override
            public void success(Result<TwitterSession> result) {
                TwitterAuthToken authToken = result.data.getAuthToken();

                String tokenString = String.format(getString(R.string.twitter_token_format), authToken.token, authToken.secret);

                loginSuccessCallback.onLoginSuccess(result.data.getUserName(), tokenString);
            }

            @Override
            public void failure(TwitterException exception) {
                Timber.e(exception, "Twitter login failed");
                Toast.makeText(getActivity(), R.string.error_twitter_auth, Toast.LENGTH_LONG).show();
            }
        });

        return view;
    }

    @Override
    public void onDestroyView() {
        ButterKnife.reset(this);
        super.onDestroyView();
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        twitterLoginButton.onActivityResult(requestCode, resultCode, data);
        callbackManager.onActivityResult(requestCode, resultCode, data);
    }

    interface LoginSuccessCallback {
        void onLoginSuccess(String userName, String authToken);
    }
}
