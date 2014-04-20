package in.stud.main;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.app.DialogFragment;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;

import java.util.ArrayList;
import java.util.Arrays;

import in.stud.R;

public class NotesUploadActivity extends Activity {

    private static final String TAG = "NotesUploadActivity";

    private static boolean photoSelected = false;

    private ArrayList<String> imagesPath = new ArrayList<String>();

    UploadingMessageDialog mDialog;

    boolean[] taskStatusList;

    /*
     * UI Elements
     */
    private EditText tagsEditText;

    String[] all_path;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_notes_upload);

        if (!photoSelected) {
            Intent i = new Intent(Action.ACTION_MULTIPLE_PICK);
            startActivityForResult(i, 200);
        }

        tagsEditText = (EditText) findViewById(R.id.tags_editText);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        all_path = data.getStringArrayExtra("all_path");

        photoSelected = true;
    }

    public class UploadFileTask extends AsyncTask<String, Void, Void> {

        int pos;

        public UploadFileTask (int pos) {
            this.pos = pos;
        }

        @Override
        protected Void doInBackground(String... path) {
            new ImageUploaderUtility().uploadSingleImage(path[0], tagsEditText.getText().toString());
            return null;
        }

        @Override
        protected void onPostExecute (Void v) {
            taskStatusList[pos] = false;
            if (!Arrays.asList(taskStatusList).contains(true)) {
                mDialog.dismiss();
            }
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

    public void upload (View v) {
        if (!tagsEditText.getText().toString().equals("")) {
            tagsEditText.setError(null);
            mDialog = new UploadingMessageDialog();
            mDialog.show(getFragmentManager(), "UPLOADING_DIALOG");
            mDialog.setCancelable(false);
            UploadFileTask[] mUploadTask = new UploadFileTask[all_path.length];
            taskStatusList = new boolean[all_path.length];
            for (int i = 0; i < all_path.length; ++i) {
                String path = all_path[i];
                mUploadTask[i] = new UploadFileTask(i);
                mUploadTask[i].execute(new String[]{path});
                taskStatusList[i] = true;
            }
        } else {
            tagsEditText.setError("Please enter tags");
        }
    }
}
