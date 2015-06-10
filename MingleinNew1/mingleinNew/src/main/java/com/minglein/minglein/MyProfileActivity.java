package com.minglein.minglein;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import com.squareup.picasso.Picasso;

import java.util.ArrayList;

public class MyProfileActivity extends Activity {

    // View
    private CircularImageView photo;
    private TextView firstNameTextView;
    private TextView lastNameTextView;
    private TextView phoneTextView;
    private ListView skillsListView;

    // Navigation
    private ImageView searchButton;
    private ImageView editButton;

    private String firstName;
    private String lastName;
    private String photoUrl;
    private String phone;
    private String id;
    private ArrayList<String> skillsArray;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_my_profile);


        Intent intent = getIntent();
        id = intent.getStringExtra("li_id");
        firstName = intent.getStringExtra("first_name");
        lastName = intent.getStringExtra("last_name");
        phone = intent.getStringExtra("phone");
        photoUrl = intent.getStringExtra("photo_url");
        skillsArray = intent.getStringArrayListExtra("skills_array");

        searchButton = (ImageView) findViewById(R.id.searchButton);
        firstNameTextView = (TextView) findViewById(R.id.firstName);
        lastNameTextView = (TextView) findViewById(R.id.lastName);
        phoneTextView = (TextView) findViewById(R.id.phone);
        skillsListView = (ListView) findViewById(R.id.skills);
        editButton = (ImageView) findViewById(R.id.editButton);
        photo = (CircularImageView) findViewById(R.id.profilePicture);

        phoneTextView.setText(phone);
        firstNameTextView.setText(firstName);
        lastNameTextView.setText(lastName);

        if (hasPhoto(photoUrl)) {
            Picasso.with(this).load(photoUrl).into(photo);
        } else {
            photo.setImageDrawable(getResources().getDrawable(R.drawable.logo));
        }

        setSearchButtonOnClick(searchButton);
        setEditButtonOnClick(editButton);

        if (skillsArray != null) {
            ArrayAdapter<String> adapter = new ArrayAdapter<String>(this,
                    android.R.layout.simple_list_item_1, android.R.id.text1);
            for (int i = 0; i < skillsArray.size(); i++) {
                adapter.add(skillsArray.get(i).toString());
            }
            skillsListView.setAdapter(adapter);
        }
    }

    private boolean hasPhoto(String photoUrl) {
        if (photoUrl != null && !photoUrl.equalsIgnoreCase(""))
            return true;
        return false;
    }

    private void setSearchButtonOnClick(View view) {
        view.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                finish();
            }
        });
    }

    private void setEditButtonOnClick(View view) {
        view.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MyProfileActivity.this, EditProfileActivity.class);
                intent.putExtra("first_name", firstName);
                intent.putExtra("last_name", lastName);
                intent.putExtra("profile_picture", photoUrl);
                intent.putExtra("li_id", id);
                startActivity(intent);
            }
        });
    }

}
