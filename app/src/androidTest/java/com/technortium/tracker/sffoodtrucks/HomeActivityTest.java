package com.technortium.tracker.sffoodtrucks;

import android.app.Fragment;
import android.test.ActivityInstrumentationTestCase2;
import android.widget.TextView;

import com.google.android.gms.maps.MapFragment;
import com.technortium.tracker.sffoodtrucks.view.HomeActivity;

/**
 * Created by root on 22/7/15.
 */
public class HomeActivityTest extends ActivityInstrumentationTestCase2<HomeActivity>{

    private HomeActivity mHomeActivity;
    private Fragment mMapFragment;
    private MapFragment mMyMapFragment;

    public HomeActivityTest() {
        super(HomeActivity.class);
    }

    @Override
    protected void setUp() throws Exception {

        super.setUp();
        mHomeActivity = getActivity();
        mMapFragment = mHomeActivity.getFragmentManager().findFragmentById(R.id.map_fragment);
        mMyMapFragment = (MapFragment) mMapFragment.getFragmentManager().findFragmentById(R.id.map);

    }

    public void testPreconditions() {

        assertNotNull("mHomeActivity is null", mHomeActivity);
        assertNotNull("mMapFragment is null", mMapFragment);
        assertNotNull("mMyMapFragment is null",mMyMapFragment);

    }

    public void testGeoCoder() {

    }

    @Override
    protected void tearDown() throws Exception {
        super.tearDown();
    }
}
