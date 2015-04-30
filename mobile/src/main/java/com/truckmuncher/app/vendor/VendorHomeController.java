package com.truckmuncher.app.vendor;

import com.truckmuncher.app.authentication.UserAccount;

import javax.inject.Inject;

public class VendorHomeController {

    private final UserAccount userAccount;
    private String selectedTruckId;
    private VendorHomeUi ui;

    @Inject
    public VendorHomeController(UserAccount userAccount) {
        this.userAccount = userAccount;
    }

    public void setVendorHomeUi(VendorHomeUi ui) {
        this.ui = ui;
    }

    /**
     * @deprecated Don't use this. it's only here for compatibility. New functionality that needs the truck id should be put in the controller anyhow
     */
    @Deprecated
    public String getSelectedTruckId() {
        return selectedTruckId;
    }

    public void setSelectedTruckId(String selectedTruckId) {
        this.selectedTruckId = selectedTruckId;
    }

    public void onEditMenuClicked() {
        if (selectedTruckId != null) {
            ui.showEditMenuUi(selectedTruckId);
        } else {
            ui.showNoTrucksError();
        }
    }

    public void onLogoutClicked() {
        userAccount.logout();
    }

    public interface VendorHomeUi {
        void showNoTrucksError();

        void showEditMenuUi(String truckId);
    }
}
