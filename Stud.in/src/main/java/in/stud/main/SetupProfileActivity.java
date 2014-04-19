package in.stud.main;

import android.app.Activity;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.media.Image;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.ProgressBar;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient.ConnectionCallbacks;
import com.google.android.gms.common.api.GoogleApiClient.OnConnectionFailedListener;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.plus.Plus;
import com.google.android.gms.plus.model.people.Person;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;

import in.stud.R;

public class SetupProfileActivity extends Activity implements
        ConnectionCallbacks, OnConnectionFailedListener{

    private GoogleApiClient mGoogleApiClient;

    private static String TAG = "SetupProfileActivity";

    private ProgressBar profileProgressBar;
    private ImageView profilePhoto;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_setup_profile);

        profileProgressBar = (ProgressBar) findViewById(R.id.profile_progress_bar);
        profilePhoto = (ImageView) findViewById(R.id.profile_photo);
        mGoogleApiClient = new GoogleApiClient.Builder(this)
                .addConnectionCallbacks(this)
                .addOnConnectionFailedListener(this)
                .addApi(Plus.API, null)
                .addScope(Plus.SCOPE_PLUS_LOGIN)
                .build();

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.setup_profile, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();
        if (id == R.id.action_settings) {
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    protected void onStart() {
        super.onStart();
        mGoogleApiClient.connect();
    }

    protected void onStop() {
        super.onStop();

        if (mGoogleApiClient.isConnected()) {
            mGoogleApiClient.disconnect();
        }
    }

    @Override
    public void onConnected(Bundle bundle) {
        if (Plus.PeopleApi.getCurrentPerson(mGoogleApiClient) != null) {
            Person currentPerson = Plus.PeopleApi.getCurrentPerson(mGoogleApiClient);
            Person.Image personPhoto = currentPerson.getImage();
            if (personPhoto.hasUrl()) {
                new DownloadProfilePhotoTask().execute(new String[] {personPhoto.getUrl()});
            }
        }
    }

    @Override
    public void onConnectionSuspended(int i) {

    }

    @Override
    public void onConnectionFailed(ConnectionResult connectionResult) {

    }

    public class DownloadProfilePhotoTask extends AsyncTask<String, Void , Bitmap> {

        @Override
        protected Bitmap doInBackground(String... strings) {
            Bitmap downloadedBitmap = null;
            try {
                String[] urlParts = strings[0].split("=");
                URL url = new URL(urlParts[0] + "=500");
                HttpURLConnection connection = (HttpURLConnection) url
                        .openConnection();
                connection.setDoInput(true);
                connection.connect();
                InputStream input = connection.getInputStream();
                downloadedBitmap = BitmapFactory.decodeStream(input);
            } catch (MalformedURLException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            }

            return downloadedBitmap;
        }

        @Override
        protected void onPostExecute (Bitmap mBitmap) {
            File destBitmapPath = new File(getFilesDir(), "myProfile.png");
            try {
                mBitmap.compress(Bitmap.CompressFormat.PNG, 90, new FileOutputStream(destBitmapPath));
            } catch (FileNotFoundException e) {
                e.printStackTrace();
                //We don't give a single fuck
            }
            profileProgressBar.setVisibility(View.GONE);
            profilePhoto.setImageBitmap(mBitmap);
            profilePhoto.setVisibility(View.VISIBLE);
        }
    }

}
