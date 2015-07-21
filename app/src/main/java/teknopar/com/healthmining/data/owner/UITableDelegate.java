package teknopar.com.healthmining.data.owner;

import android.content.ContentUris;
import android.content.ContentValues;
import android.content.UriMatcher;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteQueryBuilder;
import android.net.Uri;
import android.text.TextUtils;
import android.util.Log;

import java.util.HashMap;

import teknopar.com.healthmining.core.Constants;
import teknopar.com.healthmining.data.AbstractTableDelegate;
import teknopar.com.healthmining.utils.HMLogger;

/**
 * <b>Author</b> Ilker GURCAN
 * <br/>
 * <b>Date</b> 2/6/15
 * <br/>
 * <h3>Description</h3>
 *
 *
 */
public class UITableDelegate extends AbstractTableDelegate {

    private static UriMatcher              sUriMatcher;
    private static HashMap<String, String> sProjectionMap;

    private static final int OWNER_ID = 0;
    private static final int OWNER    = 1;

    static {
        sUriMatcher = new UriMatcher(UriMatcher.NO_MATCH);
        sUriMatcher.addURI(
                Constants.AUTHORITY,
                OwnerTable.PATH_SINGLE,
                OWNER_ID);
        sUriMatcher.addURI(
                Constants.AUTHORITY,
                OwnerTable.PATH_MULTIPLE,
                OWNER);
        sProjectionMap = new HashMap<String, String>();
        sProjectionMap.put(OwnerTable._ID, OwnerTable._ID);
        sProjectionMap.put(OwnerTable.USER_NAME, OwnerTable.USER_NAME);
        sProjectionMap.put(OwnerTable.USER_EMAIL, OwnerTable.USER_EMAIL);
        sProjectionMap.put(OwnerTable.USER_TOKEN, OwnerTable.USER_TOKEN);
        sProjectionMap.put(OwnerTable.TOKEN_PROVIDER, OwnerTable.TOKEN_PROVIDER);
        //ProfileInfo
        sProjectionMap.put(OwnerTable.TOKEN_PROVIDER, OwnerTable.DISTANCE);
        sProjectionMap.put(OwnerTable.TOKEN_PROVIDER, OwnerTable.GENDER);
        sProjectionMap.put(OwnerTable.TOKEN_PROVIDER, OwnerTable.WEIGHT);
        sProjectionMap.put(OwnerTable.TOKEN_PROVIDER, OwnerTable.BIRTHDATE);
    }

    @Override
    public Cursor query(Uri uri,
                        String[] projection,
                        String selection,
                        String[] selectionArgs,
                        String sortOrder) {

        SQLiteQueryBuilder qb = new SQLiteQueryBuilder();
        qb.setTables(OwnerTable.TABLE_NAME);

        switch(sUriMatcher.match(uri)) {
            //There can be Only One Record in This Table at a Time...
            case OWNER_ID:
                qb.setProjectionMap(sProjectionMap);
                qb.appendWhere(OwnerTable._ID + " = " +
                        uri.getPathSegments().get(OwnerTable.OWNER_ID_POSITION));
                break;
            case OWNER:
                qb.setProjectionMap(sProjectionMap);
                break;
            default:
                HMLogger.generateLogFor(
                        UITableDelegate.class,
                        Log.ERROR,
                        "Unknown URI " + uri);
                throw new IllegalArgumentException("Unknown URI " + uri);
        }//End of switch-Block...

        Cursor c = qb.query(mCP.getOpenHelper().getReadableDatabase(),
                            projection,
                            selection,
                            selectionArgs,
                            null,//group by
                            null,//having
                            sortOrder == null ? OwnerTable.DEFAULT_SORT_ORDER : sortOrder);
        // Tells the Cursor what URI to watch, so it knows when its source data changes
        c.setNotificationUri(mCP.getContext().getContentResolver(), uri);
        return c;
    }

