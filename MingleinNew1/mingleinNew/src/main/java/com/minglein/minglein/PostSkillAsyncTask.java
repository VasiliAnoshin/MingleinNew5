package com.minglein.minglein;

import android.app.ProgressDialog;
import android.content.Context;
import android.os.AsyncTask;

import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicNameValuePair;
import org.json.JSONArray;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.List;

public class PostSkillAsyncTask extends AsyncTask<Void, Void, Boolean> {

    private String URL_POST = "http://vasili.milab.idc.ac.il/add_skill_to_user.php";
    private int CODE_OK = 200;

    private ProgressDialog loading;
    private Context context;
    private ArrayList<Integer> arrayListId;
    private String matches;
    private String phoneNubmer;
    private String linkedinId;
    private String firstName;
    private String lastName;
    private String photoUrl;

    public AsyncResponseInterface response = null;

    public PostSkillAsyncTask(ArrayList<Integer> array, String linkedinIn, String firstName, String lastName, String photoUrl, String phoneNubmer,
                              Context context) {
        this.arrayListId = array;
        this.context = context;
        this.phoneNubmer = phoneNubmer;
        this.loading = new ProgressDialog(this.context);
        this.linkedinId = linkedinIn;
        this.firstName = firstName;
        this.lastName = lastName;
        this.photoUrl = photoUrl;
    }

    @Override
    protected void onPreExecute() {
        loading.show();
    }

    @Override
    protected Boolean doInBackground(Void... params) {
        HttpClient client = new DefaultHttpClient();
        HttpPost post = new HttpPost(URL_POST);
        // Compose body
        JSONArray skills = new JSONArray();
        for (int i = 0; i < this.arrayListId.size(); i++) {
            skills.put(this.arrayListId.get(i));
        }
        List<NameValuePair> nameValuePairs = new ArrayList<NameValuePair>(6);
        nameValuePairs.add(new BasicNameValuePair("li_id", linkedinId));
        nameValuePairs.add(new BasicNameValuePair("first_name", firstName));
        nameValuePairs.add(new BasicNameValuePair("last_name", lastName));
        nameValuePairs.add(new BasicNameValuePair("photo_url", photoUrl));
        nameValuePairs.add(new BasicNameValuePair("phone", phoneNubmer));
        nameValuePairs.add(new BasicNameValuePair("skills", skills.toString()));
        //
        try {
            post.setEntity(new UrlEncodedFormEntity(nameValuePairs));
        } catch (UnsupportedEncodingException e1) {
            System.err.println("Error: PostSkillAsync unsupported encoding");
        }
        try {
            HttpResponse response = client.execute(post);
            if (response != null) {
                if (response.getStatusLine().getStatusCode() == CODE_OK) {
                    matches = response.getEntity().toString();
                    return true;
                }
            }
        } catch (ClientProtocolException e) {
            System.err.println("Error: SendSKillsAsyncTask Client protocol");
        } catch (IOException e) {
            System.err.println("Error: SendSKillsAsyncTask IO");
        }
        return false;
    }

    @Override
    protected void onPostExecute(Boolean result) {
        if (!result) {
            loading.dismiss();
            System.err.println("Error: SendSKillsAsyncTask returned false");
            return;
        }
        loading.dismiss();
        response.onFinishPostInfo(this.matches);
    }
}
