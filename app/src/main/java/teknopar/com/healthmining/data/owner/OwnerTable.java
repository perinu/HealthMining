package teknopar.com.healthmining.data.owner;

import android.net.Uri;
import android.provider.BaseColumns;

import teknopar.com.healthmining.core.Constants;

/**
 * <b>Author</b> Ilker GURCAN
 * <br/>
 * <b>Date</b> 7/19/15
 * <br/>
 * <h3>Description</h3>
 *
 * An SQLite table for storing User-Information
 * such as his/her email address, authentication token, etc.
 * Note that; there can be no more than one entry in this table at a time.
 */
public final class OwnerTable implements BaseColumns {

    public static final int TABLE_ID      = 0;
    public static final String TABLE_NAME = "USER_INFO";

    // MIME Types...
    public static final String MIME_ITEM          = "vnd.com.db.provider.userInfo";
    public static final String MIME_TYPE_SINGLE   = Constants.MIME_ITEM_PREFIX + "/" + MIME_ITEM;
    public static final String MIME_TYPE_MULTIPLE = Constants.MIME_DIR_PREFIX + "/"  + MIME_ITEM;

    // URIs...
    public static final String PATH_MULTIPLE    = "/userInfo";
    public static final String PATH_SINGLE      = PATH_MULTIPLE + Constants.PATH_SINGLE_SUFFIX_NUM;
    public static final Uri CONTENT_URI         = Uri.parse(Constants.SCHEME + Constants.AUTHORITY +
                                                            PATH_MULTIPLE);
    public static final Uri CONTENT_ID_BASE_URI = Uri.parse(Constants.SCHEME + Constants.AUTHORITY +
                                                            PATH_MULTIPLE + "/");
    //Table Columns...
    public static final String IS_ACTIVE       = "IS_ACTIVE";
    public static final String USER_NAME       = "USER_NAME";
    public static final String USER_EMAIL      = "USER_EMAIL";
    public static final String USER_TOKEN      = "USER_TOKEN";
    public static final String TOKEN_PROVIDER  = "TOKEN_PROVIDER";
    //Columns related to ProfileInfo
    public static final String DISTANCE        = "DISTANCE";//M, KM, Miles ==> 0, 1, 2 respectively
    public static final String GENDER          = "GENDER";//Female, Male ==> 0, 1
    public static final String WEIGHT          = "WEIGHT";//REAL
    public static final String BIRTHDATE       = "BIRTHDATE";//ISO8601 strings ("YYYY-MM-DD HH:MM:SS.SSS")

    //Path Segment Indices.
    public static final int OWNER_ID_POSITION = 1;

    //Table Related SQL Strings...
    public static final String CREATE_CMD = "CREATE TABLE IF NOT EXISTS "
            + TABLE_NAME
            + "("
            + BaseColumns._ID + " INTEGER PRIMARY KEY AUTOINCREMENT,"
            + " " + IS_ACTIVE + " INTEGER,"
            + " " + USER_NAME + " TEXT,"
            + " " + USER_EMAIL + " TEXT,"
            + " " + USER_TOKEN + " TEXT,"
            + " " + TOKEN_PROVIDER + " TEXT"
            //ProfileInfo
            + " " + DISTANCE + " INTEGER,"
            + " " + GENDER + " INTEGER,"
            + " " + WEIGHT + " REAL,"
            + " " + BIRTHDATE + " TEXT"
            + ");";

    public static final String DROP_CMD           = "DROP TABLE IF EXISTS " + TABLE_NAME;

    public static final String DEFAULT_SORT_ORDER = "_ID ASC";//Completely Dummy...
}
