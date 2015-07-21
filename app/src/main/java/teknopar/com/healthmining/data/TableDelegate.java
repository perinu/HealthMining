package teknopar.com.healthmining.data;

import android.content.ContentProvider;
import android.content.ContentValues;
import android.database.Cursor;
import android.net.Uri;

/**
 * <b>Author</b> Ilker GURCAN
 * <br/>
 * <b>Date</b> 2/6/15
 * <br/>
 * <h3>Description</h3>
 *
 *
 */
public interface TableDelegate {

    Cursor query(Uri uri,
                 String[] projection,
                 String selection,
                 String[] selectionArgs,
                 String sortOrder);

    Uri insert(Uri uri, ContentValues values);

    int update(Uri uri,
               ContentValues values,
               String selection,
               String[] selectionArgs);

    int delete(Uri uri,
               String selection,
               String[] selectionArgs);

    void setContentProvider(AppContentProvider cp);

    String getType(Uri uri);
}
