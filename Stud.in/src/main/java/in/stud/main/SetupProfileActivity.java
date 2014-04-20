package in.stud.main;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.app.DialogFragment;
import android.app.FragmentManager;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.location.Location;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GooglePlayServicesClient;
import com.google.android.gms.common.api.GoogleApiClient.ConnectionCallbacks;
import com.google.android.gms.common.api.GoogleApiClient.OnConnectionFailedListener;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.LocationClient;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.plus.Plus;
import com.google.android.gms.plus.model.people.Person;

import org.apache.http.HttpResponse;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
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

    LocationClient mLocationClient;
    static Location mCurrentLocation = null;

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

        mLocationClient = new LocationClient(getApplicationContext(),
                new GooglePlayServicesClient.ConnectionCallbacks() {
                    @Override
                    public void onConnected(Bundle bundle) {
                        mCurrentLocation = mLocationClient.getLastLocation();
                        Log.d(TAG, "Connected !!!");
                    }

                    @Override
                    public void onDisconnected() {

                    }
                }, this);
        mLocationClient.connect();
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
            String mCoverUrl = currentPerson.getCover().getCoverPhoto().getUrl();
            Person.Image personPhoto = currentPerson.getImage();
            if (personPhoto.hasUrl()) {
                new DownloadProfilePhotoTask().execute(new String[] {personPhoto.getUrl(), mCoverUrl});
            }
        }

    }

    @Override
    public void onConnectionSuspended(int i) {

    }

    @Override
    public void onConnectionFailed(ConnectionResult connectionResult) {

    }

    public class DownloadProfilePhotoTask extends AsyncTask<String, Void , Bitmap[]> {

        @Override
        protected Bitmap[] doInBackground(String... strings) {
            Bitmap downloadedProfileBitmap = null;
            Bitmap downloadedCoverBitmap = null;
            try {
                String[] urlParts = strings[0].split("=");
                URL profileUrl = new URL(urlParts[0] + "=500");
                URL coverUrl =  new URL(strings[1]);
                HttpURLConnection connection = (HttpURLConnection) profileUrl
                        .openConnection();
                connection.setDoInput(true);
                connection.connect();
                InputStream input = connection.getInputStream();
                downloadedProfileBitmap = BitmapFactory.decodeStream(input);

                HttpURLConnection connection2 = (HttpURLConnection) coverUrl.openConnection();
                connection2.setDoInput(true);
                connection2.connect();
                InputStream input2 = connection2.getInputStream();
                downloadedCoverBitmap = BitmapFactory.decodeStream(input2);

            } catch (MalformedURLException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            }

            return new Bitmap[] {downloadedProfileBitmap, downloadedCoverBitmap};
        }

        @Override
        protected void onPostExecute (Bitmap[] mBitmaps) {
            File destProfilePath = new File(getFilesDir(), "myProfile.png");
            File destCoverPath = new File(getFilesDir(), "myCover.png");
            try {
                mBitmaps[0].compress(Bitmap.CompressFormat.PNG, 90, new FileOutputStream(destProfilePath));
                mBitmaps[1].compress(Bitmap.CompressFormat.PNG, 90, new FileOutputStream(destCoverPath));
            } catch (FileNotFoundException e) {
                e.printStackTrace();
                //We don't give a single fuck
            }
            profileProgressBar.setVisibility(View.GONE);
            profilePhoto.setImageBitmap(mBitmaps[0]);
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
                MapDialog mapDialog = new MapDialog();
                FragmentManager fm = getFragmentManager();
                mapDialog.show(fm, "MAP_DIALOG");
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

        if ( name.equals("") || tagLine.equals("") || dob.equals("") || institutionType.equals("") || institutionName.equals("") || subjects.equals("")
                || MapDialog.homePosition == null) {
            if (name.equals(""))
                personName.setError("Please enter the value");
            else
                personName.setError(null);
            if (tagLine.equals(""))
                tagLineEdit.setError("Please enter the value");
            else
                tagLineEdit.setError(null);
            if (dob.equals(""))
                dobEdit.setError("Please enter the value");
            else
                dobEdit.setError(null);
            if (institutionType.equals(""))
                institutionTypeEdit.setError("Please enter the value");
            else
                institutionTypeEdit.setError(null);
            if (institutionName.equals(""))
                institutionNameEdit.setError("Please enter the value");
            else
                institutionNameEdit.setError(null);
            if (subjects.equals(""))
                subjectsEdit.setError("Please enter the value");
            else
                subjectsEdit.setError(null);
            if (MapDialog.homePosition == null)
                Toast.makeText(getApplicationContext(), "please select address", Toast.LENGTH_SHORT).show();
        } else {
            SharedPreferences profilePrefs = getSharedPreferences("PREFS_PROFILE", MODE_WORLD_WRITEABLE);
            SharedPreferences.Editor ed = profilePrefs.edit();
            ed.putString("name", name);
            ed.putString("tagLine", tagLine);
            ed.putString("dob", dob);
            ed.putString("institutionType", institutionType);
            ed.putString("institutionName", institutionName);
            ed.putString("subjects", subjects);
            ed.putString("email", Utils.getEmail(getBaseContext()));
            ed.commit();

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
                    + "&address=" + MapDialog.homePosition.latitude + "," + MapDialog.homePosition.longitude + "&subjects=" + subjects
                    + "&gcm_id=" + System.currentTimeMillis();
            Log.d(TAG, "GET URL = " + url);

            new AsyncTask<Void, Void, Void>() {

                @Override
                protected Void doInBackground(Void... voids) {
                    HttpClient httpClient = new DefaultHttpClient();
                    HttpGet httpget = new HttpGet(url);
                    try {
                        HttpResponse response = httpClient.execute(httpget);
                        Log.d(TAG, response.getEntity().toString());
                    } catch (ClientProtocolException e) {
                        e.printStackTrace();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                    return null;
                }

                @Override
                protected void onPostExecute (Void v) {
                    SharedPreferences settings = getSharedPreferences("PREFS_MAIN", MODE_PRIVATE);
                    SharedPreferences.Editor editor = settings.edit();
                    editor.putBoolean("is_registered", true);
                    editor.commit();
                    finish();
                    Toast.makeText(getApplicationContext(), "you have been registered", Toast.LENGTH_SHORT).show();
                    Intent mainIntent = new Intent(SetupProfileActivity.this, MainActivity.class);
                    startActivity(mainIntent);
                }
            }.execute();
        }
    }

    private static class MapDialog extends DialogFragment {

        public static LatLng homePosition = null;

        public MapDialog() {
            // Empty constructor required for DialogFragment
        }

        @Override
        public Dialog onCreateDialog(Bundle savedInstanceState) {
            AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
            // Get the layout inflater
            LayoutInflater inflater = getActivity().getLayoutInflater();

            // Inflate and set the layout for the dialog
            // Pass null as the parent view because its going in the dialog layout
            View rootView = inflater.inflate(R.layout.layout_maps_dialog, null);
            builder.setView(rootView)
                    // Add action buttons
                    .setPositiveButton("Select Address", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int id) {
                            // sign in the user ...
                        }
                    });
            final GoogleMap mMap = ((MapFragment) getFragmentManager().findFragmentById(R.id.map)).getMap();
            if (mCurrentLocation != null) {
                LatLng tempLatLng = new LatLng(mCurrentLocation.getLatitude(), mCurrentLocation.getLongitude());
                mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(tempLatLng, 13.0f));
            }
            mMap.setOnMapClickListener(new GoogleMap.OnMapClickListener() {

                @Override
                public void onMapClick(LatLng latLng) {
                    MarkerOptions markerOptions = new MarkerOptions();
                    markerOptions.position(latLng);
                    markerOptions.title("My Location");
                    homePosition = latLng;
                    mMap.addMarker(markerOptions);
                }
            });
            return builder.create();
        }

    }

}
