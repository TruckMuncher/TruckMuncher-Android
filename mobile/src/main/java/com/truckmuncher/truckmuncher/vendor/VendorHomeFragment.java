package com.truckmuncher.truckmuncher.vendor;

import android.content.AsyncQueryHandler;
import android.content.ContentValues;
import android.content.Context;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CompoundButton;
import android.widget.TextView;

import com.truckmuncher.truckmuncher.R;
import com.truckmuncher.truckmuncher.data.Contract;
import com.truckmuncher.truckmuncher.data.SimpleAsyncQueryHandler;

import java.io.IOException;
import java.util.List;
import java.util.Locale;

import butterknife.ButterKnife;
import butterknife.InjectView;
import butterknife.OnCheckedChanged;
import timber.log.Timber;

public class VendorHomeFragment extends Fragment {

    private static final String ARG_CURRENT_LOCATION = "current_location";

    @InjectView(R.id.vendor_location)
    TextView vendorLocationTextView;

    private Location currentLocation;

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_vendor_home, container, false);
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        ButterKnife.inject(this, view);

        if (savedInstanceState != null) {
            currentLocation = savedInstanceState.getParcelable(ARG_CURRENT_LOCATION);
        }
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putParcelable(ARG_CURRENT_LOCATION, currentLocation);
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        ButterKnife.reset(this);
    }

    @OnCheckedChanged(R.id.serving_mode)
    void onServingModeToggled(CompoundButton servingModeSwitch, boolean isChecked) {
        int color = isChecked ? R.color.serving_mode_on : R.color.serving_mode_off;
        int resolvedColor = getResources().getColor(color);

        servingModeSwitch.setBackgroundColor(resolvedColor);
        vendorLocationTextView.setBackgroundColor(resolvedColor);

        ContentValues values = new ContentValues();
        values.put(Contract.TruckEntry.COLUMN_LATITUDE, currentLocation.getLatitude());
        values.put(Contract.TruckEntry.COLUMN_LONGITUDE, currentLocation.getLongitude());
        values.put(Contract.TruckEntry.COLUMN_IS_SERVING, isChecked);
        values.put(Contract.TruckEntry.COLUMN_IS_DIRTY, true);
        AsyncQueryHandler handler = new SimpleAsyncQueryHandler(getActivity().getContentResolver());
        // FIXME Need to use a real truck id, not a mock one
        handler.startUpdate(0, null, Contract.buildNeedsSync(Contract.TruckEntry.buildSingleTruck("de513002-5a44-11e4-aa15-123b93f75cba")), values, null, null);
    }

    public void onLocationUpdate(Location location) {
        currentLocation = location;
        new GetAddressTask(this, vendorLocationTextView).executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR, location);
    }

    /**
     * A subclass of AsyncTask that calls getFromLocation() in the
     * background. The class definition has these generic types:
     * Location - A Location object containing the current location.
     * Void     - indicates that progress units are not used
     * String   - An address passed to onPostExecute()
     */
    private static class GetAddressTask extends AsyncTask<Location, Void, String> {
        private Context context;
        private TextView addressView;
        private Fragment fragment;

        public GetAddressTask(Fragment fragment, TextView addressView) {
            super();
            this.context = fragment.getActivity();
            this.addressView = addressView;
            this.fragment = fragment;
        }

        /**
         * Get a Geocoder instance, get the latitude and longitude
         * look up the address, and return it
         *
         * @param params One or more Location objects
         * @return A string containing the address of the current
         * location, or an empty string if no address can be found,
         * or an error message
         */
        @Override
        protected String doInBackground(@NonNull Location... params) {
            // TODO GeoCoder is unreliable. Use the Google Geolocation API instead
            Geocoder geocoder = new Geocoder(context, Locale.getDefault());
            // Get the current location from the input parameter list
            Location loc = params[0];
            // Create a list to contain the result address
            List<Address> addresses;
            try {
                /*
                 * Return 1 address.
                 */
                addresses = geocoder.getFromLocation(loc.getLatitude(), loc.getLongitude(), 1);
            } catch (IOException e1) {
                Timber.d(e1, "IO Exception in getFromLocation()");
                return ("IO Exception trying to get address");
            } catch (IllegalArgumentException e2) {
                // Error message to post in the log
                String errorString = "Illegal arguments " + Double.toString(loc.getLatitude()) +
                        " , " + Double.toString(loc.getLongitude()) + " passed to address service";
                Timber.e(e2, errorString);
                return errorString;
            }
            // If the reverse geocode returned an address
            if (addresses != null && addresses.size() > 0) {
                // Get the first address
                Address address = addresses.get(0);
                /*
                 * Format the first line of address (if available),
                 * city, and country name.
                 */
                return String.format(
                        "%s, %s, %s",
                        // If there's a street address, add it
                        address.getMaxAddressLineIndex() > 0 ? address.getAddressLine(0) : "",
                        // Locality is usually a city
                        address.getLocality(),
                        // The country of the address
                        address.getCountryName());
            } else {
                return "Unable to geocode an address";
            }
        }

        /**
         * A method that's called once doInBackground() completes. Turn
         * off the indeterminate activity indicator and set
         * the text of the UI element that shows the address. If the
         * lookup failed, display the error message.
         */
        @Override
        protected void onPostExecute(@NonNull String address) {
            if (fragment.isVisible()) {

                // TODO This has the potential to show error messages and shows long addresses. Format and handle errors silently
                addressView.setText(address);
            }
            context = null;    // Don't leak a context reference
            fragment = null;
        }
    }
}
