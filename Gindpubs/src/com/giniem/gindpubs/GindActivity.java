package com.giniem.gindpubs;

import java.io.File;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.Gravity;
import android.view.Menu;
import android.view.Window;
import android.view.WindowManager;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.LinearLayout;
import android.widget.Toast;

import com.giniem.gindpubs.client.GindClientTask;
import com.giniem.gindpubs.views.FlowLayout;
import com.giniem.gindpubs.views.MagazineThumb;

public class GindActivity extends Activity {

	public final static String BOOK_JSON_KEY = "com.giniem.gindpubs.BOOK_JSON_KEY";
	public final static String MAGAZINE_NAME = "com.giniem.gindpubs.MAGAZINE_NAME";
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		try {
			// Remove title bar
			this.requestWindowFeature(Window.FEATURE_NO_TITLE);

			// Remove notification bar
			this.getWindow().setFlags(
					WindowManager.LayoutParams.FLAG_FULLSCREEN,
					WindowManager.LayoutParams.FLAG_FULLSCREEN);

			// We get the shelf json asynchronously.
			GindClientTask asyncClient = new GindClientTask(this);
			asyncClient.execute(getString(R.string.newstand_manifest_url));

			// Log.i(this.getClass().getName(), "App ID: " +
			// this.getPackageName());
			// AccountManager manager = AccountManager.get(this);
			// Account[] accounts = manager.getAccounts();
			//
			// for (Account account : accounts) {
			// Log.i(this.getClass().getName(), account.name);
			// }

		} catch (Exception e) {
			e.printStackTrace();
			Log.e(this.getClass().getName(), "Cannot load configuration.");
		}
		loadingScreen();
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.gind, menu);
		return true;
	}

	public void loadingScreen() {
		setContentView(R.layout.loading);
		WebView webview = (WebView) findViewById(R.id.loadingWebView);
		webview.getSettings().setUseWideViewPort(true);
		webview.setWebViewClient(new WebViewClient() {

			@Override
			public boolean shouldOverrideUrlLoading(WebView view, String url) {
				view.loadUrl(url);
				return true;
			}
		});
		webview.loadUrl(getString(R.string.loadingUrl));
	}

	public void createThumbnails(final JSONArray jsonArray) {
		Log.i(this.getClass().getName(),
				"Shelf json contains " + jsonArray.length() + " elements.");

		JSONObject json = null;
		try {

			this.setContentView(R.layout.activity_gind);

			FlowLayout flowLayout = (FlowLayout) findViewById(R.id.thumbsContainer);

			int length = jsonArray.length();
			SimpleDateFormat sdfInput = new SimpleDateFormat(
					getString(R.string.inputDateFormat), Locale.US);
			SimpleDateFormat sdfOutput = new SimpleDateFormat(
					getString(R.string.outputDateFormat), Locale.US);
			for (int i = 0; i < length; i++) {
				json = new JSONObject(jsonArray.getString(i));
				Log.i(this.getClass().getName(), "Parsing JSON object " + json);

				LinearLayout inner = new LinearLayout(this);
				inner.setLayoutParams(new LinearLayout.LayoutParams(0,
						LinearLayout.LayoutParams.MATCH_PARENT, 1));
				inner.setGravity(Gravity.CENTER_HORIZONTAL);

				MagazineThumb thumb = new MagazineThumb(this);
				//thumb.setBackgroundColor(Color.CYAN);
				thumb.setName(json.getString("name"));
				thumb.setTitle(json.getString("title"));
				thumb.setInfo(json.getString("info"));

				Date date = sdfInput.parse(json.getString("date"));
				String dateString = sdfOutput.format(date);

				thumb.setDate(dateString);

				if (json.has("size")) {
					thumb.setSize(json.getInt("size"));
				} else {
					thumb.setSize(0);
				}

				thumb.setCover(json.getString("cover"));
				thumb.setUrl(json.getString("url"));
				thumb.init(this, null);
				if (this.magazineExists(thumb.getName())) {
					thumb.showActions();
				}

				flowLayout.addView(thumb);
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public void viewMagazine(final BookJson book) {
		Intent intent = new Intent(this, MagazineActivity.class);
	    try {
			intent.putExtra(BOOK_JSON_KEY, book.toJSON().toString());
			intent.putExtra(MAGAZINE_NAME, book.getMagazineName());
		    startActivity(intent);
		} catch (JSONException e) {
			Toast.makeText(this, "The book.json is invalid.",
					Toast.LENGTH_LONG).show();
		}
	}

	private boolean magazineExists(final String name) {
		boolean result = false;

		File magazine = new File(Configuration.getDiskDir(this).getPath()
				+ File.separator + name);
		result = magazine.exists() && magazine.isDirectory();

		return result;
	}
}