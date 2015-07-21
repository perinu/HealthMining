package teknopar.com.healthmining.ui.login;

import android.content.Context;

import com.google.android.gms.auth.GoogleAuthUtil;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.plus.Plus;

import teknopar.com.healthmining.core.Constants;

/**
 * <b>Author</b> Ilker GURCAN
 * <br/>
 * <b>Date</b> 7/18/15
 * <br/>
 * <h3>Description</h3>
 */
class GetGoogleTokenAsync extends AbstractGetTokenAsync {

    private static final String SCOPE = "oauth2:https://www.googleapis.com/auth/userinfo.profile";

    public GetGoogleTokenAsync(OnTokenReceivedListener tokenListener) {

        super(tokenListener);
    }

    @Override
    protected String doGetToken(Object...params) throws Exception {

        Context ctx            = (Context)params[0];
        GoogleApiClient client = (GoogleApiClient)params[1];
        return GoogleAuthUtil.getToken(ctx, Plus.AccountApi.getAccountName(client), SCOPE);
    }

    @Override
    public String type() {

        return Constants.TOKEN_PROVIDER_GOOGLE;
    }
}