    @Override
    public Uri insert(Uri uri,
                      ContentValues values) {

        String msg;
        if(sUriMatcher.match(uri) != OWNER) {
            msg = "Only the full provider URI is allowed for inserts";
            HMLogger.generateLogFor(
                    UITableDelegate.class,
                    Log.ERROR,
                    msg + " : {0}",
                    uri.toString());
            throw new IllegalArgumentException(msg);
        }
        HMLogger.generateLogFor(
                UITableDelegate.class,
                Log.DEBUG,
                "A new client is being inserted...");

        if(!values.containsKey(OwnerTable.USER_NAME))
            values.put(OwnerTable.USER_NAME, "");
        if(!values.containsKey(OwnerTable.USER_EMAIL))
            values.put(OwnerTable.USER_EMAIL, "");

        SQLiteDatabase db = mCP.getOpenHelper().getWritableDatabase();
        long rowId        = db.insert(OwnerTable.TABLE_NAME,
                                      OwnerTable.USER_NAME,
                                      values);
        // If the insert succeeded, the row ID exists.
        if (rowId > 0) {
            // Creates a URI with the user ID pattern and the new row ID appended to it.
            Uri userInfoUri = ContentUris.withAppendedId(OwnerTable.CONTENT_ID_BASE_URI, rowId);
            // Notifies observers registered against this provider that the data changed.
            mCP.getContext().getContentResolver().notifyChange(userInfoUri, null);
            return userInfoUri;
        }
        // If the insert didn't succeed, then the rowID is <= 0. Throws an exception.
        msg = "Failed to insert row into DB!";
        HMLogger.generateLogFor(UITableDelegate.class,
                Log.ERROR,
                msg + " : {0}",
                uri.toString());
        throw new SQLException(msg);
    }

    @Override
    public int update(Uri uri,
                      ContentValues values,
                      String selection,
                      String[] selectionArgs) {

        String msg;
        String id = uri.getPathSegments().get(OwnerTable.OWNER_ID_POSITION);
        if(TextUtils.isEmpty(id)) {
            msg = "Update operation requires a client ID";
            HMLogger.generateLogFor(UITableDelegate.class, Log.ERROR, msg);
            throw new IllegalArgumentException(msg);
        }
        HMLogger.generateLogFor(
                UITableDelegate.class,
                Log.DEBUG,
                "The existing client with id {0} is being updated...",
                id);
        String finalWhere = OwnerTable._ID + " = " + id;
        SQLiteDatabase db = mCP.getOpenHelper().getWritableDatabase();
        //Update the DB.
        int count = db.update(OwnerTable.TABLE_NAME, values, finalWhere, null);
        /*Gets a handle to the content resolver object for the current context, and notifies it
         * that the incoming URI changed. The object passes this along to the resolver framework,
         * and observers that have registered themselves for the provider are notified.
         */
        mCP.getContext().getContentResolver().notifyChange(uri, null);
        return count;
    }

    @Override
    public int delete(Uri uri,
                      String selection,
                      String[] selectionArgs) {

        String finalWhere;
        SQLiteDatabase db = mCP.getOpenHelper().getWritableDatabase();
        int count;

        switch (sUriMatcher.match(uri)) {
            case OWNER_ID:
                finalWhere = OwnerTable._ID + " = " +
                        uri.getPathSegments().get(OwnerTable.OWNER_ID_POSITION);
                count = db.delete(OwnerTable.TABLE_NAME, finalWhere, null);
                break;
            case OWNER:
                //Delete All Records in the Table!
                count = db.delete(OwnerTable.TABLE_NAME, "1", null);
                break;
            default:
                HMLogger.generateLogFor(
                        UITableDelegate.class,
                        Log.ERROR,
                        "Unknown URI " + uri);
                throw new IllegalArgumentException("Unknown URI " + uri);
        }

        //Gets a handle to the content resolver object for the current context, and notifies it
        //that the incoming URI changed. The object passes this along to the resolver framework,
        //and observers that have registered themselves for the provider are notified.
        mCP.getContext().getContentResolver().notifyChange(uri, null);
        return count;
    }

    @Override
    public String getType(Uri uri) {

        /**
         * Chooses the MIME type based on the incoming URI pattern
         */
        switch(sUriMatcher.match(uri)) {
            case OWNER_ID:
                return OwnerTable.MIME_TYPE_SINGLE;
            case OWNER:
                return OwnerTable.MIME_TYPE_MULTIPLE;
            default:
                throw new IllegalArgumentException("Unknown URI " + uri);
        }
    }
}
