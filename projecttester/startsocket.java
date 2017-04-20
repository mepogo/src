/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package projecttester;

import java.net.URI;
import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.print.DocFlavor;
import org.json.JSONException;
import org.json.JSONObject;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;


/**
 *
 * @author Poonam
 */
public class startsocket {
	final ArrayList<String> values = new ArrayList<String>();

	HtmlDoc parserobject = null;
	ProjectApp proApp;
	ArrayList<String> getsocketlist() {

		return values;
	}
	startsocket(ProjectApp obj) {
	    
	    proApp = obj;
	    }

	startsocket() {
	    
	    }
	void emptylist() {
		values.clear();
	}

	ArrayList<String> startsocketwiki() {

		try {

			final StringBuffer text = new StringBuffer();
			
			// open websocket
			WebsocketClientEndpoint clientEndPoint = null;
			try {
				clientEndPoint = new WebsocketClientEndpoint(new URI("ws://alpha.hatnote.com:9000"));
			} catch (URISyntaxException ex) {
				Logger.getLogger(startsocket.class.getName()).log(Level.SEVERE, null, ex);
			}

			// add listener
			clientEndPoint.addMessageHandler(new WebsocketClientEndpoint.MessageHandler() {
				public void handleMessage(String message) {

					try {
						JSONObject jsonObj = new JSONObject(message);

						// System.out.println(jsonObj.getString("url"));
						String liurl = jsonObj.getString("url");
						if(!(jsonObj.getString("is_minor").equalsIgnoreCase("true")) && !(jsonObj.getString("is_bot").equalsIgnoreCase("true"))){
							values.add(liurl);
						}
						

						text.append(jsonObj.getString("page_title") + "\n" + liurl).append('\n');
						//proApp.jTextArea1.setText(text.toString());
						
					} catch (JSONException ex) {
						// Logger.getLogger(startsocket.class.getName()).log(Level.SEVERE,
						// null, ex);
					}
				}
			});

			// send message to websocket
			clientEndPoint.sendMessage("{'event':'addChannel','channel':'ok_btccny_ticker'}");

			// wait 5 seconds for messages from websocket
			Thread.sleep(10000);

		} catch (InterruptedException ex) {
			System.err.println("InterruptedException exception: " + ex.getMessage());
		}

		return values;
	}
}
