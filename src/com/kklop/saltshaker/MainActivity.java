package com.kklop.saltshaker;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.List;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

import org.apache.http.Header;
import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.StatusLine;
import org.apache.http.client.CookieStore;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.params.ClientPNames;
import org.apache.http.client.protocol.ClientContext;
import org.apache.http.cookie.Cookie;
import org.apache.http.message.BasicHeader;
import org.apache.http.protocol.HttpContext;
import org.htmlcleaner.TagNode;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.TextView;

/**
 * 
 * @author hal9000
 *
 */
@SuppressLint("SetJavaScriptEnabled")
public class MainActivity extends Activity {

	TextView textView;
	private ScheduledExecutorService scheduleTaskExecutor;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		
		setContentView(R.layout.activity_main);
		
		launchWebView("saltybet");
		
		scheduleTaskExecutor = Executors.newScheduledThreadPool(2);
		
		this.textView = (TextView) findViewById(R.id.player1);
		
		scheduleTaskExecutor.scheduleAtFixedRate(new Runnable() {
			public void run() {
				String url = new String("http://www.saltybet.com/");
				HttpClient httpClient = ((SaltyApplication)MainActivity.this.getApplicationContext()).getHttpClient();
				httpClient.getParams().setParameter(ClientPNames.ALLOW_CIRCULAR_REDIRECTS, true); 
				HttpContext context = ((SaltyApplication)MainActivity.this.getApplicationContext()).getHttpContext();
		        CookieStore cookieStore = ((SaltyApplication)MainActivity.this.getApplicationContext()).getCookieStore();
		        List<Cookie> cookies = cookieStore.getCookies();
		        if(cookies != null && cookies.size() > 0) {
		        	Log.i("ScheduleThread", "Found cookies " + cookies.size());
		        	for(Cookie cook : cookies) {
		        		Log.i("ScheduleThread", cook.toString());
		        	}
		        } else {
		        	Log.e("ScheduleThread", "No cookies found");
		        }
		        context.setAttribute(ClientContext.COOKIE_STORE, cookieStore);
				HttpGet httpGet = new HttpGet(url);
				Header header = new BasicHeader("Referer","http://www.saltybet.com/authenticate?signin=1");
				httpGet.setHeader(header);
				try {
					final String state = readJSONFeed("http://www.saltybet.com/state.json");
					final String zdata = readJSONFeed("http://www.saltybet.com/zdata.json");
					HttpResponse response = httpClient.execute(httpGet, context);
					StatusLine statusLine = response.getStatusLine();
		            int statusCode = statusLine.getStatusCode();
		            if (statusCode == 200) {
		                HttpEntity entity = response.getEntity();
		                InputStream inputStream = entity.getContent();
		                HtmlHelper helper = new HtmlHelper(inputStream);
		                final String user = helper.getUserName();
		                final List<TagNode> tags = helper.getLinksByClass("menu");
		                final String balance = helper.getBalance();
		                Log.i("ScheduleThread", "Found " + tags.size() + " tags");
		                runOnUiThread(new Runnable() {
		                	public void run() {
		                		textView.setText("Welcome, " + user + ". Balance is " + balance + ". state=" + state + " zdata=" + zdata);
		                	}
		                });
		                inputStream.close();
		            } else {
		                Log.d("Schedule", "Failed to download file");
		            }
				} catch(Exception e) {
					Log.e("ScheduleThread", "Error", e);
				}
			}
		}, 0, 5, TimeUnit.SECONDS);
	}
	
	public String readJSONFeed(String URL) {
        StringBuilder stringBuilder = new StringBuilder();
        HttpClient httpClient = ((SaltyApplication)MainActivity.this.getApplicationContext()).getHttpClient();
        HttpGet httpGet = new HttpGet(URL);
        try {
            HttpResponse response = httpClient.execute(httpGet);
            StatusLine statusLine = response.getStatusLine();
            int statusCode = statusLine.getStatusCode();
            if (statusCode == 200) {
                HttpEntity entity = response.getEntity();
                InputStream inputStream = entity.getContent();
                BufferedReader reader = new BufferedReader(
                        new InputStreamReader(inputStream));
                String line;
                while ((line = reader.readLine()) != null) {
                    stringBuilder.append(line);
                }
                inputStream.close();
            } else {
                Log.d("JSON", "Failed to download file");
            }
        } catch (Exception e) {
            Log.d("readJSONFeed", e.getLocalizedMessage());
        }        
        return stringBuilder.toString();
    }

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.main, menu);
		return true;
	}
	
	public void openLogin() {
		Intent intent = new Intent(this, LoginActivity.class);
		startActivity(intent);
	}	
	
	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		// Handle item selection
		switch (item.getItemId()) {
			case R.id.action_settings:
				openLogin();
				return true;
			default:
				return super.onOptionsItemSelected(item);
		}
		
	}

	/**
	 * 
	 * @param channelName
	 */
	@SuppressWarnings("deprecation")
	private void launchWebView(String channelName) {
		setContentView(R.layout.activity_main);
		
		WebView webView;
		webView = (WebView) findViewById(R.id.webview);
		webView.getSettings().setJavaScriptEnabled(true);
		webView.getSettings().setPluginsEnabled(true);
		webView.setWebViewClient(new WebViewClient());
		
		webView.loadUrl("http://www.twitch.tv/widgets/live_embed_player.swf?channel=" + channelName + "&auto_play=true");
		
	}

	@Override
	protected void onDestroy() {
		super.onDestroy();
		this.scheduleTaskExecutor.shutdown();
	}
	
	
}
