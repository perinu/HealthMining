package teknopar.com.healthmining.data;

import android.util.Log;

import teknopar.com.healthmining.utils.HMLogger;

/**
 * <b>Author</b> Ilker GURCAN
 * <br/>
 * <b>Date</b> 2/5/15
 * <br/>
 * <h3>Description</h3>
 *
 */
public abstract class AbstractTableDelegate implements TableDelegate {

    protected AppContentProvider mCP;


    @Override
    public void setContentProvider(AppContentProvider cp) {

        if(cp == null) {
            String msg = "ContentProvider Instance Cannot be Null!";
            HMLogger.generateLogFor(AbstractTableDelegate.this.getClass(),
                    Log.ERROR,
                    msg,
                    new Object[0]);
            throw new NullPointerException(msg);
        }
        mCP = cp;
    }
}
