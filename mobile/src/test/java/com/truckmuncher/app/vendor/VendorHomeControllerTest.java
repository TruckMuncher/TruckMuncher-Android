package com.truckmuncher.app.vendor;

import com.truckmuncher.app.authentication.UserAccount;
import com.truckmuncher.testlib.ReadableRunner;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import static org.mockito.Mockito.verify;

@RunWith(ReadableRunner.class)
public class VendorHomeControllerTest {

    @Mock
    VendorHomeController.VendorHomeUi ui;
    @Mock
    UserAccount userAccount;
    VendorHomeController controller;

    @Before
    public void setUp() {
        MockitoAnnotations.initMocks(this);
        controller = new VendorHomeController(userAccount);
        controller.setVendorHomeUi(ui);
    }

    @Test
    public void whenAskedToShowMenuAndNoTruckIsSelectedErrorIsShown() {
        controller.onEditMenuClicked();
        verify(ui).showNoTrucksError();
    }

    @Test
    public void whenAskedToShowMenuAndTruckIsSelectedMenuIsShown() {
        controller.setSelectedTruckId("TruckId");
        controller.onEditMenuClicked();
        verify(ui).showEditMenuUi("TruckId");
    }

    @Test
    public void logsOutFromAccountOnLogoutClicked() {
        controller.onLogoutClicked();
        verify(userAccount).logout();
    }
}
