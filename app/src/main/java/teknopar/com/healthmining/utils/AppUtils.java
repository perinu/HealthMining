package teknopar.com.healthmining.utils;

import android.app.Activity;
import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.util.DisplayMetrics;
import android.util.TypedValue;
import android.view.View;
import android.widget.Toast;

import teknopar.com.healthmining.R;

/**
 * <b>Author</b> Ilker GURCAN
 * <br/>
 * <b>Date</b> 7/19/15
 * <br/>
 * <h3>Description</h3>
 */
public final class AppUtils {

    // Checks whether the device currently has a network connection
    public static boolean isDeviceOnline(Context ctx) {

        ConnectivityManager connMgr = (ConnectivityManager)
                ctx.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo networkInfo = connMgr.getActiveNetworkInfo();
        if (networkInfo != null && networkInfo.isConnected())
            return true;
        Toast.makeText(ctx, R.string.toast_msg_no_internet_conn,Toast.LENGTH_LONG)
                .show();
        return false;
    }

    public static void showFullScreenProgressBar(Activity ac, View rootViewToHide) {

        View progressBarCont = ac.findViewById(R.id.progressBarCont);
        if(progressBarCont.getVisibility() == View.GONE) {
            rootViewToHide.setVisibility(View.GONE);
            progressBarCont.setVisibility(View.VISIBLE);
        }
    }

    public static void dismissFullScreenProgressBar(Activity ac, View rootViewToShow) {

        View progressBarCont = ac.findViewById(R.id.progressBarCont);
        if(progressBarCont.getVisibility() == View.VISIBLE) {
            progressBarCont.setVisibility(View.GONE);
            rootViewToShow.setVisibility(View.VISIBLE);
        }
    }

    public static float getDipInPx(Context ctx, float dip) {

        DisplayMetrics metrics = ctx.getResources().getDisplayMetrics();
        return TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, dip, metrics);
    }
}
