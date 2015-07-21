package teknopar.com.healthmining.data;

import android.content.ContentProvider;
import android.content.ContentProviderOperation;
import android.content.ContentProviderResult;
import android.content.ContentValues;
import android.content.OperationApplicationException;
import android.content.UriMatcher;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.net.Uri;
import android.support.annotation.NonNull;
import android.util.Log;
import android.util.SparseArray;

import com.db.core.Constants;
import com.db.data.activities.ActivitiesTable;
import com.db.data.activities.ActivitiesTableDelegate;
import com.db.data.coursepackage.CoursePackagesTable;
import com.db.data.coursepackage.CoursePackagesTableDelegate;
import com.db.data.courses.CoursesTable;
import com.db.data.courses.CoursesTableDelegate;
import com.db.data.userinfo.UITableDelegate;
import com.db.data.userinfo.UserInfoTable;
import com.db.utilities.DBLogger;

import java.util.ArrayList;

/**
 * <b>Author</b> Ilker GURCAN
 * <br/>
 * <b>Date</b> 2/6/15
 * <br/>
 * <h3>Description</h3>
 *
 *
 */
public class AppContentProvider extends ContentProvider {

    private DBOpenHelper               mOpenHelper;
    private SparseArray<TableDelegate> mDelegates;

    private static UriMatcher sTableMatcher;
    static {
        //Create and Initialize URI Matching...
        sTableMatcher = new UriMatcher(UriMatcher.NO_MATCH);
        //List of Tables for Matching Goes Here...
        //OwnerTable
        sTableMatcher.addURI(
                Constants.AUTHORITY,
                UserInfoTable.PATH_SINGLE,
                UserInfoTable.TABLE_ID);
        sTableMatcher.addURI(
                Constants.AUTHORITY,
                UserInfoTable.PATH_MULTIPLE,
                UserInfoTable.TABLE_ID);
        //CoursePackagesTable
        sTableMatcher.addURI(
                Constants.AUTHORITY,
                CoursePackagesTable.PATH_SINGLE,
                CoursePackagesTable.TABLE_ID);
        sTableMatcher.addURI(
                Constants.AUTHORITY,
                CoursePackagesTable.PATH_MULTIPLE,
                CoursePackagesTable.TABLE_ID);
        //CourseTable
        sTableMatcher.addURI(
                Constants.AUTHORITY,
                CoursesTable.PATH_SINGLE,
                CoursesTable.TABLE_ID);
        sTableMatcher.addURI(
                Constants.AUTHORITY,
                CoursesTable.PATH_MULTIPLE,
                CoursesTable.TABLE_ID);
        //ActivitiesTable
        sTableMatcher.addURI(
                Constants.AUTHORITY,
                ActivitiesTable.PATH_SINGLE,
                ActivitiesTable.TABLE_ID);
        sTableMatcher.addURI(
                Constants.AUTHORITY,
                ActivitiesTable.PATH_ACTIVITIES_OF_COURSE,
                ActivitiesTable.TABLE_ID);
        sTableMatcher.addURI(
                Constants.AUTHORITY,
                ActivitiesTable.PATH_MULTIPLE,
                ActivitiesTable.TABLE_ID);
    }

    @Override
    public boolean onCreate() {

        //Creates a DB If It Does not Already Exist...
        mOpenHelper = new DBOpenHelper(getContext(),
                                       Constants.DB_NAME,
                                       Constants.DB_ID);
        mDelegates = new SparseArray<>();
        createDelegates();
        int size = mDelegates.size();
        for(int i = 0; i<size; i++)
            mDelegates.valueAt(i).setContentProvider(this);
        // Assumes that any failure will be reported by a thrown "Exception".
        return true;
    }

    private void createDelegates() {
        //Create Our Delegates...
        mDelegates.put(UserInfoTable.TABLE_ID, new UITableDelegate());
        mDelegates.put(CoursePackagesTable.TABLE_ID, new CoursePackagesTableDelegate());
        mDelegates.put(CoursesTable.TABLE_ID, new CoursesTableDelegate());
        mDelegates.put(ActivitiesTable.TABLE_ID, new ActivitiesTableDelegate());
    }

    /**
     * Find the Requested Delegator...
     * @param uri {@link Uri} associated with a specific table in the Application DB
     * @return {@link com.db.data.TableDelegate} instance
     * this {@link Uri} indicates.
     */
    private TableDelegate findDelegator(Uri uri) {

        final TableDelegate del = mDelegates.get(sTableMatcher.match(uri));
        if(del == null) {
            String msg = "No Such Table Found in This Database Instance!";
            DBLogger.generateLogFor(AppContentProvider.class,
                                    Log.ERROR,
                                    msg + "\nURI Sent to the Provider is : {0}",
                                    uri.toString());
            throw new IllegalArgumentException(msg);
        }
        return del;
    }

    @Override
    public Cursor query(Uri uri,
                        String[] projection,
                        String selection,
                        String[] selectionArgs,
                        String sortOrder) {

        return findDelegator(uri).query(uri,
                                        projection,
                                        selection,
                                        selectionArgs,
                                        sortOrder);
    }

    @Override
    public Uri insert(Uri uri, ContentValues values) {

        return findDelegator(uri).insert(uri, values);
    }

    @Override
    public int update(Uri uri,
                      ContentValues values,
                      String selection,
                      String[] selectionArgs) {

        return findDelegator(uri).update(uri,
                                         values,
                                         selection,
                                         selectionArgs);
    }

    @Override
    public int delete(Uri uri,
                      String selection,
                      String[] selectionArgs) {

        return findDelegator(uri).delete(uri, selection, selectionArgs);
    }

    @Override
    public ContentProviderResult[] applyBatch(@NonNull ArrayList<ContentProviderOperation> operations)
            throws OperationApplicationException {

        ContentProviderResult[] result = new ContentProviderResult[operations.size()];
        int i = 0;
        // Opens the database object in "write" mode.
        SQLiteDatabase db = mOpenHelper.getWritableDatabase();
        // Begin a transaction. No new transaction will be started by SQLite this time!
        db.beginTransaction();
        try {
            for (ContentProviderOperation operation : operations) {
                // Chain the result for back references
                result[i++] = operation.apply(this, result, i);
            }
            db.setTransactionSuccessful();
        } catch (OperationApplicationException e) {
            DBLogger.generateLogFor(AppContentProvider.class,
                    Log.ERROR,
                    "An error occurred while applying batch operation! \n {0}",
                    e.getMessage());
        } finally {
            db.endTransaction();
        }

        return result;
    }

    @Override
    public String getType(Uri uri) {

        return findDelegator(uri).getType(uri);
    }

    public DBOpenHelper getOpenHelper() {

        return mOpenHelper;
    }
}
