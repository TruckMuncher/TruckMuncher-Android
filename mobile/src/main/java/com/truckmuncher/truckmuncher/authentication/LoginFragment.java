package com.truckmuncher.truckmuncher.authentication;

import android.app.Activity;
import android.app.Fragment;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.androidsocialnetworks.lib.AccessToken;
import com.androidsocialnetworks.lib.SocialNetworkManager;
import com.androidsocialnetworks.lib.SocialPerson;
import com.androidsocialnetworks.lib.listener.OnLoginCompleteListener;
import com.androidsocialnetworks.lib.listener.OnRequestSocialPersonCompleteListener;
import com.truckmuncher.truckmuncher.BuildConfig;
import com.truckmuncher.truckmuncher.R;

import butterknife.ButterKnife;
import butterknife.InjectView;
import butterknife.OnClick;
import timber.log.Timber;

public class LoginFragment extends Fragment implements SocialNetworkManager.OnInitializationCompleteListener {

    public static final String SOCIAL_NETWORK_TAG = SocialNetworkManager.class.getSimpleName();

    @InjectView(R.id.twitter_status)
    TextView twitterStatusView;
    @InjectView(R.id.facebook_status)
    TextView facebookStatusView;

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
    void onClick(View view) {
        switch (view.getId()) {
            case R.id.twitter_button:
                if (isLoggedInToTwitter()) {
                    logOutOfTwitter();
                } else {
                    loginWithTwitter();
                }
                break;
            case R.id.facebook_button:
                if (isLoggedInToFacebook()) {
                    logOutOfFacebook();
                } else {
                    loginWithFacebook();
                }
                break;
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
            public void onLoginSuccess(int socialNetworkID) {
                updateTwitterStatus();
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
                updateFacebookStatus();
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

    interface LoginSuccessCallback {
        void onLoginSuccess(String userName, String authToken);
    }

}
