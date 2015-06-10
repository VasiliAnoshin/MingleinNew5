package com.minglein.minglein;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.ImageView;
import android.widget.ListView;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

public class MatchesActivity extends Activity {

    private String matches;
    private ListView listview;
    private MatchAdapter matchAdapter;
    private ImageView sendAll;
    private ImageView settings;
    private ImageView search;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_matches);

        Intent intent = getIntent();
        String matches = intent.getStringExtra("matches");
        Log.v("MatchesActivity", matches);

        List<SingleMatch> classesList = new ArrayList<SingleMatch>();

        try {
            JSONArray matchesJSONArray = new JSONArray(matches);
            for (int i = 0; i < matchesJSONArray.length(); i++) {
                JSONObject user = matchesJSONArray.getJSONObject(i);

                Log.v("matches", user.getString("id"));
                Log.v("matches", user.getString("firstName"));
                Log.v("matches", user.getString("lastName"));
                Log.v("matches", user.getString("phone"));
                Log.v("matches", user.getString("photoUrl"));
                Log.v("matches", user.optString("topThreeSkills"));

//                GetTopThreeSkillsAsyncTask task = new GetTopThreeSkillsAsyncTask(this, user.getString("id"));
//                task.execute();
                classesList.add(new SingleMatch(this, user.getString("id"), user.getString("firstName"), user.getString("lastName"),
                        user.getString("phone"), user.getString("photoUrl"), user.optString("topThreeSkills").replaceAll("\r\n", "")));
            }
        } catch (JSONException e) {
            System.err.println("Error: MatchesActivity parse JSON");
        }

        listview = (ListView) findViewById(R.id.listview);
        sendAll = (ImageView) findViewById(R.id.message_icon);
//        settings = (ImageView) findViewById(R.id.settings);
        search = (ImageView) findViewById(R.id.searchButton);

        matchAdapter = new MatchAdapter(this, classesList);
        listview.setAdapter(matchAdapter);

        setMessageAllOnClick(sendAll);
//        setSettingsOnClick(settings);
        setSearchOnClick(search);
    }

    private void setSearchOnClick(View view) {
        view.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
    }

    // TODO:
//    private void setSettingsOnClick(View view) {
//        view.setOnClickListener(new OnClickListener() {
//            @Override
//            public void onClick(View v) {
//
//            }
//        });
//    }

    private void setMessageAllOnClick(View view) {
        view.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MatchesActivity.this,
                        SendSMSActivity.class);
                startActivity(intent);
            }
        });
    }
}
