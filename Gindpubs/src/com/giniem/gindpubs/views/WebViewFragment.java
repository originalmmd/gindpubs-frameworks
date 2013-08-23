package com.giniem.gindpubs.views;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.WebChromeClient;

import com.giniem.gindpubs.R;

@SuppressLint("SetJavaScriptEnabled")
public class WebViewFragment extends Fragment {
	public static final String ARG_OBJECT = "object";

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		// The last two arguments ensure LayoutParams are inflated
		// properly.
		View rootView = inflater.inflate(R.layout.fragment_collection_object,
				container, false);
		Bundle args = getArguments();
		CustomWebView view = (CustomWebView) rootView.findViewById(R.id.webpage1);
		view.getSettings().setJavaScriptEnabled(true);
		view.setWebChromeClient(new WebChromeClient());
		view.loadUrl(args.getString(ARG_OBJECT));
		return rootView;
	}
}