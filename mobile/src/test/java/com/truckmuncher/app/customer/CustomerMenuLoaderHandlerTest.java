package com.truckmuncher.app.customer;

import android.support.v4.app.LoaderManager;

import com.truckmuncher.testlib.ReadableRobolectricTestRunner;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.robolectric.Robolectric;

import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@RunWith(ReadableRobolectricTestRunner.class)
public class CustomerMenuLoaderHandlerTest {

    @Mock
    CustomerMenuLoaderHandler.DataDestination dataDestination;
    @Mock
    CustomerMenuLoaderHandler.OnTriedToLoadInvalidTruckListener invalidTruckListener;

    @Before
    public void setUp() {
        MockitoAnnotations.initMocks(this);
    }

    @Test
    public void loadInitializesCorrectLoader() {
        LoaderManager loaderManager = mock(LoaderManager.class);
        when(dataDestination.getLoaderManager()).thenReturn(loaderManager);

        String truckId = "";
        CustomerMenuLoaderHandler handler = new CustomerMenuLoaderHandler(Robolectric.application, dataDestination, truckId, invalidTruckListener);
        handler.load();

        verify(loaderManager).initLoader(CustomerMenuLoaderHandler.DataDestination.LOADER_TRUCK, null, handler);
    }
}
