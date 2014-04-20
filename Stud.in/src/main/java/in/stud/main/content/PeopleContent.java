package in.stud.main.content;

import android.content.Context;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Helper class for providing sample content for user interfaces created by
 * Android template wizards.
 * <p>
 * TODO: Replace all uses of this class before publishing your app.
 */
public class PeopleContent {

    public static final String PEOPLE_SEARCH_TYPE[] =
            {
              "NEAR ME",
              "MY COLLEGE/SCHOOL",
              "IN MY CIRCLES"
            };

    private Context mContext;


    /**
     * An array of sample (dummy) items.
     */
    public static List<PeopleItem> mItems = new ArrayList<PeopleItem>();

    public PeopleContent( Context c ) throws JSONException {

        mContext = c;

        String jsonString = loadJSONFromAsset(mContext);
        JSONArray jarr = new JSONArray(jsonString);
        int len = jarr.length();
        for (int i = 0; i < len; i ++) {
            mItems.add(new PeopleItem(jarr.getJSONObject(i)));
        }

    }

    public String loadJSONFromAsset(Context context) {
        String json = null;
        try {
            InputStream is = context.getAssets().open("example_people.json");
            int size = is.available();
            byte[] buffer = new byte[size];
            is.read(buffer);
            is.close();
            json = new String(buffer, "UTF-8");
        } catch (IOException ex) {
            ex.printStackTrace();
            return null;
        }
        return json;
    }



    /**
     * A dummy item representing a piece of content.
     */
    public static class PeopleItem {
        public String
                name = "",
                tagLine = "",
                dob = "",
                email = "",
                instType = "",
                instName = "",
                subjects = "",
                address = "",
                gcmId = "";

        public PeopleItem(JSONObject jo) {
            try {
                name = jo.getString("name");
                tagLine = jo.getString("tag_line");
                dob = jo.getString("name");
                email = jo.getString("email");
                instType = jo.getString("ins_type");
                instName = jo.getString("tag_name");
                subjects = jo.getString("subjects");
                address = jo.getString("address");
                gcmId = jo.getString("gcm_id");
            } catch ( JSONException e ) {
                //omgwtf nothing happened !!!
            }
        }


    }
}
