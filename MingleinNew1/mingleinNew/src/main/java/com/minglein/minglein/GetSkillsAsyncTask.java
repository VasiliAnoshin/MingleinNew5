package com.minglein.minglein;

import android.app.ProgressDialog;
import android.content.Context;
import android.os.AsyncTask;

import org.apache.http.HttpResponse;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;

public class GetSkillsAsyncTask extends AsyncTask<Void, Void, String> {

	private String URL_SKILLS = "http://vasili.milab.idc.ac.il/get_skills.php";
	private int CODE_OK = 200;

	private Context context;
	private ProgressDialog loading;

	public AsyncResponseInterface response = null;

	public GetSkillsAsyncTask(Context context, String skills) {
		this.context = context;
		this.loading = new ProgressDialog(this.context);
	}

	@Override
	protected void onPreExecute() {
		loading.show();
	}

	@Override
	protected String doInBackground(Void... params) {
		String skills = null;
		HttpClient client = new DefaultHttpClient();
		HttpGet get = new HttpGet(URL_SKILLS);
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
					skills = sb.toString();
				}
			}
		} catch (ClientProtocolException e) {
			System.err.println("Error: GetSkillsAsyncTask Client protocol");
		} catch (IOException e) {
			System.err.println("Error: GetSkillsAsyncTask IO");
		}

		return skills;
	}

	@Override
	protected void onPostExecute(String skills) {
		loading.dismiss();
		response.onFinishGetInfo(skills);
	}
}
