package teknopar.com.healthmining.core;

import android.app.Activity;

import teknopar.com.healthmining.ui.MainActivity;

/**
 * <b>Author</b> Ilker GURCAN
 * <br/>
 * <b>Date</b> 7/18/15
 * <br/>
 * <h3>Description</h3>
 *
 */
public final class Constants {

    // ****************************************************************************
    // ****************************System Defaults...******************************
    // ****************************************************************************

    // ****************************************************************************
    // *********************************Logging...*********************************
    // ****************************************************************************
    public static final String DEFAULT_APP_TAG = "teknopar.com.healthmining";

    public static final String LOG_FOLDER      = "logs";

    public static final String LOG_FILE_NAME   = "hm.log";

    // ****************************************************************************
    // *****************************Token Providers...*****************************
    // ****************************************************************************
    public static final String TOKEN_PROVIDER_GOOGLE   = "GoogleAndroid";

    public static final String TOKEN_PROVIDER_FACEBOOK = "com.facebook";

    // ****************************************************************************
    // *************************Activities' Constants...***************************
    // ****************************************************************************
    public static final Class<? extends Activity> HOME_ACTIVITY = MainActivity.class;

    // ****************************************************************************
    // ***************************Dialogs' Constants...****************************
    // ****************************************************************************

    // ****************************************************************************
    // ************************InputSource Protocols...****************************
    // ****************************************************************************
    public static final String IO_SOURCE_FILE                              = "file";

    public static final String IO_SOURCE_ASSET                             = "asset";

    public static final String IO_SOURCE_RES                               = "res";

    // ****************************************************************************
    // **********************ContentProviders' Constants...************************
    // ****************************************************************************
    public static final String SCHEME                 = "content://";

    public static final String AUTHORITY              = "teknopar.com.healthmining";

    public static final String MIME_DIR_PREFIX        = "vnd.android.cursor.dir";

    public static final String MIME_ITEM_PREFIX       = "vnd.android.cursor.item";

    public static final String PATH_SINGLE_SUFFIX_NUM = "/#";

    public static final String PATH_SINGLE_SUFFIX_TEX = "/*";

    public static final int DB_ID                     = 1;

    public static final String DB_NAME                = "healthmining.db";

    // ****************************************************************************
    // ***************************Services' Constants...***************************
    // ****************************************************************************
}
