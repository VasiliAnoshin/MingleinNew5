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

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;

public class GetSkilledUsersAsyncTask extends AsyncTask<Void, Void, String> {

	private String URL_SKILLED_USERS = "http://vasili.milab.idc.ac.il/get_skilled_users.php";
	private int CODE_OK = 200;

	private Context context;
	private ProgressDialog loading;
	private ArrayList<Integer> skillsId;

	public AsyncResponseInterface response = null;

	public GetSkilledUsersAsyncTask(ArrayList<Integer> array, Context context) {
		this.context = context;
		this.skillsId = array;
		this.loading = new ProgressDialog(this.context);
	}

	@Override
	protected void onPreExecute() {
		loading.show();
	}

	@Override
	protected String doInBackground(Void... params) {
		String matches = null;
		HttpClient client = new DefaultHttpClient();
		HttpPost post = new HttpPost(URL_SKILLED_USERS);
		// Compose body
		JSONArray skills = new JSONArray();
		for (int i = 0; i < this.skillsId.size(); i++) {
			skills.put(this.skillsId.get(i));
		}
		List<NameValuePair> nameValuePairs = new ArrayList<NameValuePair>(1);
		nameValuePairs.add(new BasicNameValuePair("skills", skills.toString()));
        try {
			post.setEntity(new UrlEncodedFormEntity(nameValuePairs));
			HttpResponse response = client.execute(post);
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
					matches = sb.toString();
				}
			}
		} catch (ClientProtocolException e) {
			System.err.println("Error: GetSkillsAsyncTask Client protocol");
		} catch (IOException e) {
			System.err.println("Error: GetSkillsAsyncTask IO");
		}

		return matches;
	}

	@Override
	protected void onPostExecute(String skills) {
		if (skills == null) {
			loading.dismiss();
			System.err
					.println("Error: GetSkilledUsersAsyncTask returned null (no matches)");
			return;
		}
		loading.dismiss();
		response.onFinishPostInfo(skills);
	}
}
