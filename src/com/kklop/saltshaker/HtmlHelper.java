package com.kklop.saltshaker;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

import org.htmlcleaner.HtmlCleaner;
import org.htmlcleaner.TagNode;

import android.util.Log;

public class HtmlHelper {
	TagNode rootNode;
	
	public HtmlHelper(InputStream in) throws IOException {
		HtmlCleaner cleaner = new HtmlCleaner();
		rootNode = cleaner.clean(in);
	}
	
	/**
	 * 
	 * @param CSSClassname
	 * @return
	 */
	List<TagNode> getLinksByClass(String CSSClassname) {
		List<TagNode> linkList = new ArrayList<TagNode>();
		
		TagNode linkElements[] = rootNode.getElementsByName("a", true);
		
		for(int i=0; linkElements != null && i < linkElements.length; i++) {
			String classType = linkElements[i].getAttributeByName("class");
			Log.i("HtmlHelper", linkElements[i].getText().toString());
			if(classType != null && classType.equals(CSSClassname)) {
				linkList.add(linkElements[i]);
			}
		}
		
		return linkList;
	}
	
	String getUserName() {
		String user = "";
		TagNode linkElements[] = rootNode.getElementsByName("a", true);
		
		for(int i=0; linkElements != null && i < linkElements.length; i++) {
			String href = linkElements[i].getAttributeByName("href");
			Log.i("HtmlHelper", href);
			if(("http://www.saltybet.com/options").equals(href)) {
				TagNode link = linkElements[i];
				user = link.getText().toString();
			}
		}
		return user;
	}
	
	public String getBalance() {
		String balance = "";
		TagNode spans[] = rootNode.getElementsByName("span", true);
		
		for(int i=0; spans != null && i < spans.length; i++) {
			String id = spans[i].getAttributeByName("id");
			Log.i("HtmlHelper", id != null ? id : "");
			if(("balance").equals(id)) {
				TagNode b = spans[i];
				balance = b.getText().toString();
			}
		}
		return balance;
	}
	
}
