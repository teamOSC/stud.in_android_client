package in.stud.main.fragments;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.app.DialogFragment;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.app.Fragment;
import android.os.Environment;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AbsListView;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.BaseAdapter;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.DefaultHttpClient;
import org.json.JSONArray;
import org.json.JSONException;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;

import in.stud.R;

import in.stud.main.ImageLoader;
import in.stud.main.ImageUploaderUtility;
import in.stud.main.NotesUploadActivity;
import in.stud.main.Utils;
import in.stud.main.content.NotesContent;

/**
 * A fragment representing a list of Items.
 * <p />
 * Large screen devices (such as tablets) are supported by replacing the ListView
 * with a GridView.
 * <p />
 * Activities containing this fragment MUST implement the  Callbacks
 * interface.
 */
public class NotesFragment extends Fragment implements AbsListView.OnItemClickListener {

    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    private static int loader = R.drawable.placeholder_anim;

    private OnFragmentInteractionListener mListener;

    public JSONArray mGlobalArray = null;

    /**
     * The fragment's ListView/GridView.
     */
    private AbsListView mListView;

    /**
     * The Adapter which will be used to populate the ListView/GridView with
     * Views.
     */
    private BaseAdapter mAdapter;

    // TODO: Rename and change types of parameters
    public static NotesFragment newInstance(String param1, String param2) {
        NotesFragment fragment = new NotesFragment();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        args.putString(ARG_PARAM2, param2);
        fragment.setArguments(args);
        return fragment;
    }

    /**
     * Mandatory empty constructor for the fragment manager to instantiate the
     * fragment (e.g. upon screen orientation changes).
     */
    public NotesFragment() {
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        if (getArguments() != null) {
            mParam1 = getArguments().getString(ARG_PARAM1);
            mParam2 = getArguments().getString(ARG_PARAM2);
        }

        NotesContent mContent = new NotesContent();
        mAdapter = new NotesAdapter(mContent);


    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_notes, container, false);

        // Set the adapter
        mListView = (GridView) view.findViewById(R.id.notes_list);
        mListView.setAdapter(mAdapter);

        // Set OnItemClickListener so we can be notified on item clicks
        mListView.setOnItemClickListener(this);

        return view;
    }

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        try {
            mListener = (OnFragmentInteractionListener) activity;
        } catch (ClassCastException e) {
            throw new ClassCastException(activity.toString()
                + " must implement OnFragmentInteractionListener");
        }
    }

    @Override
    public void onActivityCreated (Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        // Indicate that this fragment would like to influence the set of actions in the action bar.
        setHasOptionsMenu(true);
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mListener = null;
    }


    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        FUllImageDialog mFullDialog = new FUllImageDialog(position);
        mFullDialog.show(getFragmentManager(), "FULL_DIALOG");
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        // If the drawer is open, show the global app actions in the action bar. See also
        // showGlobalContextActionBar, which controls the top-left area of the action bar.
        inflater.inflate(R.menu.notes_fragment, menu);
        super.onCreateOptionsMenu(menu, inflater);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        if (item.getItemId() == R.id.action_notes_upload) {
            Intent mIntent = new Intent(getActivity(), NotesUploadActivity.class);
            getActivity().startActivity(mIntent);
            return true;
        }

        if (item.getItemId() == R.id.action_refresh) {
            new Utils.DownloadImageThumbnails(getActivity()) {
                @Override
                protected void onPostExecute (Void v) {
                    super.onPostExecute(v);
                    mListener.onFragmentInteraction("REFRESH_VIEW");
                }
            }.execute();
        }

        return super.onOptionsItemSelected(item);
    }

    /**
    * This interface must be implemented by activities that contain this
    * fragment to allow an interaction in this fragment to be communicated
    * to the activity and potentially other fragments contained in that
    * activity.
    * <p>
    * See the Android Training lesson <a href=
    * "http://developer.android.com/training/basics/fragments/communicating.html"
    * >Communicating with Other Fragments</a> for more information.
    */
    public interface OnFragmentInteractionListener {
        // TODO: Update argument type and name
        public void onFragmentInteraction(String id);
    }

    public class FUllImageDialog extends DialogFragment {

        int position;

        public FUllImageDialog(int position) {
            this.position = position;
        }
        @Override
        public Dialog onCreateDialog(Bundle savedInstanceState) {
            AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
            // Get the layout inflater
            LayoutInflater inflater = getActivity().getLayoutInflater();

            // Inflate and set the layout for the dialog
            // Pass null as the parent view because its going in the dialog layout
            final View fuckThis = inflater.inflate(R.layout.dialog_full_note, null);
            builder.setView(fuckThis)
                    // Add action buttons
                    .setPositiveButton("share", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int id) {
                            // sign in the user ...
                        }
                    })
                    .setNegativeButton("close", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialogInterface, int i) {

                        }
                    });
            ImageView fullImageView = (ImageView) fuckThis.findViewById(R.id.full_note_view);
            ImageLoader imgLoader = new ImageLoader(getActivity());
            try {
                imgLoader.DisplayImage(mGlobalArray.getJSONObject(position).getString("url"), loader, fullImageView);
            } catch (JSONException e) {
                e.printStackTrace();
            }
            return builder.create();
        }
    }

    public class NotesAdapter extends BaseAdapter {

        private NotesContent content;
        public JSONArray mArray;

        NotesAdapter( NotesContent nc) {
            content = nc;
            File file = new File(getActivity().getFilesDir(),"imagesData.json");

            StringBuilder text = new StringBuilder();

            try {
                BufferedReader br = new BufferedReader(new FileReader(file));
                String line;

                while ((line = br.readLine()) != null) {
                    text.append(line);
                    text.append('\n');
                }
                mArray = new JSONArray(text.toString());
                mGlobalArray = mArray;
            } catch (IOException e) {
                e.printStackTrace();
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }

        @Override
        public int getCount() {
            return mArray.length();
        }

        @Override
        public Object getItem(int i) {
            return content.mItems.get(i);
        }

        @Override
        public long getItemId(int i) {
            return i;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {

            View rowView;

            if (convertView == null) {
                LayoutInflater inflater = (LayoutInflater) getActivity().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
                rowView = inflater.inflate(R.layout.list_item_notes, null);
            } else {
                rowView = convertView;
            }

            ImageView thumbnailView = (ImageView) rowView.findViewById(R.id.notes_thumbnail);

            ImageLoader imgLoader = new ImageLoader(getActivity());

            try {
                imgLoader.DisplayImage(mArray.getJSONObject(position).getString("thumb_img"), loader, thumbnailView);
            } catch (JSONException e) {
                e.printStackTrace();
            }


            return rowView;
        }
    }

}
