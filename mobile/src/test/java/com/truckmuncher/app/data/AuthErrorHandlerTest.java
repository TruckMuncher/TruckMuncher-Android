package com.truckmuncher.app.data;

import android.support.annotation.NonNull;

import com.truckmuncher.api.auth.AuthRequest;
import com.truckmuncher.api.auth.AuthService;
import com.truckmuncher.api.exceptions.Error;
import com.truckmuncher.api.menu.FullMenusResponse;
import com.truckmuncher.app.R;
import com.truckmuncher.testlib.ReadableRobolectricTestRunner;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.robolectric.Robolectric;

import java.io.IOException;
import java.util.Collections;
import java.util.concurrent.Executor;

import retrofit.RestAdapter;
import retrofit.RetrofitError;
import retrofit.client.Client;
import retrofit.client.Header;
import retrofit.client.Request;
import retrofit.client.Response;
import retrofit.converter.WireConverter;
import retrofit.http.POST;
import retrofit.mime.TypedByteArray;
import retrofit.mime.TypedInput;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.failBecauseExceptionWasNotThrown;

@RunWith(ReadableRobolectricTestRunner.class)
public class AuthErrorHandlerTest {

    ApiErrorHandler errorHandler;

    @Before
    public void setUp() {
        errorHandler = new AuthErrorHandler(Robolectric.application);
    }

    @Test
    public void networkError() {
        RetrofitError error = RetrofitError.networkError("http://api.truckmuncher.com/com.truckmuncher.api.menu.MenuService/getFullMenus", new IOException("Failure message"));
        ApiException exception = errorHandler.handleError(error);

        assertThat(exception).isNotNull();
        assertThat(exception.getMessage()).isEqualTo(Robolectric.application.getString(R.string.error_network));
        assertThat(exception.getCause()).isEqualTo(error);
    }

    @Test
    public void errorWithEmptyBodyDoesNotCrash() {

        TestClient client = new RestAdapter.Builder()
                .setEndpoint("http://example.com")
                .setClient(new Client() {
                    @Override
                    public Response execute(Request request) throws IOException {
                        return new Response("", 400, "invalid request", Collections.<Header>emptyList(), null);
                    }
                })
                .setErrorHandler(errorHandler)
                .setExecutors(new SynchronousExecutor(), new SynchronousExecutor())
                .build()
                .create(TestClient.class);

        try {
            client.getFullMenus();
            failBecauseExceptionWasNotThrown(ApiException.class);
        } catch (ApiException e) {
            assertThat(e.getMessage()).isNull();
            assertThat(e.getCause()).isNotNull();
        }
    }

    @Test
    public void errorWithBodyHasMessageAndCause() {
        final String userMessage = "Test user message";

        TestClient client = new RestAdapter.Builder()
                .setEndpoint("http://example.com")
                .setClient(new Client() {
                    @Override
                    public Response execute(Request request) throws IOException {
                        Error apiError = new Error("1234", userMessage);
                        TypedInput input = new TypedByteArray("application/x-protobuf", apiError.toByteArray());
                        return new Response("", 400, "invalid request", Collections.<Header>emptyList(), input);
                    }
                })
                .setErrorHandler(errorHandler)
                .setExecutors(new SynchronousExecutor(), new SynchronousExecutor())
                .setConverter(new WireConverter())
                .build()
                .create(TestClient.class);

        try {
            client.getFullMenus();
            failBecauseExceptionWasNotThrown(ApiException.class);
        } catch (ApiException e) {
            assertThat(e.getMessage()).isEqualTo(userMessage);
            assertThat(e.getCause()).isNotNull();
        }
    }

    @Test
    public void unauthorizedOnAuthRouteThrowsCorrectException() {
        AuthService service = new RestAdapter.Builder()
                .setEndpoint("http://example.com")
                .setClient(new Client() {
                    @Override
                    public Response execute(Request request) throws IOException {
                        Error apiError = new Error("1234", "Invalid social credentials");
                        TypedInput input = new TypedByteArray("application/x-protobuf", apiError.toByteArray());
                        return new Response("", 401, "invalid request", Collections.<Header>emptyList(), input);
                    }
                })
                .setErrorHandler(errorHandler)
                .setExecutors(new SynchronousExecutor(), new SynchronousExecutor())
                .setConverter(new WireConverter())
                .build()
                .create(AuthService.class);

        try {
            service.getAuth(new AuthRequest());
            failBecauseExceptionWasNotThrown(SocialCredentialsException.class);
        } catch (SocialCredentialsException e) {
            // No-op
        }
    }

    interface TestClient {
        @POST("/")
        FullMenusResponse getFullMenus();
    }

    static class SynchronousExecutor implements Executor {

        @Override
        public void execute(@NonNull Runnable command) {
            command.run();
        }
    }
}
