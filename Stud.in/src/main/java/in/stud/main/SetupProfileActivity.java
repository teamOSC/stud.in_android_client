package in.stud.main;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient.ConnectionCallbacks;
import com.google.android.gms.common.api.GoogleApiClient.OnConnectionFailedListener;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.plus.Plus;
import com.google.android.gms.plus.model.people.Person;

import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.ResponseHandler;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.BasicResponseHandler;
import org.apache.http.impl.client.DefaultHttpClient;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.UnsupportedEncodingException;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLEncoder;

import in.stud.R;

public class SetupProfileActivity extends Activity implements
        Button.OnClickListener,
        ConnectionCallbacks, OnConnectionFailedListener{

    private GoogleApiClient mGoogleApiClient;

    private static String TAG = "SetupProfileActivity";

    private ProgressBar profileProgressBar;
    private ImageView profilePhoto;

    /*
     * FORM FIELDS
     */
    private Button registerButton;
    private EditText personName;
    private EditText tagLineEdit;
    private EditText dobEdit;
    private EditText institutionTypeEdit;
    private EditText institutionNameEdit;
    private Button selectResidence;
    private EditText subjectsEdit;

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

        registerButton = (Button) findViewById(R.id.button_register);
        registerButton.setOnClickListener(this);
        personName = (EditText) findViewById(R.id.person_name);
        tagLineEdit = (EditText) findViewById(R.id.tagline);
        dobEdit = (EditText) findViewById(R.id.dob);
        institutionTypeEdit = (EditText) findViewById(R.id.institution_type);
        institutionNameEdit = (EditText) findViewById(R.id.institution_name);
        selectResidence = (Button) findViewById(R.id.select_residence);
        selectResidence.setOnClickListener(this);
        subjectsEdit = (EditText) findViewById(R.id.subjects);
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

    @Override
    public void onClick (View v) {
        switch (v.getId()) {
            case R.id.button_register :
                registerUserToBackend();
                break;
            case R.id.select_residence:
                break;
        }
    }

    private void registerUserToBackend() {
        String name = personName.getText().toString();
        String tagLine = tagLineEdit.getText().toString();
        String dob = dobEdit.getText().toString();
        String institutionType = institutionTypeEdit.getText().toString();
        String institutionName = institutionNameEdit.getText().toString();
        String subjects = subjectsEdit.getText().toString();
        String email = "";

        if ( name.equals("") || tagLine.equals("") || dob.equals("") || institutionType.equals("") || institutionName.equals("") || subjects.equals("")) {
            if (name.equals(""))
                personName.setError("Please enter the value");
            if (tagLine.equals(""))
                tagLineEdit.setError("Please enter the value");
            if (dob.equals(""))
                dobEdit.setError("Please enter the value");
            if (institutionType.equals(""))
                institutionTypeEdit.setError("Please enter the value");
            if (institutionName.equals(""))
                institutionNameEdit.setError("Please enter the value");
            if (subjects.equals(""))
                subjectsEdit.setError("Please enter the value");
        } else {
            try {
                name =  URLEncoder.encode(name, "UTF-8");
                tagLine = URLEncoder.encode(tagLine, "UTF-8");
                dob = URLEncoder.encode(dob, "UTF-8");
                institutionType = URLEncoder.encode(institutionType, "UTF-8");
                institutionName = URLEncoder.encode(institutionName, "UTF-8");
                subjects = URLEncoder.encode(subjects, "UTF-8");
                email = URLEncoder.encode(Utils.getEmail(getApplicationContext()), "UTF-8");
            } catch (UnsupportedEncodingException e) {
                e.printStackTrace();
            }

            final String url = "http://tosc.in:5002/add?name="+name+"&tag_line="+tagLine+"&email="+email+
                    "&dob="+ dob + "&ins_type=" + institutionType + "&ins_name=" + institutionName
                    + "&address=" + "77.32,23.03" + "&subjects=" + subjects;
            Log.d(TAG, "GET URL = " + url);

            new AsyncTask<Void, Void, Void>() {

                @Override
                protected Void doInBackground(Void... voids) {
                    HttpClient httpClient = new DefaultHttpClient();
                    HttpGet httpget = new HttpGet(url);
                    try {
                        httpClient.execute(httpget);
                    } catch (ClientProtocolException e) {
                        e.printStackTrace();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }

                    return null;
                }

                @Override
                protected void onPostExecute (Void v) {
                    Toast.makeText(getApplicationContext(), "you have been registered", Toast.LENGTH_SHORT).show();
                    Intent mainIntent = new Intent(SetupProfileActivity.this, MainActivity.class);
                    startActivity(mainIntent);
                }
            }.execute();
        }
    }

}
