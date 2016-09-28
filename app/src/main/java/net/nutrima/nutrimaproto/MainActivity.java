package net.nutrima.nutrimaproto;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import com.amazon.identity.auth.device.authorization.api.AmazonAuthorizationManager;
import com.facebook.AccessToken;
import com.facebook.CallbackManager;
import com.facebook.FacebookCallback;
import com.facebook.FacebookException;
import com.facebook.FacebookSdk;
import com.facebook.GraphRequest;
import com.facebook.GraphResponse;
import com.facebook.login.LoginManager;
import com.facebook.login.LoginResult;
import com.google.android.gms.appindexing.Action;
import com.google.android.gms.appindexing.AppIndex;
import com.google.android.gms.appindexing.Thing;
import com.google.android.gms.common.api.GoogleApiClient;

import org.apache.poi.hssf.usermodel.HSSFSheet;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Row;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Iterator;
import java.util.List;


public class MainActivity extends Activity {
    private static final String TAG = "MainActivity";

    private static final String[] APP_SCOPES = {
            "profile"
    };

    private Button btnLoginFacebook;
    private CallbackManager callbackManager;
    private AmazonAuthorizationManager mAuthManager;
    /**
     * ATTENTION: This was auto-generated to implement the App Indexing API.
     * See https://g.co/AppIndexing/AndroidStudio for more information.
     */
    private GoogleApiClient client;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        //--- FACEBOOK ---
        FacebookSdk.sdkInitialize(getApplicationContext());
        callbackManager = CallbackManager.Factory.create();
        setContentView(R.layout.activity_main);
        CognitoSyncClientManager.init(this);

        // Populate USDA data from xls ///////////////////////////////////
        List<NutritionUSDAEntry> USDAList = populateList();

        Globals.getInstance().setUSDATable(USDAList);
        //////////////////////////////////////////////////////////////////

        //If access token is already here, set fb session and proceed to application
        final AccessToken fbAccessToken = AccessToken.getCurrentAccessToken();
        if (fbAccessToken != null) {
            Log.i("Main Activity", "======= FB Button: Inside tokenn======= ");
            setFacebookSession(fbAccessToken);
            //btnLoginFacebook.setVisibility(View.GONE);
            Intent activityChangeIntent = new Intent(MainActivity.this, PersonalInfoActivity.class);
            startActivity(activityChangeIntent);
            finish();
        }


