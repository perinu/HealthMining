package teknopar.com.healthmining.ui;

import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;

import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapFragment;
import com.google.android.gms.maps.OnMapReadyCallback;

import teknopar.com.healthmining.R;
import teknopar.com.healthmining.utils.HMLogger;

public class MainActivity extends ActionBarActivity implements OnMapReadyCallback {

    private GoogleMap mMapObj;

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        //Set a onMapReady Listener...
        MapFragment mFrag = (MapFragment) getFragmentManager().findFragmentById(R.id.map);
        mFrag.getMapAsync(this);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {

        HMLogger.generateLogFor(
                MainActivity.class,
                Log.DEBUG,
                "Map is ready for use...");
        mMapObj = googleMap;
        mMapObj.setMapType(GoogleMap.MAP_TYPE_NORMAL);
    }
}
