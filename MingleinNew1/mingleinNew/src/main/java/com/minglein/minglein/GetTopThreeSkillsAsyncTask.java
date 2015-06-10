package com.minglein.minglein;

import android.app.ProgressDialog;
import android.content.Context;
import android.os.AsyncTask;

import org.apache.http.HttpResponse;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;
import org.json.JSONArray;
import org.json.JSONException;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;

public class GetTopThreeSkillsAsyncTask extends AsyncTask<Void, Void, JSONArray> {

    private String URL_SKILLS = "http://vasili.milab.idc.ac.il/get_top_three_skills.php";
    private int CODE_OK = 200;

    private Context context;
    private ProgressDialog loading;
    private String m_ID;

    public AsyncResponseInterface response = null;

    public GetTopThreeSkillsAsyncTask(Context i_Context, String i_ID) {
        this.context = i_Context;
        this.m_ID = i_ID;
    }

    @Override
    protected void onPreExecute() {
        loading.show();
    }

    @Override
    protected JSONArray doInBackground(Void... params) {
        JSONArray skills = null;
        HttpClient client = new DefaultHttpClient();
        HttpGet get = new HttpGet(URL_SKILLS + "?id=" + m_ID);
        try {
            HttpResponse response = client.execute(get);
            if (response != null) {
                if (response.getStatusLine().getStatusCode() == CODE_OK) {
                    StringBuilder sb = new StringBuilder();
                    BufferedReader reader = new BufferedReader(
                            new InputStreamReader(response.getEntity()
                                    .getContent()));
                    String line = null;
                    while ((line = reader.readLine()) != null) {
                        sb.append(line);
                    }
                    skills = new JSONArray(sb.toString());
                }
            }
        } catch (ClientProtocolException e) {
            System.err.println("Error: GetTopThreeSkillsAsyncTask Client protocol");
        } catch (IOException e) {
            System.err.println("Error: GetTopThreeSkillsAsyncTask IO");
        } catch (JSONException e) {
            System.err.println("Error: GetTopThreeSkillsAsyncTask json parse");
        }

        return skills;
    }

    @Override
    protected void onPostExecute(JSONArray skills) {
        loading.dismiss();
        response.onFinishGetInfo(skills.toString());
    }
}