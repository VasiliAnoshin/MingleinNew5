package com.minglein.minglein;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.telephony.gsm.SmsManager;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnTouchListener;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.squareup.picasso.Picasso;

public class SendSMSActivity extends Activity {

    // View
    CircularImageView profilePicture;
    TextView firstNameTextView;
    TextView lastNameTextView;
    ImageView sendMessageButton;
    EditText messageContent;

    private String firstName;
    private String lastName;
    private String phone;
    private String photoUrl;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_send_sms);

        Intent intent = getIntent();
        firstName = intent.getStringExtra("first_name");
        lastName = intent.getStringExtra("last_name");
        phone = intent.getStringExtra("phone");
        photoUrl = intent.getStringExtra("photo_url");

        LinearLayout headerLayout = (LinearLayout) findViewById(R.id.headerLayout);
        headerLayout.setOnTouchListener(new OnTouchListener() {

            @Override
            public boolean onTouch(View v, MotionEvent event) {
                InputMethodManager imm = (InputMethodManager) getSystemService(INPUT_METHOD_SERVICE);
                imm.hideSoftInputFromWindow(getCurrentFocus().getWindowToken(),
                        0);
                return true;
            }
        });

        RelativeLayout profileLayout = (RelativeLayout) findViewById(R.id.profileLayout);
        profileLayout.setOnTouchListener(new OnTouchListener() {

            @Override
            public boolean onTouch(View v, MotionEvent event) {
                InputMethodManager imm = (InputMethodManager) getSystemService(INPUT_METHOD_SERVICE);
                imm.hideSoftInputFromWindow(getCurrentFocus().getWindowToken(),
                        0);
                return true;
            }
        });

        RelativeLayout messageLayout = (RelativeLayout) findViewById(R.id.messageMain);

        profilePicture = (CircularImageView) findViewById(R.id.profilePicture);
        if (photoUrl != null && !photoUrl.equalsIgnoreCase("")) {
            Picasso.with(this).load(photoUrl).into(profilePicture);
        } else {
            profilePicture.setImageDrawable(getResources().getDrawable(R.drawable.logo));
        }

        firstNameTextView = (TextView) findViewById(R.id.firstName);
        lastNameTextView = (TextView) findViewById(R.id.lastName);
        firstNameTextView.setText(firstName);
        lastNameTextView.setText(lastName);

        messageContent = (EditText) findViewById(R.id.messageContent);

        sendMessageButton = (ImageView) findViewById(R.id.sendMessageButton);
        sendMessageButton.setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View v) {
                sendSMS(phone, messageContent.getText().toString());
                Toast.makeText(SendSMSActivity.this, "Message was sent", Toast.LENGTH_LONG).show();
                finish();
            }
        });
    }

    public void sendSMS(String phone, String message) {

        try {
            SmsManager smsManager = SmsManager.getDefault();
            smsManager.sendTextMessage(phone, null, message, null, null);
        } catch (Exception e) {
            System.err.println("Error: SendSMSActivity message didn't send");
        }
    }

    public static void setMargins(View v, int l, int t, int r, int b) {
        if (v.getLayoutParams() instanceof ViewGroup.MarginLayoutParams) {
            ViewGroup.MarginLayoutParams p = (ViewGroup.MarginLayoutParams) v
                    .getLayoutParams();
            p.setMargins(l, t, r, b);
            v.requestLayout();
        }
    }
}
