package in.stud.main;

import android.accounts.Account;
import android.accounts.AccountManager;
import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.AsyncTask;
import android.util.Log;
import android.widget.Toast;

import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.util.EntityUtils;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;


/**
 * Created by omerjerk on 19/4/14.
 */
public class Utils {

    public static String getEmail(Context context) {
        AccountManager accountManager = AccountManager.get(context);
        Account account = getAccount(accountManager);

        if (account == null) {
            return null;
        } else {
            return account.name;
        }
    }

    private static Account getAccount(AccountManager accountManager) {
        Account[] accounts = accountManager.getAccountsByType("com.google");
        Account account;
        if (accounts.length > 0) {
            account = accounts[0];
        } else {
            account = null;
        }
        return account;
    }


    public static boolean isNetworkConnected(Context context) {

        try {
            ConnectivityManager cm = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
            NetworkInfo ni = cm.getActiveNetworkInfo();
            if (ni == null) {
                // There are no active networks.
                return false;
            } else {
                return true;
            }
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }

    }


        public static void CopyStream(InputStream is, OutputStream os)
        {
            final int buffer_size=1024;
            try
            {
                byte[] bytes=new byte[buffer_size];
                for(;;)
                {
                    int count=is.read(bytes, 0, buffer_size);
                    if(count==-1)
                        break;
                    os.write(bytes, 0, count);
                }
            }
            catch(Exception ex){}
        }

    public static class DownloadImageThumbnails extends AsyncTask<Void, Void, Void> {

        private Context context;

        public DownloadImageThumbnails(Context context) {
            this.context = context;
        }

        @Override
        protected Void doInBackground(Void... voids) {
            HttpClient httpclient = new DefaultHttpClient();
            HttpGet httpget = new HttpGet("http://tosc.in:5002/notes");
            try {
                HttpResponse response = httpclient.execute(httpget);
                String outputJSON = EntityUtils.toString(response.getEntity());
                Log.d("UTILS", "Response = " + outputJSON);
                File cacheFile = new File(context.getFilesDir(), "imagesData.json");


                BufferedWriter bw = null;
                try {
                    if (!cacheFile.exists()) {
                        cacheFile.createNewFile();
                    }

                    FileWriter fw = new FileWriter(cacheFile.getAbsoluteFile());
                    bw = new BufferedWriter(fw);
                    bw.write(outputJSON);

                } catch (Exception e){
                    e.printStackTrace();
                    Toast.makeText(context, "Sorry! Something went wrong.", Toast.LENGTH_SHORT).show();
                } finally {
                    try {
                        bw.close();
                    } catch (Exception e) {
                        e.printStackTrace();
                        //Should never happen
                    }

                }
            } catch (IOException e) {
                e.printStackTrace();
            }
            return null;
        }
    }

}
