package net.nutrima.nutrimaproto;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Bundle;
import android.support.design.widget.CollapsingToolbarLayout;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.CompoundButton;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.Switch;

import com.google.android.gms.appindexing.Action;
import com.google.android.gms.appindexing.AppIndex;
import com.google.android.gms.common.api.GoogleApiClient;

import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.Set;
import java.util.concurrent.ExecutionException;

public class BusinessDetailsActivity extends AppCompatActivity {

    private ArrayList<String> plateNamesPM;
    private ArrayList<String> plateNamesFM;
    private ArrayList<String> plateNamesToDisplay;
    /**
     * ATTENTION: This was auto-generated to implement the App Indexing API.
     * See https://g.co/AppIndexing/AndroidStudio for more information.
     */
    private GoogleApiClient client;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_business_details);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
                        .setAction("Action", null).show();
            }
        });
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        // Setting Restaurant name as title //////////////////////////////
        CollapsingToolbarLayout ctl = (CollapsingToolbarLayout) findViewById(R.id.collapsing_toolbar_layout);

        Intent intent = this.getIntent();
        String businessName = intent.getStringExtra("BUSINESS_NAME");
        ctl.setTitle(businessName);
        //////////////////////////////////////////////////////////////////

        // Adding Yelp rating image //////////////////////////////////////
        ImageView ratingImageView = (ImageView) findViewById(R.id.rating_imageview);
        Drawable ratingImage = null;
        try {
            ratingImage = new UrlAsyncTask().execute("https://s3-media3.fl.yelpcdn.com/assets/2/www/img/22affc4e6c38/ico/stars/v1/stars_large_5.png").get();
        } catch (InterruptedException e) {
            e.printStackTrace();
        } catch (ExecutionException e) {
            e.printStackTrace();
        }
        ratingImageView.setImageDrawable(ratingImage);
        //////////////////////////////////////////////////////////////////

        // Creating menu listview ////////////////////////////////////////
        final ListView menuListView = (ListView) findViewById(R.id.menu_listview);

        plateNamesFM = new ArrayList<>();
        plateNamesFM.add("Test Plate 1");
        plateNamesFM.add("Test Plate 2");
        plateNamesFM.add("Test Plate 3");

        plateNamesPM = new ArrayList<>();
        plateNamesPM.add("Test Plate 1");
        plateNamesPM.add("Test Plate 2");

        plateNamesToDisplay = new ArrayList<>();

        plateNamesToDisplay.addAll(plateNamesPM);

        final ListMenuItemAdapter customAdapter = new ListMenuItemAdapter(this,
                R.layout.menu_list_item,
                R.id.menu_listview,
                plateNamesToDisplay);

        menuListView.setAdapter(customAdapter);
        //////////////////////////////////////////////////////////////////

        // Handle full menu switch ///////////////////////////////////////
        Switch fullMenuSwitch = (Switch) findViewById(R.id.full_menu_switch);
        fullMenuSwitch.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                plateNamesToDisplay.clear();
                if (isChecked) {
                    plateNamesToDisplay.addAll(plateNamesFM);
                } else {
                    plateNamesToDisplay.addAll(plateNamesPM);
                }

                customAdapter.notifyDataSetChanged();
            }
        });
        //////////////////////////////////////////////////////////////////
        // ATTENTION: This was auto-generated to implement the App Indexing API.
        // See https://g.co/AppIndexing/AndroidStudio for more information.
        client = new GoogleApiClient.Builder(this).addApi(AppIndex.API).build();
    }

    @Override
    public void onStart() {
        super.onStart();

        // ATTENTION: This was auto-generated to implement the App Indexing API.
        // See https://g.co/AppIndexing/AndroidStudio for more information.
        client.connect();
        Action viewAction = Action.newAction(
                Action.TYPE_VIEW, // TODO: choose an action type.
                "BusinessDetails Page", // TODO: Define a title for the content shown.
                // TODO: If you have web page content that matches this app activity's content,
                // make sure this auto-generated web page URL is correct.
                // Otherwise, set the URL to null.
                Uri.parse("http://host/path"),
                // TODO: Make sure this auto-generated app URL is correct.
                Uri.parse("android-app://net.nutrima.nutrimaproto/http/host/path")
        );
        AppIndex.AppIndexApi.start(client, viewAction);
    }

    @Override
    public void onStop() {
        super.onStop();

        // ATTENTION: This was auto-generated to implement the App Indexing API.
        // See https://g.co/AppIndexing/AndroidStudio for more information.
        Action viewAction = Action.newAction(
                Action.TYPE_VIEW, // TODO: choose an action type.
                "BusinessDetails Page", // TODO: Define a title for the content shown.
                // TODO: If you have web page content that matches this app activity's content,
                // make sure this auto-generated web page URL is correct.
                // Otherwise, set the URL to null.
                Uri.parse("http://host/path"),
                // TODO: Make sure this auto-generated app URL is correct.
                Uri.parse("android-app://net.nutrima.nutrimaproto/http/host/path")
        );
        AppIndex.AppIndexApi.end(client, viewAction);
        client.disconnect();
    }
}
