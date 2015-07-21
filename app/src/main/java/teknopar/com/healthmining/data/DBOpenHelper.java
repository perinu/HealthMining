package teknopar.com.healthmining.data;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

import java.lang.reflect.Field;

import teknopar.com.healthmining.data.owner.OwnerTable;
import teknopar.com.healthmining.utils.HMLogger;

/**
 * <b>Author</b> Ilker GURCAN
 * <br/>
 * <b>Date</b> 7/19/15
 * <br/>
 * <h3>Description</h3>
 *
 * SQLite Database Open Helper.
 */
public class DBOpenHelper extends SQLiteOpenHelper {

    //Order Matters...
    private static final Class<?>[] TABLES_LIST = new Class[] {
            OwnerTable.class,
    };

    public DBOpenHelper(Context context,
                        String name,
                        int version) {

        super(context, name, null, version);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {

        //Create Tables...
        for(int i = 0; i<TABLES_LIST.length; i++) {
            try {
                final Field name         = TABLES_LIST[i].getField("TABLE_NAME");
                final Field createCmd    = TABLES_LIST[i].getField("CREATE_CMD");
                HMLogger.generateLogFor(DBOpenHelper.class,
                        Log.INFO,
                        "Creating Table {0}...",
                        (String) name.get(null));
                db.execSQL((String) createCmd.get(null));
                try {
                    final Field createIdxCmd = TABLES_LIST[i].getField("CREATE_INDEX_CMD");
                    db.execSQL((String) createIdxCmd.get(null));
                } catch (NoSuchFieldException ex) {
                    HMLogger.generateLogFor(DBOpenHelper.class,
                            Log.INFO,
                            "No Index Found for Table {0}...",
                            (String)name.get(null));
                }
            } catch (Exception e) {
                throw new RuntimeException(e);
            }
        }
    }

    @Override
    public void onUpgrade(SQLiteDatabase db,
                          int oldVersion,
                          int newVersion) {

        HMLogger.generateLogFor(
                DBOpenHelper.class,
                Log.WARN,
                "Upgrading DB from {0} to {1}...",
                Integer.toString(oldVersion),
                Integer.toString(newVersion));
        //Dropping Tables...
        for(int i = 0; i<TABLES_LIST.length; i++) {
            try {
                final Field name    = TABLES_LIST[i].getField("TABLE_NAME");
                final Field dropCmd = TABLES_LIST[i].getField("DROP_CMD");
                HMLogger.generateLogFor(DBOpenHelper.class,
                        Log.INFO,
                        "Dropping Table {0}...",
                        (String)name.get(null));
                db.execSQL((String) dropCmd.get(null));
            } catch (Exception e) {
                throw new RuntimeException(e);
            }
        }
        //Recreate the Tables...
        onCreate(db);
    }
}
