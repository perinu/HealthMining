package teknopar.com.healthmining.utils;

import java.util.Random;

/**
 * <b>Author</b> Ilker GURCAN
 * <br/>
 * <b>Date</b> 2/22/15
 * <br/>
 * <h3>Description</h3>
 */
public class Backoff {

    private static final long INITIAL_WAIT = 1000 + new Random().nextInt(1000);
    private static final long MAX_BACKOFF  = 1800 * 1000;

    private long mWaitInterval  = INITIAL_WAIT;
    private boolean mBackingOff = true;

    public boolean shouldRetry() {

        return mBackingOff;
    }

    private void noRetry() {

        mBackingOff = false;
    }

    public void backoff() {

        if (mWaitInterval > MAX_BACKOFF) {
            noRetry();
        } else if (mWaitInterval > 0) {
            try {
                Thread.sleep(mWaitInterval);
            } catch (InterruptedException e) {
                // life's a bitch, then you die
            }
        }

        mWaitInterval = (mWaitInterval == 0) ? INITIAL_WAIT : mWaitInterval * 2;
    }
}
