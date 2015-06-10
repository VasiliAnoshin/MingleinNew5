package com.minglein.minglein;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnTouchListener;
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.TextView.OnEditorActionListener;


import org.json.JSONArray;
import org.json.JSONException;

import java.util.ArrayList;

public class SearchActivity extends Activity implements AsyncResponseInterface {

	final String TAG = "Search";
	final String SKILL_ID = "id";
	final String SKILL = "skill";

	// Auto complete
	AutoCompleteTextView searchField;
	ArrayAdapter<String> autoCompleteAdapter;
	String item;

	// View
	TextView searchWelcome;
	TextView searchQuestion;
	ImageView nextButton;
	ImageView backButton;
	ImageView searchButton;
	ImageView settingsButton;
	RelativeLayout searchLayout;

	// Skill list
	ListView listView;
	ArrayList<String> arrayList;
	ArrayAdapter<String> skillAdapter;

	// JSON Parse
	JSONArray jsonArray = null;
	ArrayList<String> skillsList = null;
	ArrayList<Integer> skillsIdList = null;

	private String skills;

    private String linkedinId;
    private String firstName;
    private String lastName;
    private String phone;
    private String photoUrl;
    private ArrayList<String> skillsArray;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_search);

        Intent intent = getIntent();
        linkedinId = intent.getStringExtra("li_id");
        firstName = intent.getStringExtra("first_name");
        lastName = intent.getStringExtra("last_name");
        phone = intent.getStringExtra("phone");
        photoUrl = intent.getStringExtra("profile_picture");
        skillsArray = intent.getStringArrayListExtra("skills_array");

		LinearLayout mainLayout = (LinearLayout) findViewById(R.id.searchMainLayout);
		mainLayout.setOnTouchListener(new OnTouchListener() {

			@Override
			public boolean onTouch(View v, MotionEvent event) {
				InputMethodManager imm = (InputMethodManager) getSystemService(INPUT_METHOD_SERVICE);
				imm.hideSoftInputFromWindow(getCurrentFocus().getWindowToken(),
						0);
				return true;
			}
		});

		settingsButton = (ImageView) findViewById(R.id.settingButton);
		settingsButton.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				Intent intent = new Intent(SearchActivity.this,
						MyProfileActivity.class);
                intent.putExtra("li_id", linkedinId);
				intent.putExtra("first_name", firstName);
				intent.putExtra("last_name", lastName);
                intent.putExtra("phone", phone);
				intent.putExtra("photo_url", photoUrl);
                if (skillsArray != null) {
                    intent.putExtra("skills_array", skillsArray);
                }
				startActivity(intent);
			}
		});

		searchWelcome = (TextView) findViewById(R.id.searchWelcome);
		searchWelcome.setText("Welcome to IDC");

		searchQuestion = (TextView) findViewById(R.id.searchQuestion);

		searchLayout = (RelativeLayout) findViewById(R.id.search_layout);

		searchField = (AutoCompleteTextView) findViewById(R.id.search);
		searchField.setOnEditorActionListener(new OnEditorActionListener() {
			public boolean onEditorAction(TextView v, int actionId,
					KeyEvent event) {
				if ((event != null && (event.getKeyCode() == KeyEvent.KEYCODE_ENTER))
						|| (actionId == EditorInfo.IME_ACTION_DONE)) {
					sendToServer();
				}
				return false;
			}
		});
		searchButton = (ImageView) findViewById(R.id.searchButton);
		searchButton.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				sendToServer();

			}
		});

		// Skills List view
		listView = (ListView) findViewById(R.id.skills_list);
		arrayList = new ArrayList<String>();
		skillAdapter = new ArrayAdapter<String>(this,
				android.R.layout.simple_list_item_1, arrayList);
		listView.setAdapter(skillAdapter);
        skillsIdList = new ArrayList<Integer>();
		//

		GetSkillsAsyncTask async = new GetSkillsAsyncTask(this, skills);
		async.response = this;

		async.execute();
	}

    @Override
    public void onStart() {
        super.onStart();
        arrayList.clear();
        skillAdapter.clear();
        skillsIdList.clear();
    }

	private int getSkillId(String skill, JSONArray array) {
		for (int i = 0; i < array.length(); i++) {
			try {
				if (array.getJSONObject(i).getString(SKILL)
						.equalsIgnoreCase(skill)) {
					return array.getJSONObject(i).getInt(SKILL_ID);
				}
			} catch (JSONException ex) {

			}
		}
		return -1;
	}

	@Override
	public void onFinishGetInfo(String output) {
		if (output == null) {
			System.err.println("Error: SearchActivity no skills found");
			// TODO: Tell user connect to Internet
			return;
		}

		try {
			jsonArray = new JSONArray(output);
			skillsList = new ArrayList<String>();
			for (int i = 0; i < jsonArray.length(); i++) {
				skillsList.add(jsonArray.getJSONObject(i).getString(SKILL));
			}
		} catch (JSONException e) {
			System.err.println("Error: SearchActivity parse json");
		}

		if (skillsList == null) {
			return;
		}

		autoCompleteAdapter = new ArrayAdapter<String>(this,
				android.R.layout.simple_dropdown_item_1line, skillsList);
		searchField.setAdapter(autoCompleteAdapter);

		item = null;
		// Auto complete on click
		searchField
				.setOnItemClickListener(new AdapterView.OnItemClickListener() {
					@Override
					public void onItemClick(AdapterView<?> parent, View view,
							int position, long id) {
						// Clear text
						searchField.setText("");
						// Add to list view only if not already contain
						item = autoCompleteAdapter.getItem(position).toString();
						if (!arrayList.contains(item)) {
							skillAdapter.add(item);
							skillsIdList.add(getSkillId(item, jsonArray));
						}

					}
				});
	}

	@Override
	public void onFinishPostInfo(String matches) {
		Intent intent = new Intent(SearchActivity.this, MatchesActivity.class);
		intent.putExtra("matches", matches);
		startActivity(intent);
	}

	private void sendToServer() {
		GetSkilledUsersAsyncTask getSkilledUsers = new GetSkilledUsersAsyncTask(
				this.skillsIdList, this);
		getSkilledUsers.response = SearchActivity.this;
		getSkilledUsers.execute();
	}

}
