package com.truckmuncher.api.auth;

import retrofit.Callback;
import retrofit.RetrofitError;
import retrofit.http.Body;

public class MockAuthService implements AuthService {

    @Override
    public AuthResponse getAuth(@Body AuthRequest request) throws RetrofitError {
        throw new UnsupportedOperationException("Not yet implemented");
    }

    @Override
    public void getAuth(@Body AuthRequest request, Callback<AuthResponse> callback) {
        callback.success(getAuth(request), null);
    }

    @Override
    public DeleteAuthResponse deleteAuth(@Body AuthRequest request) throws RetrofitError {
        throw new UnsupportedOperationException("Not yet implemented");
    }

    @Override
    public void deleteAuth(@Body AuthRequest request, Callback<DeleteAuthResponse> callback) {
        callback.success(deleteAuth(request), null);
    }
}
