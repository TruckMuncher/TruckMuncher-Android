package com.truckmuncher.app.vendor;

import javax.inject.Inject;

public class VendorHomeController {

    private String selectedTruckId;
    private VendorHomeUi ui;

    @Inject
    public VendorHomeController() {
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

    public interface VendorHomeUi {
        void showNoTrucksError();

        void showEditMenuUi(String truckId);
    }
}