        btnLoginFacebook = (Button) findViewById(R.id.fb_button);
        btnLoginFacebook.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // start Facebook Login
                Log.i("Main Activity", "======= FB Button: Inside listen======= ");
                LoginManager.getInstance().logInWithReadPermissions(MainActivity.this, Arrays.asList("public_profile", "user_friends","user_birthday","user_about_me","email"));
                LoginManager.getInstance().registerCallback(callbackManager, new FacebookCallback<LoginResult>() {



                    @Override
                    public void onSuccess(LoginResult loginResult) {
                        Log.i("Main Activity", "======= FB Button: Inside onSuccess======= ");
                        btnLoginFacebook.setVisibility(View.INVISIBLE);
                        new GetFbName(loginResult).execute();
                        setFacebookSession(loginResult.getAccessToken());
                        Intent activityChangeIntent = new Intent(MainActivity.this, PersonalInfoActivity.class);
                        startActivity(activityChangeIntent);
                        finish();
                    }

                    @Override
                    public void onCancel() {
                        Toast.makeText(MainActivity.this, "Facebook login cancelled",
                                Toast.LENGTH_LONG).show();
                    }

                    @Override
                    public void onError(FacebookException error) {
                        Toast.makeText(MainActivity.this, "Error in Facebook login " +
                                error.getMessage(), Toast.LENGTH_LONG).show();
                    }
                });
            }
        });
        btnLoginFacebook.setEnabled(getString(R.string.facebook_app_id) != "facebook_app_id");

        //--- GOOGLE + ---
        //TODO: Handle Google + login


        final Button button = (Button) findViewById(R.id.sneak_peak_button);
        button.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                Intent activityChangeIntent = new Intent(MainActivity.this, PersonalInfoActivity.class);
                startActivity(activityChangeIntent);
            }
        });
        // ATTENTION: This was auto-generated to implement the App Indexing API.
        // See https://g.co/AppIndexing/AndroidStudio for more information.
        client = new GoogleApiClient.Builder(this).addApi(AppIndex.API).build();
    }

    private ArrayList<NutritionUSDAEntry> populateList() {
        ArrayList<NutritionUSDAEntry> list = new ArrayList<>();

        InputStream file = null;
        try {
            file = getResources().openRawResource(getResources().getIdentifier("nutrition",
                    "raw", getPackageName()));

            BufferedReader br = new BufferedReader(new InputStreamReader(file));
            //Create Workbook instance holding reference to .xlsx file
            HSSFWorkbook workbook = new HSSFWorkbook(file);

            //Get first/desired sheet from the workbook
            HSSFSheet sheet = workbook.getSheetAt(0);

            //Iterate through each rows one by one
            Iterator<Row> rowIterator = sheet.iterator();

            // Skip first two rows
            rowIterator.next();
            rowIterator.next();

            while (rowIterator.hasNext()) {
                Row row = rowIterator.next();
                //For each row, iterate through all the columns
                Iterator<Cell> cellIterator = row.cellIterator();

                Cell cell;

                // i is to index the sheet columns, format dependent
                int i = 0;
                NutritionUSDAEntry newEntry = new NutritionUSDAEntry();
                while (cellIterator.hasNext()) {
                    cell = cellIterator.next();
                    if (i == 0)
                        newEntry.setFood(cell.getStringCellValue().toLowerCase());
                    else if (i == 1)
                        newEntry.setServing(cell.getStringCellValue());
                    else if (i == 2)
                        newEntry.setWeight(cell.getNumericCellValue());
                    else if (i == 3)
                        newEntry.setCalories(cell.getNumericCellValue());
                    else if (i == 4)
                        newEntry.setTotalFat(cell.getNumericCellValue());
                    else if (i == 5)
                        newEntry.setCarbohydrates(cell.getNumericCellValue());
                    else if (i == 6)
                        newEntry.setProtein(cell.getNumericCellValue());
                    i++;
                }
                list.add(newEntry);
            }
            file.close();

        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return list;
    }

    private void setFacebookSession(AccessToken accessToken) {
        Log.i("Main Activity", "======= FB Button: Inside Token======= ");
        CognitoSyncClientManager.addLogins("graph.facebook.com",
                accessToken.getToken());
        //btnLoginFacebook.setVisibility(View.GONE);

    }

    /**
     * ATTENTION: This was auto-generated to implement the App Indexing API.
     * See https://g.co/AppIndexing/AndroidStudio for more information.
     */
    public Action getIndexApiAction() {
        Thing object = new Thing.Builder()
                .setName("Main Page") // TODO: Define a title for the content shown.
                // TODO: Make sure this auto-generated URL is correct.
                .setUrl(Uri.parse("http://www.nutrima.net"))
                .build();
        return new Action.Builder(Action.TYPE_VIEW)
                .setObject(object)
                .setActionStatus(Action.STATUS_TYPE_COMPLETED)
                .build();
    }

    @Override
    public void onStart() {
        super.onStart();

        // ATTENTION: This was auto-generated to implement the App Indexing API.
        // See https://g.co/AppIndexing/AndroidStudio for more information.
        client.connect();
        AppIndex.AppIndexApi.start(client, getIndexApiAction());
    }

    @Override
    public void onStop() {
        super.onStop();

        // ATTENTION: This was auto-generated to implement the App Indexing API.
        // See https://g.co/AppIndexing/AndroidStudio for more information.
        AppIndex.AppIndexApi.end(client, getIndexApiAction());
        client.disconnect();
    }

    private class GetFbName extends AsyncTask<Void, Void, String> {
        private final LoginResult loginResult;
        private ProgressDialog dialog;

        public GetFbName(LoginResult loginResult) {
            this.loginResult = loginResult;
        }

        @Override
        protected void onPreExecute() {
            dialog = ProgressDialog.show(MainActivity.this, "Wait", "Getting user name");
        }

        @Override
        protected String doInBackground(Void... params) {
            GraphRequest request = GraphRequest.newMeRequest(
                    loginResult.getAccessToken(),
                    new GraphRequest.GraphJSONObjectCallback() {
                        @Override
                        public void onCompleted(
                                JSONObject object,
                                GraphResponse response) {
                            // Application code
                            Log.v("LoginActivity", response.toString());
                        }
                    });
            Bundle parameters = new Bundle();
            parameters.putString("fields", "name");
            request.setParameters(parameters);
            GraphResponse graphResponse = request.executeAndWait();
            try {
                return graphResponse.getJSONObject().getString("name");
            } catch (JSONException e) {
                return null;
            }
        }

        @Override
        protected void onPostExecute(String response) {
            dialog.dismiss();
            if (response != null) {
                Toast.makeText(MainActivity.this, "Hello " + response, Toast.LENGTH_LONG).show();
            } else {
                Toast.makeText(MainActivity.this, "Unable to get user name from Facebook",
                        Toast.LENGTH_LONG).show();
            }
        }
    }
    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data){
        super.onActivityResult(requestCode, resultCode, data);
        callbackManager.onActivityResult(requestCode, resultCode, data);
    }

}
