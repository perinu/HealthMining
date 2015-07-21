package teknopar.com.healthmining.core;

import android.app.Application;
import android.util.Log;

import teknopar.com.healthmining.utils.HMLogger;

/**
 * Created by iliTheFallen on 7/20/15.
 */
public class HMApplication extends Application {

    private boolean mIsAlreadyLoggedOut;

    @Override
    public void onCreate() {

        super.onCreate();
        HMLogger.initializeLogging(Log.VERBOSE, null);//Initialize App Logger...
    }

    public boolean isAlreadyLoggedOut() {

        return mIsAlreadyLoggedOut;
    }

    public void setIsAlreadyLoggedOut(boolean isAlreadyLoggedOut) {

        mIsAlreadyLoggedOut =isAlreadyLoggedOut;
    }
}
