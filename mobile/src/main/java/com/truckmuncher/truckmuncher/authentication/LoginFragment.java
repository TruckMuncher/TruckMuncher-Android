package com.truckmuncher.truckmuncher.authentication;

import android.app.Activity;
import android.app.Fragment;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.facebook.Request;
import com.facebook.Response;
import com.facebook.Session;
import com.facebook.SessionState;
import com.facebook.UiLifecycleHelper;
import com.facebook.model.GraphUser;
import com.facebook.widget.LoginButton;
import com.truckmuncher.truckmuncher.R;
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

    private LoginSuccessCallback loginSuccessCallback;
    private Session.StatusCallback callback;
    private UiLifecycleHelper uiLifecycleHelper;

    @InjectView(R.id.twitter_login_button)
    TwitterLoginButton twitterLoginButton;

    @InjectView(R.id.facebook_login_button)
    LoginButton facebookLoginButton;

    public static LoginFragment newInstance() {
        return new LoginFragment();
    }

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

        callback = new Session.StatusCallback() {
            @Override
            public void call(Session session, SessionState state, Exception exception) {
                onSessionStateChange(session, state, exception);
            }
        };

        uiLifecycleHelper = new UiLifecycleHelper(getActivity(), callback);
        uiLifecycleHelper.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_login, container, false);
        ButterKnife.inject(this, view);

        facebookLoginButton.setPublishPermissions("public_profile");

        twitterLoginButton.setCallback(new Callback<TwitterSession>() {
            @Override
            public void success(Result<TwitterSession> result) {
                TwitterAuthToken authToken = result.data.getAuthToken();

                String tokenString = String.format(getString(R.string.twitter_token_format), authToken.token, authToken.secret);

                loginSuccessCallback.onLoginSuccess(result.data.getUserName(), tokenString);
            }

            @Override
            public void failure(TwitterException exception) {
                Timber.e("Twitter login failed: %s", exception.getMessage());
            }
        });

        return view;
    }

    @Override
    public void onResume() {
        super.onResume();

        // For scenarios where the main activity is launched and user
        // session is not null, the session state change notification
        // may not be triggered. Trigger it if it's open/closed.
        Session session = Session.getActiveSession();
        if (session != null &&
                (session.isOpened() || session.isClosed()) ) {
            onSessionStateChange(session, session.getState(), null);
        }

        uiLifecycleHelper.onResume();
    }

    @Override
    public void onPause() {
        super.onPause();
        uiLifecycleHelper.onPause();
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        ButterKnife.reset(this);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        uiLifecycleHelper.onDestroy();
    }

    @Override
    public void onDetach() {
        super.onDetach();
        loginSuccessCallback = null;
    }

    @Override
    public void onSaveInstanceState(Bundle state) {
        super.onSaveInstanceState(state);
        uiLifecycleHelper.onSaveInstanceState(state);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        twitterLoginButton.onActivityResult(requestCode, resultCode, data);
        uiLifecycleHelper.onActivityResult(requestCode, resultCode, data);
    }

    private void onSessionStateChange(final Session session, SessionState state, Exception exception) {
        if (exception != null) {
            Timber.e("Facebook error: %s", exception.getMessage());
        }

        if (state.isOpened()) {
            Request request = Request.newMeRequest(session, new Request.GraphUserCallback() {
                @Override
                public void onCompleted(GraphUser user, Response response) {
                    if (session.equals(Session.getActiveSession()) && user != null) {
                        String name = user.getName();

                        String tokenString = String.format(getString(R.string.facebook_token_format), session.getAccessToken());

                        loginSuccessCallback.onLoginSuccess(name, tokenString);
                    }
                }
            });

            Request.executeBatchAsync(request);
        }
    }

    interface LoginSuccessCallback {
        void onLoginSuccess(String userName, String authToken);
    }
}
