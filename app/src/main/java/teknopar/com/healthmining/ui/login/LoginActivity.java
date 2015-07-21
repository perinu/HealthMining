package teknopar.com.healthmining.ui.login;

import android.app.Activity;
import android.content.ContentUris;
import android.content.ContentValues;
import android.content.Intent;
import android.content.IntentSender;
import android.database.Cursor;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import com.google.android.gms.auth.GoogleAuthException;
import com.google.android.gms.auth.UserRecoverableAuthException;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GooglePlayServicesUtil;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.GoogleApiClient.ConnectionCallbacks;
import com.google.android.gms.common.api.GoogleApiClient.OnConnectionFailedListener;
import com.google.android.gms.plus.Plus;
import com.google.android.gms.plus.model.people.Person;

import java.io.IOException;

import teknopar.com.healthmining.R;
import teknopar.com.healthmining.core.Constants;
import teknopar.com.healthmining.core.HMApplication;
import teknopar.com.healthmining.data.owner.OwnerTable;
import teknopar.com.healthmining.utils.AppUtils;
import teknopar.com.healthmining.utils.Backoff;
import teknopar.com.healthmining.utils.HMLogger;


/**
 * <b>Author</b> Ilker GURCAN
 * <br/>
 * <b>Date</b> 7/19/15
 * <br/>
 * <h3>Description</h3>
 *
 * A login screen that offers :
 * -Google+ Sign-in
 * -Facebook Sign-in
 */
