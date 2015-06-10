package com.minglein.minglein;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.squareup.picasso.Picasso;

import org.json.JSONArray;
import org.json.JSONException;

import java.util.List;

class MatchAdapter extends BaseAdapter {
    private CircularImageView photo;
    private Context context;
    private List<SingleMatch> classesList;
    private static LayoutInflater inflater = null;
    TextView firstSkill;
    TextView secondSkill;
    TextView thirdSkill;

    public MatchAdapter(Context context, List<SingleMatch> classesList) {
        this.context = context;
        // this.data = data;
        this.classesList = classesList;
        inflater = (LayoutInflater) context
                .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
    }

    @Override
    public int getCount() {
        return classesList.size();
    }

    @Override
    public Object getItem(int position) {
        return classesList.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        View vi = convertView;
        if (vi == null)
            vi = inflater.inflate(R.layout.match_single_view, null);
        vi.setBackgroundResource(R.color.white);
        SingleMatch match = classesList.get(position);
        String firstName = match.getFirstName();
        String lastName = match.getLastName();
        String photoURL = match.getPhotoUrl();

        String phone = match.getPhoneNumber();

        // !!!!!!!!!!!!!!!!! CHECK !!!!!!!!!!!!!!!!!!!
        try {
            JSONArray topSkills = new JSONArray(match.getTopSkills());
            firstSkill = (TextView) vi.findViewById(R.id.firstSkill);
            firstSkill.setText(topSkills.getString(0).replace("\r\n" , ""));

            //if (topSkills.getString(0).length() + 100 + topSkills.getString(1).length() <=300 ) {
            secondSkill = (TextView) vi.findViewById(R.id.secondSkill);
            secondSkill.setText(topSkills.getString(1).replace("\r\n", ""));

            //}


             thirdSkill = (TextView) vi.findViewById(R.id.thirdSkill);
            thirdSkill.setText(topSkills.getString(2));
        } catch (JSONException e) {
            System.err.println("MatchAdapter: Couldn't parse to JSONArray ");
        }
        // !!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!

        TextView fn = (TextView) vi.findViewById(R.id.firstName);
        fn.setText(firstName);
        TextView ln = (TextView) vi.findViewById(R.id.lastName);
        ln.setText(lastName);

        //ImageView imageView = (ImageView) vi.findViewById(R.id.image);
        photo = (CircularImageView) vi.findViewById(R.id.image);

        // User has an image
    if (photoURL != null && !photoURL.equalsIgnoreCase("")) {
        //Picasso.with(vi.getContext()).load(photoUrl).into(imageView);
        Picasso.with(vi.getContext()).load(photoURL).into(photo);
    } else {
        //imageView.setImageDrawable(vi.getResources().getDrawable(R.drawable.logo));
        photo.setImageDrawable(vi.getResources().getDrawable(R.drawable.default_avatar));
    }

    setViewOnClickListener(vi, firstName, lastName, photoURL, phone, firstSkill.toString() , secondSkill.toString() , thirdSkill.toString());
    return vi;
}

    private void setViewOnClickListener(View view, final String firstName, final String lastName, final String photoURL, final String phone ,
                                        final String firstSkill, final String secondSkill,  final String thirdSkill) {
        view.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(context, SendSMSActivity.class);
                intent.putExtra("phone", phone);
                intent.putExtra("first_name", firstName);
                intent.putExtra("last_name", lastName);
                intent.putExtra("photo_url", photoURL);
                intent.putExtra("fSkill", firstSkill);
                intent.putExtra("sSkill", secondSkill);
                intent.putExtra("tSkill",thirdSkill);
                context.startActivity(intent);
            }
        });
    }
}