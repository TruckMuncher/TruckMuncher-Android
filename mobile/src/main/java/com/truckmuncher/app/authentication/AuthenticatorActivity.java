package com.truckmuncher.app.authentication;

import android.accounts.Account;
import android.content.ContentResolver;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.NavUtils;
import android.support.v7.app.AppCompatActivity;
import android.view.MenuItem;

import com.truckmuncher.app.data.PublicContract;

public class AuthenticatorActivity extends AppCompatActivity
        implements LoginFragment.LoginSuccessCallback {

    private Fragment fragment;

    public static Intent newIntent(Context context) {
        return new Intent(context, AuthenticatorActivity.class);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        fragment = LoginFragment.newInstance();

        getSupportFragmentManager().beginTransaction()
                .add(android.R.id.content, fragment)
                .commit();
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                NavUtils.navigateUpFromSameTask(this);
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    @Override
    public void onLoginSuccess() {

        // FIXME Setup the account to be syncable. Needs to be made generic
        Account account = AccountGeneral.getAccount(this);
        ContentResolver.setSyncAutomatically(account, PublicContract.CONTENT_AUTHORITY, true);
        ContentResolver.setIsSyncable(account, PublicContract.CONTENT_AUTHORITY, 1);

        setResult(RESULT_OK);
        finish();
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (fragment != null) {
            fragment.onActivityResult(requestCode, resultCode, data);
        }
    }
}
