package com.minglein.minglein;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
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
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.TextView.OnEditorActionListener;
import android.widget.Toast;

import com.squareup.picasso.Picasso;

import org.json.JSONArray;
import org.json.JSONException;

import java.util.ArrayList;

public class EditProfileActivity extends Activity implements
        AsyncResponseInterface {

    final String TAG = "EditProfile";
    final String SKILL_ID = "id";
    final String SKILL = "skill";

    // Phone number
    EditText phoneNumberEditText;

    // Auto complete
    AutoCompleteTextView searchField;
    ArrayAdapter<String> autoCompleteAdapter;
    String item;

    // View
    TextView activityName;
    ImageView nextButton;
    ImageView backButton;
    CircularImageView profilePicture;

    // Skill list
    ListView listView;
    ArrayList<String> arrayList;
    ArrayAdapter<String> skillAdapter;

    private String skills;

    // JSON Parse
    JSONArray jsonArray = null;
    ArrayList<String> skillsList = null;
    ArrayList<Integer> skillsIdList = null;

    // Linkedin
    private String linkedinId;
    private String firstName;
    private String lastName;
    private String profilePictureUrl;

    private SharedPreferences prefs;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_profile);

        Intent intent = getIntent();
        linkedinId = intent.getStringExtra("li_id");
        firstName = intent.getStringExtra("first_name");
        lastName = intent.getStringExtra("last_name");
        profilePictureUrl = intent.getStringExtra("profile_picture");

        RelativeLayout mainLayout = (RelativeLayout) findViewById(R.id.mainLayout);
        mainLayout.setOnTouchListener(new OnTouchListener() {

            @Override
            public boolean onTouch(View v, MotionEvent event) {
                InputMethodManager imm = (InputMethodManager) getSystemService(INPUT_METHOD_SERVICE);
                imm.hideSoftInputFromWindow(getCurrentFocus().getWindowToken(),
                        0);
                return true;
            }
        });

        // Profile picture
        profilePicture = (CircularImageView) findViewById(R.id.profilePicture);
        if (profilePictureUrl != null) {
            Picasso.with(EditProfileActivity.this)
                    .load(profilePictureUrl).into(profilePicture);
        } else {
            profilePicture.setImageDrawable(getResources().getDrawable(
                    R.drawable.logo));
        }

        // Welcome text
        TextView helloTextView = (TextView) findViewById(R.id.hellowTextView);
        helloTextView.setText("Hello" + " " + firstName + " " + lastName);

        TextView editTextView = (TextView) findViewById(R.id.editProfileText);
        editTextView.setText("Please edit your profile");

        // Navigation buttons
        nextButton = (ImageView) findViewById(R.id.nextButton);
        nextButton.setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View v) {
                sendToServer();
            }
        });

        // Phone number edit text
        phoneNumberEditText = (EditText) findViewById(R.id.editProfilePhoneNumber);

        // Skills auto complete
        searchField = (AutoCompleteTextView) findViewById(R.id.search);
        searchField.setOnEditorActionListener(new OnEditorActionListener() {
            public boolean onEditorAction(TextView v, int actionId,
                                          KeyEvent event) {
                if ((event != null && (event.getKeyCode() == KeyEvent.KEYCODE_ENTER))
                        || (actionId == EditorInfo.IME_ACTION_DONE)
                        || (actionId == EditorInfo.IME_ACTION_NEXT)) {
                    sendToServer();
                }
                return false;
            }
        });

        // Skills list view
        listView = (ListView) findViewById(R.id.skills_list);
        arrayList = new ArrayList<String>();
        skillAdapter = new ArrayAdapter<String>(this,
                android.R.layout.simple_list_item_1, arrayList);
        listView.setAdapter(skillAdapter);
        //

        GetSkillsAsyncTask async = new GetSkillsAsyncTask(this, skills);
        async.response = this;

        async.execute();
    }

    @Override
    public void onFinishGetInfo(String output) {
        if (output == null) {
            System.err.println("Error: EditProfileActivity no skills found");
            // TODO: Tell user connect to Internet
            return;
        }

        try {
            jsonArray = new JSONArray(output);
            skillsList = new ArrayList<String>();
            skillsIdList = new ArrayList<Integer>();
            for (int i = 0; i < jsonArray.length(); i++) {
                skillsList.add(jsonArray.getJSONObject(i).getString(SKILL));
            }
        } catch (JSONException e) {
            System.err.println("Error: EditProfileActivity parse json");
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
    public void onFinishPostInfo(String matches) {
        Intent intent = new Intent(EditProfileActivity.this,
                SearchActivity.class);
        intent.putExtra("li_id", linkedinId);
        intent.putExtra("first_name", firstName);
        intent.putExtra("last_name", lastName);
        intent.putExtra("phone", phoneNumberEditText.getText().toString());
        intent.putExtra("profile_picture", profilePictureUrl);
        intent.putExtra("skills_array", arrayList);

        // Save this user in pref
        SharedPreferences prefs = getSharedPreferences("com.minglein.minglein", Context.MODE_PRIVATE);
        String li_id = "com.minglein.minglein.li_id";
        prefs.edit().putString(li_id, linkedinId).apply();

        startActivity(intent);
    }

    private void sendToServer() {
        if (checkNumOfSkills()) {
            PostSkillAsyncTask post = new PostSkillAsyncTask(this.skillsIdList, linkedinId, firstName, lastName, profilePictureUrl,
                    phoneNumberEditText.getText().toString(), this);
            post.response = this;
            post.execute();
        } else {
            Toast.makeText(this, "You must enter at least 2 skills", Toast.LENGTH_LONG).show();
        }
    }

    private boolean checkNumOfSkills() {
        if (skillsIdList.size() < 2) {
            return false;
        }
        return true;
    }
}
