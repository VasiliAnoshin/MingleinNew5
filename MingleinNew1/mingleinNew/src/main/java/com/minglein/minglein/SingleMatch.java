package com.minglein.minglein;

import android.content.Context;

public class SingleMatch {

    private Context context;
    private String id;
    private String firstName;
    private String lastName;
    private String phoneNumber;
    private String photoUrl;
    private String topSkills;

    public SingleMatch(Context context, String id, String firstName, String lastName,
                       String phoneNumber, String photoUrl, String topSkills) {
        super();
        this.id = id;
        this.firstName = firstName;
        this.lastName = lastName;
        this.phoneNumber = phoneNumber;
        this.context = context;
        this.photoUrl = photoUrl;
        this.topSkills = topSkills;
    }

    public Context getContext() {
        return this.context;
    }

    public String getFirstName() {
        return this.firstName;
    }

    public String getLastName() {
        return this.lastName;
    }

    public String getPhoneNumber() {
        return this.phoneNumber;
    }

    public String getPhotoUrl() {
        return this.photoUrl;
    }

    public String getId() {
        return this.id;
    }

    public String getTopSkills() { return this.topSkills; }
}
