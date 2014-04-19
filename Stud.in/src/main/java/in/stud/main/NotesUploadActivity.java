package in.stud.main;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.app.DialogFragment;
import android.content.ClipData;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Toast;

import java.util.ArrayList;

import in.stud.R;

public class NotesUploadActivity extends Activity {

    private static final String TAG = "NotesUploadActivity";

    private static boolean photoSelected = false;

    private ArrayList<String> imagesPath = new ArrayList<String>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_notes_upload);

        if (!photoSelected) {
            Intent i = new Intent(Action.ACTION_MULTIPLE_PICK);
            startActivityForResult(i, 200);
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        String[] all_path = data.getStringArrayExtra("all_path");

        for (String path : all_path) {
            Log.d(TAG, "File Path = " + path);
        }
        photoSelected = true;

        UploadingMessageDialog mDialog = new UploadingMessageDialog();
        mDialog.show(getFragmentManager(), "UPLOADING_DIALOG");
        mDialog.setCancelable(false);
        new UploadFilesTask(mDialog).execute();
    }

    private class UploadFilesTask extends AsyncTask<Void, Void, Void> {

        private UploadingMessageDialog mDialog;

        public UploadFilesTask(UploadingMessageDialog mDialog) {
            this.mDialog = mDialog;
        }

        @Override
        protected Void doInBackground(Void... voids) {
            
            return null;
        }
    }

    public class UploadingMessageDialog extends DialogFragment {
        @Override
        public Dialog onCreateDialog(Bundle savedInstanceState) {
            // Use the Builder class for convenient dialog construction
            AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
            builder.setMessage("Uploading ...");
            // Create the AlertDialog object and return it
            return builder.create();
        }
    }
}
