package teknopar.com.healthmining.ui.login;

import android.os.AsyncTask;
import android.util.Log;

import teknopar.com.healthmining.utils.Backoff;
import teknopar.com.healthmining.utils.HMLogger;

/**
 * <b>Author</b> Ilker GURCAN
 * <br/>
 * <b>Date</b> 7/18/15
 * <br/>
 * <h3>Description</h3>
 */
abstract class AbstractGetTokenAsync extends AsyncTask<Object, Void, String> {

    private OnTokenReceivedListener mTokenListener;
    private Exception               mEx;

    private Object[]                mParams;
    private String                  mToken;

    public AbstractGetTokenAsync(OnTokenReceivedListener tokenListener) {

        mTokenListener = tokenListener;
        mEx            = null;
    }

    //Implementation in parent-class does nothing.
    @Override
    protected void onPreExecute() {

        HMLogger.generateLogFor(
                AbstractGetTokenAsync.class,
                Log.DEBUG,
                "Fetching Token Asynchronously from {0}...",
                type());
        if(mTokenListener != null)
            mTokenListener.onStarted(type());
    }

    @Override
    protected String doInBackground(Object... params) {

        String token = null;

        try {
            shouldWait(params);
            token = doGetToken(params);
            if(mTokenListener != null) {
                mParams = params;
                mToken  = token;
            }
        } catch (Exception e) {
            mEx = e;
        }

        return token;
    }

    private void shouldWait(Object[] params) {

        if(params != null && params.length > 0) {
            Object o = params[params.length - 1];
            if(o instanceof Backoff) {
                final Backoff b = (Backoff)o;
                if(b.shouldRetry())
                    b.backoff();
            }
        }
    }

    protected abstract String doGetToken(Object...params) throws Exception;

    //Implementation in parent-class does nothing.
    @Override
    protected void onPostExecute(String s) {

        String msg = "Fetching Token Asynchronously from {0} is Completed";
        int level;
        Object[] args;

        if(mEx != null) {
            msg  += " with an exception :\n {1}";
            level = Log.ERROR;
            args = new Object[]{type(), mEx.getMessage()};
        } else {
            msg   += " without any problem!";
            level  = Log.DEBUG;
            args   = new Object[]{type()};
        }

        HMLogger.generateLogFor(AbstractGetTokenAsync.class, level, msg, args);
        if(mTokenListener != null)
            mTokenListener.onFinished(type(), mToken, mEx, mParams);
        //Clear the exception and params for future invocations.
        mEx     = null;
        mParams = null;
        mToken  = null;
    }

    public abstract String type();

    /**
     * Listener interface for clients who are interested in token-fetch events.
     */
    public static interface OnTokenReceivedListener {

        void onStarted(String serviceType);

        void onFinished(String serviceType,
                        String token,
                        Exception ex,
                        Object... params);
    }
}
