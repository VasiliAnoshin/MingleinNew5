package com.minglein.minglein;

//import com.facebook.widget.LoginButton;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;


public class MainFragment extends Fragment {
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		View view = inflater.inflate(R.layout.activity_login_facebook,
				container, false);

		//LoginButton authButton = (LoginButton) view
		//		.findViewById(R.id.authButton);
		//authButton.setFragment(this);
		//authButton.setReadPermissions(Arrays.asList("public_profile"));

		return view;
	}

}