public class LoginActivity extends Activity
        implements ConnectionCallbacks, OnConnectionFailedListener,
        AbstractGetTokenAsync.OnTokenReceivedListener {

    private View                  mLoginButtonsCont;
    private AbstractGetTokenAsync mGetTokenAsync;
    private boolean               mSecondTry;
    private Backoff               mBackoff;

    // *********************************************************************************************
    // ********************************** G+ Login Related Fields. *********************************
    // *********************************************************************************************
    //Request code used to invoke sign in user interactions.
    private static final int RC_SIGN_IN    = 0;
    private static final int RC_AUTH_TOKEN = 1;
    // Track whether the sign-in button has been clicked so that we know to resolve
    //all issues preventing sign-in without waiting.
    private boolean mSignInClicked;
    //Used to interact with Google APIs...
    private GoogleApiClient mGoogleAPIClient;
    //A flag indicating that a PendingIntent is in progress and prevents
    //us from starting further intents.
    private boolean mIntentInProgress;
    //Store the connection result from onConnectionFailed callbacks so that we can
    //resolve them when the user clicks sign-in.
    private ConnectionResult mConnResult;

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        mLoginButtonsCont = findViewById(R.id.login_buttons_cont);
        mSecondTry        = false;
        mGetTokenAsync    = null;
        mBackoff          = new Backoff();
        //Reset the Login Flag...
        ((HMApplication)getApplication()).setIsAlreadyLoggedOut(false);
        prepareGoogleSignIn();
    }

    private void prepareGoogleSignIn() {

        mSignInClicked    = false;
        mIntentInProgress = false;
        mGoogleAPIClient  = new GoogleApiClient.Builder(this)
                .addConnectionCallbacks(this)
                .addOnConnectionFailedListener(this)
                .addApi(Plus.API)
                        //Want to have access only to profile info of the client.
                .addScope(Plus.SCOPE_PLUS_PROFILE)
                .build();
        findViewById(R.id.google_sign_in_button).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if (!AppUtils.isDeviceOnline(LoginActivity.this))
                    return;
                if (!mGoogleAPIClient.isConnecting()) {
                    int available = GooglePlayServicesUtil.isGooglePlayServicesAvailable(LoginActivity.this);
                    if (available != ConnectionResult.SUCCESS) {
                        //TODO Show a DialogFragment here...
                        return;
                    }
                    mSignInClicked = true;
                    //Resolve previous connection errors such as user not selected an account.
                    if (mConnResult != null)
                        LoginActivity.this.resolveSignInError();
                    else {
                        AppUtils.showFullScreenProgressBar(LoginActivity.this, mLoginButtonsCont);
                        mGoogleAPIClient.connect();
                    }
                }
            }
        });
    }

    // *********************************************************************************************
    // ************************************* Common Functions. *************************************
    // *********************************************************************************************
    private void getToken(String serviceType, boolean hasDelay) {
        //Logic starts here.
        if(serviceType.equals(Constants.TOKEN_PROVIDER_GOOGLE)) {
            mGetTokenAsync = new GetGoogleTokenAsync(this);
            if (!hasDelay)
                mGetTokenAsync.execute(LoginActivity.this, mGoogleAPIClient);
            else
                mGetTokenAsync.execute(LoginActivity.this, mGoogleAPIClient, mBackoff);
        }
    }

    // *********************************************************************************************
    // ************************ Callbacks for Fetching-Token Asynchronously. ***********************
    // *********************************************************************************************
    //in UI-Thread.
    @Override
    public void onStarted(String serviceType) {

        //if any IOException occurs during the process of fetching token;
        //then display loading bar once again.
        AppUtils.showFullScreenProgressBar(LoginActivity.this, mLoginButtonsCont);
    }

    //in UI-Thread.
    @Override
    public void onFinished(String serviceType,
                           String token,
                           Exception ex,
                           Object...params) {


        AppUtils.dismissFullScreenProgressBar(LoginActivity.this, mLoginButtonsCont);
        if(ex == null) {
            //Save account owner info into the SQLite instance.
            HMLogger.generateLogFor(
                    LoginActivity.class,
                    Log.DEBUG,
                    "Inserting/Updating Client Login Information...");
            String userEmail = "";
            if(serviceType.equals(Constants.TOKEN_PROVIDER_GOOGLE))
                userEmail = Plus.AccountApi.getAccountName(mGoogleAPIClient);
            ContentValues values = new ContentValues();
            //Common Values...
            values.put(OwnerTable.USER_TOKEN, token);
            values.put(OwnerTable.IS_ACTIVE, 1);
            //Find if user already exists.
            Cursor c = getContentResolver().query(
                    OwnerTable.CONTENT_URI,
                    new String[]{OwnerTable._ID},
                    "(" + OwnerTable.USER_NAME + " = ? OR " +
                            OwnerTable.USER_EMAIL + " = ?)" +
                            " AND " + OwnerTable.TOKEN_PROVIDER + " = ?",
                    new String[]{resp.getUserName(), userEmail, serviceType},
                    OwnerTable.DEFAULT_SORT_ORDER);
            //Update database.
            if(c.moveToNext()) {
                getContentResolver().update(
                        ContentUris.withAppendedId(OwnerTable.CONTENT_ID_BASE_URI,
                                c.getInt(c.getColumnIndex(OwnerTable._ID))),
                        values,
                        null,
                        null);
            } else {
                values.put(OwnerTable.USER_NAME, resp.getUserName());
                values.put(OwnerTable.USER_EMAIL, userEmail);
                values.put(OwnerTable.TOKEN_PROVIDER, serviceType);
                getContentResolver().insert(OwnerTable.CONTENT_URI, values);
            }
            c.close();//Never forget to close it to avoid memory leaks!...
        } else {
            if(serviceType.equals(Constants.TOKEN_PROVIDER_GOOGLE)) {
                if(ex instanceof UserRecoverableAuthException) {
                    if(!mSecondTry) {
                        final UserRecoverableAuthException uEx = (UserRecoverableAuthException)ex;
                        startActivityForResult(uEx.getIntent(), RC_AUTH_TOKEN);
                    } else
                        Toast.makeText(
                                LoginActivity.this,
                                R.string.toast_msg_mul_conn_failed,
                                Toast.LENGTH_SHORT).show();
                } else if(ex instanceof GoogleAuthException) {
                    HMLogger.generateLogFor(
                            LoginActivity.class,
                            Log.ERROR,
                            "An unrecoverable exception has occurred! : \n{0}",
                            ex.getMessage());
                    Toast.makeText(
                            LoginActivity.this,
                            R.string.toast_msg_unrecoverable_error,
                            Toast.LENGTH_SHORT).show();
                } else if(ex instanceof IOException) {
                    Toast.makeText(
                            LoginActivity.this,
                            R.string.toast_msg_reconnect,
                            Toast.LENGTH_SHORT).show();
                    //Something is stressed out; the auth servers are by
                    //definition high-traffic and you can't count on
                    //100% success. But it would be bad to retry instantly, so back off
                    getToken(Constants.TOKEN_PROVIDER_GOOGLE, true);
                }
            }
        }//End of outermost-else-block.
    }
    // *********************************************************************************************
    // ********************************** G+ Login Related Methods. ********************************
    // *********************************************************************************************
    //After attempting to resolve errors, this method will be called as a callback handler.
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {

        if(requestCode == RC_SIGN_IN) {
            mIntentInProgress = false; //Previous resolution intent no longer in progress.
            if(resultCode == RESULT_OK) {
                // Resolved a recoverable error, now try connect() again.
                if(!mGoogleAPIClient.isConnected() && !mGoogleAPIClient.isConnecting()) {
                    if(!AppUtils.isDeviceOnline(LoginActivity.this))
                        return;
                    AppUtils.showFullScreenProgressBar(LoginActivity.this, mLoginButtonsCont);
                    mGoogleAPIClient.connect();
                }
            } else {
                mSignInClicked = false; // No longer in the middle of resolving sign-in errors.
                if(resultCode == RESULT_CANCELED) {
                    Toast.makeText(
                            LoginActivity.this,
                            R.string.toast_msg_user_canceled_login,
                            Toast.LENGTH_SHORT).show();
                } else
                    HMLogger.generateLogFor(
                            LoginActivity.class,
                            Log.ERROR,
                            "Error during resolving recoverable error");
            }
        } else if(requestCode == RC_AUTH_TOKEN) {
            if(resultCode == RESULT_OK) {
                mSecondTry= true;
                //Obtain an Authorization Token from Google (Asynchronously).
                getToken(Constants.TOKEN_PROVIDER_GOOGLE, false);
            } else if(resultCode == RESULT_CANCELED) {
                Toast.makeText(
                        LoginActivity.this,
                        R.string.toast_msg_user_canceled_login,
                        Toast.LENGTH_SHORT).show();
            }
        }
        super.onActivityResult(requestCode, resultCode, data);
    }

    //A helper method to resolve the current ConnectionResult error.
    private void resolveSignInError() {

        if(mConnResult.hasResolution()) {
            try {
                mIntentInProgress = true;
                startIntentSenderForResult(
                        mConnResult.getResolution().getIntentSender(),
                        RC_SIGN_IN,
                        null, 0, 0, 0);
            } catch (IntentSender.SendIntentException e) {
                // The intent was canceled before it was sent.  Return to the default
                // state and attempt to connect to get an updated ConnectionResult.
                mIntentInProgress = false;
                if(!AppUtils.isDeviceOnline(LoginActivity.this))
                    return;
                AppUtils.showFullScreenProgressBar(LoginActivity.this, mLoginButtonsCont);
                mGoogleAPIClient.connect();
            }
        }//End of if-Block...
    }

    @Override
    public void onConnected(Bundle bundle) {

        mSignInClicked       = false;
        final Person person  = Plus.PeopleApi.getCurrentPerson(mGoogleAPIClient);
        HMLogger.generateLogFor(
                LoginActivity.class,
                Log.INFO,
                "User {0} Logged in Via Google+",
                person != null ? person.getDisplayName() : "Unknown User");
        //Obtain an Authorization Token from Google (Asynchronously).
        getToken(Constants.TOKEN_PROVIDER_GOOGLE, false);
    }

    //Google Play services will trigger the onConnectionSuspended
    // callback if our Activity loses its service connection
    // Typically you will want to attempt to
    // reconnect when this happens in order to
    // retrieve a new ConnectionResult that can be resolved by the user
    @Override
    public void onConnectionSuspended(int cause) {

        mGoogleAPIClient.connect();
    }

    @Override
    public void onConnectionFailed(ConnectionResult connectionResult) {

        if(connectionResult.getErrorCode() == ConnectionResult.NETWORK_ERROR) {
            if(AppUtils.isDeviceOnline(LoginActivity.this))
                Toast.makeText(
                        LoginActivity.this,
                        R.string.toast_msg_networking_error,
                        Toast.LENGTH_LONG).show();
            AppUtils.dismissFullScreenProgressBar(LoginActivity.this, mLoginButtonsCont);
            return;
        }
        if(!mIntentInProgress) {
            // Store the ConnectionResult so that we can use it later when the user clicks
            // 'sign-in'.
            mConnResult = connectionResult;
            if(mSignInClicked) {
                // The user has already clicked 'sign-in' so we attempt to resolve all
                // errors until the user is signed in, or they cancel.
                resolveSignInError();
            }
        }
    }
}
