package com.kklop.saltshaker;

import org.apache.http.client.HttpClient;
import org.apache.http.impl.client.BasicCookieStore;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.protocol.BasicHttpContext;
import org.apache.http.protocol.HttpContext;

import android.app.Application;
/**
 * 
 * @author hal9000
 *
 */
public class SaltyApplication extends Application {
	private boolean loggedIn = false;
	private HttpClient httpClient = null;
	private BasicCookieStore cookieStore = null;
	private HttpContext httpContext = null;

	public boolean isLoggedIn() {
		return loggedIn;
	}

	public void setLoggedIn(boolean loggedIn) {
		this.loggedIn = loggedIn;
	}

	public HttpClient getHttpClient() {
		if(this.httpClient == null) {
			this.httpClient = new DefaultHttpClient();
		}
		return httpClient;
	}

	public BasicCookieStore getCookieStore() {
		if(this.cookieStore == null) {
			this.cookieStore = new BasicCookieStore();
		}
		return cookieStore;
	}

	public HttpContext getHttpContext() {
		if(this.httpContext == null) {
			this.httpContext = new BasicHttpContext();
		}
		return httpContext;
	}
}
