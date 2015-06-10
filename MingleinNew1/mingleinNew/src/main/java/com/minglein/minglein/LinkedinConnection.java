package com.minglein.minglein;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;

import com.linkedin.platform.APIHelper;
import com.linkedin.platform.LISessionManager;
import com.linkedin.platform.errors.LIApiError;
import com.linkedin.platform.errors.LIAuthError;
import com.linkedin.platform.listeners.ApiListener;
import com.linkedin.platform.listeners.ApiResponse;
import com.linkedin.platform.listeners.AuthListener;
import com.linkedin.platform.utils.Scope;

import org.json.JSONException;
import org.json.JSONObject;

public class LinkedinConnection extends Activity {
    //=================================================================
//                       Constants
//=================================================================
    private static final String TAG = LinkedinConnection.class.getSimpleName();

    //================================================================= //                       Activity Lifecycle
//=================================================================

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        // Store a reference to the current activity
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_linkedin_connection);

        authenticateLinkedin();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        LISessionManager.getInstance(getApplicationContext()).onActivityResult(this, requestCode, resultCode, data);
    }

    //=================================================================
//                         Private Methods //=================================================================

    // Build the list of member permissions our LinkedIn session requires
    private static Scope buildScope() {
        return Scope.build(Scope.R_BASICPROFILE, Scope.W_SHARE);
    }

    private void authenticateLinkedin() {
        final Activity thisActivity = this;

        // Authenticate and get token
        LISessionManager.getInstance(getApplicationContext()).init(thisActivity, buildScope(), new AuthListener() {

            @Override
            public void onAuthSuccess() {
                // Authentication was successful.  You can now do
                // other calls with the SDK.
                Log.d(TAG, "onAuthSuccess");

                getBasicProfile();
            }

            @Override
            public void onAuthError(LIAuthError error) {
                // Handle authentication errors
                Log.d(TAG, "onAuthError");

            }
        }, true); // true will launch google play market if linked app is not installed
    }

    private void getBasicProfile() {

        String url = "https://api.linkedin.com/v1/people/~:(id,picture-urls::(original),first-name,last-name)?format=json";

        APIHelper apiHelper = APIHelper.getInstance(getApplicationContext());

        apiHelper.getRequest(LinkedinConnection.this, url, new ApiListener() {

            @Override
            public void onApiSuccess(ApiResponse apiResponse) {
                // Success!
                Log.d(TAG, apiResponse.getResponseDataAsString());
                JSONObject apiJson = apiResponse.getResponseDataAsJson();

                SharedPreferences prefs = getSharedPreferences("com.minglein.minglein", Context.MODE_PRIVATE);
                String li_id = "com.minglein.minglein.li_id";
                String alreadySigned = prefs.getString(li_id, null);
                Intent intent = null;
                try {
                    if (alreadySigned != null && alreadySigned.equalsIgnoreCase(apiJson.get("id").toString())) {
                        intent = new Intent(LinkedinConnection.this, SearchActivity.class);
                    } else {
                        intent = new Intent(LinkedinConnection.this, EditProfileActivity.class);
                    }
                    intent.putExtra("li_id", apiJson.get("id").toString());
                    intent.putExtra("first_name", apiJson.get("firstName").toString());
                    intent.putExtra("last_name", apiJson.get("lastName").toString());
                    if (!apiJson.getJSONObject("pictureUrls").get("_total").toString().equalsIgnoreCase("0")) {
                        intent.putExtra("profile_picture", apiJson.getJSONObject("pictureUrls").getJSONArray("values").get(0).toString());
                    }
                    startActivity(intent);
                } catch (JSONException e) {
                    System.err.println("Error: LinkedinConnection parse JSON");
                }

            }

            @Override
            public void onApiError(LIApiError liApiError) {
                // Error making GET request!
                Log.d(TAG, liApiError.getApiErrorResponse().getMessage());
            }
        });
    }
}